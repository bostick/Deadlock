package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.view.Canvas;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.PreviewPanel;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.CarEventListener;
import com.gutabi.deadlock.world.cursor.Cursor;
import com.gutabi.deadlock.world.cursor.RegularCursor;
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
public class World extends ScreenBase implements Sweepable {
	
	enum WorldMode {
		EDITING,
		RUNNING,
		PAUSED,
	}
	
	public final double worldWidth;
	public final double worldHeight;
	
	public AABB worldViewport;
	
	public WorldMode mode;
	
	public Preview preview;
	
	public Cursor cursor;
	
	public Entity hilited;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	public BufferedImage quadrantGrass;
	public BufferedImage canvasGrassImage;
	public BufferedImage canvasGraphImage;
	
	public static Random RANDOM = new Random(1);
	
	/*
	 * simulation state
	 */
	public double t;
	
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
		
		int quadrantCols = ini[0].length;
		int quadrantRows = ini.length;
		
		quadrantMap = new QuadrantMap(this, ini);
		
		worldWidth = quadrantCols * APP.QUADRANT_WIDTH;
		
		worldHeight = quadrantRows * APP.QUADRANT_HEIGHT;
		
		quadrantGrass = new BufferedImage(
				512,
				512,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D quadrantGrassG2 = quadrantGrass.createGraphics();
		for (int i = 0; i < (int)Math.round(APP.PIXELS_PER_METER * APP.QUADRANT_WIDTH)/32; i++) {
			for (int j = 0; j < (int)Math.round(APP.PIXELS_PER_METER * APP.QUADRANT_HEIGHT)/32; j++) {
				quadrantGrassG2.drawImage(VIEW.sheet,
						32 * i, 32 * j, 32 * i + 32, 32 * j + 32,
						0, 224, 0+32, 224+32, null);
			}
		}	
		
		mode = WorldMode.EDITING;
		
		cursor = new RegularCursor(this);
		
		preview = new Preview(this);
		
		stats = new Stats(this);
		
		graph = new Graph(this);
		
		carMap = new CarMap(this);
		
		pathFactory = new GraphPositionPathFactory(this);
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		b2dWorld.setContactListener(new CarEventListener(this));
		
		computeAABB();
		
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
		preview.init();
	}
	
	public void canvasPostDisplay() {	
		
		int canvasWidth = VIEW.canvas.getWidth();
		int canvasHeight = VIEW.canvas.getHeight();
		
		canvasGrassImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		canvasGraphImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
		
		worldViewport = new AABB(
				-(canvasWidth / APP.PIXELS_PER_METER) / 2 + worldWidth/2 ,
				-(canvasHeight / APP.PIXELS_PER_METER) / 2 + worldHeight/2,
				canvasWidth / APP.PIXELS_PER_METER,
				canvasHeight / APP.PIXELS_PER_METER);
		
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
	
	public void startRunning() {
		
		mode = WorldMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable(this));
		t.start();
		
	}
	
	public void stopRunning() {
		
		mode = WorldMode.EDITING;
		
	}
	
	public void pauseRunning() {
		
		mode = WorldMode.PAUSED;
	}
	
	public void unpauseRunning() {
		
		mode = WorldMode.RUNNING;
		
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public void zoom(double factor) {
		
		APP.PIXELS_PER_METER = factor * APP.PIXELS_PER_METER; 
		
		int canvasWidth = VIEW.canvas.getWidth();
		int canvasHeight = VIEW.canvas.getHeight();
		
		double newWidth =  canvasWidth / APP.PIXELS_PER_METER;
		double newHeight = canvasHeight / APP.PIXELS_PER_METER;
		
		worldViewport = new AABB(worldViewport.center.x - newWidth/2, worldViewport.center.y - newHeight/2, newWidth, newHeight);
		
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
	
	
	
	public void qKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.qKey();
			break;
		}
	}
	
