package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;
import com.gutabi.deadlock.model.CrashInfo;
import com.gutabi.deadlock.model.CrashSite;

@SuppressWarnings("serial")
public class SimulationRunnable implements Runnable {
	
	List<Car> specials = new ArrayList<Car>();
	
	long step = 0;
	long lastSpawnStep = 0;
	
	List<Edge> edgesCopy;
	List<Intersection> intersectionsCopy;
	List<Source> sourcesCopy;
	List<Sink> sinksCopy;
	ControlMode modeCopy;
	List<Car> movingCarsCopy;
	List<Car> crashedCarsCopy;
	
	List<Source> sources;
	
	@Override
	public void run() {
		
		firstCrashSite = null;
		crashes.clear();
		
		synchronized (MODEL) {
			
			MODEL.graph.calculateChoices();
			
			MODEL.movingCars.clear();
			MODEL.crashedCars.clear();
			
			edgesCopy = new ArrayList<Edge>();
			for (Edge e : MODEL.getEdges()) {
				edgesCopy.add(e);
			}
			intersectionsCopy = new ArrayList<Intersection>(MODEL.getIntersections());
			sourcesCopy = new ArrayList<Source>(MODEL.getSources());
			sinksCopy = new ArrayList<Sink>(MODEL.getSinks());
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
				c.updateNext();
			}
			
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
	
	private List<Source> activeSources() {
		
		List<Source> sources = new ArrayList<Source>();
		for (Source s : sourcesCopy) {
			if (s.getEdges().size() >= 1) {
				sources.add(s);
			}
		}
		
		List<Source> toRemove = new ArrayList<Source>();
		
		for (Car c : movingCarsCopy) {
			Position carPos = c.getPosition();
			for (Source s : sources) {
				if (carPos.distanceTo(new VertexPosition(s, null, null, 0)) <= 10) {
					toRemove.add(s);
				}
			}
		}
		for (Car c : crashedCarsCopy) {
			Position carPos = c.getPosition();
			for (Source s : sources) {
				if (carPos.distanceTo(new VertexPosition(s, null, null, 0)) <= 10) {
					toRemove.add(s);
				}
			}
		}
		for (Vertex v : toRemove) {
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
			
			final Source s = sources.get(i);
			
			c.source = s;
			c.startingStep = step;
			
			c.nextPath = new Path(new ArrayList<Position>(){{add(new VertexPosition(s, null, null, 0));}});
			c.nextState = CarState.VERTEX;
			
			sources.remove(s);
		}
		
		updateCurrentFromNext(newCars);
		
		List<Car> cantMoveCars = new ArrayList<Car>();
		for (Car c : newCars) {
			boolean moving = c.updateNext();
			if (!moving) {
				cantMoveCars.add(c);
			} else {
				movingCarsCopy.add(c);
			}
		}
		
		updateCurrentFromNext(cantMoveCars);
		
		lastSpawnStep = step;
		
	}
	
	private void movingFixPoint() {
		
		findCrashesMoving(movingCarsCopy);
		findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
		while (firstCrashSite != null) {
			List<Car> newlyCrashedCars = processCrashInfo(movingCarsCopy);
			
			updateCurrentFromNext(newlyCrashedCars);
			
			findCrashesMoving(movingCarsCopy);
			findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
		}
		
		updateCurrentFromNext(new ArrayList<Car>(){{addAll(movingCarsCopy);}});
		
		//checkDistances();
	}
	
//	private boolean checkDistances(List<Car> cars) {
//	for (int i = 0; i < cars.size(); i++) {
//		Car c = cars.get(i);
//		Position cPos = c.getLastFuturePosition();
//		for (int j = i + 1; j < cars.size(); j++) {
//			Car d = cars.get(j);
//			Position dPos = d.getLastFuturePosition();
//			double dist = cPos.distanceTo(dPos);
//			assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
//		}
//	}
//	return true;
//}
	
	private void findCrashesMoving(List<Car> cars) {
		
		for (int i = 0; i < cars.size(); i++) {
			final Car ci = cars.get(i);
			jloop: for (int j = i+1; j < cars.size(); j++) {
				Car cj = cars.get(j);
				
				double ciTraveled = 0;
				Path ciFuturePath = ci.getNextPath();
				for (int k = 0; k < ciFuturePath.size()-1; k++) {
					Position cia = ciFuturePath.get(k);
					Position cib = ciFuturePath.get(k+1);
					ciTraveled += cia.distanceTo(cib); 
					
					int iDir = (Position.COMPARATOR.compare(cia, cib) == -1) ? 1 : -1;
					
					double cjTraveled = 0;
					Path cjFuturePath = cj.getNextPath();
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
									adjustedCib = ((VertexPosition)cia).travel(cib.prevDirEdge, cib.prevDir, cia.distanceTo(cib)-travelDiff);
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
									adjustedCjb = ((VertexPosition)cja).travel(cjb.prevDirEdge, cjb.prevDir, cja.distanceTo(cjb)-travelDiff);
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
									newAdjustedCib = ((VertexPosition)cia).travel(adjustedCib.prevDirEdge, adjustedCib.prevDir, cia.distanceTo(adjustedCib)-inc);
								}
								double newAdjustedCiTraveled = adjustedCiTraveled-inc;
								Position newAdjustedCjb;
								if (cja instanceof EdgePosition) {
									newAdjustedCjb = ((EdgePosition)cja).travel(adjustedCjb.prevDir, cja.distanceTo(adjustedCjb)-inc);
								} else {
									newAdjustedCjb = ((VertexPosition)cja).travel(adjustedCjb.prevDirEdge, adjustedCjb.prevDir, cja.distanceTo(adjustedCjb)-inc);
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
				Path ciFuturePath = ci.getNextPath();
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
							adjustedCib = ((VertexPosition)cia).travel(cib.prevDirEdge, cib.prevDir, cia.distanceTo(cib)-inc);
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
				i.nextPathCrash(ip, ik);
				newlyCrashedCars.add(i);
			}
			
			if (jDir != 0) {
				j.nextPathCrash(jp, jl);
				newlyCrashedCars.add(j);
			}
			
		}
		
		firstCrashSite = null;
		crashes.clear();
		
		return newlyCrashedCars;
	}
	
	private void updateCurrentFromNext(List<Car> cars) {
		for (Car c : cars) {
			switch (c.nextState) {
			case NEW:
				assert false;
				break;
			case EDGE:
			case VERTEX:
				break;
			case CRASHED: {
				movingCarsCopy.remove(c);
				crashedCarsCopy.add(c);
				break;
			}
			case SINKED: {
				boolean res = movingCarsCopy.remove(c);
				assert res;
				break;
			}
			}
			c.updateCurrentFromNext();
		}
	}
	
}
