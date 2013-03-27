package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Graph;
import com.gutabi.deadlock.world.graph.Intersection;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourStud;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.physics.PhysicsWorld;
import com.gutabi.deadlock.world.sprites.AnimatedExplosion;

public class World extends PhysicsWorld {
	
	public DebuggerScreen debuggerScreen;
	
	private Image background;
	Image previewImage;
	
	public QuadrantMap quadrantMap;
	public Graph graph;
	
	public double t;
	
	public CarMap carMap;
	
	public ExplosionMap explosionMap = new ExplosionMap();
	
	public RoadMarkMap roadMarkMap;
	public GrassMarkMap grassMarkMap;
	
	public World(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		super(worldScreen);
		
		this.debuggerScreen = debuggerScreen;
		
		graph = new Graph(this);
		
		carMap = new CarMap(this);
		
		roadMarkMap = new RoadMarkMap();
		grassMarkMap = new GrassMarkMap();
		
	}
	
	public static World createWorld(WorldScreen worldScreen, DebuggerScreen debuggerScreen, int[][] ini) {
		
		World w = new World(worldScreen, debuggerScreen);
		
		QuadrantMap qm = new QuadrantMap(w, ini);
		
		w.quadrantMap = qm;
		
		return w;
	}
	
	public void panelPostDisplay() {
		
		background = APP.platform.createImage(
				(int)worldScreen.contentPane.worldPanel.aabb.width,
				(int)worldScreen.contentPane.worldPanel.aabb.height);
		
		quadrantMap.panelPostDisplay();
		
		worldScreen.worldViewport = new AABB( 
				-(worldScreen.contentPane.worldPanel.aabb.width / worldScreen.pixelsPerMeter) / 2 + quadrantMap.worldWidth/2 ,
				-(worldScreen.contentPane.worldPanel.aabb.height / worldScreen.pixelsPerMeter) / 2 + quadrantMap.worldHeight/2,
				worldScreen.contentPane.worldPanel.aabb.width / worldScreen.pixelsPerMeter,
				worldScreen.contentPane.worldPanel.aabb.height / worldScreen.pixelsPerMeter);
		
		worldScreen.origWorldViewport = worldScreen.worldViewport;
		
	}
	
