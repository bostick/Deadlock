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

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.InputEvent;
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
	BufferedImage previewImage;
	
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
	
	public void panelPostDisplay() {
		
		background = new BufferedImage((int)screen.contentPane.worldPanel.aabb.width, (int)screen.contentPane.worldPanel.aabb.height, BufferedImage.TYPE_INT_RGB);
		
		quadrantMap.panelPostDisplay();
		
		screen.worldViewport = new AABB(
				-(screen.contentPane.worldPanel.aabb.width / screen.pixelsPerMeter) / 2 + quadrantMap.worldWidth/2 ,
				-(screen.contentPane.worldPanel.aabb.height / screen.pixelsPerMeter) / 2 + quadrantMap.worldHeight/2,
				screen.contentPane.worldPanel.aabb.width / screen.pixelsPerMeter,
				screen.contentPane.worldPanel.aabb.height / screen.pixelsPerMeter);
	}
	
	public void previewPostDisplay() {
		
		previewImage = new BufferedImage(
				(int)screen.contentPane.controlPanel.previewAABB.width,
				(int)screen.contentPane.controlPanel.previewAABB.height, BufferedImage.TYPE_INT_RGB);
		
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
		
		synchronized (APP) {
			
			graph.postStop();
			
			carMap.postStop();
			
			explosionMap.postStop();
			
			roadMarkMap.postStop();
			grassMarkMap.postStop();
		}
		
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
			
			graph.postStep(t);
		}
		
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
		
		Intersection i = new Intersection(this, p);
		
		quadrantMap.grassMap.mowGrass(i.getShape());
		
		return graph.addVertexTop(i);
	}
	
	public Intersection splitRoad(RoadPosition pos) {
		
		Intersection i = graph.split(pos);
		
		quadrantMap.grassMap.mowGrass(i.getShape());
		
		return i;
	}
	
	public Set<Vertex> createRoad(Vertex v0, Vertex v1, List<Point> roadPts) {
		
		Road r = new Road(this, v0, v1, roadPts);
		
		quadrantMap.grassMap.mowGrass(r.getShape());
		
		return graph.createRoadTop(r);
	}
	
	public Set<Vertex> createMerger(Point p) {
		
		Merger m = Merger.createMergerAndFixtures(this, screen.contentPane.controlPanel, p);
		
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
		Graph g = Graph.fromFileString(w, screen.contentPane.controlPanel, graphStringBuilder.toString());
		
		w.quadrantMap = qm;
		w.graph = g;
		
		return w;
	}
	
	public void zoom(double factor) {
		
		screen.pixelsPerMeter = factor * screen.pixelsPerMeter; 
		
		double newWidth =  screen.contentPane.worldPanel.aabb.width / screen.pixelsPerMeter;
		double newHeight = screen.contentPane.worldPanel.aabb.height / screen.pixelsPerMeter;
		
		screen.worldViewport = new AABB(screen.worldViewport.center.x - newWidth/2, screen.worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public void previewPan(Point prevDp) {
		Point worldDP = screen.contentPane.controlPanel.previewToWorld(prevDp);
		
		screen.worldViewport = new AABB(
				screen.worldViewport.x + worldDP.x,
				screen.worldViewport.y + worldDP.y,
				screen.worldViewport.width,
				screen.worldViewport.height);
	}
	
	
	public Point lastPressedWorldPoint;
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
//	public Point lastClickedWorldPoint;
	
	
	public void pressed(InputEvent ev) {
		
		lastPressedWorldPoint = ev.p;
		lastDraggedWorldPoint = null;
		
//		Car c = carMap.carHitTest(lastPressedWorldPoint);
//		if (c != null) {
//			carMap.pressedCar = c;
//		}
		
		
		
	}
	
	public void dragged(InputEvent ev) {
		
		lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
		lastDraggedWorldPoint = ev.p;
		lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
		
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedWorldPoint = ev.p;
		lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
//		lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
		
	}
	
	public void clicked(InputEvent ev) {
		
//		lastClickedWorldPoint = ev.p;
		
//		line
	}
	
	
	
	public void render_worldPanel() {
		
		Graphics2D backgroundG2 = background.createGraphics();
		
		backgroundG2.setColor(Color.LIGHT_GRAY);
		backgroundG2.fillRect(0, 0, (int)screen.contentPane.worldPanel.aabb.width, (int)screen.contentPane.worldPanel.aabb.height);
		
		backgroundG2.scale(screen.pixelsPerMeter, screen.pixelsPerMeter);
		backgroundG2.translate(-screen.worldViewport.x, -screen.worldViewport.y);
		
		RenderingContext backgroundCtxt = new RenderingContext(backgroundG2);
//		backgroundCtxt.screen = screen;
		
		quadrantMap.render_panel(backgroundCtxt);
		graph.render_panel(backgroundCtxt);
		
		backgroundG2.dispose();
	}
	
	public void render_preview() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		boolean oldDebug = APP.DEBUG_DRAW;
		APP.DEBUG_DRAW = false;
		
		RenderingContext ctxt = new RenderingContext(previewImageG2); 
		
		previewImageG2.setColor(Color.LIGHT_GRAY);
		previewImageG2.fillRect(
				0, 0, (int)screen.contentPane.controlPanel.previewAABB.width, (int)screen.contentPane.controlPanel.previewAABB.height);
		
		AffineTransform origTrans = ctxt.getTransform();
		ctxt.translate(
				screen.contentPane.controlPanel.previewAABB.width/2 - (screen.contentPane.controlPanel.previewPixelsPerMeter * screen.world.quadrantMap.worldWidth / 2),
				screen.contentPane.controlPanel.previewAABB.height/2 - (screen.contentPane.controlPanel.previewPixelsPerMeter * screen.world.quadrantMap.worldHeight / 2));
		
		previewImageG2.scale(screen.contentPane.controlPanel.previewPixelsPerMeter, screen.contentPane.controlPanel.previewPixelsPerMeter);
		
		screen.world.quadrantMap.render_preview(ctxt);
		
		screen.world.graph.render_preview(ctxt);
		
		ctxt.setTransform(origTrans);
		
		APP.DEBUG_DRAW = oldDebug;
		
		previewImageG2.dispose();
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.paintImage(
				background,
				0, 0, (int)screen.contentPane.worldPanel.aabb.width, (int)screen.contentPane.worldPanel.aabb.height,
				0, 0, background.getWidth(), background.getHeight());
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.scale(screen.pixelsPerMeter);
		ctxt.translate(-screen.worldViewport.x, -screen.worldViewport.y);
		
		synchronized (APP) {
			
			roadMarkMap.paintScene(ctxt);
			grassMarkMap.paintScene(ctxt);
			
			quadrantMap.paintScene(ctxt);
			
			graph.paintScene(ctxt);
			
			carMap.paint(ctxt);
			explosionMap.paint(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			graph.paintIDs(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.paintString(0, 0, 1.0/screen.pixelsPerMeter, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/screen.pixelsPerMeter, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/screen.pixelsPerMeter, "car count: " + carMap.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/screen.pixelsPerMeter, "splosions count: " + explosionMap.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