	public void wKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.wKey();
			break;
		}
	}
	
	public void gKey() {
		
		quadrantMap.toggleGrid();
		
		cursor.setPoint(quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
		
		render();
		repaint();
	}
	
	public void deleteKey() {
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					removeCarTop(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					removeVertexTop(v);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					removeRoadTop(e);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					removeMergerTop(e);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		render();
		repaint();
	}
	
	public void insertKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.insertKey();
			break;
		}
	}
	
	public void escKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.escKey();
			break;
		}
	}
	
	public void d1Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case EDITING:
			cursor.d1Key();
			break;
		}
	}
	
	public void d2Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case EDITING:
			cursor.d2Key();
			break;
		}
	}
	
	public void d3Key() {
		switch (mode) {
		case PAUSED:
		case RUNNING:
			break;
		case EDITING:
			cursor.d3Key();
			break;
		}
	}
	
	public void plusKey() {
		
		zoom(1.1);
		
		lastMovedOrDraggedWorldPoint = canvasToWorld(VIEW.canvas.lastMovedOrDraggedCanvasPoint);
		
		Entity closest = hitTest(lastMovedOrDraggedWorldPoint);
		
		synchronized (APP) {
			hilited = closest;
		}
		
		quadrantMap.computeGridSpacing();
		
		cursor.setPoint(quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
		
		render();
		repaint();
	}
	
	public void minusKey() {
		
		zoom(0.9);
		
		lastMovedOrDraggedWorldPoint = canvasToWorld(VIEW.canvas.lastMovedOrDraggedCanvasPoint);
		
		Entity closest = hitTest(lastMovedOrDraggedWorldPoint);
		
		synchronized (APP) {
			hilited = closest;
		}
		
		quadrantMap.computeGridSpacing();
		
		cursor.setPoint(quadrantMap.getPoint(lastMovedOrDraggedWorldPoint));
		
		render();
		repaint();
	}
	
	public void aKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.aKey();
			break;
		}
	}
	
	public void sKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.sKey();
			break;
		}
	}
	
	public void dKey() {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.dKey();
			break;
		}
	}
	
	public Point canvasToWorld(Point p) {
		return new Point(
				p.x / APP.PIXELS_PER_METER + worldViewport.x,
				p.y / APP.PIXELS_PER_METER + worldViewport.y);
	}
	
	public Point lastPressedWorldPoint;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressedWorldPoint = canvasToWorld(p);
		lastDraggedWorldPoint = null;
		
	}
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	public void dragged(InputEvent ev) {
		
		if (ev.c instanceof Canvas) {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			
			switch (mode) {
			case RUNNING:
			case PAUSED:
				break;
			case EDITING:
				cursor.dragged(ev);
				break;
			}
		} else if (ev.c instanceof PreviewPanel) {
			preview.dragged(ev);
		}
		
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.released(ev);
			break;
		}
	}
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		VIEW.canvas.requestFocusInWindow();
		
		Point p = ev.p;
		
		lastMovedWorldPoint = canvasToWorld(p);
		lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.moved(ev);
			break;	
		}
	}
	
	public void exited(InputEvent ev) {
		
		switch (mode) {
		case RUNNING:
		case PAUSED:
			break;
		case EDITING:
			cursor.exited(ev);
			break;
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			VIEW.controlPanel.stopButton.setEnabled(true);
			
			startRunning();
			
		} else if (e.getActionCommand().equals("stop")) {
			
			VIEW.controlPanel.startButton.setText("Start");
			VIEW.controlPanel.startButton.setActionCommand("start");
			
			VIEW.controlPanel.stopButton.setEnabled(false);
			
			stopRunning();
			
		} else if (e.getActionCommand().equals("pause")) {
			
			VIEW.controlPanel.startButton.setText("Unpause");
			VIEW.controlPanel.startButton.setActionCommand("unpause");
			
			pauseRunning();
			
		} else if (e.getActionCommand().equals("unpause")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			unpauseRunning();
			
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = VIEW.controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				APP.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = VIEW.controlPanel.debugCheckBox.isSelected();
			
			APP.DEBUG_DRAW = state;
			
			render();
			repaint();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			APP.FPS_DRAW = state;
			
			render();
			repaint();
			
		} else if (e.getActionCommand().equals("stopSignDraw")) {
			
			boolean state = VIEW.controlPanel.stopSignCheckBox.isSelected();
			
			APP.STOPSIGN_DRAW = state;
			
			render();
			repaint();
			
		} else if (e.getActionCommand().equals("carTextureDraw")) {
			
			boolean state = VIEW.controlPanel.carTextureCheckBox.isSelected();
			
			APP.CARTEXTURE_DRAW = state;
			
			repaint();
			
		} else if (e.getActionCommand().equals("explosionsDraw")) {
			
			boolean state = VIEW.controlPanel.explosionsCheckBox.isSelected();
			
			APP.EXPLOSIONS_DRAW = state;
			
			repaint();
		}
	}
	
	public void render() {
		assert !Thread.holdsLock(APP);
		
		synchronized (VIEW) {
			Graphics2D canvasGrassImageG2 = canvasGrassImage.createGraphics();
			
			canvasGrassImageG2.setColor(Color.WHITE);
			canvasGrassImageG2.fillRect(0, 0, VIEW.canvas.getWidth(), VIEW.canvas.getHeight());
			
			canvasGrassImageG2.translate((int)(-worldViewport.x * APP.PIXELS_PER_METER), (int)(-worldViewport.y * APP.PIXELS_PER_METER));
			
			canvasGrassImageG2.scale(APP.PIXELS_PER_METER, APP.PIXELS_PER_METER);
			
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
			canvasGraphImageG2.fillRect(0, 0, VIEW.canvas.getWidth(), VIEW.canvas.getHeight());
			canvasGraphImageG2.setComposite(orig);
			
			canvasGraphImageG2.translate((int)((-worldViewport.x) * APP.PIXELS_PER_METER), (int)((-worldViewport.y) * APP.PIXELS_PER_METER));
			
			canvasGraphImageG2.scale(APP.PIXELS_PER_METER, APP.PIXELS_PER_METER);
			
			RenderingContext canvasGraphContext = new RenderingContext(canvasGraphImageG2, RenderingContextType.CANVAS);
			
			graph.renderBackground(canvasGraphContext);
			
			canvasGraphImageG2.dispose();
			
		}
		
		preview.render();
		
	}
	
	public void repaint() {
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (mode == WorldMode.RUNNING) {
				return;
			}
		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.scale(APP.PIXELS_PER_METER);
				ctxt.translate(-worldViewport.x, -worldViewport.y);
				
				paintGrass(ctxt);
				paintGraph(ctxt);
//				paintSkidmarks(ctxt);
				
				paintScene(ctxt);
				
				if (APP.DEBUG_DRAW) {
					ctxt.setColor(Color.BLACK);
					ctxt.setPixelStroke(1);
					aabb.draw(ctxt);
					
				}
				
				cursor.draw(ctxt);
				
				if (APP.FPS_DRAW) {
					
					ctxt.translate(worldViewport.x, worldViewport.y);
					
					stats.paint(ctxt);
				}
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());
		
		VIEW.controlPanel.repaint();
		
	}
	
	private void paintGrass(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		ctxt.translate(worldViewport.x, worldViewport.y);
		
		ctxt.paintImage(
				0, 0, 1 / APP.PIXELS_PER_METER,
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
				0, 0, 1 / APP.PIXELS_PER_METER,
				canvasGraphImage,
				0, 0, canvasGraphImage.getWidth(), canvasGraphImage.getHeight(),
				0, 0, canvasGraphImage.getWidth(), canvasGraphImage.getHeight());
		
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintScene(RenderingContext ctxt) {
		
		graph.paintScene(ctxt);
		
		Entity hilitedCopy;
		synchronized (APP) {
			hilitedCopy = hilited;
		}
		
		synchronized (APP) {
			carMap.paint(ctxt);
			explosionMap.paint(ctxt);
		}
		
		if (hilitedCopy != null) {
			hilitedCopy.paintHilite(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			
			graph.paintIDs(ctxt);
			
		}
		
	}
	
	public void paintStats(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.paintString(0, 0, 1 / APP.PIXELS_PER_METER, "time: " + t);
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1 / APP.PIXELS_PER_METER, "body count: " + b2dWorld.getBodyCount());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1 / APP.PIXELS_PER_METER, "car count: " + carMap.size());
		
		ctxt.translate(0, 1);
		
		ctxt.paintString(0, 0, 1 / APP.PIXELS_PER_METER, "splosions count: " + explosionMap.size());
		
		ctxt.translate(0, 1);
		
		graph.paintStats(ctxt);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c instanceof Canvas) {
			VIEW.canvas.bs.show();
		} else if (ev.c instanceof PreviewPanel) {
			preview.paint(ev.ctxt);
		}
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
