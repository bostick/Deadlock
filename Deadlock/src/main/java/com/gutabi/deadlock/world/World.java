package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarEventListener;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.GraphPositionPathFactory;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.sprites.AnimatedExplosion;
import com.gutabi.deadlock.world.sprites.AnimatedGrass;

@SuppressWarnings("static-access")
public class World implements Sweepable {
	
	public static final double QUADRANT_WIDTH = 16.0;
	public static final double QUADRANT_HEIGHT = QUADRANT_WIDTH;
	
	public final double worldWidth;
	public final double worldHeight;
	
	public AABB worldViewport;
	
	public int canvasWidth;
	public int canvasHeight;
	
	public double pixelsPerMeter = 32.0;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
	public BufferedImage quadrantGrass;
	public BufferedImage canvasGrassImage;
	public BufferedImage canvasGraphImage;
	
	public QuadrantMap quadrantMap;
	
	AnimatedGrass animatedGrass1;
	AnimatedGrass animatedGrass2;
	AnimatedGrass animatedGrass3;
	
	public Graph graph;
	
	public CarMap carMap;
	
	public GraphPositionPathFactory pathFactory;
	
	public ExplosionMap explosionMap = new ExplosionMap();
	
//	private List<Point> skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	
	public AABB aabb;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World(int[][] ini) {
		
		assert ini.length > 0;
		assert ini[0].length > 0;
		
		int quadrantRows = ini.length;
		int quadrantCols = ini[0].length;
		
		quadrantMap = new QuadrantMap(this, ini);
		
		worldWidth = quadrantCols * QUADRANT_WIDTH;
		
		worldHeight = quadrantRows * QUADRANT_HEIGHT;
		
		graph = new Graph(this);
		
		carMap = new CarMap(this);
		
		pathFactory = new GraphPositionPathFactory(this);
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		b2dWorld.setContactListener(new CarEventListener(this));
		
		computeAABB();
		
	}
	
	public void init() {
		
		int quadrantWidthPixels = (int)Math.ceil(pixelsPerMeter * QUADRANT_WIDTH);
		int quadrantHeightPixels = (int)Math.ceil(pixelsPerMeter * QUADRANT_HEIGHT);
		
		quadrantGrass = new BufferedImage(quadrantWidthPixels, quadrantHeightPixels, BufferedImage.TYPE_INT_ARGB);
		Graphics2D quadrantGrassG2 = quadrantGrass.createGraphics();
		
		int maxCols = (int)Math.ceil(quadrantWidthPixels/32.0);
		int maxRows = (int)Math.ceil(quadrantHeightPixels/32.0);
		for (int i = 0; i < maxRows; i++) {
			for (int j = 0; j < maxCols; j++) {
				quadrantGrassG2.drawImage(
						VIEW.sheet,
						32 * j, 32 * i, 32 * j + 32, 32 * i + 32,
						0, 224, 0+32, 224+32, null);
			}
		}
		
	}
	
	public void canvasPostDisplay(Dim dim) {
		
		canvasWidth = (int)dim.width;
		canvasHeight = (int)dim.height;
		
		canvasGrassImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
		canvasGraphImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
		
		worldViewport = new AABB(
				-(canvasWidth / pixelsPerMeter) / 2 + worldWidth/2 ,
				-(canvasHeight / pixelsPerMeter) / 2 + worldHeight/2,
				canvasWidth / pixelsPerMeter,
				canvasHeight / pixelsPerMeter);
	}
	
	public void addVertexTop(Vertex v) {
		Set<Vertex> affected = graph.addVertexTop(v);
		
		postIdleTop(affected);
	}
	
	public void createRoadTop(Vertex start, Vertex end, List<Point> pts) {
		Set<Vertex> affected = graph.createRoadTop(start, end, pts);
		
		postDraftingTop(affected);
		
//		assert checkConsistency();
	}
	
	public Vertex splitRoadTop(RoadPosition pos) {
		return graph.split(pos);
	}
	
	public void removeVertexTop(Vertex v) {
		Set<Vertex> affected = graph.removeVertexTop(v);
		
		postIdleTop(affected);
	}
	
	public void removeRoadTop(Road e) {
		Set<Vertex> affected = graph.removeRoadTop(e);
		
		postIdleTop(affected);
	}
	
	public void removeMergerTop(Merger m) {
		Set<Vertex> affected = graph.removeMergerTop(m);
		
		postIdleTop(affected);
	}
	
	public void removeStopSignTop(StopSign s) {
		s.e.removeStopSignTop(s);
	}
	
	public void removeCarTop(Car c) {
		
		c.destroy();
		
		synchronized (APP) {
			carMap.remove(c);
		}
		
		postRunningTop();
	}
	
	public void insertMergerTop(Point p) {
		Set<Vertex> affected = graph.insertMergerTop(p);
		
		postIdleTop(affected);
		
	}
	
	public void addExplosion(AnimatedExplosion x) {
		explosionMap.add(x);
	}
	
	public List<SweepEvent> sweepStart(Circle c) {
		return graph.sweepStart(c);
	}
	
	public List<SweepEvent> sweep(Capsule s) {
		return graph.sweep(s);
	}
	
	private void postIdleTop(Set<Vertex> affected) {
		
		graph.computeVertexRadii(affected);
		
		computeAABB();
	}
	
	public void postDraftingTop(Set<Vertex> affected) {
		
		graph.computeVertexRadii(affected);
		
		computeAABB();
	}
	
	private void postRunningTop() {
		;
	}
	
	private void computeAABB() {
		aabb = quadrantMap.aabb;
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
		
		synchronized (APP) {
			carMap.postStop();
			
			explosionMap.postStop();
			
		}
		
//		skidMarks.clear();
	}
	
