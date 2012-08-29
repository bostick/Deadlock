package com.gutabi.deadlock.controller;


import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.IntersectionPosition;
import com.gutabi.deadlock.core.IntersectionType;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;
import com.gutabi.deadlock.model.CrashInfo;
import com.gutabi.deadlock.model.CrashSite;


public class SimulationRunnable implements Runnable {
	
	List<Car> specials = new ArrayList<Car>();
	
	long step = 0;
	long lastSpawnStep = 0;
	
	List<Edge> edgesCopy;
	List<Intersection> intersectionsCopy;
	ControlMode modeCopy;
	List<Car> movingCarsCopy;
	List<Car> crashedCarsCopy;
	
	List<Intersection> sources;
	
	//@SuppressWarnings("serial")
	@Override
	public void run() {
		
		firstCrashSite = null;
		crashes.clear();
		
		synchronized (MODEL) {
			
			MODEL.movingCars.clear();
			MODEL.crashedCars.clear();
			
			edgesCopy = new ArrayList<Edge>();
			for (Edge e : MODEL.getEdges()) {
				edgesCopy.add(e);
			}
			intersectionsCopy = new ArrayList<Intersection>();
			for (Intersection v : MODEL.getIntersections()) {
				v.hasCrash = false;
				intersectionsCopy.add(v);
			}
			modeCopy = MODEL.getMode();
			movingCarsCopy = new ArrayList<Car>(MODEL.movingCars);
			crashedCarsCopy = new ArrayList<Car>(MODEL.crashedCars);
		}
		
		outer:
		while (true) {
			
			synchronized (MODEL) {
				modeCopy = MODEL.getMode();
				if (modeCopy == ControlMode.IDLE) {
					break outer;
				}
			}
			
			sources = activeSources();
			
			if (sources.isEmpty() && movingCarsCopy.isEmpty()) {
				break outer;
			}
			
			if (MODEL.SPAWN_FREQUENCY > 0 && (step == 0 || (step - lastSpawnStep) >= MODEL.SPAWN_FREQUENCY)) {
				spawnNewCars();
			}
			
			for (Car c : movingCarsCopy) {
				
				c.calculateFuturePath();
				
			}  // for 
			
			movingFixPoint();
			
			synchronized (MODEL) {
				MODEL.movingCars = new ArrayList<Car>(movingCarsCopy);
				MODEL.crashedCars = new ArrayList<Car>(crashedCarsCopy);
			}
			
			VIEW.repaint();
			
			try {
				Thread.sleep(MODEL.WAIT);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			step++;
			
		} // outer
		
	}
	
	private List<Intersection> activeSources() {
		
		List<Intersection> sources = new ArrayList<Intersection>();
		for (Intersection v : intersectionsCopy) {
			if (v.getType() == IntersectionType.SOURCE && !v.hasCrash) {
				sources.add(v);
			}
		}
		List<Intersection> toRemove = new ArrayList<Intersection>();
		for (Car c : movingCarsCopy) {
			Position carPos = c.getPosition();
			for (Intersection v : sources) {
				if (carPos.distanceTo(new IntersectionPosition(v, null, null, 0)) <= 10) {
					toRemove.add(v);
				}
			}
		}
		for (Intersection v : toRemove) {
			sources.remove(v);
		}
		
		return sources;
		
	}
	
	private void spawnNewCars() {
		
		List<Car> newCars = new ArrayList<Car>();
		
		int n = sources.size();
		
		for (int i = 0; i < n; i++) {
			newCars.add(new Car());
		}
		
		for (Car c : newCars) {
			if (sources.size() == 0) {
				continue;
			}
			int i = MODEL.RANDOM.nextInt(sources.size());
			
			Intersection v = sources.get(i);
			
			c.startingIntersection = v;
			c.startingStep = step;
			
			c.futurePathAdd(new IntersectionPosition(v, null, null, 0));
			
			c.futureState = CarState.INTERSECTION;
			
			sources.remove(v);
		}
		
		collapseFutures(newCars);
		
		movingCarsCopy.addAll(newCars);
		
		lastSpawnStep = step;
		
	}
	
	private void movingFixPoint() {
		
		findCrashesMoving(movingCarsCopy);
		findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
		while (firstCrashSite != null) {
			List<Car> newlyCrashedCars = processCrashInfo(movingCarsCopy);
			
			collapseFutures(newlyCrashedCars);
			removeSinked(movingCarsCopy);
			
			movingCarsCopy.removeAll(newlyCrashedCars);
			crashedCarsCopy.addAll(newlyCrashedCars);
			
			findCrashesMoving(movingCarsCopy);
			findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
		}
		
		collapseFutures(movingCarsCopy);
		removeSinked(movingCarsCopy);
		
	}
	
	private void findCrashesMoving(List<Car> cars) {
		
		for (int i = 0; i < cars.size(); i++) {
			final Car ci = cars.get(i);
			jloop: for (int j = i+1; j < cars.size(); j++) {
				Car cj = cars.get(j);
				
				double ciTraveled = 0;
				List<Position> ciFuturePath = ci.getFuturePath();
				for (int k = 0; k < ciFuturePath.size()-1; k++) {
					Position cia = ciFuturePath.get(k);
					Position cib = ciFuturePath.get(k+1);
					ciTraveled += cia.distanceTo(cib); 
					
					int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
					
					double cjTraveled = 0;
					List<Position> cjFuturePath = cj.getFuturePath();
					for (int l = 0; l < cjFuturePath.size()-1; l++) {
						Position cja = cjFuturePath.get(l);
						Position cjb = cjFuturePath.get(l+1);
						cjTraveled += cja.distanceTo(cjb);
						
						int jDir = (Position.COMPARATOR.compare(cja, cjb) == -1) ? 1 : -1;
						
						double dist = cib.distanceTo(cjb);
						if (DMath.doubleEquals(dist, 10.0)) {
							assert DMath.doubleEquals(ciTraveled, cjTraveled);
							saveCrashInfo(new CrashInfo(new CrashSite(ciTraveled), ci, cj, cib, cjb, iDir, jDir, dist, k+1, l+1));
							
							continue jloop;
						} else if (dist < 10) {
							Position adjustedCib = cib;
							Position adjustedCjb = cjb;
							double adjustedCiTraveled = ciTraveled;
							double adjustedCjTraveled = cjTraveled;
							/*
							 * figure out who has traveled more and back up
							 */
							if (DMath.doubleEquals(adjustedCiTraveled, adjustedCjTraveled)) {
								
							} else if (adjustedCiTraveled > adjustedCjTraveled) {
								/*
								 * ci backs up
								 */
								double travelDiff = adjustedCiTraveled - adjustedCjTraveled;
								if (cia instanceof EdgePosition) {
									adjustedCib = ((EdgePosition)cia).travel(cib.prevDir, cia.distanceTo(cib)-travelDiff);
								} else {
									adjustedCib = ((IntersectionPosition)cia).travel(cib.prevDirEdge, cib.prevDir, cia.distanceTo(cib)-travelDiff);
								}
								adjustedCiTraveled -= travelDiff;
								
							} else {
								/*
								 * cj backs up
								 */
								double travelDiff = adjustedCjTraveled - adjustedCiTraveled;
								if (cja instanceof EdgePosition) {
									adjustedCjb = ((EdgePosition)cja).travel(cjb.prevDir, cja.distanceTo(cjb)-travelDiff);
								} else {
									adjustedCjb = ((IntersectionPosition)cja).travel(cjb.prevDirEdge, cjb.prevDir, cja.distanceTo(cjb)-travelDiff);
								}
								adjustedCjTraveled -= travelDiff;
							}
							
							/*
							 * ci and cj have now traveled the same amount, the crash site may still be wrong
							 */
							
							double newDist = adjustedCib.distanceTo(adjustedCjb);
							if (DMath.doubleEquals(newDist, 10.0)) {
								assert DMath.doubleEquals(adjustedCiTraveled, adjustedCjTraveled);
								saveCrashInfo(new CrashInfo(new CrashSite(adjustedCiTraveled), ci, cj, adjustedCib, adjustedCjb, iDir, jDir, newDist, k+1, l+1));
								
								continue jloop;
							} else if (newDist < 10) {
								double diff = 10 - newDist;
								double inc = diff / 2;
								assert DMath.doubleEquals(adjustedCiTraveled, adjustedCjTraveled);
								Position newAdjustedCib;
								if (cia instanceof EdgePosition) {
									newAdjustedCib = ((EdgePosition)cia).travel(adjustedCib.prevDir, cia.distanceTo(adjustedCib)-inc);
								} else {
									newAdjustedCib = ((IntersectionPosition)cia).travel(adjustedCib.prevDirEdge, adjustedCib.prevDir, cia.distanceTo(adjustedCib)-inc);
								}
								double newAdjustedCiTraveled = adjustedCiTraveled-inc;
								Position newAdjustedCjb;
								if (cja instanceof EdgePosition) {
									newAdjustedCjb = ((EdgePosition)cja).travel(adjustedCjb.prevDir, cja.distanceTo(adjustedCjb)-inc);
								} else {
									newAdjustedCjb = ((IntersectionPosition)cja).travel(adjustedCjb.prevDirEdge, adjustedCjb.prevDir, cja.distanceTo(adjustedCjb)-inc);
								}
								double newNewDist = newAdjustedCib.distanceTo(newAdjustedCjb);
								assert DMath.doubleEquals(newNewDist, 10);
								saveCrashInfo(new CrashInfo(new CrashSite(newAdjustedCiTraveled), ci, cj, newAdjustedCib, newAdjustedCjb, iDir, jDir, newDist, k+1, l+1));
								
								continue jloop;
							}
							
						}
					} // l loop
					
				} // k loop
				
			} // j loop
		} // i loop
		
	}
	
	private void findCrashesMovingCrashed(List<Car> moving, List<Car> crashed) {
		
		for (int i = 0; i < moving.size(); i++) {
			final Car ci = moving.get(i);
			jloop: for (int j = 0; j < crashed.size(); j++) {
				Car cj = crashed.get(j);
				Position cjp = cj.getPosition();
				
				double ciTraveled = 0;
				List<Position> ciFuturePath = ci.getFuturePath();
				for (int k = 0; k < ciFuturePath.size()-1; k++) {
					Position cia = ciFuturePath.get(k);
					Position cib = ciFuturePath.get(k+1);
					ciTraveled += cia.distanceTo(cib); 
					
					int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
					
					double dist = cib.distanceTo(cjp);
					if (DMath.doubleEquals(dist, 10.0)) {
						saveCrashInfo(new CrashInfo(new CrashSite(ciTraveled), ci, cj, cib, cjp, iDir, 0, dist, k+1, -1));
						continue jloop;
					} else if (dist < 10) {
						double diff = 10 - dist;
						double inc = diff;
						Position adjustedCib;
						if (cia instanceof EdgePosition) {
							adjustedCib = ((EdgePosition)cia).travel(cib.prevDir, cia.distanceTo(cib)-inc);
						} else {
							adjustedCib = ((IntersectionPosition)cia).travel(cib.prevDirEdge, cib.prevDir, cia.distanceTo(cib)-inc);
						}
						double adjustedCiTraveled = ciTraveled;
						adjustedCiTraveled -= inc;
						double newDist = adjustedCib.distanceTo(cjp);
						assert DMath.doubleEquals(newDist, 10);
						saveCrashInfo(new CrashInfo(new CrashSite(adjustedCiTraveled), ci, cj, adjustedCib, cjp, iDir, 0, dist, k+1, -1));
						continue jloop;
					}
					
				} // k loop
				
			} // j loop
		} // i loop
		
	}
	
	
	
	CrashSite firstCrashSite;
	List<CrashInfo> crashes = new ArrayList<CrashInfo>();
	
	private void saveCrashInfo(CrashInfo ci) {
		
		CrashSite site = ci.crashSite;
		
		if (firstCrashSite == null) {
			firstCrashSite = site;
			assert crashes.isEmpty();
			crashes.add(ci);
		} else if (firstCrashSite.equals(site)) {
			crashes.add(ci);
		} else if (site.time < firstCrashSite.time) {
			firstCrashSite = site;
			crashes.clear();
			crashes.add(ci);
		} else {
			;
		}
		
	}
	
	private List<Car> processCrashInfo(List<Car> cars) {
		
		List<Car> newlyCrashedCars = new ArrayList<Car>();
		
		for (CrashInfo info : crashes) {
			Car i = info.i;
			Car j = info.j;
			Position ip = info.ip;
			Position jp = info.jp;
			int iDir = info.iDir;
			int jDir = info.jDir;
			int ik = info.ik;
			int jl = info.jl;
			
			double dist = ip.distanceTo(jp);
			assert DMath.doubleEquals(dist, 10.0);
			
			if (iDir != 0) {
				i.futurePathCrash(ip, ik);
				i.futureState = CarState.CRASHED;
				newlyCrashedCars.add(i);
				
				for (Intersection v : MODEL.getIntersections()) {
					if (ip.distanceTo(new IntersectionPosition(v, null, null, 0)) <= 10) {
						v.hasCrash = true;
					}
				}
			}
			
			if (jDir != 0) {
				j.futurePathCrash(jp, jl);
				j.futureState = CarState.CRASHED;
				newlyCrashedCars.add(j);
				
				for (Intersection v : MODEL.getIntersections()) {
					if (jp.distanceTo(new IntersectionPosition(v, null, null, 0)) <= 10) {
						v.hasCrash = true;
					}
				}
			}
			
		}
		
		firstCrashSite = null;
		crashes.clear();
		
		return newlyCrashedCars;
	}
	
//	private boolean checkDistances(List<Car> cars) {
//		for (int i = 0; i < cars.size(); i++) {
//			Car c = cars.get(i);
//			Position cPos = c.getLastFuturePosition();
//			for (int j = i + 1; j < cars.size(); j++) {
//				Car d = cars.get(j);
//				Position dPos = d.getLastFuturePosition();
//				double dist = cPos.distanceTo(dPos);
//				assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
//			}
//		}
//		return true;
//	}
	
	private void collapseFutures(List<Car> cars) {
		
		for (Car c : cars) {
			Position p = c.getLastFuturePosition();
			c.setPosition(p);
			c.futurePathClear();
			c.futurePathAdd(p);
			
			CarState s = c.futureState;
			c.setState(s);
		}
		
	}
	
	private void removeSinked(List<Car> cars) {
		List<Car> toRemove = new ArrayList<Car>();
		for (Car c : cars) {
			if (c.getState() == CarState.SINKED) {
				toRemove.add(c);
			}
		}
		for (Car c : toRemove) {
			cars.remove(c);
		}
	}
	
}
