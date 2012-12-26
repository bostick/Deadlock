package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.ControlPanel;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarEventListener;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.Intersection;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class World {
	
	WorldCamera cam;
	ControlPanel cp;
	
	public QuadrantMap quadrantMap;
	public Graph graph;
	
	public double t;
	
	private BufferedImage background;
	
	public CarMap carMap;
	
	public ExplosionMap explosionMap = new ExplosionMap();
	
	public RoadMarkMap roadMarkMap;
	public GrassMarkMap grassMarkMap;
	
	public org.jbox2d.dynamics.World b2dWorld;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World(WorldCamera cam, ControlPanel cp) {
		this.cam = cam;
		this.cp = cp;
		
		graph = new Graph(cam, this);
		
		carMap = new CarMap(cam, this);
		
		roadMarkMap = new RoadMarkMap(cam);
		grassMarkMap = new GrassMarkMap(cam);
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		b2dWorld.setContactListener(new CarEventListener(cam, this));
	}
	
	public static World createWorld(WorldCamera cam, ControlPanel cp, int[][] ini) {
		
		World w = new World(cam, cp);
		
		QuadrantMap qm = new QuadrantMap(cam, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public void canvasPostDisplay(Dim dim) {
		
		cam.canvasWidth = (int)dim.width;
		cam.canvasHeight = (int)dim.height;
		
		quadrantMap.canvasPostDisplay();
		
		background = new BufferedImage(cam.canvasWidth, cam.canvasHeight, BufferedImage.TYPE_INT_RGB);
		
		cam.worldViewport = new AABB(
				-(cam.canvasWidth / cam.pixelsPerMeter) / 2 + quadrantMap.worldWidth/2 ,
				-(cam.canvasHeight / cam.pixelsPerMeter) / 2 + quadrantMap.worldHeight/2,
				cam.canvasWidth / cam.pixelsPerMeter,
				cam.canvasHeight / cam.pixelsPerMeter);
	}
	
	public void preStart() {
		
		quadrantMap.preStart();
		
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
		
		roadMarkMap.postStop();
		grassMarkMap.postStop();
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
		
		quadrantMap.preStep(t);
		
		graph.preStep(t);
		
		carMap.preStep(t);
		
		explosionMap.preStep(t);
		
	}
	
	private void postStep() {
		
		synchronized (APP) {
			
			carMap.postStep(t);
			
			explosionMap.postStep(t);
			
		}
		
		graph.postStep(t);
		
	}
	
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
	
	public Set<Vertex> addFixture(Fixture f) {
		
		quadrantMap.grassMap.mowGrass(f.getShape());
		
		return graph.addVertexTop(f);
	}
	
	public Set<Vertex> createIntersection(Point p) {
		
		Intersection i = new Intersection(cam, p);
		
		quadrantMap.grassMap.mowGrass(i.getShape());
		
		return graph.addVertexTop(i);
	}
	
	public Intersection splitRoad(RoadPosition pos) {
		
		Intersection i = graph.split(pos);
		
		quadrantMap.grassMap.mowGrass(i.getShape());
		
		return i;
	}
	
	public Set<Vertex> createRoad(Vertex v0, Vertex v1, List<Point> roadPts) {
		
		Road r = new Road(cam, v0, v1, roadPts);
		
		quadrantMap.grassMap.mowGrass(r.getShape());
		
		return graph.createRoadTop(r);
	}
	
	public Set<Vertex> createMerger(Point p) {
		
//		Merger m = graph.createMergerAndFixtures(p);
		Merger m = Merger.createMergerAndFixtures(cam, this, cp, p);
		
		quadrantMap.grassMap.mowGrass(m.getShape());
		quadrantMap.grassMap.mowGrass(m.top.getShape());
		quadrantMap.grassMap.mowGrass(m.left.getShape());
		quadrantMap.grassMap.mowGrass(m.right.getShape());
		quadrantMap.grassMap.mowGrass(m.bottom.getShape());
		
		return graph.insertMergerTop(m);
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
	
	public static World fromFileString(WorldCamera cam, ControlPanel cp, String s) {
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
		
		World w = new World(cam, cp);
		
		QuadrantMap qm = QuadrantMap.fromFileString(cam, quadrantMapStringBuilder.toString());
		Graph g = Graph.fromFileString(cam, w, cp, graphStringBuilder.toString());
		
		w.quadrantMap = qm;
		w.graph = g;
		
		return w;
	}
	
	public void zoom(double factor) {
		
		cam.pixelsPerMeter = factor * cam.pixelsPerMeter; 
		
		double newWidth =  cam.canvasWidth / cam.pixelsPerMeter;
		double newHeight = cam.canvasHeight / cam.pixelsPerMeter;
		
		cam.worldViewport = new AABB(cam.worldViewport.center.x - newWidth/2, cam.worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / cam.pixelsPerMeter + cam.worldViewport.x,
				p.y / cam.pixelsPerMeter + cam.worldViewport.y);
	}
	
	public AABB canvasToWorld(AABB aabb) {
		Point ul = canvasToWorld(aabb.ul);
		Point br = canvasToWorld(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point worldToCanvas(Point p) {
		return new Point(
				(p.x - cam.worldViewport.x) * cam.pixelsPerMeter,
				(p.y - cam.worldViewport.y) * cam.pixelsPerMeter);
	}
	
	public AABB worldToCanvas(AABB aabb) {
		Point ul = worldToCanvas(aabb.ul);
		Point br = worldToCanvas(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public void renderCanvas() {
		assert !Thread.holdsLock(APP);
		
		synchronized (VIEW) {
			
			Graphics2D backgroundG2 = background.createGraphics();
			
			backgroundG2.setColor(Color.DARK_GRAY);
			backgroundG2.fillRect(0, 0, cam.canvasWidth, cam.canvasHeight);
			
			backgroundG2.scale(cam.pixelsPerMeter, cam.pixelsPerMeter);
			backgroundG2.translate(-cam.worldViewport.x, -cam.worldViewport.y);
			
			RenderingContext backgroundCtxt = new RenderingContext(RenderingContextType.CANVAS);
			backgroundCtxt.g2 = backgroundG2;
			
			quadrantMap.render(backgroundCtxt);
			graph.render(backgroundCtxt);
			
			backgroundG2.dispose();
			
		}
		
	}
	
	public void paintWorldBackground(RenderingContext ctxt) {
		
//		synchronized (VIEW) {
			ctxt.paintImage(
					background,
					0, 0, cam.canvasWidth, cam.canvasHeight,
					0, 0, cam.canvasWidth, cam.canvasHeight);
//		}
		
	}
	
	public void paintWorldScene(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			
			roadMarkMap.paintScene(ctxt);
			grassMarkMap.paintScene(ctxt);
			
			quadrantMap.paintScene(ctxt);
			
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
			break;
		}
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.paintString(cam.pixelsPerMeter, 0, 0, 1, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(cam.pixelsPerMeter, 0, 0, 1, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(cam.pixelsPerMeter, 0, 0, 1, "car count: " + carMap.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(cam.pixelsPerMeter, 0, 0, 1, "splosions count: " + explosionMap.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
