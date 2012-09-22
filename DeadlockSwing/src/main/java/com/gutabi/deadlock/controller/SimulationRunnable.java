package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Path;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
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
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	@Override
	public void run() {
		
		firstUnprocessedCrashTime = -1;
		unprocessedCrashes.clear();
		
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
				if (DMath.doubleEquals(dist, MODEL.CAR_WIDTH) || dist < MODEL.CAR_WIDTH) {
					toRemove.add(s);
				}
			}
		}
		for (Car c : crashedCarsCopy) {
			Position carPos = c.getPosition();
			for (Source s : sources) {
				double dist = carPos.distanceTo(s);
				if (DMath.doubleEquals(dist, MODEL.CAR_WIDTH) || dist < MODEL.CAR_WIDTH) {
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
			//int i = MODEL.RANDOM.nextInt(sources.size());
			int i = 0;
			
			final Source s = sources.get(i);
			
			c.source = s;
			c.startingStep = step;
			
			c.nextState = CarState.VERTEX;
			
			sources.remove(s);
			
			c.setPosition(s);
			c.setState(CarState.VERTEX);
		}
		
		movingCarsCopy.addAll(newCars);
		
		lastSpawnStep = step;
		
	}
	
	
	double lastSyncTime;
	
	private void movingFixPoint() {
		
		lastSyncTime = 0.0;
		
		for (Car c : movingCarsCopy) {
			c.updateNext();
		}
		for (Car c : crashedCarsCopy) {
			c.updateNext();
		}
		
		findCrashesMoving(movingCarsCopy);
		findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
		int iter = 0;
		while (firstUnprocessedCrashTime != -1) {
			
			List<Car> newlyCrashedCars = processCrashInfo(new ArrayList<Car>(){{addAll(movingCarsCopy);addAll(crashedCarsCopy);}});
			
			assert checkTimes(new ArrayList<Car>(){{addAll(movingCarsCopy);addAll(crashedCarsCopy);}});
			
			movingCarsCopy.removeAll(newlyCrashedCars);
			crashedCarsCopy.addAll(newlyCrashedCars);
			
			findCrashesMoving(movingCarsCopy);
			findCrashesMovingCrashed(movingCarsCopy, crashedCarsCopy);
			
			iter = iter + 1;
		}
		
		List<Car> newlySinkedCars = updateCurrentFromNext(new ArrayList<Car>(){{addAll(movingCarsCopy);addAll(crashedCarsCopy);}});
		
		movingCarsCopy.removeAll(newlySinkedCars);
		
		checkDistances(new ArrayList<Car>(){{addAll(movingCarsCopy);addAll(crashedCarsCopy);}});
		
	}
	
	private boolean checkTimes(List<Car> cars) {
		for (Car c : cars) {
			Path p = c.nextPath;
			assert DMath.doubleEquals(p.getStartTime(), lastSyncTime);
			assert DMath.doubleEquals(p.getEndTime(), 1.0);
		}
		return true;
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
	
	private boolean checkDistances(Car c, Car d) {
		Position cPos = c.getPosition();
		if (c == d) {
			return true;
		}
		Position dPos = d.getPosition();
		double dist = Point.distance(cPos.getPoint(), dPos.getPoint());
		assert DMath.doubleEquals(dist, MODEL.CAR_WIDTH) || dist > MODEL.CAR_WIDTH;
		return true;
	}
	
	private boolean checkDistances(Car c, List<Car> cars) {
		for (int i = 0; i < cars.size(); i++) {
			Car d = cars.get(i);
			checkDistances(c, d);
		}
		return true;
	}
	
	private boolean checkDistances(List<Car> cars) {
		for (int i = 0; i < cars.size(); i++) {
			Car c = cars.get(i);
			checkDistances(c, cars);
		}
		return true;
	}
	
	private void findCrashesMoving(List<Car> cars) {
		
		for (int i = 0; i < cars.size(); i++) {
			final Car ci = cars.get(i);
			
			jloop:
			for (int j = i+1; j < cars.size(); j++) {
				Car cj = cars.get(j);
				
				boolean res = carCar(ci, cj);
				
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
				
				boolean res = carCar(ci, cj);
				
				if (res) {
					continue jloop;
				}
				
			}
		}
		
	}
	
	private boolean carCar(Car ci, Car cj) {
		
		Path ciFuturePath = ci.getNextPath();
		Path cjFuturePath = cj.getNextPath();
		
		double intersectionTime = Path.intersection(ciFuturePath, cjFuturePath, MODEL.CAR_WIDTH);
		if (intersectionTime != -1) {
			saveCrashInfo(new CrashInfo(intersectionTime, ci, cj));
			return true;
		} else {
			return false;
		}
	}
	
	
	double firstUnprocessedCrashTime = -1;
	List<CrashInfo> unprocessedCrashes = new ArrayList<CrashInfo>();
	
	private void saveCrashInfo(CrashInfo ci) {
		
		double t = ci.crashTime;
		
		if (firstUnprocessedCrashTime == -1) {
			firstUnprocessedCrashTime = t;
			assert unprocessedCrashes.isEmpty();
			unprocessedCrashes.add(ci);
		} else if (firstUnprocessedCrashTime == t) {
			unprocessedCrashes.add(ci);
		} else if (t < firstUnprocessedCrashTime) {
			firstUnprocessedCrashTime = t;
			unprocessedCrashes.clear();
			unprocessedCrashes.add(ci);
		} else {
			;
		}
		
	}
	
	private List<Car> processCrashInfo(List<Car> allCars) {
		
		List<Car> newlyCrashedCars = new ArrayList<Car>();
		
		for (CrashInfo info : unprocessedCrashes) {
			double crashTime = info.crashTime;
			assert DMath.doubleEquals(crashTime, firstUnprocessedCrashTime);
			
			Car i = info.i;
			Car j = info.j;
			
			if (i.nextState != CarState.CRASHED) {
				i.nextPathCrash(firstUnprocessedCrashTime);
				i.crashingStep = step;
				i.nextState = CarState.CRASHED;
				newlyCrashedCars.add(i);
			}
			
			if (j.nextState != CarState.CRASHED) {
				j.nextPathCrash(firstUnprocessedCrashTime);
				j.crashingStep = step;
				j.nextState = CarState.CRASHED;
				newlyCrashedCars.add(j);
			}
			
		}
		
		/*
		 * synchronize other cars to crashTime
		 */
		for (Car c : allCars) {
			if (newlyCrashedCars.contains(c)) {
				// already synchronized
				continue;
			}
			
			c.nextPathSynchronize(firstUnprocessedCrashTime);
		}
		
		lastSyncTime = firstUnprocessedCrashTime;
		
		firstUnprocessedCrashTime = -1;
		unprocessedCrashes.clear();
		
		return newlyCrashedCars;
	}
	
	private List<Car> updateCurrentFromNext(List<Car> cars) {
		
		List<Car> newlySinkedCars = new ArrayList<Car>();
		
		for (Car c : cars) {
			boolean moving = c.updateCurrentFromNext();
			if (!moving) {
				newlySinkedCars.add(c);
			}
		}
		
		return newlySinkedCars;
	}
	
}
