package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.core.geom.Sweeper;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Edge;
import com.gutabi.deadlock.core.graph.Graph;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.view.AnimatedExplosion;
import com.gutabi.deadlock.view.AnimatedGrass;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class World implements Sweepable {
	
	public final double worldWidth;
	public final double worldHeight;
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
	public Map map;
	
	AnimatedGrass animatedGrass1;
	AnimatedGrass animatedGrass2;
	AnimatedGrass animatedGrass3;
	
	public Graph graph = new Graph();
	
	public List<Car> cars = new ArrayList<Car>();
	
	private List<AnimatedExplosion> explosions = new ArrayList<AnimatedExplosion>();
	
//	private List<Point> skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	public AABB aabb;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
		int[][] ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0},
		};
		
		map = new Map(ini);
		
		worldWidth = map.quadrantCols * MODEL.QUADRANT_WIDTH;
		
		worldHeight = map.quadrantRows * MODEL.QUADRANT_HEIGHT;
		
//		animatedGrass1 = new AnimatedGrass(new Point(worldWidth/4, worldHeight/4));
		
//		animatedGrass2 = new AnimatedGrass(new Point(3*WORLD_WIDTH/4, 2*WORLD_HEIGHT/4));
//		animatedGrass3 = new AnimatedGrass(new Point(WORLD_WIDTH/4, 3*WORLD_HEIGHT/4));
//		
//		WorldSource a = new WorldSource(new Point(WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
//		WorldSource b = new WorldSource(new Point(2*WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
//		WorldSource t1 = new WorldSource(new Point(3*WORLD_WIDTH/4, 0), Axis.TOPBOTTOM);
//		
//		WorldSource c = new WorldSource(new Point(0, WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSource d = new WorldSource(new Point(0, 2*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSource t2 = new WorldSource(new Point(0, 3*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		
//		WorldSink e = new WorldSink(new Point(WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
//		WorldSink f = new WorldSink(new Point(2*WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
//		WorldSink t3 = new WorldSink(new Point(3*WORLD_WIDTH/4, WORLD_HEIGHT), Axis.TOPBOTTOM);
//		
//		WorldSink g = new WorldSink(new Point(WORLD_WIDTH, WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSink h = new WorldSink(new Point(WORLD_WIDTH, 2*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		WorldSink t4 = new WorldSink(new Point(WORLD_WIDTH, 3*WORLD_HEIGHT/4), Axis.LEFTRIGHT);
//		
//		a.matchingSink = e;
//		e.matchingSource = a;
//		
//		b.matchingSink = f;
//		f.matchingSource = b;
//		
//		t1.matchingSink = t3;
//		t3.matchingSource = t1;
//		
//		c.matchingSink = g;
//		g.matchingSource = c;
//		
//		d.matchingSink = h;
//		h.matchingSource = d;
//		
//		t2.matchingSink = t4;
//		t4.matchingSource = t2;
//		
//		graph.addVertexTop(a);
//		graph.addVertexTop(b);
//		graph.addVertexTop(t1);
//		
//		graph.addVertexTop(c);
//		graph.addVertexTop(d);
//		graph.addVertexTop(t2);
//		
//		graph.addVertexTop(e);
//		graph.addVertexTop(f);
//		graph.addVertexTop(t3);
//		
//		graph.addVertexTop(g);
//		graph.addVertexTop(h);
//		graph.addVertexTop(t4);
		
	}
	
	public void init() throws Exception {
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		computeAABB();
		
	}
	
	public Quadrant findQuadrant(Point p) {
		return map.findQuadrant(p);
	}
	
	public Quadrant upFixPoint(Quadrant q) {
		return map.upFixPoint(q);
	}
	
	public Quadrant leftFixPoint(Quadrant q) {
		return map.leftFixPoint(q);
	}
	
	public Quadrant rightFixPoint(Quadrant q) {
		return map.rightFixPoint(q);
	}
	
	public Quadrant downFixPoint(Quadrant q) {
		return map.downFixPoint(q);
	}
	
	
	
	public void addVertexTop(Vertex v) {
		graph.addVertexTop(v);
		
		postIdleTop();
	}
	
	public void createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		graph.createRoadTop(start, end, pts);
		
		postDraftingTop();
		
