package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.core.geom.Sweeper;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Edge;
import com.gutabi.deadlock.core.graph.Graph;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.fixture.WorldSink;
import com.gutabi.deadlock.model.fixture.WorldSource;
import com.gutabi.deadlock.view.AnimatedExplosion;
import com.gutabi.deadlock.view.AnimatedGrass;

@SuppressWarnings("static-access")
public class World implements Sweepable {
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public static final double WORLD_WIDTH = 16.0;
	public static final double WORLD_HEIGHT = WORLD_WIDTH;
	
	public static final int GRASS_WIDTH = 32;
	public static final int GRASS_HEIGHT = 32;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
	AnimatedGrass animatedGrass1;
	AnimatedGrass animatedGrass2;
	AnimatedGrass animatedGrass3;
	
	private Graph graph;
	
	private List<Car> cars = new ArrayList<Car>();
	
	private List<AnimatedExplosion> explosions = new ArrayList<AnimatedExplosion>();
	
//	private List<Point> skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	static Color lightGreen = new Color(128, 255, 128);
	
	private Rect worldRect;
	private Rect aabb;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
		graph = new Graph();
		
		animatedGrass1 = new AnimatedGrass(new Point(WORLD_WIDTH/4, WORLD_HEIGHT/4));
		animatedGrass2 = new AnimatedGrass(new Point(3*WORLD_WIDTH/4, 2*WORLD_HEIGHT/4));
		animatedGrass3 = new AnimatedGrass(new Point(WORLD_WIDTH/4, 3*WORLD_HEIGHT/4));
		
