package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.DeadlockMain;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.EdgeDirection;
import com.gutabi.deadlock.core.graph.EdgePosition;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.Intersection;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Turning;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.core.graph.VertexPosition;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.StopSign;
import com.gutabi.deadlock.model.Stroke;
import com.gutabi.deadlock.model.cursor.FixtureCursor;
import com.gutabi.deadlock.model.cursor.MergerCursor;
import com.gutabi.deadlock.model.cursor.RegularCursor;
import com.gutabi.deadlock.model.fixture.WorldSink;
import com.gutabi.deadlock.model.fixture.WorldSource;

@SuppressWarnings("static-access")
public class DeadlockController implements ActionListener {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public ControlMode mode;
	
	public MouseController mc;
	public KeyboardController kc;
	
	ExecutorService e;
	
	Logger logger = Logger.getLogger(DeadlockController.class);
	
	public DeadlockController() {
		mode = ControlMode.IDLE;
	}
	
	public void init() {
		
		mc = new MouseController();
		kc = new KeyboardController();
		
		VIEW.canvas.addMouseListener(mc);
		VIEW.canvas.addMouseMotionListener(mc);
		
		VIEW.previewPanel.addMouseListener(mc);
		VIEW.previewPanel.addMouseMotionListener(mc);
		
		kc.init();
		
//		MODEL.debugStroke = new Stroke(Vertex.INIT_VERTEX_RADIUS);
		
		e = Executors.newSingleThreadExecutor();
		
		try {
			queueAndWait(new Runnable(){

				@Override
				public void run() {
					Thread.currentThread().setName("controller");
					Thread.currentThread().setUncaughtExceptionHandler(DeadlockMain.handler);
				}});
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void queue(Runnable task) {
		//Future f = e.submit(task);
		e.execute(task);
	}
	
	public void queueAndWait(Runnable task) throws InterruptedException, ExecutionException {
		Future<?> f = e.submit(task);
		f.get();
	}
	
//	Runnable renderRunnable = new Runnable() {
//		public void run() {
//			renderAndPaint();
//		}
//	};
	
//	public void renderAndPaintInBackground() {
//		SwingUtilities.invokeLater(renderRunnable);
//	}
	
	public void renderBackgroundAndPaint() {
		VIEW.renderBackgroundFresh();
		VIEW.repaint();
	}
	
	Point lastPressPanelPoint;
	Point lastPressPreviewPoint;
	long lastPressTime;
	
	Point origWorldViewLoc;
	
	public void pressed(InputEvent ev) {
		
		Component c = ev.c;
		
		Point p = ev.p;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			lastPressPanelPoint = p;
			lastPressTime = System.currentTimeMillis();
			
			lastDragPanelPoint = null;
			lastDragTime = -1;
			
		} else if (c == VIEW.previewPanel) {
			
			VIEW.previewPanel.requestFocusInWindow();
			
			lastPressPreviewPoint = p;
			lastPressTime = System.currentTimeMillis();
			
			lastDragPreviewPoint = null;
			lastDragTime = -1;
			
		}
		
	}
	
	
	Point lastDragPanelPoint;
	Point lastDragWorldPoint;
	Point lastDragPreviewPoint;
	long lastDragTime;
	
	int originalX;
	int originalY;
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.c;
		
		Point p = ev.p;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			boolean lastDragPanelPointWasNull = (lastDragPanelPoint == null);
			
			lastDragPanelPoint = p;
			lastDragWorldPoint = VIEW.panelToWorld(p);
			lastDragTime = System.currentTimeMillis();
			
			MODEL.cursor.setPoint(lastDragWorldPoint);
			
			switch (mode) {
			case IDLE: {
				
				if (lastDragPanelPointWasNull) {
					// first drag
					draftStart(lastPressPanelPoint);
					draftMove(lastDragPanelPoint);
					
					VIEW.repaint();
					
				} else {
					assert false;
				}
				
				break;
			}
			case DRAFTING:
				
				draftMove(lastDragPanelPoint);
				//VIEW.renderBackground();
				VIEW.repaint();
				break;
				
			case RUNNING:
			case PAUSED:
			case MERGERCURSOR:
			case FIXTURECURSOR:
				;
				break;
			}
			
		} else if (c == VIEW.previewPanel) {
			
			VIEW.previewPanel.requestFocusInWindow();
			
			boolean lastDragPreviewPointWasNull = (lastDragPreviewPoint == null);
			
			lastDragPreviewPoint = p;
			lastDragTime = System.currentTimeMillis();
			
			if (lastDragPreviewPointWasNull) {
				
				originalX = VIEW.worldOriginX;
				originalY = VIEW.worldOriginY;
				
			}
			
			int x = (int)(lastDragPreviewPoint.x - lastPressPreviewPoint.x);
			int y = (int)(lastDragPreviewPoint.y - lastPressPreviewPoint.y);
			
			VIEW.worldOriginX = originalX + 10*x;
			VIEW.worldOriginY = originalY + 10*y;
			
			VIEW.repaint();
			
		}
		
	}
	
	
	long lastReleaseTime;
	
	public void released(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			lastReleaseTime = System.currentTimeMillis();
			
			switch (mode) {
			case IDLE: {
				
				if (lastReleaseTime - lastPressTime < 500 && lastDragPanelPoint == null) {
					// click
					
//					Point w = VIEW.panelToWorld(lastPressPanelPoint);
//					
//					Entity hit = MODEL.world.hitTest(w);
//					
//					if (hit != null) {
//						
//						if (hit instanceof Vertex) {
//							
////							((Vertex) hit).getEdges();
//							
//						} else if (hit instanceof Road) {
//							
//						}
//						
//					}
				}
				
				break;
			}
			case DRAFTING:
				draftEnd();
				
				renderBackgroundAndPaint();
				
				break;
			case RUNNING:
			case PAUSED:
			case MERGERCURSOR:
			case FIXTURECURSOR:
				;
				break;
			}
			
		}
		
	}
	
	
	Point lastMovedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			Point p = ev.p;
			
			lastMovedWorldPoint = VIEW.panelToWorld(p);
			
			switch (mode) {
			case RUNNING:
				break;
			case PAUSED:
			case DRAFTING:
				assert false;
				break;
			case IDLE:
				Entity closest = MODEL.world.hitTest(lastMovedWorldPoint);
				MODEL.hilited = closest;
			case MERGERCURSOR:
			case FIXTURECURSOR:
				
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
				
				VIEW.repaint();
				break;
			}
			
		}
		
	}
	
	public void qKey() {
		
		switch (mode) {
		case IDLE:
			
			if (!MODEL.world.cursorIntersect(MODEL.cursor)) {
				
				MODEL.world.addVertexTop(new Intersection(MODEL.cursor.getPoint()));
				
				renderBackgroundAndPaint();
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void wKey() {
		
		switch (mode) {
		case IDLE:
			
			mode = ControlMode.FIXTURECURSOR;
			
			MODEL.cursor = new FixtureCursor();
			
			MODEL.cursor.setPoint(lastMovedWorldPoint);
			
			VIEW.repaint();
			
			break;
		case FIXTURECURSOR:
			FixtureCursor fc = (FixtureCursor)MODEL.cursor;
			Axis axis = fc.getAxis();
			
			if (!MODEL.world.cursorIntersect(MODEL.cursor)) {
				
				WorldSource source = new WorldSource(fc.getSourcePoint(), axis);
				WorldSink sink = new WorldSink(fc.getSinkPoint(), axis);
				
				source.matchingSink = sink;
				sink.matchingSource = source;
				
				MODEL.world.addVertexTop(source);
				
				MODEL.world.addVertexTop(sink);
				
				mode = ControlMode.IDLE;
				
				MODEL.cursor = new RegularCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				renderBackgroundAndPaint();
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void gKey() {
		
		MODEL.grid = !MODEL.grid;
		
		renderBackgroundAndPaint();
		
	}
	
	public void deleteKey() {
		
		if (MODEL.hilited != null) {
			
			if (MODEL.hilited.isDeleteable()) {
				
				if (MODEL.hilited instanceof Car) {
					Car c = (Car)MODEL.hilited;
					
					removeCarTop(c);
					
				} else if (MODEL.hilited instanceof Vertex) {
					Vertex v = (Vertex)MODEL.hilited;
					
					removeVertexTop(v);
					
				} else if (MODEL.hilited instanceof Road) {
					Road e = (Road)MODEL.hilited;
					
					removeRoadTop(e);
					
				} else if (MODEL.hilited instanceof Merger) {
					Merger e = (Merger)MODEL.hilited;
					
					removeMergerTop(e);
					
				} else if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				MODEL.hilited = null;
				
			}
			
		}
		
		renderBackgroundAndPaint();
		
	}
	
	public void insertKey() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					s.setEnabled(true);
					
					renderBackgroundAndPaint();
				}
				
			} else {
				
				mode = ControlMode.MERGERCURSOR;
				
				MODEL.cursor = new MergerCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				VIEW.repaint();
				
			}
			
			break;
		case MERGERCURSOR:
			
			if (!MODEL.world.cursorIntersect(MODEL.cursor)) {
				
				MODEL.world.insertMergerTop(MODEL.cursor.getPoint());
				
				mode = ControlMode.IDLE;
				
				MODEL.cursor = new RegularCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				renderBackgroundAndPaint();
				
			}
			
			break;
		case RUNNING:
		case PAUSED:
		case FIXTURECURSOR:
		case DRAFTING:
			break;
		}
		
	}
	
	public void escKey() {
		
		switch (mode) {
		case MERGERCURSOR:
		case FIXTURECURSOR:
			mode = ControlMode.IDLE;
			
			MODEL.cursor = new RegularCursor();
			
			MODEL.cursor.setPoint(lastMovedWorldPoint);
			
			VIEW.repaint();
			
			break;
		case RUNNING:
		case PAUSED:
		case DRAFTING:
		case IDLE:
			break;
		}
		
	}
	
	public void d1Key() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Intersection) {
					Intersection i = (Intersection)MODEL.hilited;
					
					i.setTurning(Turning.COUNTERCLOCKWISE);
					
					renderBackgroundAndPaint();
				} else if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(Axis.NONE, EdgeDirection.STARTTOEND);
					
					renderBackgroundAndPaint();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void d2Key() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Intersection) {
					Intersection i = (Intersection)MODEL.hilited;
					
					i.setTurning(Turning.CLOCKWISE);
					
					renderBackgroundAndPaint();
					
				} else if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(Axis.NONE, EdgeDirection.ENDTOSTART);
					
					renderBackgroundAndPaint();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}

	public void d3Key() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Intersection) {
					Intersection i = (Intersection)MODEL.hilited;
					
					i.setTurning(Turning.NONE);
					
					renderBackgroundAndPaint();
				} else if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(Axis.NONE, EdgeDirection.NONE);
				
					renderBackgroundAndPaint();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void startRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		mode = ControlMode.RUNNING;
		
		MODEL.cursor = null;
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.cursor = new RegularCursor();
		
		MODEL.cursor.setPoint(lastMovedWorldPoint);
		
		mode = ControlMode.IDLE;
		
