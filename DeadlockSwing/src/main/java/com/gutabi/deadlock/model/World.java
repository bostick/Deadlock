package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Segment;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.path.STPointPath;

public class World {
	
	public final double CAR_WIDTH = 1.0;
	public final double ROAD_WIDTH = CAR_WIDTH;
	public final double VERTEX_WIDTH = Math.sqrt(2 * ROAD_WIDTH * ROAD_WIDTH);
	
	
	public final double WORLD_WIDTH = 16.0;
	public final double WORLD_HEIGHT = WORLD_WIDTH;
	
	public final int PIXELS_PER_METER = 32;
	
	public final double MOUSE_RADIUS = VERTEX_WIDTH * PIXELS_PER_METER;
	
	/*
	 * spawn cars every SPAWN_FREQUENCY milliseconds
	 * -1 means no spawning
	 */
	public int SPAWN_FREQUENCY = 3000;
	
	/*
	 * move physics forward by dt milliseconds
	 */
	public final long dt = 70;
	
	public Random RANDOM = new Random(1);
	
	
	/*
	 * simulation state
	 */
	public long t;
	public long lastSpawnTime;
	
	
	public Graph graph = new Graph();
	
	public List<Car> movingCars = new ArrayList<Car>();
	public List<Car> crashedCars = new ArrayList<Car>();
	
	public World() {
		
		Source a = new Source(new Point(WORLD_WIDTH/3, 0));
		Source b = new Source(new Point(2*WORLD_WIDTH/3, 0));
		Source c = new Source(new Point(0, WORLD_HEIGHT/3));
		Source d = new Source(new Point(0, 2*WORLD_HEIGHT/3));
		
		Sink e = new Sink(new Point(WORLD_WIDTH/3, WORLD_HEIGHT));
		Sink f = new Sink(new Point(2*WORLD_WIDTH/3, WORLD_HEIGHT));
		Sink g = new Sink(new Point(WORLD_WIDTH, WORLD_HEIGHT/3));
		Sink h = new Sink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/3));
		
		a.matchingSink = e;
		e.matchingSource = a;
		
		b.matchingSink = f;
		f.matchingSource = b;
		
		c.matchingSink = g;
		g.matchingSource = c;
		
		d.matchingSink = h;
		h.matchingSource = d;
		
		graph.sources.add(a);
		graph.sources.add(b);
		graph.sources.add(c);
		graph.sources.add(d);
		
		graph.sinks.add(e);
		graph.sinks.add(f);
		graph.sinks.add(g);
		graph.sinks.add(h);
		
	}
	
	/*
	 * run before game loop start
	 */
	public void preStart() {
		
		graph.preStart();
		
		for (Source s : graph.getSources()) {
			s.preStart();
		}
		
		t = 0;
		lastSpawnTime = -1;
		
	}
	
	/*
	 * run after game loop stops
	 */
	public void postStop() {
		movingCars.clear();
		crashedCars.clear();
	}
	
	public void integrate(long t) {
		
		this.t = t;
		
		if (SPAWN_FREQUENCY > 0 && (t - lastSpawnTime) >= SPAWN_FREQUENCY) {
			spawnNewCars();
		}
		
		movingFixPoint();
		
	}
	
	private void spawnNewCars() {
		
		List<Source> sources = activeSources();
		
		List<Car> newCars = new ArrayList<Car>();
		
		int n = sources.size();
		
		for (int i = 0; i < n; i++) {
			
			Car c = MODEL.world.createNewCar(sources.get(i));
			
			if (c != null) {
				
				c.startingTime = t;
				
				newCars.add(c);
				
			}
			
		}
		
		movingCars.addAll(newCars);
		
		lastSpawnTime = t;
		
	}
	
	private List<Source> activeSources() {
		
		List<Source> sources = new ArrayList<Source>();
		for (Source s : graph.sources) {
			if (s.getPathToMatchingSink() != null) {
				sources.add(s);
			}
		}
		
		List<Source> toRemove = new ArrayList<Source>();
		
		/*
		 * if only moving cars are blocking a source, then a car will be spawned on the next spawn step that there are no blocking cars 
		 */
		
		for (Car c : movingCars) {
//			GraphPosition carPos = graph.hitTest(c.realPos);
			for (Source s : sources) {
				double dist = c.realPos.distanceTo(s.getPoint());
				if (DMath.lessThanEquals(dist, CAR_WIDTH)) {
					toRemove.add(s);
				}
			}
		}
		for (Car c : crashedCars) {
//			GraphPosition carPos = graph.hitTest(c.realPos);
			for (Source s : sources) {
				double dist = c.realPos.distanceTo(s.getPoint());
				if (DMath.lessThanEquals(dist, CAR_WIDTH)) {
					toRemove.add(s);
				}
			}
		}
		for (Vertex v : toRemove) {
			sources.remove(v);
		}
		
		return sources;
		
	}
	
	private Car createNewCar(Source s) {
		
		boolean normal = VIEW.controlPanel.normalCarButton.isSelected();
		boolean fast = VIEW.controlPanel.fastCarButton.isSelected();
		
		if (normal && fast) {
			
			int r = RANDOM.nextInt(2);
			if (r == 0) {
				return new NormalCar(s);
			} else {
				return new FastCar(s);
			}
			
		} else if (normal) {
			return new NormalCar(s);
		} else if (fast) {
			return new FastCar(s);
		} else {
			return null;
		}
		
	}
	
	private void movingFixPoint() {
		
		for (Car c : movingCars) {
			c.updateNext();
		}
		for (Car c : crashedCars) {
			c.updateNext();
		}
		
		findCrashesMoving(movingCars);
		findCrashesMovingCrashed(movingCars, crashedCars);
		int iter = 0;
		while (firstUnprocessedCrashTime != -1) {
			
			List<Car> newlyCrashedCars = processCrashInfo();
			
			movingCars.removeAll(newlyCrashedCars);
			crashedCars.addAll(newlyCrashedCars);
			
			findCrashesMoving(movingCars);
			findCrashesMovingCrashed(movingCars, crashedCars);
			
			iter = iter + 1;
		}
		
		List<Car> toBeRemoved = updateCurrentFromNext();
		
		movingCars.removeAll(toBeRemoved);
		
//		checkDistances(new ArrayList<Car>(){{addAll(movingCars);addAll(crashedCars);}});
		
	}
	
