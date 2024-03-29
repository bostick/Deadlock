package com.brentonbostick.capsloc.world;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brentonbostick.capsloc.Entity;
import com.brentonbostick.capsloc.SimulationRunnable;
import com.brentonbostick.capsloc.geom.MutableAABB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.InputEvent;
import com.brentonbostick.capsloc.ui.Shimmer;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.graph.BypassBoard;
import com.brentonbostick.capsloc.world.graph.BypassStud;
import com.brentonbostick.capsloc.world.graph.Fixture;
import com.brentonbostick.capsloc.world.graph.Graph;
import com.brentonbostick.capsloc.world.graph.Intersection;
import com.brentonbostick.capsloc.world.graph.Merger;
import com.brentonbostick.capsloc.world.graph.Road;
import com.brentonbostick.capsloc.world.graph.RoadPosition;
import com.brentonbostick.capsloc.world.graph.Vertex;
import com.brentonbostick.capsloc.world.physics.PhysicsWorld;

public class World extends PhysicsWorld {
	
	public WorldCamera worldCamera;
	
	public AtomicBoolean simThreadTrigger = new AtomicBoolean();
	public Thread simThread;
	
	public WorldBackground background;
//	public Image previewImage;
	
	public QuadrantMap quadrantMap;
	public Graph graph;
	
	public CarMap carMap;
	
//	public ExplosionMap explosionMap = new ExplosionMap();
	
//	public RoadMarkMap roadMarkMap;
//	public GrassMarkMap grassMarkMap;
	
	public Stats stats;
	
	Shimmer shimmer;
	
	public World() {
		
		background = new WorldBackground(this);
		
		graph = new Graph(this);
		
		carMap = new CarMap(this);
		
//		roadMarkMap = new RoadMarkMap();
//		grassMarkMap = new GrassMarkMap();
		
		worldCamera = new WorldCamera();
		
		stats = new Stats(this);
		
	}
	
//	public void previewPostDisplay() {
//		
//		previewImage = APP.platform.createImage(
//				(int)worldCamera.previewAABB.width,
//				(int)worldCamera.previewAABB.height);
//		
//	}
	
	public void preStart() {
		
		quadrantMap.preStart();
		
		graph.preStart();
		
		t = 0;
		
		shimmer = new Shimmer(System.currentTimeMillis());
	}
	
	/*
	 * run after game loop stops
	 */
	public void postStop() {
		
		graph.postStop();
		
		carMap.postStop();
		
//		explosionMap.postStop();
		
//		roadMarkMap.postStop();
//		grassMarkMap.postStop();
		
	}
	
	public boolean integrate(double t) {
				
		this.t = t;
		
		boolean res = false;
		
		res = res | preStep();
		
		res = res | step();
		
		postStep();
		
		return res;
	}
	
	private boolean preStep() {
		
		boolean res = false;
		
		res = res | graph.preStep(t);
		
		res = res | carMap.preStep(t);
		
//		explosionMap.preStep(t);
		
		return res;
	}
	
	public boolean step() {
		
		super.step();
		
		boolean res = false;
		
		res = res | quadrantMap.step(t);
		res = res | shimmer.step();
		
		return res;
	}
	
	private void postStep() {
		
		carMap.postStep(t);
		
//		explosionMap.postStep(t);
		
		graph.postStep(t);
		
	}
	
	public void carCrash(Point p) {
		
//		explosionMap.add(new AnimatedExplosion(p));
		
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
		
		quadrantMap.mowGrass(f.shape);
		
		return graph.addVertexTop(f);
	}
	
	public Set<Vertex> addIntersection(Intersection i) {
		
		quadrantMap.mowGrass(i.shape);
		
		return graph.addVertexTop(i);
	}
	
	public Intersection splitRoad(RoadPosition pos) {
		
		Intersection i = graph.split(pos);
		
		quadrantMap.mowGrass(i.shape);
		
		return i;
	}
	
	public Set<Vertex> createRoad(Vertex v0, Vertex v1, List<Point> roadPts) {
		
		Road r = new Road(this, v0, v1, roadPts);
		
		quadrantMap.mowGrass(r.shape);
		
		return graph.createRoadTop(r);
	}
	
