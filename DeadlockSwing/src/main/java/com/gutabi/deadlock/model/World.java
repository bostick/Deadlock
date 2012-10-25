package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Intersection;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.StopSign;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;
import com.gutabi.deadlock.utils.ImageUtils;

@SuppressWarnings("static-access")
public class World {
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public static final double WORLD_WIDTH = 16.0;
	public static final double WORLD_HEIGHT = WORLD_WIDTH;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
	
	private Graph graph;
	
	public List<Car> cars = new ArrayList<Car>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	public static BufferedImage normalCar;
	public static BufferedImage fastCar;
	public static BufferedImage randomCar;
	public static BufferedImage stopSign;
	private static BufferedImage tiledGrass;
	
	private BufferedImage backgroundImage;
	
	public Point renderingUpperLeft;
	public Dim renderingDim;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
	}
	
	public void init() throws Exception {
		
		normalCar = ImageIO.read(new File("media\\normalCar.png"));
		normalCar = ImageUtils.createResizedCopy(
				normalCar,
				(int)(Car.CAR_LENGTH * MODEL.PIXELS_PER_METER),
				(int)(Car.CAR_LENGTH * MODEL.PIXELS_PER_METER), true);
		
		fastCar = ImageIO.read(new File("media\\fastCar.png"));
		fastCar = ImageUtils.createResizedCopy(
				fastCar,
				(int)(Car.CAR_LENGTH * MODEL.PIXELS_PER_METER),
				(int)(Car.CAR_LENGTH * MODEL.PIXELS_PER_METER), true);
		
		randomCar = ImageIO.read(new File("media\\randomCar.png"));
		randomCar = ImageUtils.createResizedCopy(
				randomCar,
				(int)(Car.CAR_LENGTH * MODEL.PIXELS_PER_METER),
				(int)(Car.CAR_LENGTH * MODEL.PIXELS_PER_METER), true);
		
		stopSign = ImageIO.read(new File("media\\stop.png"));
		stopSign = ImageUtils.createResizedCopy(
				stopSign,
				(int)(StopSign.STOPSIGN_SIZE),
				(int)(StopSign.STOPSIGN_SIZE), true);
		
		BufferedImage grass = ImageIO.read(new File("media\\grass.png"));
		
		tiledGrass = new BufferedImage(
				(int)(WORLD_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(WORLD_HEIGHT * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tiledGrass.createGraphics();
		
		for (int i = 0; i < (WORLD_WIDTH * MODEL.PIXELS_PER_METER)/grass.getWidth(); i++) {
			for (int j = 0; j < (WORLD_HEIGHT * MODEL.PIXELS_PER_METER)/grass.getHeight(); j++) {
				g2.drawImage(grass, grass.getWidth() * i, grass.getHeight() * j, null);
			}
		}
		
		graph = new Graph();
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		
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
		
		graph.addSource(a);
		graph.addSource(b);
		graph.addSource(c);
		graph.addSource(d);
		
		graph.addSink(e);
		graph.addSink(f);
		graph.addSink(g);
		graph.addSink(h);
		
		computeRenderingRect();
	}
	
	
	
	public void removeVertexTop(Vertex v) {
		graph.removeVertexTop(v);
		postTop();
	}
	
	public void removeEdgeTop(Edge e) {
		graph.removeEdgeTop(e);
		postTop();
	}
	
	public void removeCarTop(Car c) {
		c.b2dCleanup();
		
		synchronized (MODEL) {
			cars.remove(c);
		}
		
		postTop();
	}
	
	public void processNewStrokeTop(Stroke stroke) {
		
		List<Edge> newEdges = new ArrayList<Edge>();
		
		Point startPoint = stroke.getWorldPoint(0);
		GraphPosition startPos = graph.findClosestGraphPosition(startPoint, Vertex.INIT_VERTEX_RADIUS);
		
		int i;
		for (i = 1; i < stroke.size(); i++) {
			Point b = stroke.getWorldPoint(i);
			GraphPosition bPos = graph.findClosestGraphPosition(b, Vertex.INIT_VERTEX_RADIUS);
			if (bPos != null) {
				if (startPos != null && bPos.getEntity() == startPos.getEntity()) {
					continue;
				} else {
					break;
				}
			}
		}
		if (i == stroke.size()) {
			/*
			 * we know that the loop reached the end
			 */
			i = stroke.size()-1;
		}
		
		Point endPoint = stroke.getWorldPoint(i);
		GraphPosition endPos = graph.findClosestGraphPosition(endPoint, Vertex.INIT_VERTEX_RADIUS);
		
		if (DMath.lessThanEquals(Point.distance(startPoint, endPoint), Vertex.INIT_VERTEX_RADIUS + Vertex.INIT_VERTEX_RADIUS)) {
			/*
			 * the two new vertices would be overlapping
			 */
			return;
		}
		
		if (startPos == null) {
			Intersection start = new Intersection(startPoint);
			graph.addIntersection(start);
		}
		
		if (endPos == null) {
			Intersection end = new Intersection(endPoint);
			graph.addIntersection(end);
		}
		
		startPos = graph.findClosestGraphPosition(startPoint, Vertex.INIT_VERTEX_RADIUS);
		assert startPos != null;
		Vertex start;
		if (startPos instanceof EdgePosition) {
			start = graph.split((EdgePosition)startPos);
		} else {
			start = ((VertexPosition)startPos).getVertex();
		}
		
		endPos = graph.findClosestGraphPosition(endPoint, Vertex.INIT_VERTEX_RADIUS);
		assert endPos != null;
		Vertex end;
		if (endPos instanceof EdgePosition) {
			end = graph.split((EdgePosition)endPos);
		} else {
			end = ((VertexPosition)endPos).getVertex();
		}
		
		Edge e = graph.createEdgeTop(start, end, stroke.getWorldPoints().subList(0, i+1));
		newEdges.add(e);
		
		postTop();
	}
	
	private void postTop() {
		
		graph.computeVertexRadii();
		
		computeRenderingRect();
	}
	
	private void computeRenderingRect() {
		
		Object[] combo = graph.getRenderingRectCombo(0.0, 0.0, WORLD_WIDTH, WORLD_HEIGHT);
		
		renderingUpperLeft = (Point)combo[0];
		renderingDim = (Dim)combo[1];
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
		
		synchronized (MODEL) {
			for (Car c : cars) {
				c.b2dCleanup();
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
		
		for (Car c : cars) {
			c.preStep(t);
		}
	}
	
	private void postStep() {
		
		List<Car> toBeRemoved = new ArrayList<Car>();
		
		synchronized (MODEL) {
			for (Car c : cars) {
				boolean shouldPersist = c.postStep();
				if (!shouldPersist) {
					if (MODEL.hilited == c) {
						MODEL.hilited = null;
					}
					c.b2dCleanup();
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
		synchronized (MODEL) {
			for (Car c : cars) {
				if (c.hitTest(p)) {
					return c;
				}
			}
		}
		return null;
	}
	
	public Entity graphHitTest(Point p) {
		return graph.hitTest(p);
	}
	
	public void paint(Graphics2D g2) {
		paintBackground(g2);
		paintScene(g2);
	}
	
	private void paintScene(Graphics2D g2) {
		
		switch (MODEL.getMode()) {
		case DRAFTING:
			break;
		case IDLE:
		case RUNNING:
		case PAUSED: {
			
			graph.paintScene(g2);
			
			AffineTransform origTransform = g2.getTransform();
			AffineTransform trans = (AffineTransform)origTransform.clone();
			trans.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
			g2.setTransform(trans);
			
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
		
		int x = (int)((renderingUpperLeft.x * MODEL.PIXELS_PER_METER));
		int y = (int)((renderingUpperLeft.y * MODEL.PIXELS_PER_METER));
		
		g2.drawImage(backgroundImage, x, y, null);
	}
	
	public void renderBackground() {
		assert !Thread.holdsLock(MODEL);
		
		backgroundImage = new BufferedImage(
				(int)(renderingDim.width * MODEL.PIXELS_PER_METER),
				(int)(renderingDim.height * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D backgroundImageG2 = backgroundImage.createGraphics();
		
		backgroundImageG2.translate(
				(int)((-renderingUpperLeft.x) * MODEL.PIXELS_PER_METER),
				(int)((-renderingUpperLeft.y) * MODEL.PIXELS_PER_METER));
		
		backgroundImageG2.drawImage(tiledGrass, 0, 0, null);
		
		graph.renderBackground(backgroundImageG2);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
