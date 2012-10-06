package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.Hilitable;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Segment;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.model.b2d.CarContactListener;

public class World {
	
	public final double CAR_LENGTH = 1.0;
	public final double ROAD_WIDTH = CAR_LENGTH;
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
	
//	private List<Car> movingCars = new ArrayList<Car>();
//	private List<Car> crashedCars = new ArrayList<Car>();
	public List<Car> cars = new ArrayList<Car>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	CarContactListener listener;
	
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
		
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		boolean doSleep = true;
		b2dWorld = new org.jbox2d.dynamics.World(gravity, doSleep);
		
		listener = new CarContactListener();
		
		b2dWorld.setContactListener(listener);
		
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
		
		for (Car c : cars) {
			c.b2dCleanUp();
		}
		cars.clear();
		
	}
	
	
	
	float timeStep = ((float)dt) / 1000.0f;
	int velocityIterations = 6;
	int positionIterations = 2;
	
	public void integrate(long t) {
		
		this.t = t;
		
		synchronized (MODEL) {
			
			if (SPAWN_FREQUENCY > 0 && (t == 0 || (t - lastSpawnTime) >= SPAWN_FREQUENCY)) {
				spawnNewCars();
			}
			
			for (Car c : cars) {
				c.preStep();
			}
			
			b2dWorld.step(timeStep, velocityIterations, positionIterations);
			
			List<Car> toBeRemoved = new ArrayList<Car>();
			for (Car c : cars) {
				boolean shouldPersist = c.postStep();
				if (!shouldPersist) {
					toBeRemoved.add(c);
				}
			}
			
			cars.removeAll(toBeRemoved);
			
		}
		
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
		
		cars.addAll(newCars);
		
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
		
		for (Car c : cars) {
			for (Source s : sources) {
				double dist = c.getPoint().distanceTo(s.getPoint());
				if (DMath.lessThanEquals(dist, CAR_LENGTH)) {
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
	
	public List<Car> getCars() {
		return cars;
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
	
	public Hilitable findClosestHilitable(Point a, double radius, boolean onlyDeleteables) {
		
		Hilitable closestCar = null;
		double closestCarDist = Double.POSITIVE_INFINITY;
		for (Car c : cars) {
			double dist = Point.distance(a, c.getPoint());
			if (dist < radius && dist < closestCarDist) {
				closestCar = c;
				closestCarDist = dist;
			}
		}
		
		if (closestCar != null) {
			return closestCar;
		}
		
		Position closestPos = graph.findClosestPosition(a, null, radius, onlyDeleteables);
		
		if (closestPos != null) {
			return closestPos.getHilitable();
		} else {
			return null;
		}
		
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
