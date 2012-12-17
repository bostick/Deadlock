package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarEventListener;
import com.gutabi.deadlock.world.graph.Graph;

//@SuppressWarnings("static-access")
public class World {
	
	public QuadrantMap quadrantMap;
	public Graph graph;
	
	public AABB worldViewport;
	
	public int canvasWidth;
	public int canvasHeight;
	
	public double pixelsPerMeter = 32.0;
	
	public double t;
	
//	AnimatedGrassMap
//	AnimatedGrass animatedGrass1;
//	AnimatedGrass animatedGrass2;
//	AnimatedGrass animatedGrass3;
	
	public CarMap carMap;
	
	public ExplosionMap explosionMap = new ExplosionMap();
	
//	private SkidMarkMap skidMarks = new ArrayList<Point>();
	
	public org.jbox2d.dynamics.World b2dWorld;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World() {
		
		graph = new Graph(this);
		
		carMap = new CarMap(this);
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		b2dWorld.setContactListener(new CarEventListener(this));
		
	}
	
	public static World createWorld(int[][] ini) {
		
		World w = new World();
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public void canvasPostDisplay(Dim dim) {
		
		canvasWidth = (int)dim.width;
		canvasHeight = (int)dim.height;
		
		quadrantMap.canvasPostDisplay();
		graph.canvasPostDisplay();
		
		worldViewport = new AABB(
				-(canvasWidth / pixelsPerMeter) / 2 + quadrantMap.worldWidth/2 ,
				-(canvasHeight / pixelsPerMeter) / 2 + quadrantMap.worldHeight/2,
				canvasWidth / pixelsPerMeter,
				canvasHeight / pixelsPerMeter);
	}
	
	public void preStart() {
		
//		renderSkidMarksFresh();
//		renderSkidMarksIncremental();
//		skidMarks = new ArrayList<Point>();
		
//		if (animatedGrass1 != null) {
//			animatedGrass1.preStart();
//		}
//		if (animatedGrass2 != null) {
//			animatedGrass2.preStart();
//		}
//		if (animatedGrass3 != null) {
//			animatedGrass3.preStart();
//		}
		
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
		
//		if (animatedGrass1 != null) {
//			animatedGrass1.preStep(t);
//		}
//		if (animatedGrass2 != null) {
//			animatedGrass2.preStep(t);
//		}
//		if (animatedGrass3 != null) {
//			animatedGrass3.preStep(t);
//		}
		
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
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("version 1\n");
		
		s.append("start world\n");
		
		s.append(quadrantMap.toFileString());
		s.append(graph.toFileString());
		
		s.append("end world\n");
		
		return s.toString();
	}
	
	public static World fromFileString(String s) {
		BufferedReader r = new BufferedReader(new StringReader(s));
		
		StringBuilder quadrantMapStringBuilder = null;
		StringBuilder graphStringBuilder = null;
		
		try {
			String l = r.readLine();
			assert l.equals("version 1");
			
			l = r.readLine();
			assert l.equals("start world");
			
			quadrantMapStringBuilder = new StringBuilder();
			
			while (true) {
				l = r.readLine();
				quadrantMapStringBuilder.append(l+"\n");
				if (l.equals("end quadrantMap")) {
					break;
				}
			}
			
			graphStringBuilder = new StringBuilder();
			
			while (true) {
				l = r.readLine();
				graphStringBuilder.append(l+"\n");
				if (l.equals("end graph")) {
					break;
				}
			}
			
			l = r.readLine();
			assert l.equals("end world");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		QuadrantMap qm = QuadrantMap.fromFileString(quadrantMapStringBuilder.toString());
		Graph g = Graph.fromFileString(graphStringBuilder.toString());
		
		World w = new World();
		w.quadrantMap = qm;
		w.graph = g;
		return w;
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
			quadrantMap.renderCanvas();
			graph.renderCanvas();
		}
		
	}
	
	public void paintWorld(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			quadrantMap.paint(ctxt);
			
//			if (animatedGrass1 != null) {
//				animatedGrass1.paint(ctxt);
//			}
//			if (animatedGrass2 != null) {
//				animatedGrass2.paint(ctxt);
//			}
//			if (animatedGrass3 != null) {
//				animatedGrass3.paint(ctxt);
//			}
			
			graph.paint(ctxt);
//			paintSkidmarks(ctxt);
			
			graph.paintScene(ctxt);
			
			synchronized (APP) {
				carMap.paint(ctxt);
				explosionMap.paint(ctxt);
			}
			
			if (APP.DEBUG_DRAW) {
				
				graph.paintIDs(ctxt);
				
			}
			
			break;
		case PREVIEW:
			quadrantMap.paint(ctxt);
			graph.paint(ctxt);
			break;
		}
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.paintString(0, 0, 1, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1, "car count: " + carMap.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1, "splosions count: " + explosionMap.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