//		assert checkConsistency();
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
		
		graph.computeVertexRadii();
		
		computeAABB();
	}
	
	public void postDraftingTop() {
		
		graph.computeVertexRadii();
		
		computeAABB();
	}
	
	private void postRunningTop() {
		;
	}
	
	private void computeAABB() {
		aabb = map.aabb;
		aabb = AABB.union(aabb, graph.getAABB());
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	
	
	/*
	 * run before game loop start
	 */
	public void preStart() {
		
//		renderSkidMarksFresh();
//		renderSkidMarksIncremental();
//		skidMarks = new ArrayList<Point>();
		
		if (animatedGrass1 != null) {
			animatedGrass1.preStart();
		}
		if (animatedGrass2 != null) {
			animatedGrass2.preStart();
		}
		if (animatedGrass3 != null) {
			animatedGrass3.preStart();
		}
		
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
		
		if (animatedGrass1 != null) {
			animatedGrass1.preStep(t);
		}
		if (animatedGrass2 != null) {
			animatedGrass2.preStep(t);
		}
		if (animatedGrass3 != null) {
			animatedGrass3.preStep(t);
		}
		
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
		
		graph.postStep(t);
		
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
	
	public Entity graphHitTest(Point p) {
		return graph.graphHitTest(p);
	}
	
	public Entity pureGraphBestHitTest(Shape s) {
		return graph.pureGraphBestHitTest(s);
	}
	
	public RoadPosition findClosestRoadPosition(Point p, double radius) {
		return graph.findClosestRoadPosition(p, radius);
	}
	
	public boolean completelyContains(Shape s) {
		return map.completelyContains(s);
	}
	
	public void paint(RenderingContext ctxt) {
		
		paintBackground(ctxt);
		
		paintScene(ctxt);
			
		if (MODEL.DEBUG_DRAW) {
			aabb.draw(ctxt);
			
		}
	}
	
	private void paintBackground(RenderingContext ctxt) {
		
		ctxt.paintImage(0, 0, VIEW.canvasGrassImage, 0, 0, VIEW.canvasGrassImage.getWidth(), VIEW.canvasGrassImage.getHeight(),
				0, 0, VIEW.canvasGrassImage.getWidth(), VIEW.canvasGrassImage.getHeight());
		
		if (animatedGrass1 != null) {
			animatedGrass1.paint(ctxt);
		}
		if (animatedGrass2 != null) {
			animatedGrass2.paint(ctxt);
		}
		if (animatedGrass3 != null) {
			animatedGrass3.paint(ctxt);
		}
		
		ctxt.paintImage(aabb.x, aabb.y, VIEW.canvasGraphImage, 0, 0, VIEW.canvasGraphImage.getWidth(), VIEW.canvasGraphImage.getHeight(),
				0, 0, VIEW.canvasGraphImage.getWidth(), VIEW.canvasGraphImage.getHeight());
		
//		drawSkidMarks(g2);
		
	}
	
	private void paintScene(RenderingContext ctxt) {
		
		graph.paintScene(ctxt);
		
		synchronized (MODEL) {
			
			for (Car c : cars) {
				c.paint(ctxt);
			}
			
			for (AnimatedExplosion x : explosions) {
				x.paint(ctxt);
			}
			
		}
		
		if (MODEL.hilited != null) {
			MODEL.hilited.paintHilite(ctxt);
		}
		
		if (MODEL.DEBUG_DRAW) {
			
			graph.paintIDs(ctxt);
			
		}
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.g2.getTransform();
		
		ctxt.paintString(0, 0, "time: " + t);
		
		ctxt.g2.translate(0, 1);
		
		ctxt.paintString(0, 0, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.g2.translate(0, 1);
		
		ctxt.paintString(0, 0, "car count: " + cars.size());
		
		ctxt.g2.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.g2.setTransform(origTransform);
		
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
	
//	public void renderBackgroundX() {
//		assert !Thread.holdsLock(MODEL);
//		
//		VIEW.backgroundGrassImage = new BufferedImage(
//				VIEW.metersToPixels(worldWidth),
//				VIEW.metersToPixels(worldHeight),
//				BufferedImage.TYPE_INT_ARGB);
//		
//		Graphics2D backgroundGrassImageG2 = VIEW.backgroundGrassImage.createGraphics();
//		
//		VIEW.previewBackgroundImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
//		
//		Graphics2D previewBackgroundImageG2 = VIEW.previewBackgroundImage.createGraphics();
//		
//		
//		previewBackgroundImageG2.setColor(Color.WHITE);
//		previewBackgroundImageG2.fillRect(0, 0, 100, 100);
//		
//		
//		AffineTransform origBackgroundTransform = backgroundGrassImageG2.getTransform();
//		
//		backgroundGrassImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
//		
//		RenderingContext backgroundCanvasContext = new RenderingContext(backgroundGrassImageG2, RenderingContextType.CANVAS);
//		
//		
//		
//		AffineTransform origPreviewTransform = previewBackgroundImageG2.getTransform();
//		
//		previewBackgroundImageG2.scale(100.0 / VIEW.metersToPixels(worldWidth), 100.0 / VIEW.metersToPixels(worldHeight));
//		
//		previewBackgroundImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
//		
//		RenderingContext backgroundPreviewContext = new RenderingContext(previewBackgroundImageG2, RenderingContextType.PREVIEW);
//		
//		
//		map.renderBackground(backgroundCanvasContext);
//		
//		map.renderBackground(backgroundPreviewContext);
//		
//		backgroundGrassImageG2.setTransform(origBackgroundTransform);
//		previewBackgroundImageG2.setTransform(origPreviewTransform);
//		
//		
//		
//		VIEW.backgroundGraphImage = new BufferedImage(
//				VIEW.metersToPixels(aabb.width),
//				VIEW.metersToPixels(aabb.height),
//				BufferedImage.TYPE_INT_ARGB);
//		
//		Graphics2D backgroundGraphImageG2 = VIEW.backgroundGraphImage.createGraphics();
//		
//		backgroundGraphImageG2.setStroke(VIEW.worldStroke);
//		
//		backgroundGraphImageG2.translate(
//				-VIEW.metersToPixels(aabb.x),
//				-VIEW.metersToPixels(aabb.y));
//		
//		backgroundGraphImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
//		
//		backgroundCanvasContext = new RenderingContext(backgroundGraphImageG2, RenderingContextType.CANVAS);
//		
//		
//		
//		previewBackgroundImageG2.scale(VIEW.PIXELS_PER_METER_DEBUG, VIEW.PIXELS_PER_METER_DEBUG);
//		previewBackgroundImageG2.scale(100.0 / VIEW.metersToPixels(worldWidth), 100.0 / VIEW.metersToPixels(worldHeight));
//		
//		
//		
//		graph.renderBackground(backgroundCanvasContext);
//		
//		graph.renderBackground(backgroundPreviewContext);
//		
//		
//		
//		backgroundGraphImageG2.dispose();
//		backgroundGrassImageG2.dispose();
//		
//	}
	
	
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
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
