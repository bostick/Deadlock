package com.gutabi.deadlock.model.fixture;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.car.FastCar;
import com.gutabi.deadlock.model.car.NormalCar;
import com.gutabi.deadlock.model.car.RandomCar;
import com.gutabi.deadlock.model.car.ReallyFastCar;

@SuppressWarnings("static-access")
public class WorldSource extends Source {
	
	/*
	 * spawn cars every SPAWN_FREQUENCY seconds
	 * -1 means no spawning
	 */
	public static double SPAWN_FREQUENCY_SECONDS = 1.0;
	
	public WorldSink matchingSink;
	GraphPositionPath shortestPathToMatchingSink;
	
	public double lastSpawnTime;
	
	public int outstandingCars;
	
	static int carIDCounter;
	
	public  WorldSource(Point p, Axis a) {
		super(p, a);
		hiliteColor = new Color(0, 255, 255);
	}
	
	public boolean isDeleteable() {
		return false;
	}
	
	public void postStop() {
		carQueue.clear();
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public void preStart() {
		
		assert matchingSink != null;
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		shortestPathToMatchingSink = GraphPositionPath.createShortestPathFromSkeleton(poss);
		
		lastSpawnTime = -1;
		outstandingCars = 0;
		carIDCounter = 0;
	}
	
	public GraphPositionPath getShortestPathToMatchingSink() {
		return shortestPathToMatchingSink;
	}
	
	public GraphPositionPath getRandomPathToMatchingSink() {
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		GraphPositionPath path = GraphPositionPath.createRandomPathFromSkeleton(poss);
		return path;
	}
	
	public void preStep(double t) {
//		if (SPAWN_FREQUENCY_SECONDS > 0 && (t == 0 || (t - lastSpawnTime) >= SPAWN_FREQUENCY_SECONDS)) {
//			if (active()) {
//				spawnNewCar(t);
//			}
//		}
		if (active(t)) {
			spawnNewCar(t);
		}
	}
	
	private void spawnNewCar(double t) {
		
		Car c = createNewCar();
		
		if (c != null) {
//			assert c.hitTest(p, r);
			c.startingTime = t;
			c.id = carIDCounter;
			carIDCounter++;
			synchronized (MODEL) {
				MODEL.world.addCar(c);
			}
			lastSpawnTime = t;
			outstandingCars++;
		}
		
	}
	
	private boolean active(double t) {
		
//		if (outstandingCars > 0) {
//			return false;
//		}
		if (lastSpawnTime != -1 && (t - lastSpawnTime) < SPAWN_FREQUENCY_SECONDS) {
			return false;
		}
		
		double d = MODEL.world.distanceBetweenVertices(this, matchingSink);
		
		if (d == Double.POSITIVE_INFINITY) {
			return false;
		}
		
		if (MODEL.world.isUnderAnyCars(this)) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings({"rawtypes"})
	private Car createNewCar() {
		
		boolean normal = VIEW.controlPanel.normalCarButton.isSelected();
		boolean fast = VIEW.controlPanel.fastCarButton.isSelected();
		boolean random = VIEW.controlPanel.randomCarButton.isSelected();
		boolean really = VIEW.controlPanel.reallyFastCarButton.isSelected();
		
		List<Class> l = new ArrayList<Class>();
		if (normal) {
			l.add(NormalCar.class);
		}
		if (fast) {
			l.add(FastCar.class);
		}
		if (random) {
			l.add(RandomCar.class);
		}
		if (really) {
			l.add(ReallyFastCar.class);
		}
		
		if (l.isEmpty()) {
			return null;
		}
		
		int r = MODEL.world.RANDOM.nextInt(l.size());
		
		Class c = l.get(r);
		
		if (c == NormalCar.class) {
			return new NormalCar(this);
		} else if (c == FastCar.class) {
			return new FastCar(this);
		} else if (c == RandomCar.class) {
			return new RandomCar(this);
		} else if (c == ReallyFastCar.class) {
			return new ReallyFastCar(this);
		} else {
			throw new AssertionError();
		}
		
	}
	
}