	public Set<Vertex> createMerger(Point p) {
		
		Merger m = Merger.createMergerAndFixtures(this, p);
		
		quadrantMap.mowGrass(m.shape);
		quadrantMap.mowGrass(m.top.shape);
		quadrantMap.mowGrass(m.left.shape);
		quadrantMap.mowGrass(m.right.shape);
		quadrantMap.mowGrass(m.bottom.shape);
		
		return graph.insertMergerTop(m);
	}
	
	public BypassBoard createBypassBoard(Point p, char[][] boardIni) {
		
		BypassBoard b = new BypassBoard(this, p, boardIni);
		
		for (BypassStud stud : b.studs) {
			quadrantMap.mowGrass(stud.aabb);
		}
		
		graph.insertBypassBoardTop(b);
		
		return b;
	}
	
	public void startRunning() {
		
		simThreadTrigger.set(true);
		
		preStart();
		
		simThread = new Thread(new SimulationRunnable(simThreadTrigger));
		simThread.start();
		
	}
	
	public void stopRunning() {
		
		simThreadTrigger.set(false);
		
		try {
			simThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		postStop();
		
	}
	
	public void pauseRunning() {
		
		simThreadTrigger.set(false);
		
		try {
			simThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unpauseRunning() {
		
		simThreadTrigger.set(true);
		
		simThread = new Thread(new SimulationRunnable(simThreadTrigger));
		simThread.start();
	}
	
//	public String toFileString() {
//		StringBuilder s = new StringBuilder();
//		
//		s.append("version 1\n");
//		
//		s.append("start world\n");
//		
//		s.append(quadrantMap.toFileString());
//		s.append(graph.toFileString());
//		
//		s.append("end world\n");
//		
//		return s.toString();
//	}
	
//	public static World fromFileString(String s) {
//		BufferedReader r = new BufferedReader(new StringReader(s));
//		
//		StringBuilder quadrantMapStringBuilder = null;
//		StringBuilder graphStringBuilder = null;
//		
//		try {
//			String l = r.readLine();
//			assert l.equals("version 1");
//			
//			l = r.readLine();
//			assert l.equals("start world");
//			
//			quadrantMapStringBuilder = new StringBuilder();
//			
//			while (true) {
//				l = r.readLine();
//				quadrantMapStringBuilder.append(l+"\n");
//				if (l.equals("end quadrantMap")) {
//					break;
//				}
//			}
//			
//			graphStringBuilder = new StringBuilder();
//			
//			while (true) {
//				l = r.readLine();
//				graphStringBuilder.append(l+"\n");
//				if (l.equals("end graph")) {
//					break;
//				}
//			}
//			
//			l = r.readLine();
//			assert l.equals("end world");
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		World w = new World();
//		
//		QuadrantMap qm = QuadrantMap.fromFileString(quadrantMapStringBuilder.toString());
//		Graph g = Graph.fromFileString(w, graphStringBuilder.toString());
//		
//		w.quadrantMap = qm;
//		w.graph = g;
//		
//		return w;
//	}
	
	
	public Point lastPressedWorldPoint;
	public Point lastDraggedWorldPoint;
//	public Point lastReleasedWorldPoint;
	
	public Point penDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	public Point lastPressPreviewPoint;
	public Point lastDragPreviewPoint;
	public Point penDragPreviewPoint;
	
	
	public void pressed(InputEvent ev) {
		
		lastPressedWorldPoint = ev.p;
		lastDraggedWorldPoint = null;
		
	}
	
	public void released(InputEvent ev) {
		
	}
	
	public void canceled(InputEvent ev) {
		
	}
	
	public void dragged(InputEvent ev) {
		
		lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
		penDraggedWorldPoint = lastDraggedWorldPoint;
		lastDraggedWorldPoint = ev.p;
		lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
		
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedWorldPoint = ev.p;
		lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
		
	}
	
	public void clicked(InputEvent ev) {
		
	}
	
	public void postDisplay(int width, int height) {
		
		worldCamera.worldViewport.setShape(
				-(width / worldCamera.pixelsPerMeter) / 2 + quadrantMap.worldAABB.width/2 ,
				-(height / worldCamera.pixelsPerMeter) / 2 + quadrantMap.worldAABB.height/2,
				width / worldCamera.pixelsPerMeter,
				height / worldCamera.pixelsPerMeter);
		
		worldCamera.origWorldViewport = worldCamera.worldViewport.copy();
		
		background.panelPostDisplay(width, height);
		
	}

	public void render() {
		
		background.render();
	}
	
	
//	RenderingContext ctxt = APP.platform.createRenderingContext();
//	
//	public void render_preview() {
//		
//		APP.platform.setRenderingContextFields1(ctxt, previewImage);
//		ctxt.cam = worldCamera;
//		
//		boolean oldDebug = APP.DEBUG_DRAW;
//		APP.DEBUG_DRAW = false; 
//		
//		ctxt.pushTransform();
//		
//		ctxt.translate(
//				worldCamera.previewAABB.width/2 - (worldCamera.previewPixelsPerMeter * quadrantMap.worldAABB.width / 2),
//				worldCamera.previewAABB.height/2 - (worldCamera.previewPixelsPerMeter * quadrantMap.worldAABB.height / 2));
//		
//		ctxt.scale(worldCamera.previewPixelsPerMeter);
//		
//		quadrantMap.paint_preview(ctxt);
//		
//		graph.paint_preview(ctxt);
//		
//		ctxt.popTransform();
//		
//		APP.DEBUG_DRAW = oldDebug;
//		
//		ctxt.dispose();
//	}
	
	public void paint(RenderingContext ctxt) {
		
		background.paint_pixels(ctxt);
		
		ctxt.pushTransform();
		
		ctxt.scale(worldCamera.pixelsPerMeter, worldCamera.pixelsPerMeter);
		ctxt.translate(-worldCamera.worldViewport.x, -worldCamera.worldViewport.y);
		
		background.paint_worldCoords(ctxt);
		
//		roadMarkMap.paintScene(ctxt);
//		grassMarkMap.paintScene(ctxt);
		
		quadrantMap.paintScene(ctxt);
		
		graph.paintScene(ctxt);
		
		carMap.paint(ctxt);
//		explosionMap.paint(ctxt);
		
		if (APP.DEBUG_DRAW) {
			graph.paintIDs(ctxt);
		}
		
//		if (APP.FPS_DRAW) {
//			
//			stats.paint(ctxt);
//		}
		
		ctxt.popTransform();
		
		Car c = carMap.redCar;
		if (c != null) {
			
			Point.worldToPanel(c.shape.aabb, worldCamera, shimmerTmp);
			
			shimmer.setShape(shimmerTmp);
			
			shimmer.paint(ctxt);
		}
		
	}
	
	MutableAABB shimmerTmp = new MutableAABB();
	
	
//	public void paintPreview_controlPanel(RenderingContext ctxt) {
//		
//		ctxt.pushTransform();
//		
//		ctxt.translate(worldCamera.previewAABB.x, worldCamera.previewAABB.y);
//		
//		ctxt.paintImage(previewImage,
//				0, 0, (int)worldCamera.previewAABB.width, (int)worldCamera.previewAABB.height,
//				0, 0, (int)worldCamera.previewAABB.width, (int)worldCamera.previewAABB.height);
//		
//		Point prevLoc = Point.worldToPreview(worldCamera.worldViewport.x, worldCamera.worldViewport.y, worldCamera);
//		
//		Point prevDim = Point.worldToPreview(new Point(worldCamera.worldViewport.width, worldCamera.worldViewport.height), worldCamera);
//		
//		AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
//		
//		ctxt.translate(
//				worldCamera.previewAABB.width/2 - (worldCamera.previewPixelsPerMeter * quadrantMap.worldAABB.width / 2),
//				worldCamera.previewAABB.height/2 - (worldCamera.previewPixelsPerMeter * quadrantMap.worldAABB.height / 2));
//		
//		ctxt.setColor(Color.BLUE);
//		prev.draw(ctxt);
//		
//		ctxt.popTransform();
//	}
	
//	public void paintStats(RenderingContext ctxt) {
//		
//		ctxt.pushTransform();
//		
//		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "time: " + t);
//		
//		ctxt.translate(0, 1);
//		
//		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "body count: " + getBodyCount());
//		
//		ctxt.translate(0, 1);
//		
//		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "car count: " + carMap.size());
//		
////		ctxt.translate(0, 1);
//		
////		ctxt.paintString(0, 0, 1.0/ctxt.cam.pixelsPerMeter, "splosions count: " + explosionMap.size());
//		
//		ctxt.translate(0, 1);
//		
//		graph.paintStats(ctxt);
//		
//		ctxt.popTransform();
//	}
	
//	public boolean checkConsistency() {
//		return graph.checkConsistency();
//	}
	
}