//	private boolean checkDistances(Car c, Car d) {
//		Position cPos = c.getPosition();
//		if (c == d) {
//			return true;
//		}
//		Position dPos = d.getPosition();
//		double dist = Point.distance(cPos.getPoint(), dPos.getPoint());
//		assert DMath.greaterThanEquals(dist, MODEL.world.CAR_WIDTH);
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
		
		double intersectionTime = STPointPath.intersection(
				ci.nextRealPath,
				cj.nextRealPath,
				MODEL.world.CAR_WIDTH,
				Math.min(ci.nextRealPath.end.getTime(), cj.nextRealPath.end.getTime()));
		
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
	
	private List<Car> processCrashInfo() {
		
		List<Car> newlyCrashedCars = new ArrayList<Car>();
		
		for (CrashInfo info : unprocessedCrashes) {
			double crashTime = info.crashTime;
			assert DMath.equals(crashTime, firstUnprocessedCrashTime);
			
			Car i = info.i;
			Car j = info.j;
			
			if (!i.nextCrashed) {
				i.nextRealPath = i.nextRealPath.crash(firstUnprocessedCrashTime);
				i.crashingTime = t;
				i.nextCrashed = true;
				newlyCrashedCars.add(i);
			}
			
			if (!j.nextCrashed) {
				j.nextRealPath = j.nextRealPath.crash(firstUnprocessedCrashTime);
				j.crashingTime = t;
				j.nextCrashed = true;
				newlyCrashedCars.add(j);
			}
			
		}
		
		firstUnprocessedCrashTime = -1;
		unprocessedCrashes.clear();
		
		return newlyCrashedCars;
	}
	
	/**
	 * return cars to be removed
	 */
	private List<Car> updateCurrentFromNext() {
		
		List<Car> toBeRemoved = new ArrayList<Car>();
		
		for (Car c : movingCars) {
			boolean shouldPersist = c.updateCurrentFromNext();
			if (!shouldPersist) {
				toBeRemoved.add(c);
			}
		}
		for (Car c : crashedCars) {
			boolean shouldPersist = c.updateCurrentFromNext();
			assert shouldPersist;
		}
		
		return toBeRemoved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * the next choice to make
	 */
	public Vertex shortestPathChoice(Vertex start, Vertex end) {
		return graph.shortestPathChoice(start, end);
	}
	
	public double distanceBetweenVertices(Vertex start, Vertex end) {
		return graph.distanceBetweenVertices(start, end);
	}
	
	public boolean areNeighbors(Edge a, Edge b) {
		return graph.areNeighbors(a, b);
	}
	
	public Position hitTest(Point p) {
		return graph.hitTest(p);
	}
	
	public void processNewWorldStroke(List<Point> stroke) {
		graph.processNewStroke(stroke);	
	}
	
	public void removeEdge(Edge e) {
		graph.removeEdgeTop(e);
	}
	
	public void removeVertex(Vertex i) {
		graph.removeVertexTop(i);
	}
	
	public void addVertexTop(Point p) {
		graph.addVertexTop(p);
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
	public Position findClosestPosition(Point a, double radius) {
		return graph.findClosestPosition(a, radius);
	}
	
	public List<Segment> findAllSegments(Point a) {
		return graph.getSegmentTree().findAllSegments(a);
	}
	
	public Position findClosestPosition(Point p) {
		return graph.findClosestPosition(p);
	}
	
	public Position findClosestDeleteablePosition(Point p) {
		return graph.findClosestDeleteablePosition(p);
	}
	
	
}
