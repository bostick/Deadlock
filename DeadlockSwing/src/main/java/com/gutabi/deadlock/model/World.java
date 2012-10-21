package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.GraphController;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.utils.ImageUtils;

@SuppressWarnings("static-access")
public class World {
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public static final double WORLD_WIDTH = 16.0;
	public static final double WORLD_HEIGHT = WORLD_WIDTH;
	
	/*
	 * spawn cars every SPAWN_FREQUENCY seconds
	 * -1 means no spawning
	 */
	public static double SPAWN_FREQUENCY_SECONDS = 3.0;
	
	public static Random RANDOM = new Random(1);
	
	
	/*
	 * simulation state
	 */
	public double t;
	public double lastSpawnTime;
	
	
	private Graph graph;
	private GraphController gc;
	
	private List<Car> cars = new ArrayList<Car>();
	
	org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	public static BufferedImage normalCar;
	public static BufferedImage fastCar;
	private static BufferedImage tiledGrass;
	
	private BufferedImage backgroundImage;
	
	public Point renderingUpperLeft;
	public Point renderingBottomRight;
	
	private static Logger logger = Logger.getLogger(World.class);
	
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
		gc = new GraphController(graph);
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		
		Source a = new Source(new Point(WORLD_WIDTH/3, 0), graph);
		Source b = new Source(new Point(2*WORLD_WIDTH/3, 0), graph);
		Source c = new Source(new Point(0, WORLD_HEIGHT/3), graph);
		Source d = new Source(new Point(0, 2*WORLD_HEIGHT/3), graph);
		