		WorldSource a = new WorldSource(new Point(WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
		WorldSource b = new WorldSource(new Point(2*WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
		WorldSource t1 = new WorldSource(new Point(3*WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
		
		WorldSource c = new WorldSource(new Point(0, WORLD_HEIGHT/4), Axis.LEFTRIGHT);
		WorldSource d = new WorldSource(new Point(0, 2*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
		WorldSource t2 = new WorldSource(new Point(0, 3*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
		
		WorldSink e = new WorldSink(new Point(WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
		WorldSink f = new WorldSink(new Point(2*WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
		WorldSink t3 = new WorldSink(new Point(3*WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
		
		WorldSink g = new WorldSink(new Point(WORLD_WIDTH, WORLD_HEIGHT/4), Axis.LEFTRIGHT);
		WorldSink h = new WorldSink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
		WorldSink t4 = new WorldSink(new Point(WORLD_WIDTH, 3*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
		
		a.matchingSink = e;
		e.matchingSource = a;
		
		b.matchingSink = f;
		f.matchingSource = b;
		
		t1.matchingSink = t3;
		t3.matchingSource = t1;
		
		c.matchingSink = g;
		g.matchingSource = c;
		
		d.matchingSink = h;
		h.matchingSource = d;
		
		t2.matchingSink = t4;
		t4.matchingSource = t2;
		
		graph.addVertexTop(a);
		graph.addVertexTop(b);
		graph.addVertexTop(t1);
		
		graph.addVertexTop(c);
		graph.addVertexTop(d);
		graph.addVertexTop(t2);
		
		graph.addVertexTop(e);
		graph.addVertexTop(f);
		graph.addVertexTop(t3);
		
		graph.addVertexTop(g);
		graph.addVertexTop(h);
		graph.addVertexTop(t4);

		
	}
	
	public void init() throws Exception {
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		worldRect = new Rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		
		computeAABB();
		
	}
	
	public void addVertexTop(Vertex v) {
		graph.addVertexTop(v);
		
		postIdleTop();
	}
	
	public void createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		graph.createRoadTop(start, end, pts);
		
		postDraftingTop();
		
//		CONTROLLER.renderAndPaint();
		assert checkConsistency();
	}
	
	public Vertex splitRoadTop(RoadPosition pos) {
		return graph.split(pos);
	}
	
	public void removeVertexTop(Vertex v) {
		graph.removeVertexTop(v);
		postIdleTop();
	}
	
	public void removeRoadTop(Road e) {
		graph.removeRoadTop(e);
		postIdleTop();
	}
	
	public void removeMergerTop(Merger m) {
		graph.removeMergerTop(m);
		postIdleTop();
	}
	
	public void removeStopSignTop(StopSign s) {
		s.e.removeStopSignTop(s);
		postIdleTop();
	}
	
	public void removeCarTop(Car c) {
		
		c.destroy();
		
		synchronized (MODEL) {
			cars.remove(c);
		}
		
		postRunningTop();
	}
	
	public void insertMergerTop(Point p) {
		
		graph.insertMergerTop(p);
		
		postIdleTop();
		
	}
	
	
	
	public void addExplosion(AnimatedExplosion x) {
		explosions.add(x);
	}
	
	
	
	
	
	
	
	public void sweepStart(Sweeper s) {
		graph.sweepStart(s);
	}
	
	public void sweep(Sweeper s, int index) {
		graph.sweep(s, index);
	}
	
	
	
	
	
	
	
	
	private void postIdleTop() {
//		assert MODEL.mode == ControlMode.IDLE;
		
		graph.computeVertexRadii();
		
		computeAABB();
	}
	
	public void postDraftingTop() {
//		assert MODEL.mode == ControlMode.DRAFTING;
		
		graph.computeVertexRadii();
		
		computeAABB();
	}
	
	private void postRunningTop() {
//		assert MODEL.mode == ControlMode.RUNNING;
		;
	}
	
	private void computeAABB() {
		aabb = worldRect;
		aabb = Rect.union(aabb, graph.getAABB());
	}
	
	public Rect getAABB() {
		return aabb;
	}
	
	
	
	/*
	 * run before game loop start
	 */
	public void preStart() {
		
//		renderSkidMarksFresh();
//		renderSkidMarksIncremental();
//		skidMarks = new ArrayList<Point>();
		
		animatedGrass1.preStart();
		animatedGrass2.preStart();
		animatedGrass3.preStart();
		
		graph.preStart();
		
		t = 0;
	}
	
	/*
	 * run after game loop stops
	 */
	public void postStop() {
		
		graph.postStop();
		
		synchronized (MODEL) {
			for (Car c : cars) {
				c.destroy();
			}
			cars.clear();
			
			explosions.clear();
			
		}
		
//		skidMarks.clear();
	}
	
	
	
	int velocityIterations = 6;
	int positionIterations = 2;
	
	public void integrate(double t) {
		
		this.t = t;
		
		preStep();
		
		b2dWorld.step((float)MODEL.dt, velocityIterations, positionIterations);
		
		postStep();
		
	}
	
	private void preStep() {
		
		animatedGrass1.preStep(t);
		animatedGrass2.preStep(t);
		animatedGrass3.preStep(t);
		
		graph.preStep(t);
		
		List<Car> carsCopy;
		synchronized (MODEL) {
			carsCopy = new ArrayList<Car>(cars);
		}
		
		for (Car c : carsCopy) {
			c.preStep(t);
		}
		
		synchronized (MODEL) {
			
			for (AnimatedExplosion x : explosions) {
				x.preStep(t);
			}
			
		}
	}
	
	public void addCar(Car c) {
		cars.add(c);
	}
	
	private void postStep() {
		
		List<Car> toBeRemoved = new ArrayList<Car>();
		
		synchronized (MODEL) {
			
			for (Car c : cars) {
				boolean shouldPersist = c.postStep(t);
				if (!shouldPersist) {
					if (MODEL.hilited == c) {
						MODEL.hilited = null;
					}
					c.destroy();
					toBeRemoved.add(c);
				}
			}
		
			cars.removeAll(toBeRemoved);
			
			List<AnimatedExplosion> exToBeRemoved = new ArrayList<AnimatedExplosion>();
			
			for (AnimatedExplosion e : explosions) {
				boolean shouldPersist = e.postStep(t);
				if (!shouldPersist) {
					exToBeRemoved.add(e);
				}
			}
			
			explosions.removeAll(exToBeRemoved);
			
		}
		
//		if (!skidMarks.isEmpty()) {
////			renderSkidMarksIncremental();
//			skidMarks.clear();
//		}
		
	}
	
//	public void addSkidMarks(Point a, Point b) {
//		skidMarks.add(a);
//		skidMarks.add(b);
//	}
	
	/**
	 * the next choice to make
	 */
	public Vertex shortestPathChoice(Vertex start, Vertex end) {
		return graph.shortestPathChoice(start, end);
	}
	
	public Vertex randomPathChoice(Edge prev, Vertex start, Vertex end) {
		return graph.randomPathChoice(prev, start, end);
	}
	
	public double distanceBetweenVertices(Vertex start, Vertex end) {
		return graph.distanceBetweenVertices(start, end);
	}
	
	/**
	 * returns a sorted list of car proximity events
	 */
	public Car carProximityTest(Car test, GraphPositionPathPosition center, double dist) {
		
//		GraphPositionPathPosition closest = null;
		
		GraphPositionPath path = center.path;
		
		for (Car c : cars) {
			if (c == test) {
				continue;
			}
			if (c.overallPos != null) {
				GraphPositionPathPosition otherCarCenter = path.hitTest(c.overallPos.gpos);
				if (otherCarCenter != null) {
					if (DMath.greaterThanEquals(otherCarCenter.combo, center.combo)) {
						double centerCenterDist = center.distanceTo(otherCarCenter);
						if (DMath.lessThanEquals(centerCenterDist, dist)) {
							return c;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	
	public Entity hitTest(Point p) {
		Car c = carHitTest(p);
		if (c != null) {
			return c;
		}
		Entity h = graphHitTest(p);
		if (h != null) {
			return h;
		}
		return null;
	}
	
	public Car carHitTest(Point p) {
		synchronized (MODEL) {
			for (Car c : cars) {
				if (c.hitTest(p) != null) {
					return c;
				}
			}
		}
		return null;
	}
	
	public Car carBestHitTest(Shape s) {
		synchronized (MODEL) {
			for (Car c : cars) {
				if (c.bestHitTest(s) != null) {
					return c;
				}
			}
		}
		return null;
	}
	
	public Entity graphHitTest(Point p) {
		return graph.graphHitTest(p);
	}
	
	public Entity graphBestHitTest(Shape s) {
		return graph.graphBestHitTest(s);
	}
	
	public Entity pureGraphBestHitTest(Shape s) {
		return graph.pureGraphBestHitTest(s);
	}
	
	public RoadPosition findClosestRoadPosition(Point p, double radius) {
		return graph.findClosestRoadPosition(p, radius);
	}
	
	public boolean isValidRoad(Road r) {
		return graph.edges.contains(r);
	}
	
	/*
	 * is this vertex under any cars?
	 */
//	public boolean isUnderAnyCars(Vertex v) {
//		
//		synchronized (MODEL) {
//			for (Car c : MODEL.world.cars) {
//				if (c.bestHitTest(v.shape) != null) {
//					return true;
//				}
//			}
//		}
//		
//		return false;
//	}
	
	public boolean cursorIntersect(Cursor c) {
		return graph.cursorIntersect(c);
	}
	
	public void paint(Graphics2D g2) {
		
		paintBackground(g2);
		
		paintScene(g2);
			
		if (MODEL.DEBUG_DRAW) {
			
			paintAABB(g2);
			
		}
	}
	
	private void paintBackground(Graphics2D g2) {
		
		g2.drawImage(backgroundGrassImage, 0, 0, null);
		
		animatedGrass1.paint(g2);
		animatedGrass2.paint(g2);
		animatedGrass3.paint(g2);
		
		int x = (int)((aabb.x * MODEL.PIXELS_PER_METER));
		int y = (int)((aabb.y * MODEL.PIXELS_PER_METER));
		g2.drawImage(backgroundGraphImage, x, y, null);
		
//		drawSkidMarks(g2);
		
	}
	
	private void paintScene(Graphics2D g2) {
		
		switch (CONTROLLER.mode) {
		case DRAFTING:
		case IDLE:
		case RUNNING:
		case PAUSED:
		case MERGEROUTLINE: {
			
			graph.paintScene(g2);
			
			synchronized (MODEL) {
				
				for (Car c : cars) {
					c.paint(g2);
				}
				
				for (AnimatedExplosion x : explosions) {
					x.paint(g2);
				}
				
			}
			
			if (MODEL.hilited != null) {
				MODEL.hilited.paintHilite(g2);
			}
			
			if (MODEL.DEBUG_DRAW) {
				
				graph.paintIDs(g2);
				
			}
			
			break;
		}
		}
		
	}
	
	public void paintStats(Graphics2D g2) {
		
		Point p = new Point(1, 1).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("time: " + t, (int)p.x, (int)p.y);
		
		p = new Point(1, 2).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("body count: " + b2dWorld.getBodyCount(), (int)p.x, (int)p.y);
		
		p = new Point(1, 3).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("car count: " + cars.size(), (int)p.x, (int)p.y);
		
		g2.translate(0, 3 * MODEL.PIXELS_PER_METER);
		
		graph.paintStats(g2);
		
	}
	
	
//	static java.awt.Stroke skidMarkStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
//	private void drawSkidMarks(Graphics2D g2) {
//		g2.setColor(Color.BLACK);
//		for (int i = 0; i < skidMarks.size(); i+=2) {
//			Point s0 = skidMarks.get(i);
//			Point s1 = skidMarks.get(i+1);
//			g2.drawLine(
//					(int)(s0.x * MODEL.PIXELS_PER_METER),
//					(int)(s0.y * MODEL.PIXELS_PER_METER),
//					(int)(s1.x * MODEL.PIXELS_PER_METER),
//					(int)(s1.y * MODEL.PIXELS_PER_METER));
//		}
//	}
//	
	
	private BufferedImage backgroundGrassImage;
	
	private BufferedImage backgroundGraphImage;
	
	public void renderBackgroundFresh() {
		assert !Thread.holdsLock(MODEL);
//		assert SwingUtilities.isEventDispatchThread();
//		assert Thread.currentThread().getName().equals("controller");
		
		backgroundGraphImage = new BufferedImage(
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D backgroundGraphImageG2 = backgroundGraphImage.createGraphics();
		
		backgroundGrassImage = new BufferedImage(
				(int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D backgroundGrassImageG2 = backgroundGrassImage.createGraphics();
		
		if (!MODEL.DEBUG_DRAW) {
			
			backgroundGrassImageG2.drawImage(VIEW.tiledGrass, 0, 0, null);
			
		} else {
			
			backgroundGrassImageG2.setColor(lightGreen);
			backgroundGrassImageG2.fillRect(0, 0, (int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER), (int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER));
			
		}
		
		backgroundGraphImageG2.setStroke(VIEW.worldStroke);
		
		if (!MODEL.DEBUG_DRAW) {
			
			backgroundGraphImageG2.translate(
					(int)((-aabb.x) * MODEL.PIXELS_PER_METER),
					(int)((-aabb.y) * MODEL.PIXELS_PER_METER));
			
			graph.renderBackground(backgroundGraphImageG2);
			
		} else {
			
			backgroundGraphImageG2.translate(
					(int)((-aabb.x) * MODEL.PIXELS_PER_METER),
					(int)((-aabb.y) * MODEL.PIXELS_PER_METER));
			
			graph.renderBackground(backgroundGraphImageG2);
			
		}
		
		backgroundGrassImageG2.dispose();
		backgroundGraphImageG2.dispose();
		
	}
	
	
//	private BufferedImage skidMarksImage;
	
//	public void renderSkidMarksFresh() {
//		skidMarksImage = new BufferedImage(
//				(int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER),
//				(int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
//				BufferedImage.TYPE_INT_ARGB);
//		skidMarksImageG2 = skidMarksImage.createGraphics();
//		skidMarksImageG2.setColor(Color.BLACK);
//		skidMarksImageG2.setStroke(skidMarkStroke);
//	}
	
	
	
//	static java.awt.Stroke skidMarkStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	
//	Graphics2D skidMarksImageG2;
	
//	public void renderSkidMarksIncremental() {
//		assert !Thread.holdsLock(MODEL);
//		
//		for (int i = 0; i < skidMarks.size(); i+=2) {
//			Point s0 = skidMarks.get(i);
//			Point s1 = skidMarks.get(i+1);
//			skidMarksImageG2.drawLine(
//					(int)(s0.x * MODEL.PIXELS_PER_METER),
//					(int)(s0.y * MODEL.PIXELS_PER_METER),
//					(int)(s1.x * MODEL.PIXELS_PER_METER),
//					(int)(s1.y * MODEL.PIXELS_PER_METER));
//		}
//		
////		skidMarksImageG2.dispose();
//		
//	}
	
	private void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabb.x * MODEL.PIXELS_PER_METER),
				(int)(aabb.y * MODEL.PIXELS_PER_METER),
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
