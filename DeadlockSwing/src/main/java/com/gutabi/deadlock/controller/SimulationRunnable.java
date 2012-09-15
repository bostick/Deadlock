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
import com.gutabi.deadlock.core.STPosition;
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
				if (carPos.distanceTo(new VertexPosition(s)) <= 10) {
					toRemove.add(s);
				}
			}
		}
		for (Car c : crashedCarsCopy) {
			Position carPos = c.getPosition();
			for (Source s : sources) {
				if (carPos.distanceTo(new VertexPosition(s)) <= 10) {
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
			
			c.nextPath = new Path(new ArrayList<STPosition>(){{add(new STPosition(new VertexPosition(s), 0.0));}});
			c.nextState = CarState.VERTEX;
			
			sources.remove(s);
		}
		
		updateCurrentFromNext(newCars);
		
		List<Car> cantMoveCars = new ArrayList<Car>();
		List<Car> canMoveCars = new ArrayList<Car>();
		for (Car c : newCars) {
			boolean moving = c.updateNext();
			if (!moving) {
				cantMoveCars.add(c);
			} else {
				canMoveCars.add(c);
			}
		}
		
		updateCurrentFromNext(cantMoveCars);
		
		resetToNew(canMoveCars);
		
		movingCarsCopy.addAll(canMoveCars);
		
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
			Path ciFuturePath = ci.getNextPath();
			
			jloop: for (int j = i+1; j < cars.size(); j++) {
				Car cj = cars.get(j);
				Path cjFuturePath = cj.getNextPath();
				
				
				
				//double ciTraveled = 0;
				for (int k = 0; k < ciFuturePath.size()-1; k++) {
					STPosition cia = ciFuturePath.get(k);
					STPosition cib = ciFuturePath.get(k+1);
					//ciTraveled += cia.getSpace().distanceTo(cib.getSpace());
					double ciOrigDistance = cia.getSpace().distanceTo(cib.getSpace());
					double ciOrigTime = cib.getTime() - cia.getTime();
					double ciSpeed = ciOrigDistance / ciOrigTime;
					
					
					//double cjTraveled = 0;
					for (int l = 0; l < cjFuturePath.size()-1; l++) {
						STPosition cja = cjFuturePath.get(l);
						STPosition cjb = cjFuturePath.get(l+1);
						//cjTraveled += cja.getSpace().distanceTo(cjb.getSpace());
						double cjOrigDistance = cja.getSpace().distanceTo(cjb.getSpace());
						double cjOrigTime = cjb.getTime() - cja.getTime();
						double cjSpeed = cjOrigDistance / cjOrigTime;
						
						
						
						double dist = cib.getSpace().distanceTo(cjb.getSpace());
						if (DMath.doubleEquals(dist, 10.0)) {
							//assert DMath.doubleEquals(ciTraveled, cjTraveled);
							assert DMath.doubleEquals(cib.getTime(), cjb.getTime());
							
							saveCrashInfo(new CrashInfo(new CrashSite(cib.getTime()), ci, cj, cib, cjb, true, true, dist, k+1, l+1));
							
							continue jloop;
						} else if (dist < 10) {
							STPosition adjustedCib = cib;
							STPosition adjustedCjb = cjb;
							//double adjustedCiTraveled = ciTraveled;
							//double adjustedCjTraveled = cjTraveled;
							double adjustedCiTime = cib.getTime();
							double adjustedCjTime = cjb.getTime();
							double ciAdjustedDistance = ciOrigDistance;
							double cjAdjustedDistance = cjOrigDistance;
							
							
							/*
							 * figure out who has traveled more and back up
							 */
							if (DMath.doubleEquals(adjustedCiTime, adjustedCjTime)) {
								
							} else if (adjustedCiTime > adjustedCjTime) {
								/*
								 * ci backs up
								 */
								//double travelDiff = adjustedCiTraveled - adjustedCjTraveled;
								double timeDiff = adjustedCiTime - adjustedCjTime;
								if (cia.getSpace() instanceof EdgePosition) {
									EdgePosition ciaa = (EdgePosition)cia.getSpace();
									//adjustedCib = new STPosition(ciaa.travel(ciaa.getDest(), origDistance-travelDiff), cib.getTime() - origTime * travelDiff/origDistance);
									adjustedCib = new STPosition(ciaa.travel(ciaa.getDest(), ciOrigDistance-(ciSpeed * timeDiff)), cib.getTime() - timeDiff);
								} else {
									VertexPosition ciaa = (VertexPosition)cia.getSpace();
									if (cib.getSpace() instanceof EdgePosition) {
										EdgePosition cibb = (EdgePosition)cib.getSpace();
										adjustedCib = new STPosition(ciaa.travel(cibb.getEdge(), cibb.getDest(), ciOrigDistance-(ciSpeed * timeDiff)), cib.getTime() - timeDiff);
									} else {
										VertexPosition cibb = (VertexPosition)cib.getSpace();
										adjustedCib = new STPosition(ciaa.travel(Vertex.commonConnector(ciaa.getVertex(), cibb.getVertex()), cibb.getVertex(), ciOrigDistance-(ciSpeed * timeDiff)), cib.getTime() - timeDiff);
									}
								}
								//adjustedCiTraveled -= travelDiff;
								adjustedCiTime -= timeDiff;
								ciAdjustedDistance = cia.getSpace().distanceTo(adjustedCib.getSpace());
								
							} else {
								/*
								 * cj backs up
								 */
								//double travelDiff = adjustedCjTraveled - adjustedCiTraveled;
								double timeDiff = adjustedCjTime - adjustedCiTime;
								if (cja.getSpace() instanceof EdgePosition) {
									EdgePosition cjaa = (EdgePosition)cja.getSpace();
									adjustedCjb = new STPosition(cjaa.travel(cjaa.getDest(), cjOrigDistance-(cjSpeed * timeDiff)), cjb.getTime() - timeDiff);
								} else {
									VertexPosition cjaa = (VertexPosition)cja.getSpace();
									if (cjb.getSpace() instanceof EdgePosition) {
										EdgePosition cjbb = (EdgePosition)cjb.getSpace();
										adjustedCjb = new STPosition(cjaa.travel(cjbb.getEdge(), cjbb.getDest(), cjOrigDistance-(cjSpeed * timeDiff)), cjb.getTime() - timeDiff);
									} else {
										VertexPosition cjbb = (VertexPosition)cjb.getSpace();
										adjustedCjb = new STPosition(cjaa.travel(Vertex.commonConnector(cjaa.getVertex(), cjbb.getVertex()), cjbb.getVertex(), cjOrigDistance-(cjSpeed * timeDiff)), cib.getTime() - timeDiff);
									}
								}
								//adjustedCjTraveled -= travelDiff;
								adjustedCjTime -= timeDiff;
								cjAdjustedDistance = cja.getSpace().distanceTo(adjustedCjb.getSpace());
								
							}
							
							/*
							 * ci and cj have now traveled the same amount, the crash site may still be wrong
							 */
							
							assert DMath.doubleEquals(adjustedCiTime, adjustedCjTime);
							
							double newDist = adjustedCib.getSpace().distanceTo(adjustedCjb.getSpace());
							if (DMath.doubleEquals(newDist, 10.0)) {
								
								saveCrashInfo(new CrashInfo(new CrashSite(adjustedCiTime), ci, cj, adjustedCib, adjustedCjb, true, true, newDist, k+1, l+1));
								
								continue jloop;
							} else if (newDist < 10) {
								//double diff = 10 - newDist;
								//double inc = diff / 2;
								
								// solve for the time that ci and cj hit
								double crashTime = adjustedCiTime + (10 - newDist) / (-ciSpeed - cjSpeed);
								
								STPosition newAdjustedCib;
								if (cia.getSpace() instanceof EdgePosition) {
									EdgePosition ciaa = (EdgePosition)cia.getSpace();
									newAdjustedCib = new STPosition(ciaa.travel(ciaa.getDest(), ciAdjustedDistance-(ciSpeed * (adjustedCiTime - crashTime))), crashTime);
								} else {
									VertexPosition ciaa = (VertexPosition)cia.getSpace();
									if (cib.getSpace() instanceof EdgePosition) {
										EdgePosition cibb = (EdgePosition)cib.getSpace();
										newAdjustedCib = new STPosition(ciaa.travel(cibb.getEdge(), cibb.getDest(), ciAdjustedDistance-(ciSpeed * (adjustedCiTime - crashTime))), crashTime);
									} else {
										VertexPosition cibb = (VertexPosition)cib.getSpace();
										newAdjustedCib = new STPosition(ciaa.travel(Vertex.commonConnector(ciaa.getVertex(), cibb.getVertex()), cibb.getVertex(), ciAdjustedDistance-(ciSpeed * (adjustedCiTime - crashTime))), crashTime);
									}
								}
								//double newAdjustedCiTraveled = adjustedCiTraveled-inc;
								
								
								STPosition newAdjustedCjb;
								if (cja.getSpace() instanceof EdgePosition) {
									EdgePosition cjaa = (EdgePosition)cja.getSpace();
									newAdjustedCjb = new STPosition(cjaa.travel(cjaa.getDest(), cjAdjustedDistance-(cjSpeed * (adjustedCjTime - crashTime))), crashTime);
								} else {
									VertexPosition cjaa = (VertexPosition)cja.getSpace();
									if (cjb.getSpace() instanceof EdgePosition) {
										EdgePosition cjbb = (EdgePosition)cjb.getSpace();										
										newAdjustedCjb = new STPosition(cjaa.travel(cjbb.getEdge(), cjbb.getDest(), cjAdjustedDistance-(cjSpeed * (adjustedCjTime - crashTime))), crashTime);
									} else {
										VertexPosition cjbb = (VertexPosition)cjb.getSpace();										
										newAdjustedCjb = new STPosition(cjaa.travel(Vertex.commonConnector(cjaa.getVertex(), cjbb.getVertex()), cjbb.getVertex(), cjAdjustedDistance-(cjSpeed * (adjustedCjTime - crashTime))), crashTime);
									}									
								}
								
								
								double newNewDist = newAdjustedCib.getSpace().distanceTo(newAdjustedCjb.getSpace());
								assert DMath.doubleEquals(newNewDist, 10);
								
								saveCrashInfo(new CrashInfo(new CrashSite(crashTime), ci, cj, newAdjustedCib, newAdjustedCjb, true, true, newDist, k+1, l+1));
								
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
			//double ciTraveled = 0;
			Path ciFuturePath = ci.getNextPath();
			
			
			jloop: for (int j = 0; j < crashed.size(); j++) {
				Car cj = crashed.get(j);
				//Path cjFuturePath = cj.getNextPath();
				Position cjp = cj.getPosition();
				
				//
				
				for (int k = 0; k < ciFuturePath.size()-1; k++) {
					STPosition cia = ciFuturePath.get(k);
					STPosition cib = ciFuturePath.get(k+1);
					double ciOrigDistance = cia.getSpace().distanceTo(cib.getSpace());
					double ciOrigTime = cib.getTime() - cia.getTime();
					double ciSpeed = ciOrigDistance / ciOrigTime;
					//ciTraveled += cia.getSpace().distanceTo(cib.getSpace()); 
					
					//int iDir = (Position.COMPARATOR.compare(cia.getSpace(), cib.getSpace()) == -1) ? 1 : -1;
					
					double dist = cib.getSpace().distanceTo(cjp);
					if (DMath.doubleEquals(dist, 10.0)) {
						saveCrashInfo(new CrashInfo(new CrashSite(cib.getTime()), ci, cj, cib, new STPosition(cjp, cib.getTime()), true, false, dist, k+1, -1));
						continue jloop;
					} else if (dist < 10) {
						//double diff = 10 - dist;
						//double timeDiff = ;
						
						double crashTime = cib.getTime() + (10 - dist) / (-ciSpeed - 0);
						
						STPosition adjustedCib;
						if (cia.getSpace() instanceof EdgePosition) {
							EdgePosition ciaa = (EdgePosition)cia.getSpace();
							adjustedCib = new STPosition(ciaa.travel(ciaa.getDest(), ciOrigDistance-(ciSpeed * (ciOrigTime - crashTime))), crashTime);
						} else {
							VertexPosition ciaa = (VertexPosition)cia.getSpace();
							EdgePosition cibb = (EdgePosition)cib.getSpace();
							adjustedCib = new STPosition(ciaa.travel(cibb.getEdge(), cibb.getDest(), ciOrigDistance-(ciSpeed * (ciOrigTime - crashTime))), crashTime);
						}
						//double adjustedCiTraveled = ciTraveled;
						//adjustedCiTraveled -= travelDiff;
						double newDist = adjustedCib.getSpace().distanceTo(cjp);
						assert DMath.doubleEquals(newDist, 10);
						saveCrashInfo(new CrashInfo(new CrashSite(crashTime), ci, cj, adjustedCib, new STPosition(cjp, adjustedCib.getTime()), true, false, dist, k+1, -1));
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
			STPosition ip = info.ip;
			STPosition jp = info.jp;
			boolean iCrashed = info.iCrashed;
			boolean jCrashed = info.jCrashed;
			int ik = info.ik;
			int jl = info.jl;
			
			double dist = ip.getSpace().distanceTo(jp.getSpace());
			assert DMath.doubleEquals(dist, 10.0);
			
			if (iCrashed) {
				i.nextPathCrash(ip, ik);
				newlyCrashedCars.add(i);
			}
			
			if (jCrashed) {
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
	
	private void resetToNew(List<Car> cars) {
		for (final Car c : cars) {
			c.nextPath = new Path(new ArrayList<STPosition>(){{add(new STPosition(new VertexPosition(c.source), 0.0));}});
			c.nextState = CarState.VERTEX;
		}
	}
	
}
