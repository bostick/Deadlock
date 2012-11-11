package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.geom.SweepEventListener;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Edge;
import com.gutabi.deadlock.core.graph.Graph;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.fixture.WorldSink;
import com.gutabi.deadlock.model.fixture.WorldSource;

@SuppressWarnings("static-access")
public class World {
	
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
	
	
	public Graph graph;
	
	public List<Car> cars = new ArrayList<Car>();
	
	List<Point> skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	public static BufferedImage sheet;
	private static BufferedImage tiledGrass;
	static Color lightGreen = new Color(128, 255, 128);
	
	private Rect worldRect;
	private Rect aabb;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
		graph = new Graph();
		
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
		
		sheet = ImageIO.read(new File("media\\sheet.png"));
		
		tiledGrass = new BufferedImage(
				(int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tiledGrass.createGraphics();
		
		for (int i = 0; i < (WORLD_WIDTH * MODEL.PIXELS_PER_METER)/GRASS_WIDTH; i++) {
			for (int j = 0; j < (WORLD_HEIGHT * MODEL.PIXELS_PER_METER)/GRASS_HEIGHT; j++) {
				g2.drawImage(sheet, GRASS_WIDTH * i, GRASS_HEIGHT * j, GRASS_WIDTH * i + GRASS_WIDTH, GRASS_HEIGHT * j + GRASS_HEIGHT, 0, 224, 0+GRASS_WIDTH, 224+GRASS_HEIGHT, null);
			}
		}
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		worldRect = new Rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		
		computeAABB();
		
	}
	
//	public void addSource(WorldSource s) {
//		graph.addVertex(s);
//	}
//	
//	public void addSink(WorldSink s) {
//		graph.addVertex(s);
//	}
//	
//	public void addIntersection(Intersection i) {
//		graph.addVertex(i);
//	}
	
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
	
	public void processNewStrokeTop(Stroke stroke) {
		
		stroke.processNewStrokeTop(graph);
		
		postDraftingTop();
	}
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		graph.sweepStart(s, l);
	}
	
//	public void sweepEnd(Stroke s, SweepEventListener l) {
//		graph.sweepEnd(s, l);
//	}
	
	public void sweep(Stroke s, int index, SweepEventListener l) {
		graph.sweep(s, index, l);
	}
	
	public void insertMergerTop(Point p) {
		
		graph.insertMergerTop(p);
		
		postIdleTop();
		
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
		
		renderSkidMarksFresh();
		
		graph.preStart();
		
		t = 0;
	}
	
	/*
	 * run after game loop stops
	 */
	public void postStop() {
		
		graph.postStop();
		
//		skidMarks.clear();
		
		synchronized (MODEL) {
			for (Car c : cars) {
				c.destroy();
			}
			cars.clear();
		}
		
		skidMarksImage = null;
		
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
		
		graph.preStep(t);
		
		synchronized (MODEL) {
			for (Car c : cars) {
				c.preStep(t);
			}
		}
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
		}
		
		if (!skidMarks.isEmpty()) {
			renderSkidMarksIncremental();
			skidMarks.clear();
		}
		
	}
	
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
	
	public Entity hitTest(Point p) {
		Car c = carHitTest(p);
		if (c != null) {
			return c;
		}
//		StopSign s = signHitTest(p);
//		if (s != null) {
//			return s;
//		}
		Entity h = graphHitTest(p);
		if (h != null) {
			return h;
		}
		return null;
	}
	
//	public Entity bestHitTestX(Point p, double r) {
//		Car c = carBestHitTest(p, r);
//		if (c != null) {
//			return c;
//		}
////		StopSign s = signBestHitTest(p, r);
////		if (s != null) {
////			return s;
////		}
//		Entity h = graphBestHitTest(p, r);
//		if (h != null) {
//			return h;
//		}
//		return null;
//	}
	
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
	
	public Car carBestHitTest(Point p, double r) {
		synchronized (MODEL) {
			for (Car c : cars) {
				if (c.bestHitTest(p, r) != null) {
					return c;
				}
			}
		}
		return null;
	}
	
	public Entity graphHitTest(Point p) {
		return graph.graphHitTest(p);
	}
	
//	public StopSign signHitTest(Point p) {
//		return graph.signHitTest(p);
//	}
//	
//	public StopSign signBestHitTest(Point p, double r) {
//		return graph.signBestHitTest(p, r);
//	}
	
//	public Entity graphBestHitTest(Point p, double r) {
//		return graph.graphBestHitTest(p, r);
//	}
	
