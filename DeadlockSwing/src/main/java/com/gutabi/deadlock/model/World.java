package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.core.geom.Sweeper;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Edge;
import com.gutabi.deadlock.core.graph.Graph;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.cursor.RegularCursor;
import com.gutabi.deadlock.view.AnimatedExplosion;
import com.gutabi.deadlock.view.AnimatedGrass;

@SuppressWarnings("static-access")
public class World implements Sweepable {
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
//	public static final double WORLD_WIDTH = 2 * 16.0;
//	public static final double WORLD_HEIGHT = WORLD_WIDTH;
	
	public static final double QUADRANT_WIDTH = 16.0;
	public static final double QUADRANT_HEIGHT = QUADRANT_WIDTH;
	
//	public static final int GRASS_WIDTH = 32;
//	public static final int GRASS_HEIGHT = 32;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
	AnimatedGrass animatedGrass1;
	AnimatedGrass animatedGrass2;
	AnimatedGrass animatedGrass3;
	
	public int[][] quadrants;
	public int quadrantCols;
	public int quadrantRows;
	
	private Graph graph = new Graph();
	
	public List<Car> cars = new ArrayList<Car>();
	
	private List<AnimatedExplosion> explosions = new ArrayList<AnimatedExplosion>();
	
//	private List<Point> skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	private CarEventListener listener;
	
	static Color lightGreen = new Color(128, 255, 128);
	
	private AABB worldRect;
	private AABB aabb;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
		quadrants = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0},
		};
		
		quadrantCols = quadrants[0].length;
		quadrantRows = quadrants.length;
		
//		animatedGrass1 = new AnimatedGrass(new Point(WORLD_WIDTH/4, WORLD_HEIGHT/4));
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
	
	public double getWorldWidth() {
		return quadrantCols * QUADRANT_WIDTH;
	}
	
	public double getWorldHeight() {
		return quadrantRows * QUADRANT_HEIGHT;
	}
	
	public int findQuadrant(Point p) {
		int col = (int)Math.floor(p.x / QUADRANT_WIDTH);
		int row = (int)Math.floor(p.y / QUADRANT_HEIGHT);
		if (col < 0 || col > quadrantCols-1) {
			return -1;
		}
		if (row < 0 || row > quadrantRows-1) {
			return -1;
		}
		return row * quadrantCols + col;
	}
	
	public int quadrantUp(int c) {
		assert c != -1;
		if (!(c > quadrantCols - 1)) {
			return -1;
		}
		return c - quadrantCols;
	}
	
	public int quadrantDown(int c) {
		assert c != -1;
		if (!(c < quadrantRows * quadrantCols)) {
			return -1;
		}
		return c + quadrantCols;
	}
	
	public int quadrantLeft(int c) {
		assert c != -1;
		if (!(c % quadrantCols > 0)) {
			return -1;
		}
		return c - 1;
	}
	
	public int quadrantRight(int c) {
		assert c != -1;
		if (!(c % quadrantCols < quadrantCols-1)) {
			return -1;
		}
		return c + 1;
	}
	
	public boolean isValidQuadrant(int cur) {
		return cur >= 0 && cur < quadrantRows * quadrantCols;
	}
	
	public boolean isActiveQuadrant(int cur) {
		assert cur != -1;
		assert cur >= 0;
		assert cur < quadrantRows * quadrantCols;
		int r = cur / quadrantCols;
		int c = cur % quadrantCols;
		if (r < 0 || r > quadrantRows-1) {
			return false;
		}
		if (c < 0 || c > quadrantCols-1) {
			return false;
		}
		return quadrants[r][c] == 1;
	}
	
	public Point quadrantCenter(int q) {
		int r = q / quadrantCols;
		int c = q % quadrantCols;
		return new Point(c * QUADRANT_WIDTH + QUADRANT_WIDTH/2, r * QUADRANT_HEIGHT + QUADRANT_HEIGHT/2);
	}
	
	public void init() throws Exception {
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		listener = new CarEventListener();
		b2dWorld.setContactListener(listener);
		
		worldRect = new AABB(null, 0, 0, getWorldWidth(), getWorldHeight());
		
		computeAABB();
		
	}
	
	public void addVertexTop(Vertex v) {
		graph.addVertexTop(v);
		
		postIdleTop();
	}
	
	public void createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		graph.createRoadTop(start, end, pts);
		
		postDraftingTop();
		
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
		aabb = worldRect;
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
	
	public Entity pureGraphBestHitTest(Entity e) {
		return graph.pureGraphBestHitTest(e);
	}
	
	public Entity pureGraphBestHitTest(Shape s) {
		return graph.pureGraphBestHitTest(s);
	}
	
	public RoadPosition findClosestRoadPosition(Point p, double radius) {
		return graph.findClosestRoadPosition(p, radius);
	}
	