	int velocityIterations = 6;
	int positionIterations = 2;
	
	public void integrate(double t) {
		
		this.t = t;
		
		preStep();
		
		b2dWorld.step((float)APP.dt, velocityIterations, positionIterations);
		
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
		
		carMap.preStep(t);
		
		explosionMap.preStep(t);
		
	}
	
	public void addCar(Car c) {
		carMap.addCar(c);
	}
	
	private void postStep() {
		
		synchronized (APP) {
			
			carMap.postStep(t);
			
			explosionMap.postStep(t);
			
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
	
	public Entity hitTest(Point p) {
		Car c;
		synchronized (APP) {
			c = carMap.carHitTest(p);
		}
		if (c != null) {
			return c;
		}
		Entity h = graph.graphHitTest(p);
		if (h != null) {
			return h;
		}
		return null;
	}
	
	public void zoom(double factor) {
		
		pixelsPerMeter = factor * pixelsPerMeter; 
		
		double newWidth =  canvasWidth / pixelsPerMeter;
		double newHeight = canvasHeight / pixelsPerMeter;
		
		worldViewport = new AABB(worldViewport.center.x - newWidth/2, worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / pixelsPerMeter + worldViewport.x,
				p.y / pixelsPerMeter + worldViewport.y);
	}
	
	public AABB canvasToWorld(AABB aabb) {
		Point ul = canvasToWorld(aabb.ul);
		Point br = canvasToWorld(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point worldToCanvas(Point p) {
		return new Point(
				(p.x - worldViewport.x) * pixelsPerMeter,
				(p.y - worldViewport.y) * pixelsPerMeter);
	}
	
	public AABB worldToCanvas(AABB aabb) {
		Point ul = worldToCanvas(aabb.ul);
		Point br = worldToCanvas(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public void renderCanvas() {
		assert !Thread.holdsLock(APP);
		
		synchronized (VIEW) {
			Graphics2D canvasGrassImageG2 = canvasGrassImage.createGraphics();
			
			canvasGrassImageG2.setColor(Color.LIGHT_GRAY);
			canvasGrassImageG2.fillRect(0, 0, canvasWidth, canvasHeight);
			
			canvasGrassImageG2.translate((int)(-worldViewport.x * pixelsPerMeter), (int)(-worldViewport.y * pixelsPerMeter));
			
			canvasGrassImageG2.scale(pixelsPerMeter, pixelsPerMeter);
			
			RenderingContext canvasGrassContext = new RenderingContext(canvasGrassImageG2, RenderingContextType.CANVAS);
			
			quadrantMap.renderBackground(canvasGrassContext);
			
			canvasGrassImageG2.dispose();
		}
		
		synchronized (VIEW) {
			Graphics2D canvasGraphImageG2 = canvasGraphImage.createGraphics();
			
			Composite orig = canvasGraphImageG2.getComposite();
			AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC, 0.0f);
			canvasGraphImageG2.setComposite(c);
			canvasGraphImageG2.setColor(new Color(0, 0, 0, 0));
			canvasGraphImageG2.fillRect(0, 0, canvasWidth, canvasHeight);
			canvasGraphImageG2.setComposite(orig);
			
			canvasGraphImageG2.translate((int)((-worldViewport.x) * pixelsPerMeter), (int)((-worldViewport.y) * pixelsPerMeter));
			
			canvasGraphImageG2.scale(pixelsPerMeter, pixelsPerMeter);
			
			RenderingContext canvasGraphContext = new RenderingContext(canvasGraphImageG2, RenderingContextType.CANVAS);
			
			graph.renderBackground(canvasGraphContext);
			
			canvasGraphImageG2.dispose();
			
		}
	}
	
	public void paintWorld(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			paintGrass(ctxt);
			paintGraph(ctxt);
//			paintSkidmarks(ctxt);
			
			paintScene(ctxt);
			
			if (APP.DEBUG_DRAW) {
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke(1);
				aabb.draw(ctxt);
			}
			
			break;
		case PREVIEW:
			paintGrass(ctxt);
			paintGraph(ctxt);
			break;
		}
		
	}
	
	private void paintGrass(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		ctxt.translate(worldViewport.x, worldViewport.y);
		
		ctxt.paintImage(
				0, 0, 1 / pixelsPerMeter,
				canvasGrassImage,
				0, 0, canvasGrassImage.getWidth(), canvasGrassImage.getHeight(),
				0, 0, canvasGrassImage.getWidth(), canvasGrassImage.getHeight());
		
		ctxt.setTransform(origTransform);
		
		if (animatedGrass1 != null) {
			animatedGrass1.paint(ctxt);
		}
		if (animatedGrass2 != null) {
			animatedGrass2.paint(ctxt);
		}
		if (animatedGrass3 != null) {
			animatedGrass3.paint(ctxt);
		}
		
//		drawSkidMarks(g2);
		
	}
	
	private void paintGraph(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		origTransform = ctxt.getTransform();
		ctxt.translate(worldViewport.x, worldViewport.y);
		
		ctxt.paintImage(
				0, 0, 1 / pixelsPerMeter,
				canvasGraphImage,
				0, 0, canvasGraphImage.getWidth(), canvasGraphImage.getHeight(),
				0, 0, canvasGraphImage.getWidth(), canvasGraphImage.getHeight());
		
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintScene(RenderingContext ctxt) {
		
		graph.paintScene(ctxt);
		
		synchronized (APP) {
			carMap.paint(ctxt);
			explosionMap.paint(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			
			graph.paintIDs(ctxt);
			
		}
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.paintString(0, 0, 1 / pixelsPerMeter, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1 / pixelsPerMeter, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1 / pixelsPerMeter, "car count: " + carMap.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1 / pixelsPerMeter, "splosions count: " + explosionMap.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
