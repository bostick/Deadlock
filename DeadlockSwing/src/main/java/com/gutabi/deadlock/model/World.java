package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.Edge;
import com.gutabi.deadlock.core.graph.Graph;
import com.gutabi.deadlock.core.graph.Intersection;
import com.gutabi.deadlock.core.graph.Sink;
import com.gutabi.deadlock.core.graph.Source;
import com.gutabi.deadlock.core.graph.StopSign;
import com.gutabi.deadlock.core.graph.SweepEvent;
import com.gutabi.deadlock.core.graph.SweepEventListener;
import com.gutabi.deadlock.core.graph.Vertex;

@SuppressWarnings("static-access")
public class World implements SweepEventListener {
	
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
	
	
	private Graph graph;
	
	public List<Car> cars = new ArrayList<Car>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	public static BufferedImage sheet;
	private static BufferedImage tiledGrass;
	static Color lightGreen = new Color(128, 255, 128);
	
	private BufferedImage backgroundImage;
	
	private Rect worldRect;
	private Rect aabb;
	
	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
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
		
		graph = new Graph();
		graph.addSweepEventListener(this);
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		
		Source a = new Source(new Point(WORLD_WIDTH/4, 0));
		Source b = new Source(new Point(2*WORLD_WIDTH/4, 0));
		Source t1 = new Source(new Point(3*WORLD_WIDTH/4, 0));
		
		Source c = new Source(new Point(0, WORLD_HEIGHT/4));
		Source d = new Source(new Point(0, 2*WORLD_HEIGHT/4));
		Source t2 = new Source(new Point(0, 3*WORLD_HEIGHT/4));
		
		Sink e = new Sink(new Point(WORLD_WIDTH/4, WORLD_HEIGHT));
		Sink f = new Sink(new Point(2*WORLD_WIDTH/4, WORLD_HEIGHT));
		Sink t3 = new Sink(new Point(3*WORLD_WIDTH/4, WORLD_HEIGHT));
		
		Sink g = new Sink(new Point(WORLD_WIDTH, WORLD_HEIGHT/4));
		Sink h = new Sink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/4));
		Sink t4 = new Sink(new Point(WORLD_WIDTH, 3*WORLD_HEIGHT/4));
		
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
		
		graph.addSource(a);
		graph.addSource(b);
		graph.addSource(t1);
		
		graph.addSource(c);
		graph.addSource(d);
		graph.addSource(t2);
		
		graph.addSink(e);
		graph.addSink(f);
		graph.addSink(t3);
		
		graph.addSink(g);
		graph.addSink(h);
		graph.addSink(t4);
		
		worldRect = new Rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		
		computeAABB();
		
	}
	
	
	
	public void removeVertexTop(Vertex v) {
		graph.removeVertexTop(v);
		postIdleTop();
	}
	
	public void removeEdgeTop(Edge e) {
		graph.removeEdgeTop(e);
		postIdleTop();
	}
	
	public void removeStopSignTop(StopSign e) {
		graph.removeStopSignTop(e);
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
		
		List<Entity> startHits = graphHitTest(stroke.pts.get(0), Vertex.INIT_VERTEX_RADIUS);
		List<Entity> endHits = graphHitTest(stroke.pts.get(stroke.pts.size()-1), Vertex.INIT_VERTEX_RADIUS);
		
		events = new ArrayList<SweepEvent>();
		
		for (int i = 0; i < stroke.pts.size()-1; i++) {
			sweep(stroke, i);
		}
		
		if (startHits.isEmpty() && endHits.isEmpty() && events.isEmpty()) {
			
			Intersection start = new Intersection(stroke.pts.get(0));
			graph.addIntersection(start);
			Intersection end = new Intersection(stroke.pts.get(stroke.pts.size()-1));
			graph.addIntersection(end);
			
			graph.createEdgeTop(start, end, stroke.pts);
			
		} else {
			
			if (!events.isEmpty()) {
				
				for (SweepEvent e : events) {
					
					logger.debug(e.type + " " + e.index + "." + e.param + " " + e.o);
					
				}
				
			}
			
		}
		
		postDraftingTop();
	}
	
	private void sweep(Stroke s, int index) {
		graph.sweep(s, index);
		
	}
	
	
	List<SweepEvent> events = new ArrayList<SweepEvent>();
	
	public void enter(SweepEvent e) {
//		logger.debug("enter " + combo + " " + c.hashCode());
		events.add(e);
	}
	
	public void exit(SweepEvent e) {
//		logger.debug("exit " + combo + " " + c.hashCode());
		events.add(e);
	}
	
	public void intersect(SweepEvent e) {
//		logger.debug("intersect " + combo + " " + c.hashCode());
		events.add(e);
	}
	
	
	
	private void postIdleTop() {
		assert MODEL.mode == ControlMode.IDLE;
		
		graph.computeVertexRadii();
		
		computeAABB();
	}
	
	private void postDraftingTop() {
		assert MODEL.mode == ControlMode.DRAFTING;
		
		graph.computeVertexRadii();
		
		computeAABB();
	}
	
	private void postRunningTop() {
		assert MODEL.mode == ControlMode.RUNNING;
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
		}
		
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
		Entity h = graphHitTest(p);
		if (h != null) {
			return h;
		}
		return null;
	}
	
	public Car carHitTest(Point p) {
		for (Car c : cars) {
			if (c.hitTest(p)) {
				return c;
			}
		}
		return null;
	}
	
	public Entity graphHitTest(Point p) {
		return graph.hitTest(p);
	}
	
	public List<Entity> graphHitTest(Point p, double radius) {
		return graph.hitTest(p, radius);
	}
	
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
		
		switch (MODEL.mode) {
		case DRAFTING:
			break;
		case IDLE:
		case RUNNING:
		case PAUSED: {
			
			graph.paintScene(g2);
			
			AffineTransform origTransform = g2.getTransform();
			
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
			
			graph.paintIDs(g2);
			
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
		
		int x = (int)((aabb.x * MODEL.PIXELS_PER_METER));
		int y = (int)((aabb.y * MODEL.PIXELS_PER_METER));
		
		g2.drawImage(backgroundImage, x, y, null);
	}
	
	public void renderBackground() {
		assert !Thread.holdsLock(MODEL);
		
		backgroundImage = new BufferedImage(
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D backgroundImageG2 = backgroundImage.createGraphics();
		
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
