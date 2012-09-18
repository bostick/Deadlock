package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.STPosition;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarState;
import com.gutabi.deadlock.model.CrashInfo;

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
		
		firstCrashTime = -1;
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
		
		/*
		 * if only moving cars are blocking a source, then a car will be spawned on the next spawn step that there are no blocking cars 
		 */
		
		for (Car c : movingCarsCopy) {
			Position carPos = c.getPosition();
			for (Source s : sources) {
				double dist = carPos.distanceTo(s);
				if (DMath.doubleEquals(dist, 10.0) || dist < 10) {
					toRemove.add(s);
				}
			}
		}
		for (Car c : crashedCarsCopy) {
			Position carPos = c.getPosition();
			for (Source s : sources) {
				double dist = carPos.distanceTo(s);
				if (DMath.doubleEquals(dist, 10.0) || dist < 10) {
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
			
			c.nextPath = new Path(new ArrayList<STPosition>(){{add(new STPosition(s, 0.0));}});
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
		while (firstCrashTime != -1) {
			List<Car> newlyCrashedCars = processCrashInfo(movingCarsCopy);
			
			updateCurrentFromNext(newlyCrashedCars);
			
//			assert checkDistances(new ArrayList<Car>(){{addAll(movingCarsCopy);addAll(crashedCarsCopy);}});
			
			findCrashesMoving(movingCarsCopy);
			findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
		}
		
		updateCurrentFromNext(new ArrayList<Car>(){{addAll(movingCarsCopy);}});
		
//		assert checkDistances(new ArrayList<Car>(){{addAll(movingCarsCopy);addAll(crashedCarsCopy);}});
	}
	
//	private boolean checkFutureDistances(Car c, Car d) {
//		Position cPos = c.getLastNextPosition().getSpace();
//		if (c == d) {
//			return true;
//		}
//		Position dPos = d.getLastNextPosition().getSpace();
//		double dist = cPos.distanceTo(dPos);
//		assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
//		return true;
//	}
//	
//	private boolean checkDistances(Car c, Car d) {
//		Position cPos = c.getPosition();
//		if (c == d) {
//			return true;
//		}
//		Position dPos = d.getPosition();
//		double dist = cPos.distanceTo(dPos);
//		assert DMath.doubleEquals(dist, 10.0) || dist > 10.0;
//		return true;
//	}
//	
//	private boolean checkDistances(Car c, List<Car> cars) {
//		for (int i = 0; i < cars.size(); i++) {
//			Car d = cars.get(i);
//			checkDistances(c, d);
//		}
//		return true;
//	}
//	
//	private boolean checkDistances(List<Car> cars) {
//		for (int i = 0; i < cars.size(); i++) {
//			Car c = cars.get(i);
//			checkDistances(c, cars);
//		}
//		return true;
//	}
	
	private void findCrashesMoving(List<Car> cars) {
		
		for (int i = 0; i < cars.size(); i++) {
			final Car ci = cars.get(i);
			
			jloop:
			for (int j = i+1; j < cars.size(); j++) {
				Car cj = cars.get(j);
				
				//assert checkFutureDistances(ci, cj);
				
				boolean res = carCar(ci, cj);
				
				//assert checkFutureDistances(ci, cj);
				
				if (res) {
					continue jloop;
				}
				
			}
		}
		
	}
	
	private void findCrashesMovingCrashed(List<Car> moving, List<Car> crashed) {
		
		for (int i = 0; i < moving.size(); i++) {
			final Car ci = moving.get(i);
			
			jloop:
			for (int j = 0; j < crashed.size(); j++) {
				Car cj = crashed.get(j);
				
				//assert checkFutureDistances(ci, cj);
				
				boolean res = carCar(ci, cj);
				
				//assert checkFutureDistances(ci, cj);
				
				if (res) {
					continue jloop;
				}
				
			}
		}
		
	}
	
	private boolean carCar(Car ci, Car cj) {
		
		Path ciFuturePath = ci.getNextPath();
		Path cjFuturePath = cj.getNextPath();
		
		double intersectionTime = Path.intersection(ciFuturePath, cjFuturePath, 10);
		if (intersectionTime != -1) {
			saveCrashInfo(new CrashInfo(intersectionTime, ci, cj));
			return true;
		}
		
//		for (int k = 0; k < ciFuturePath.size()-1; k++) {
//			STPosition cia = ciFuturePath.get(k);
//			STPosition cib = ciFuturePath.get(k+1);
//			double ciOrigDistance = cia.getSpace().distanceTo(cib.getSpace());
//			double ciOrigTime = cib.getTime() - cia.getTime();
//			double ciSpeed = ciOrigDistance / ciOrigTime;
//			
//			for (int l = 0; l < cjFuturePath.size()-1; l++) {
//				STPosition cja = cjFuturePath.get(l);
//				STPosition cjb = cjFuturePath.get(l+1);
//				double cjOrigDistance = cja.getSpace().distanceTo(cjb.getSpace());
//				double cjOrigTime = cjb.getTime() - cja.getTime();
//				double cjSpeed = cjOrigDistance / cjOrigTime;
//				
//				double dist = cib.getSpace().distanceTo(cjb.getSpace());
//				if (DMath.doubleEquals(dist, 10.0)) {
//					assert DMath.doubleEquals(cib.getTime(), cjb.getTime());
//					saveCrashInfo(new CrashInfo(new CrashSite(cib.getTime()), ci, cj, cib, cjb, dist, k+1, l+1));
//					return true;
//				} else if (dist < 10) {
//					STPosition adjustedCib = cib;
//					STPosition adjustedCjb = cjb;
//					double adjustedCiTime = cib.getTime();
//					double adjustedCjTime = cjb.getTime();
//					double ciAdjustedDistance = ciOrigDistance;
//					double cjAdjustedDistance = cjOrigDistance;
//					
//					/*
//					 * figure out who has traveled more and back up
//					 */
//					if (DMath.doubleEquals(adjustedCiTime, adjustedCjTime)) {
//						;
//					} else if (adjustedCiTime > adjustedCjTime) {
//						/*
//						 * ci backs up
//						 */
//						adjustedCiTime = adjustedCjTime;
//						ciAdjustedDistance = ciSpeed * (adjustedCiTime - cia.getTime());
//						
//						if (cia.getSpace() instanceof EdgePosition) {
//							EdgePosition ciaa = (EdgePosition)cia.getSpace();
//							adjustedCib = new STPosition(ciaa.travel(ciaa.getDest(), ciAdjustedDistance), adjustedCiTime);
//						} else {
//							Vertex ciaa = (Vertex)cia.getSpace();
//							if (cib.getSpace() instanceof EdgePosition) {
//								EdgePosition cibb = (EdgePosition)cib.getSpace();
//								adjustedCib = new STPosition(ciaa.travel(cibb.getEdge(), cibb.getDest(), ciAdjustedDistance), adjustedCiTime);
//							} else {
//								Vertex cibb = (Vertex)cib.getSpace();
//								adjustedCib = new STPosition(ciaa.travel(Vertex.commonConnector(ciaa, cibb), cibb, ciAdjustedDistance), adjustedCiTime);
//							}
//						}
//						
//					} else {
//						/*
//						 * cj backs up
//						 */
//						adjustedCjTime = adjustedCiTime;
//						cjAdjustedDistance = cjSpeed * (adjustedCjTime - cja.getTime());
//						
//						if (cja.getSpace() instanceof EdgePosition) {
//							EdgePosition cjaa = (EdgePosition)cja.getSpace();
//							adjustedCjb = new STPosition(cjaa.travel(cjaa.getDest(), cjAdjustedDistance), adjustedCjTime);
//						} else {
//							Vertex cjaa = (Vertex)cja.getSpace();
//							if (cjb.getSpace() instanceof EdgePosition) {
//								EdgePosition cjbb = (EdgePosition)cjb.getSpace();
//								adjustedCjb = new STPosition(cjaa.travel(cjbb.getEdge(), cjbb.getDest(), cjAdjustedDistance), adjustedCjTime);
//							} else {
//								Vertex cjbb = (Vertex)cjb.getSpace();
//								adjustedCjb = new STPosition(cjaa.travel(Vertex.commonConnector(cjaa, cjbb), cjbb, cjAdjustedDistance), adjustedCjTime);
//							}
//						}
//						
//					}
//					
//					/*
//					 * ci and cj have now traveled the same amount, the crash site may still be wrong
//					 */
//					
//					double newDist = adjustedCib.getSpace().distanceTo(adjustedCjb.getSpace());
//					if (DMath.doubleEquals(newDist, 10.0)) {
//						
//						saveCrashInfo(new CrashInfo(new CrashSite(adjustedCiTime), ci, cj, adjustedCib, adjustedCjb, newDist, k+1, l+1));
//						
//						return true;
//					} else if (newDist < 10) {
//						
//						// solve for the time that ci and cj hit
//						double crashTime = adjustedCiTime + (10 - newDist) / (-ciSpeed - cjSpeed);
//						
//						double newAdjustedCiDistance = ciSpeed * (crashTime - cia.getTime());
//						double newAdjustedCjDistance = cjSpeed * (crashTime - cja.getTime());
//						
//						STPosition newAdjustedCib;
//						if (cia.getSpace() instanceof EdgePosition) {
//							EdgePosition ciaa = (EdgePosition)cia.getSpace();
//							newAdjustedCib = new STPosition(ciaa.travel(ciaa.getDest(), newAdjustedCiDistance), crashTime);
//						} else {
//							Vertex ciaa = (Vertex)cia.getSpace();
//							if (cib.getSpace() instanceof EdgePosition) {
//								EdgePosition cibb = (EdgePosition)cib.getSpace();
//								newAdjustedCib = new STPosition(ciaa.travel(cibb.getEdge(), cibb.getDest(), newAdjustedCiDistance), crashTime);
//							} else {
//								Vertex cibb = (Vertex)cib.getSpace();
//								newAdjustedCib = new STPosition(ciaa.travel(Vertex.commonConnector(ciaa, cibb), cibb, newAdjustedCiDistance), crashTime);
//							}
//						}
//						
//						STPosition newAdjustedCjb;
//						if (cja.getSpace() instanceof EdgePosition) {
//							EdgePosition cjaa = (EdgePosition)cja.getSpace();
//							newAdjustedCjb = new STPosition(cjaa.travel(cjaa.getDest(), newAdjustedCjDistance), crashTime);
//						} else {
//							Vertex cjaa = (Vertex)cja.getSpace();
//							if (cjb.getSpace() instanceof EdgePosition) {
//								EdgePosition cjbb = (EdgePosition)cjb.getSpace();										
//								newAdjustedCjb = new STPosition(cjaa.travel(cjbb.getEdge(), cjbb.getDest(), newAdjustedCjDistance), crashTime);
//							} else {
//								Vertex cjbb = (Vertex)cjb.getSpace();										
//								newAdjustedCjb = new STPosition(cjaa.travel(Vertex.commonConnector(cjaa, cjbb), cjbb, newAdjustedCjDistance), crashTime);
//							}									
//						}
//						
//						
//						double newNewDist = newAdjustedCib.getSpace().distanceTo(newAdjustedCjb.getSpace());
//						assert DMath.doubleEquals(newNewDist, 10);
//						
//						saveCrashInfo(new CrashInfo(new CrashSite(crashTime), ci, cj, newAdjustedCib, newAdjustedCjb, newDist, k+1, l+1));
//						
//						return true;
//					}
//					
//				}
//			} // l loop
//			
//		} // k loop
		
		return false;
	}
	
	
	double firstCrashTime = -1;
	List<CrashInfo> crashes = new ArrayList<CrashInfo>();
	
	private void saveCrashInfo(CrashInfo ci) {
		
		double t = ci.crashTime;
		
		if (firstCrashTime == -1) {
			firstCrashTime = t;
			assert crashes.isEmpty();
			crashes.add(ci);
		} else if (firstCrashTime == t) {
			crashes.add(ci);
		} else if (t < firstCrashTime) {
			firstCrashTime = t;
			crashes.clear();
			crashes.add(ci);
		} else {
			;
		}
		
//		Car i = ci.i;
//		Car j = ci.j;
//		Position ip = i.nextPath.getPosition(t);
//		Position jp = j.nextPath.getPosition(t);
//		
//		double distance = ip.distanceTo(jp);
//		assert DMath.doubleEquals(distance, 10);
	}
	
	private List<Car> processCrashInfo(List<Car> cars) {
		
		List<Car> newlyCrashedCars = new ArrayList<Car>();
		
		for (CrashInfo info : crashes) {
			double crashTime = info.crashTime;
			Car i = info.i;
			Car j = info.j;
			
			Position cip = i.nextPath.getPosition(crashTime);
			Position cjp = j.nextPath.getPosition(crashTime);
			
			double dist = cip.distanceTo(cjp);
			assert DMath.doubleEquals(dist, 10.0);
			
			if (i.getState() != CarState.CRASHED) {
				i.nextPathCrash(crashTime);
				i.crashingStep = step;
				newlyCrashedCars.add(i);
			}
			
			if (j.getState() != CarState.CRASHED) {
				j.nextPathCrash(crashTime);
				j.crashingStep = step;
				newlyCrashedCars.add(j);
			}
			
//			assert checkFutureDistances(i, j);
			
		}
		
		firstCrashTime = -1;
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
			c.nextPath = new Path(new ArrayList<STPosition>(){{add(new STPosition(c.source, 0.0));}});
			c.nextState = CarState.VERTEX;
		}
	}
	
}