//	public Entity bestHitTest(Point p, double radius) {
//		return graph.bestHitTest(p, radius);
//	}
	
	
	
	public void paint(Graphics2D g2) {
		
		if (!MODEL.DEBUG_DRAW) {
			
			paintBackground(g2);
			paintScene(g2);
			
		} else {
			
			paintBackground(g2);
			paintScene(g2);
			
			paintAABB(g2);
			
		}
	}
	
	private void paintScene(Graphics2D g2) {
		
		switch (CONTROLLER.mode) {
		case DRAFTING:
		case IDLE:
		case RUNNING:
		case PAUSED:
		case MERGEROUTLINE: {
			
			graph.paintScene(g2);
			
			AffineTransform origTransform = g2.getTransform();
			
//			g2.setTransform(origTransform);
			
			g2.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
			
			synchronized (MODEL) {
				for (Car c : cars) {
					c.paint(g2);
				}
			}
			
			if (MODEL.hilited != null) {
				MODEL.hilited.paintHilite(g2);
			}
			
			g2.setTransform(origTransform);
			
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
	
	private void paintBackground(Graphics2D g2) {
		
		drawBackground(g2);
		
		if (skidMarksActive) {
			drawSkidMarks(g2);
		}
		
	}
	
	private void drawBackground(Graphics2D g2) {
		int x = (int)((aabb.x * MODEL.PIXELS_PER_METER));
		int y = (int)((aabb.y * MODEL.PIXELS_PER_METER));
		g2.drawImage(backgroundImage, x, y, null);
	}
	
	private void drawSkidMarks(Graphics2D g2) {
		g2.drawImage(skidMarksImage, 0, 0, null);
	}
	
//	public Runnable renderBackgroundRunnable = new Runnable() {
//		public void run() {
//			renderBackground();
//		}
//	};
	
	
	private BufferedImage backgroundImage;
	private Graphics2D backgroundImageG2;
	
	public void renderBackgroundFresh() {
		assert !Thread.holdsLock(MODEL);
		assert SwingUtilities.isEventDispatchThread();
//		assert Thread.currentThread().getName().equals("controller");
		
		backgroundImage = new BufferedImage(
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		backgroundImageG2 = backgroundImage.createGraphics();
		
		backgroundImageG2.setStroke(VIEW.worldStroke);
		
		if (!MODEL.DEBUG_DRAW) {
			
			AffineTransform orig = backgroundImageG2.getTransform();
			
			backgroundImageG2.translate(
					(int)((-aabb.x) * MODEL.PIXELS_PER_METER),
					(int)((-aabb.y) * MODEL.PIXELS_PER_METER));
			
			backgroundImageG2.drawImage(tiledGrass, 0, 0, null);
			
			graph.renderBackground(backgroundImageG2);
			
			backgroundImageG2.setTransform(orig);
			
		} else {
			
			AffineTransform orig = backgroundImageG2.getTransform();
			
			backgroundImageG2.translate(
					(int)((-aabb.x) * MODEL.PIXELS_PER_METER),
					(int)((-aabb.y) * MODEL.PIXELS_PER_METER));
			
			backgroundImageG2.setColor(lightGreen);
			backgroundImageG2.fillRect(0, 0, (int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER), (int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER));
			
			graph.renderBackground(backgroundImageG2);
			
			backgroundImageG2.setTransform(orig);
			
		}
		
	}
	
	
	private BufferedImage skidMarksImage;
	private Graphics2D skidMarksImageG2;
	private boolean skidMarksActive;
	
	public void renderSkidMarksFresh() {
		skidMarksImage = new BufferedImage(
				(int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		skidMarksImageG2 = skidMarksImage.createGraphics();
	}
	
	
	
	static java.awt.Stroke skidMarkStroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	public void renderSkidMarksIncremental() {
		assert !Thread.holdsLock(MODEL);
//		assert SwingUtilities.isEventDispatchThread();
//		assert Thread.currentThread().getName().equals("controller");
		
//		AffineTransform origTransform = skidMarksImageG2.getTransform();
		
//		skidMarksImageG2.translate(
//				(int)((-aabb.x) * MODEL.PIXELS_PER_METER),
//				(int)((-aabb.y) * MODEL.PIXELS_PER_METER));
		
		skidMarksImageG2.setColor(Color.BLACK);
		skidMarksImageG2.setStroke(skidMarkStroke);
		
		for (int i = 0; i < skidMarks.size(); i+=2) {
			Point s0 = skidMarks.get(i);
			Point s1 = skidMarks.get(i+1);
			skidMarksImageG2.drawLine(
					(int)(s0.x * MODEL.PIXELS_PER_METER),
					(int)(s0.y * MODEL.PIXELS_PER_METER),
					(int)(s1.x * MODEL.PIXELS_PER_METER),
					(int)(s1.y * MODEL.PIXELS_PER_METER));
		}
		
		skidMarksActive = true;
		
//		skidMarksImageG2.setTransform(origTransform);
		
	}
	
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