	public void previewPostDisplay() {
		
		previewImage = APP.platform.createImage(
				(int)debuggerScreen.contentPane.controlPanel.previewAABB.width,
				(int)debuggerScreen.contentPane.controlPanel.previewAABB.height);
		
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
	
	public void integrate(double t) {
		
		String.class.getName();
		
		synchronized (b2dWorld) {
			
			this.t = t;
			
			preStep();
			
			step();
			
			postStep();
			
		}
		
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
	
	public void carCrash(Point p) {
		explosionMap.add(new AnimatedExplosion(this, p));
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
	
	public Set<Vertex> addIntersection(Intersection i) {
		
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
		
		Merger m = Merger.createMergerAndFixtures(this, debuggerScreen.contentPane.controlPanel, p);
		
		quadrantMap.grassMap.mowGrass(m.getShape());
		quadrantMap.grassMap.mowGrass(m.top.getShape());
		quadrantMap.grassMap.mowGrass(m.left.getShape());
		quadrantMap.grassMap.mowGrass(m.right.getShape());
		quadrantMap.grassMap.mowGrass(m.bottom.getShape());
		
		return graph.insertMergerTop(m);
	}
	
	public RushHourBoard createRushHourBoard(Point p, char[][] boardIni) {
		
		RushHourBoard b = new RushHourBoard(this, p, boardIni);
		
		for (RushHourStud stud : b.studs) {
			quadrantMap.grassMap.mowGrass(stud.aabb);
		}
		
		graph.insertRushHourBoardTop(b);
		
		return b;
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
	
	public static World fromFileString(WorldScreen screen, DebuggerScreen debuggerScreen, String s) {
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
		
		World w = new World(screen, debuggerScreen);
		
		QuadrantMap qm = QuadrantMap.fromFileString(w, quadrantMapStringBuilder.toString());
		Graph g = Graph.fromFileString(w, debuggerScreen.contentPane.controlPanel, graphStringBuilder.toString());
		
		w.quadrantMap = qm;
		w.graph = g;
		
		return w;
	}
	
	public void zoomRelative(double factor) {
		
		worldScreen.pixelsPerMeter = factor * worldScreen.pixelsPerMeter; 
		
		double newWidth =  worldScreen.contentPane.worldPanel.aabb.width / worldScreen.pixelsPerMeter;
		double newHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.pixelsPerMeter;
		
		worldScreen.worldViewport = new AABB(
				worldScreen.worldViewport.center.x - newWidth/2,
				worldScreen.worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public void zoomAbsolute(double factor) {
		
		worldScreen.pixelsPerMeter = factor * worldScreen.origPixelsPerMeter; 
		
		double newWidth =  worldScreen.contentPane.worldPanel.aabb.width / worldScreen.pixelsPerMeter;
		double newHeight = worldScreen.contentPane.worldPanel.aabb.height / worldScreen.pixelsPerMeter;
		
		worldScreen.worldViewport = new AABB(
				worldScreen.worldViewport.center.x - newWidth/2,
				worldScreen.worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public void previewPan(Point prevDp) {
		Point worldDP = debuggerScreen.contentPane.controlPanel.previewToWorld(prevDp);
		
		worldScreen.worldViewport = new AABB( 
				worldScreen.worldViewport.x + worldDP.x,
				worldScreen.worldViewport.y + worldDP.y,
				worldScreen.worldViewport.width,
				worldScreen.worldViewport.height);
	}
	
	
	public Point lastPressedWorldPoint;
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	
	public void pressed(InputEvent ev) {
		
		lastPressedWorldPoint = ev.p;
		lastDraggedWorldPoint = null;
		
	}
	
	public void dragged(InputEvent ev) {
		
		lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
		lastDraggedWorldPoint = ev.p;
		lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
		
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedWorldPoint = ev.p;
		lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
		
	}
	
	public void clicked(InputEvent ev) {
		
	}
	
	
	public void render_worldPanel() {
		
		RenderingContext ctxt = APP.platform.createRenderingContext(background);
		
		ctxt.setColor(Color.LIGHT_GRAY);
		ctxt.fillRect(0, 0, (int)worldScreen.contentPane.worldPanel.aabb.width, (int)worldScreen.contentPane.worldPanel.aabb.height);
		
		ctxt.scale(worldScreen.pixelsPerMeter);
		ctxt.translate(-worldScreen.worldViewport.x, -worldScreen.worldViewport.y);
		
		quadrantMap.render_panel(ctxt);
		graph.render_panel(ctxt);
		
		ctxt.dispose();
	}
	
	public void render_preview() {
		
		RenderingContext ctxt = APP.platform.createRenderingContext(previewImage);
		
		boolean oldDebug = APP.DEBUG_DRAW;
		APP.DEBUG_DRAW = false; 
		
		ctxt.setColor(Color.LIGHT_GRAY);
		ctxt.fillRect(
				0, 0, (int)debuggerScreen.contentPane.controlPanel.previewAABB.width, (int)debuggerScreen.contentPane.controlPanel.previewAABB.height);
		
		Transform origTrans = ctxt.getTransform();
		ctxt.translate(
				debuggerScreen.contentPane.controlPanel.previewAABB.width/2 - (debuggerScreen.contentPane.controlPanel.previewPixelsPerMeter * worldScreen.world.quadrantMap.worldWidth / 2),
				debuggerScreen.contentPane.controlPanel.previewAABB.height/2 - (debuggerScreen.contentPane.controlPanel.previewPixelsPerMeter * worldScreen.world.quadrantMap.worldHeight / 2));
		
		ctxt.scale(debuggerScreen.contentPane.controlPanel.previewPixelsPerMeter);
		
		worldScreen.world.quadrantMap.render_preview(ctxt);
		
		worldScreen.world.graph.render_preview(ctxt);
		
		ctxt.setTransform(origTrans);
		
		APP.DEBUG_DRAW = oldDebug;
		
		ctxt.dispose();
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		Transform origTrans = ctxt.getTransform();
		
		ctxt.paintImage(
				background,
				worldScreen.origPixelsPerMeter,
				worldScreen.origWorldViewport.x,
				worldScreen.origWorldViewport.y,
				worldScreen.origWorldViewport.brX,
				worldScreen.origWorldViewport.brY,
				0, 0, background.getWidth(), background.getHeight());
		
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
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.paintString(0, 0, 1.0/worldScreen.pixelsPerMeter, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/worldScreen.pixelsPerMeter, "body count: " + getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/worldScreen.pixelsPerMeter, "car count: " + carMap.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1.0/worldScreen.pixelsPerMeter, "splosions count: " + explosionMap.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public boolean checkConsistency() {
		return graph.checkConsistency();
	}
	
}