		Sink e = new Sink(new Point(WORLD_WIDTH/3, WORLD_HEIGHT), graph);
		Sink f = new Sink(new Point(2*WORLD_WIDTH/3, WORLD_HEIGHT), graph);
		Sink g = new Sink(new Point(WORLD_WIDTH, WORLD_HEIGHT/3), graph);
		Sink h = new Sink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/3), graph);
		
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
		
		computeRenderingOffsets();
	}
	
	public void removeVertexTop(Vertex v) {
		gc.removeVertexTop(v);
		postTop();
	}
	
	public void removeEdgeTop(Edge e) {
		gc.removeEdgeTop(e);
		postTop();
	}
	
	public void removeCarTop(Car c) {
		c.b2dCleanup();
		
		synchronized (MODEL) {
			cars.remove(c);
		}
		
		postTop();
	}
	
	public void processNewStrokeTop(List<Point> stroke) {
		gc.processNewStrokeTop(stroke);
		postTop();
	}
	
	private void postTop() {
		computeRenderingOffsets();
	}
	
	private void computeRenderingOffsets() {
		
		double renderingUpperLeftX = 0.0;
		double renderingUpperLeftY = 0.0;
		double renderingBottomRightX = WORLD_WIDTH;
		double renderingBottomRightY = WORLD_HEIGHT;
		
		for (Vertex v : graph.getAllVertices()) {
			if (v.renderingUpperLeft.x < renderingUpperLeftX) {
				renderingUpperLeftX = v.renderingUpperLeft.x;
			}
			if (v.renderingUpperLeft.y < renderingUpperLeftY) {
				renderingUpperLeftY = v.renderingUpperLeft.y;
			}
			if (v.renderingBottomRight.x > renderingBottomRightX) {
				renderingBottomRightX = v.renderingBottomRight.x;
			}
			if (v.renderingBottomRight.y > renderingBottomRightY) {
				renderingBottomRightY = v.renderingBottomRight.y;
			}
		}
		for (Edge ed : graph.getEdges()) {
			if (ed.renderingUpperLeft.x < renderingUpperLeftX) {
				renderingUpperLeftX = ed.renderingUpperLeft.x;
			}
			if (ed.renderingUpperLeft.y < renderingUpperLeftY) {
				renderingUpperLeftY = ed.renderingUpperLeft.y;
			}
			if (ed.renderingBottomRight.x > renderingBottomRightX) {
				renderingBottomRightX = ed.renderingBottomRight.x;
			}
			if (ed.renderingBottomRight.y > renderingBottomRightY) {
				renderingBottomRightY = ed.renderingBottomRight.y;
			}
		}
		
		renderingUpperLeft = new Point(renderingUpperLeftX, renderingUpperLeftY);
		renderingBottomRight = new Point(renderingBottomRightX, renderingBottomRightY);
	}
	
	
	
	
	
	/*
	 * run before game loop start
	 */
	public void preStart() {
		
		graph.preStart();
		
		t = 0;
		lastSpawnTime = -1;
		
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
	
	
	
	//public float timeStep = ((float)dt) / 1000.0f;
	int velocityIterations = 6;
	int positionIterations = 2;
	
	public void integrate(double t) {
		
//		logger.debug("integrate " + t);
		
		this.t = t;
		
		preStep();
		
//		logger.debug("before step " + t);
		b2dWorld.step((float)MODEL.dt, velocityIterations, positionIterations);
//		logger.debug("after step " + t);
		
		postStep();
		
	}
	
	private void preStep() {
		if (SPAWN_FREQUENCY_SECONDS > 0 && (t == 0 || (t - lastSpawnTime) >= SPAWN_FREQUENCY_SECONDS)) {
			spawnNewCars();
		}
		synchronized (MODEL) {
			for (Car c : cars) {
				c.preStep();
			}
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
		
		synchronized (MODEL) {
			cars.addAll(newCars);
		}
		
		lastSpawnTime = t;
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
	
	private List<Source> activeSources() {
		
		List<Source> sources = new ArrayList<Source>();
		for (Source s : graph.getSources()) {
			if (s.getPathToMatchingSink() != null) {
				sources.add(s);
			}
		}
		
		List<Source> toRemove = new ArrayList<Source>();
		
		/*
		 * if only moving cars are blocking a source, then a car will be spawned on the next spawn step that there are no blocking cars 
		 */
		synchronized (MODEL) {
			for (Car c : cars) {
				for (Source s : sources) {
					double dist = c.distanceTo(s.getPoint());
					if (DMath.lessThanEquals(dist, c.length)) {
						toRemove.add(s);
					}
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
	
	
	
	
	
	public Entity hitTest(Point p) {
		Car c = carHitTest(p);
		if (c != null) {
			return c;
		}
		Entity h = graph.hitTest(p);
		if (h != null) {
			return h;
		}
		return null;
	}
	
	private Car carHitTest(Point p) {
		synchronized (MODEL) {
			for (Car c : cars) {
				if (c.hitTest(p)) {
					return c;
				}
			}
		}
		return null;
	}
	
	
	public void paint(Graphics2D g2) {
		paintBackground(g2);
		paintScene(g2);
	}
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	private void paintScene(Graphics2D g2) {
		
		frameCount++;
		
		logger.debug("draw scene " + frameCount);
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		switch (MODEL.getMode()) {
		case DRAFTING: {
			
			MODEL.stroke.paint(g2);
			
			break;
		}
		case IDLE:
		case RUNNING:
		case PAUSED: {
			
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
			
			break;
		}
		}
		
		if (MODEL.FPS_DRAW) {
			paintFPS(g2);
		}
		
	}
	
	private void paintBackground(Graphics2D g2) {
		
		int x = (int)(((renderingUpperLeft.x) * MODEL.PIXELS_PER_METER));
		int y = (int)(((renderingUpperLeft.y) * MODEL.PIXELS_PER_METER));
		
		g2.drawImage(backgroundImage, x, y, null);
	}
	
	public void renderBackground() {
		assert !Thread.holdsLock(MODEL);
		
		backgroundImage = new BufferedImage(
				(int)((renderingBottomRight.x - renderingUpperLeft.x) * MODEL.PIXELS_PER_METER),
				(int)((renderingBottomRight.y - renderingUpperLeft.y) * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D backgroundImageG2 = backgroundImage.createGraphics();
		
		backgroundImageG2.translate(
				(int)((-renderingUpperLeft.x) * MODEL.PIXELS_PER_METER),
				(int)((-renderingUpperLeft.y) * MODEL.PIXELS_PER_METER));
		
		backgroundImageG2.drawImage(tiledGrass, 0, 0, null);
		
		backgroundImageG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>(MODEL.world.graph.getEdges());
			verticesCopy = new ArrayList<Vertex>(MODEL.world.graph.getAllVertices());
		}
		
		AffineTransform origTransform = backgroundImageG2.getTransform();
		AffineTransform trans = (AffineTransform)origTransform.clone();
		trans.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
		backgroundImageG2.setTransform(trans);
		
		for (Edge e : edgesCopy) {
			e.paint(backgroundImageG2);
		}
		
		for (Vertex v : verticesCopy) {
			v.paint(backgroundImageG2);
		}
		
		backgroundImageG2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			for (Vertex v : verticesCopy) {
				v.paintID(backgroundImageG2);
			}
			
			for (Edge e : edgesCopy) {
				e.paintSkeleton(backgroundImageG2);
				e.paintBorders(backgroundImageG2);
			}
		}
		
	}
	
	/**
	 * 
	 * @param g2
	 */
	public void paintFPS(Graphics2D g2) {
		
//		g2.setColor(Color.WHITE);
//		g2.fillRect(0, 0, 200, 200);
		
		g2.setColor(Color.WHITE);
		
		Point p = new Point(1, 1).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("FPS: " + fps, (int)p.x, (int)p.y);
		
		p = new Point(1, 2).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("time: " + t, (int)p.x, (int)p.y);
		
		p = new Point(1, 3).multiply(MODEL.PIXELS_PER_METER);		
		g2.drawString("body count: " + b2dWorld.getBodyCount(), (int)p.x, (int)p.y);
		
		p = new Point(1, 4).multiply(MODEL.PIXELS_PER_METER);		
		g2.drawString("edge count: " + graph.getEdges().size(), (int)p.x, (int)p.y);
		
		p = new Point(1, 5).multiply(MODEL.PIXELS_PER_METER);		
		g2.drawString("vertex count: " + graph.getAllVertices().size(), (int)p.x, (int)p.y);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