//	public boolean isValidRoad(Road r) {
//		return graph.edges.contains(r);
//	}
	
	public boolean graphIntersect(Cursor c) {
		return graph.cursorIntersect(c);
	}
	
	public boolean completelyContains(Shape s) {
		
		if (!s.aabb.completelyWithin(worldRect)) {
			return false;
		} else {
			
			for (int i = 0; i < quadrantRows; i++) {
				for (int j = 0; j < quadrantCols; j++) {
					if (quadrants[j][i] == 0) {
						
						AABB quadAABB = new AABB(null, i * QUADRANT_WIDTH, j * QUADRANT_HEIGHT, QUADRANT_WIDTH, QUADRANT_HEIGHT);
						
						if (ShapeUtils.intersect(s, quadAABB)) {
							return false;
						}
						
					} else {
						
					}
				}
			}
			
		}
		
		return true;
		
	}
	
	public boolean completelyContains(Cursor c) {
		
		if (!c.completelyWithin(worldRect)) {
			return false;
		} else {
			
			for (int i = 0; i < quadrantRows; i++) {
				for (int j = 0; j < quadrantCols; j++) {
					if (quadrants[j][i] == 0) {
						
						AABB quadAABB = new AABB(null, i * QUADRANT_WIDTH, j * QUADRANT_HEIGHT, QUADRANT_WIDTH, QUADRANT_HEIGHT);
						
						if (c.intersect(quadAABB)) {
							return false;
						}
						
					} else {
						
					}
				}
			}
			
		}
		
		return true;
		
	}
	
	public void paint(Graphics2D g2) {
		
		paintBackground(g2);
		
		paintScene(g2);
			
		if (MODEL.DEBUG_DRAW) {
			aabb.draw(g2);
			
		}
	}
	
	private void paintBackground(Graphics2D g2) {
		
		g2.drawImage(backgroundGrassImage, 0, 0, null);
		
//		if (MODEL.hilitedQuad != -1) {
//			
//			int r = MODEL.hilitedQuad / quadrantCols;
//			int c = MODEL.hilitedQuad % quadrantCols;
//			
//			g2.setColor(Color.BLUE);
//			g2.drawRect(
//					(int)(c * QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
//					(int)(r * QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER),
//					(int)(QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
//					(int)(QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER));
//		}
		
		if (animatedGrass1 != null) {
			animatedGrass1.paint(g2);
		}
		if (animatedGrass2 != null) {
			animatedGrass2.paint(g2);
		}
		if (animatedGrass3 != null) {
			animatedGrass3.paint(g2);
		}
		
		int x = (int)((aabb.x * MODEL.PIXELS_PER_METER));
		int y = (int)((aabb.y * MODEL.PIXELS_PER_METER));
		g2.drawImage(backgroundGraphImage, x, y, null);
		
//		drawSkidMarks(g2);
		
	}
	
	private void paintScene(Graphics2D g2) {
		
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
		
		backgroundGraphImage = new BufferedImage(
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D backgroundGraphImageG2 = backgroundGraphImage.createGraphics();
		
		backgroundGrassImage = new BufferedImage(
				(int)(getWorldWidth() * MODEL.PIXELS_PER_METER),
				(int)(getWorldHeight() * MODEL.PIXELS_PER_METER),
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D backgroundGrassImageG2 = backgroundGrassImage.createGraphics();
		
		if (!MODEL.DEBUG_DRAW) {
			
			BufferedImage quadrantGrass = new BufferedImage(
					(int)(QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
					(int)(QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = quadrantGrass.createGraphics();
			
			for (int i = 0; i < (QUADRANT_WIDTH * MODEL.PIXELS_PER_METER)/32; i++) {
				for (int j = 0; j < (QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER)/32; j++) {
					g2.drawImage(VIEW.sheet,
							32 * i, 32 * j, 32 * i + 32, 32 * j + 32,
							0, 224, 0+32, 224+32, null);
				}
			} 
			
			for (int i = 0; i < quadrantRows; i++) {
				for (int j = 0; j < quadrantCols; j++) {
					if (quadrants[i][j] == 1) {
						backgroundGrassImageG2.drawImage(quadrantGrass, (int)(j * QUADRANT_WIDTH * MODEL.PIXELS_PER_METER), (int)(i * QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER), null);
					}
				}
			}
			
		} else {
			
			backgroundGrassImageG2.setColor(lightGreen);
			
			for (int i = 0; i < quadrantRows; i++) {
				for (int j = 0; j < quadrantCols; j++) {
					if (quadrants[i][j] == 1) {
						backgroundGrassImageG2.fillRect(
								(int)(j * QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
								(int)(i * QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER),
								(int)(QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
								(int)(QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER));
					}
				}
			}
			
		}
		
		if (MODEL.grid) {
			
			backgroundGrassImageG2.setColor(Color.GRAY);
			backgroundGrassImageG2.setStroke(RegularCursor.solidOutlineStroke);
			
			for (int i = 0; i < quadrantRows; i++) {
				for (int j = 0; j < quadrantCols; j++) {
					if (quadrants[j][i] == 1) {
						for (int k = 0; k <= QUADRANT_HEIGHT; k+=2) {
							backgroundGrassImageG2.drawLine(
									(int)((i * QUADRANT_WIDTH + 0) * MODEL.PIXELS_PER_METER),
									(int)((j * QUADRANT_HEIGHT + k) * MODEL.PIXELS_PER_METER),
									(int)((i * QUADRANT_WIDTH + QUADRANT_WIDTH) * MODEL.PIXELS_PER_METER),
									(int)((j * QUADRANT_HEIGHT + k) * MODEL.PIXELS_PER_METER));
						}
						for (int k = 0; k <= QUADRANT_WIDTH; k+=2) {
							backgroundGrassImageG2.drawLine(
									(int)((i * QUADRANT_WIDTH + k) * MODEL.PIXELS_PER_METER),
									(int)((j * QUADRANT_HEIGHT + 0) * MODEL.PIXELS_PER_METER),
									(int)((i * QUADRANT_WIDTH + k) * MODEL.PIXELS_PER_METER),
									(int)((j * QUADRANT_HEIGHT + QUADRANT_HEIGHT) * MODEL.PIXELS_PER_METER));	
						}
					}
				}
			}
			
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
	
//	private void paintAABB(Graphics2D g2) {
//		
//		g2.setColor(Color.BLACK);
//		g2.drawRect(
//				(int)(aabb.x * MODEL.PIXELS_PER_METER),
//				(int)(aabb.y * MODEL.PIXELS_PER_METER),
//				(int)(aabb.width * MODEL.PIXELS_PER_METER),
//				(int)(aabb.height * MODEL.PIXELS_PER_METER));
//		
//	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
