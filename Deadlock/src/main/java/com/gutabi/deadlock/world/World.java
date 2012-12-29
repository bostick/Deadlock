package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

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
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarEventListener;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.Intersection;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.Vertex;

public class World {
	
	public WorldScreen screen;
	
	private BufferedImage background;
	private BufferedImage previewImage;
	
	AABB previewAABB = new AABB(0, 0, 100, 100);
	
	public QuadrantMap quadrantMap;
	public Graph graph;
	
	public double t;
	
	public CarMap carMap;
	
	public ExplosionMap explosionMap = new ExplosionMap();
	
	public RoadMarkMap roadMarkMap;
	public GrassMarkMap grassMarkMap;
	
	public org.jbox2d.dynamics.World b2dWorld;
	
//	private static Logger logger = Logger.getLogger(World.class);
	
	public World(WorldScreen screen) {
		this.screen = screen;
		
		graph = new Graph(this);
		
		carMap = new CarMap(this);
		
		roadMarkMap = new RoadMarkMap();
		grassMarkMap = new GrassMarkMap();
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		b2dWorld.setContactListener(new CarEventListener(this));
	}
	
	public static World createWorld(WorldScreen screen, int[][] ini) {
		
		World w = new World(screen);
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public void canvasPostDisplay(Dim dim) {
		
		screen.cam.canvasWidth = (int)dim.width;
		screen.cam.canvasHeight = (int)dim.height;
		
		screen.previewWidth = 100;
		screen.previewHeight = 100;
		
		background = new BufferedImage(screen.cam.canvasWidth, screen.cam.canvasHeight, BufferedImage.TYPE_INT_RGB);
		previewImage = new BufferedImage(screen.previewWidth, screen.previewHeight, BufferedImage.TYPE_INT_RGB);
		
		quadrantMap.canvasPostDisplay();
		
		screen.cam.worldViewport = new AABB(
				-(screen.cam.canvasWidth / screen.cam.pixelsPerMeter) / 2 + quadrantMap.worldWidth/2 ,
				-(screen.cam.canvasHeight / screen.cam.pixelsPerMeter) / 2 + quadrantMap.worldHeight/2,
				screen.cam.canvasWidth / screen.cam.pixelsPerMeter,
				screen.cam.canvasHeight / screen.cam.pixelsPerMeter);
	}
	
	public void setPreviewLocation(double x, double y) {
		previewAABB = new AABB(x, y, previewAABB.width, previewAABB.height);
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
		
		b2dWorld.step((float)screen.DT, velocityIterations, positionIterations);
		
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
	
	public boolean previewHitTest(Point p) {
		if (previewAABB.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public Set<Vertex> addFixture(Fixture f) {
		
		quadrantMap.grassMap.mowGrass(f.getShape());
		
		return graph.addVertexTop(f);
	}
	
	public Set<Vertex> createIntersection(Point p) {
		
		Intersection i = new Intersection(p);
		
		quadrantMap.grassMap.mowGrass(i.getShape());
		
		return graph.addVertexTop(i);
	}
	
	public Intersection splitRoad(RoadPosition pos) {
		
		Intersection i = graph.split(pos);
		
		quadrantMap.grassMap.mowGrass(i.getShape());
		
		return i;
	}
	
	public Set<Vertex> createRoad(Vertex v0, Vertex v1, List<Point> roadPts) {
		
		Road r = new Road(v0, v1, roadPts);
		
		quadrantMap.grassMap.mowGrass(r.getShape());
		
		return graph.createRoadTop(r);
	}
	
	public Set<Vertex> createMerger(Point p) {
		
		Merger m = Merger.createMergerAndFixtures(this, screen.controlPanel, p);
		
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
	
	public static World fromFileString(WorldScreen screen, String s) {
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
		
		World w = new World(screen);
		
		QuadrantMap qm = QuadrantMap.fromFileString(w, quadrantMapStringBuilder.toString());
		Graph g = Graph.fromFileString(w, screen.controlPanel, graphStringBuilder.toString());
		
		w.quadrantMap = qm;
		w.graph = g;
		
		return w;
	}
	
	public void zoom(double factor) {
		
		screen.cam.pixelsPerMeter = factor * screen.cam.pixelsPerMeter; 
		
		double newWidth =  screen.cam.canvasWidth / screen.cam.pixelsPerMeter;
		double newHeight = screen.cam.canvasHeight / screen.cam.pixelsPerMeter;
		
		screen.cam.worldViewport = new AABB(screen.cam.worldViewport.center.x - newWidth/2, screen.cam.worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / screen.cam.pixelsPerMeter + screen.cam.worldViewport.x,
				p.y / screen.cam.pixelsPerMeter + screen.cam.worldViewport.y);
	}
	
	public AABB canvasToWorld(AABB aabb) {
		Point ul = canvasToWorld(aabb.ul);
		Point br = canvasToWorld(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point worldToCanvas(Point p) {
		return new Point(
				(p.x - screen.cam.worldViewport.x) * screen.cam.pixelsPerMeter,
				(p.y - screen.cam.worldViewport.y) * screen.cam.pixelsPerMeter);
	}
	
	public AABB worldToCanvas(AABB aabb) {
		Point ul = worldToCanvas(aabb.ul);
		Point br = worldToCanvas(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point previewToWorld(Point p) {
		
		double pixelsPerMeterWidth = screen.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = screen.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((1/s) * p.x, (1/s) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		
		double pixelsPerMeterWidth = screen.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = screen.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		return new Point((s) * p.x, (s) * p.y);
	}
	
	public void previewPan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		screen.cam.worldViewport = new AABB(
				screen.cam.worldViewport.x + worldDP.x,
				screen.cam.worldViewport.y + worldDP.y,
				screen.cam.worldViewport.width,
				screen.cam.worldViewport.height);
	}
	
	public void render_canvas() {
		
		Graphics2D backgroundG2 = background.createGraphics();
		
		backgroundG2.setColor(Color.DARK_GRAY);
		backgroundG2.fillRect(0, 0, screen.cam.canvasWidth, screen.cam.canvasHeight);
		
		backgroundG2.scale(screen.cam.pixelsPerMeter, screen.cam.pixelsPerMeter);
		backgroundG2.translate(-screen.cam.worldViewport.x, -screen.cam.worldViewport.y);
		
		RenderingContext backgroundCtxt = new RenderingContext();
		backgroundCtxt.g2 = backgroundG2;
		backgroundCtxt.cam = screen.cam;
		
		quadrantMap.render_canvas(backgroundCtxt);
		graph.render_canvas(backgroundCtxt);
		
		backgroundG2.dispose();
		
	}
	
	public void render_preview() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		RenderingContext previewContext = new RenderingContext();
		previewContext.g2 = previewImageG2;
		
		previewImageG2.setColor(Color.LIGHT_GRAY);
		previewImageG2.fillRect(0, 0, screen.previewWidth, screen.previewHeight);
		
		double pixelsPerMeterWidth = screen.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = screen.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		AffineTransform origTrans = previewContext.getTransform();
		previewContext.translate(screen.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2), screen.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
		
		previewImageG2.scale(s, s);
		
		screen.world.quadrantMap.render_preview(previewContext);
		
		screen.world.graph.render_preview(previewContext);
		
		previewContext.setTransform(origTrans);
		
		previewImageG2.dispose();
		
	}
	
	public void paint_canvas(RenderingContext ctxt) {
		
		ctxt.paintImage(
				background,
				0, 0, screen.cam.canvasWidth, screen.cam.canvasHeight,
				0, 0, screen.cam.canvasWidth, screen.cam.canvasHeight);
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.scale(ctxt.cam.pixelsPerMeter);
		ctxt.translate(-ctxt.cam.worldViewport.x, -ctxt.cam.worldViewport.y);
		
		roadMarkMap.paintScene(ctxt);
		grassMarkMap.paintScene(ctxt);
		
		quadrantMap.paintScene(ctxt);
		
		graph.paintScene(ctxt);
		
		synchronized (APP) {
			carMap.paint(ctxt);
			explosionMap.paint(ctxt);
		}
		
		if (ctxt.DEBUG_DRAW) {
			graph.paintIDs(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(previewAABB.x, previewAABB.y);
		
		ctxt.paintImage(previewImage,
				0, 0, (int)previewAABB.width, (int)previewAABB.height,
				0, 0, screen.previewWidth, screen.previewHeight);
		
		Point prevLoc = worldToPreview(screen.cam.worldViewport.ul);
		
		Point prevDim = worldToPreview(new Point(screen.cam.worldViewport.width, screen.cam.worldViewport.height));
		
		AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
		
		double pixelsPerMeterWidth = screen.previewWidth / screen.world.quadrantMap.worldWidth;
		double pixelsPerMeterHeight = screen.previewHeight / screen.world.quadrantMap.worldHeight;
		double s = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
		
		ctxt.translate(
				screen.previewWidth/2 - (s * screen.world.quadrantMap.worldWidth / 2),
				screen.previewHeight/2 - (s * screen.world.quadrantMap.worldHeight / 2));
		
		ctxt.setColor(Color.BLUE);
		prev.draw(ctxt);
		
		ctxt.setTransform(origTrans);
		
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