//		VIEW.repaint();
	}
	
	public void pauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		MODEL.cursor.setPoint(lastMovedWorldPoint);
		
		mode = ControlMode.IDLE;
		
		mode = ControlMode.PAUSED;
	}
	
	public void unpauseRunning() {
		assert Thread.currentThread().getName().equals("controller");
		
		mode = ControlMode.RUNNING;
		
		MODEL.cursor = null;
		
		synchronized (MODEL.pauseLock) {
			MODEL.pauseLock.notifyAll();
		}
	}
	
	
	
	private void draftStart(Point p) {
		assert Thread.currentThread().getName().equals("controller");
			
		mode = ControlMode.DRAFTING;
		
		MODEL.hilited = null;
		
		MODEL.stroke = new Stroke(Vertex.INIT_VERTEX_RADIUS);
		MODEL.stroke.add(VIEW.panelToWorld(p));
			
	}
	
	private void draftMove(Point p) {
		assert Thread.currentThread().getName().equals("controller");

		MODEL.stroke.add(VIEW.panelToWorld(p));
	}
	
	private void draftEnd() {
		assert Thread.currentThread().getName().equals("controller");
		
		processNewStroke();
		
		assert MODEL.world.checkConsistency();
		
		MODEL.debugStroke2 = MODEL.debugStroke;
		MODEL.debugStroke = MODEL.stroke;
		MODEL.stroke = null;
		
//		MODEL.world.renderBackground();
//		VIEW.repaint();
		
		mode = ControlMode.IDLE;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			VIEW.controlPanel.stopButton.setEnabled(true);
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.startRunning();
				}}
			);
			
		} else if (e.getActionCommand().equals("stop")) {
			
			VIEW.controlPanel.startButton.setText("Start");
			VIEW.controlPanel.startButton.setActionCommand("start");
			
			VIEW.controlPanel.stopButton.setEnabled(false);
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.stopRunning();
				}}
			);
		} else if (e.getActionCommand().equals("pause")) {
			
			VIEW.controlPanel.startButton.setText("Unpause");
			VIEW.controlPanel.startButton.setActionCommand("unpause");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.pauseRunning();
				}}
			);
		} else if (e.getActionCommand().equals("unpause")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.unpauseRunning();
				}}
			);
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = VIEW.controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				MODEL.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					
				}}
			);
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = VIEW.controlPanel.debugCheckBox.isSelected();
			
			MODEL.DEBUG_DRAW = state;
			
			renderBackgroundAndPaint();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			MODEL.FPS_DRAW = state;
			
			renderBackgroundAndPaint();
			
		}
	}
	
	
	
	private void removeVertexTop(Vertex v) {
		MODEL.world.removeVertexTop(v);
	}
	
	private void removeRoadTop(Road e) {
		MODEL.world.removeRoadTop(e);
	}
	
	private void removeMergerTop(Merger m) {
		MODEL.world.removeMergerTop(m);
	}
	
	private void removeStopSignTop(StopSign s) {
		MODEL.world.removeStopSignTop(s);
	}
	
	private void removeCarTop(Car c) {
		MODEL.world.removeCarTop(c);
	}
	
	
	
	public void processNewStroke() {
		
		Stroke s = MODEL.stroke;
		
		List<SweepEvent> events = s.events();
		
		/*
		 * go through and create or split where needed to make sure vertices are present
		 */
		for (int i = 0; i < events.size(); i++) {
			
			SweepEvent e = events.get(i);
			
			if (e.type == null) {
				
				Entity hit = MODEL.world.pureGraphBestHitTest(e.circle);
				assert hit == null;
				logger.debug("create");
				Intersection v = new Intersection(e.p);
				MODEL.world.addVertexTop(v);
				
				e.setVertex(v);
				
				assert MODEL.world.checkConsistency();
				
			} else if (e.type == SweepEventType.ENTERCAPSULE || e.type == SweepEventType.EXITCAPSULE) {
				
				Entity hit = MODEL.world.pureGraphBestHitTest(e.circle);
				
				if (hit == null || hit instanceof Road) {
					
					GraphPosition pos = null;
					
					/*
					 * find better place to split by checking for intersection with road
					 */
					for (int j = e.index; (e.type == SweepEventType.ENTERCAPSULE) ? j < s.pts.size()-1 : j >= 0; j += (e.type == SweepEventType.ENTERCAPSULE) ? 1 : -1) {
						Point a = s.pts.get(j);
						Point b = s.pts.get(j+1);
						
						if (hit == null) {
							if (e.type == SweepEventType.ENTERCAPSULE) {
								hit = MODEL.world.pureGraphBestHitTest(new Circle(null, b, e.circle.radius));
							} else {
								hit = MODEL.world.pureGraphBestHitTest(new Circle(null, a, e.circle.radius));
							}
							if (hit == null) {
								continue;
							} else if (hit instanceof Vertex) {
								pos = new VertexPosition((Vertex)hit);
								break;
							}
						}
						
						RoadPosition skeletonIntersection = ((Road)hit).findSkeletonIntersection(a, b);
						
						if (skeletonIntersection != null) {
							
							double strokeCombo = j + Point.param(skeletonIntersection.p, a, b);
							
							if ((e.type == SweepEventType.ENTERCAPSULE) ? DMath.greaterThanEquals(strokeCombo, (e.index+e.param)) : DMath.lessThanEquals(strokeCombo, (e.index+e.param))
									&& DMath.lessThanEquals(Point.distance(skeletonIntersection.p, e.p), s.r + s.r)
									) {
								pos = skeletonIntersection;
								
								logger.debug("found intersection");
								
								break;
							}
						}
						
					}
					
					if (pos == null) {
						logger.debug("pos was null");
						pos = MODEL.world.findClosestRoadPosition(e.p, e.circle.radius);
					}
					
					Entity hit2;
					if (pos instanceof EdgePosition) {
						hit2 = MODEL.world.pureGraphBestHitTest(new Circle(null, pos.p, e.circle.radius));
					} else {
						hit2 = ((VertexPosition)pos).v;
					}
					
					if (hit2 instanceof Road) {
						Vertex v = MODEL.world.splitRoadTop((RoadPosition)pos);
						
						assert e.circle.intersect(v.shape);
						
						e.setVertex(v);
					} else {
						e.setVertex((Vertex)hit2);
					}
					
					assert MODEL.world.checkConsistency();
					
				} else if (hit instanceof Vertex) {
					e.setVertex((Vertex)hit);
				} else {
					assert false;
				}
				
			} else if (e.type == SweepEventType.ENTERCIRCLE) {
				e.setVertex((Vertex)e.shape.parent);
			} else if (e.type == SweepEventType.EXITCIRCLE) {
				e.setVertex((Vertex)e.shape.parent);
			}
			
			assert e.getVertex() != null;
		}
		
		/*
		 * now go through and create roads
		 */
		for (int i = 0; i < events.size()-1; i++) {
			SweepEvent e0 = events.get(i);
			SweepEvent e1 = events.get(i+1);
			
			if (e0.type == SweepEventType.ENTERCIRCLE && e1.type == SweepEventType.EXITCIRCLE) {
				
				logger.debug("skipping");
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			} else if (e0.type == SweepEventType.ENTERCAPSULE && e1.type == SweepEventType.EXITCAPSULE) {
				
				logger.debug("skipping");
				
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			} else if (e0.type == SweepEventType.ENTERQUAD || e1.type == SweepEventType.EXITQUAD) {
				
				return;
				
			}
			
//			Vertex v0 = (Vertex)MODEL.world.pureGraphBestHitTest(e0.circle);
//			Vertex v1 = (Vertex)MODEL.world.pureGraphBestHitTest(e1.circle);
			Vertex v0 = e0.getVertex();
			Vertex v1 = e1.getVertex();
			
			if (v0 == v1) {
				logger.debug("same vertex");
//				assert false;
//				return;
				continue;
			}
			
			List<Point> roadPts = new ArrayList<Point>();
			roadPts.add(e0.p);
			for (int j = e0.index+1; j < e1.index; j++) {
				roadPts.add(s.pts.get(j));
			}
			roadPts.add(s.pts.get(e1.index));
			if (!DMath.equals(e1.param, 0.0)) {
				roadPts.add(e1.p);
			}
			
			MODEL.world.createRoadTop(v0, v1, roadPts);
		}
		
	}
	
}
