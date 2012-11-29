package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEventType;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.EdgeDirection;
import com.gutabi.deadlock.core.graph.EdgePosition;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.Intersection;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
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
		
	}
	
	Point lastPressCanvasPoint;
	Point lastPressPreviewPoint;
	long lastPressTime;
	
	Point origWorldViewLoc;
	
	public void pressed(InputEvent ev) {
		
		Component c = ev.c;
		
		Point p = ev.p;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			lastPressCanvasPoint = p;
			lastPressTime = System.currentTimeMillis();
			
			lastDragCanvasPoint = null;
			lastDragTime = -1;
			
		} else if (c == VIEW.previewPanel) {
			
			VIEW.previewPanel.requestFocusInWindow();
			
			lastPressPreviewPoint = p;
			lastPressTime = System.currentTimeMillis();
			
			lastDragPreviewPoint = null;
			lastDragTime = -1;
			
		}
		
	}
	
	
	Point lastDragCanvasPoint;
	Point lastDragWorldPoint;
	Point lastDragPreviewPoint;
	long lastDragTime;
	
	AABB originalViewport;
	
	public void dragged(InputEvent ev) {
		
		Component c = ev.c;
		
		Point p = ev.p;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			boolean lastDragCanvasPointWasNull = (lastDragCanvasPoint == null);
			
			lastDragCanvasPoint = p;
			lastDragWorldPoint = VIEW.canvasToWorld(p);
			lastDragTime = System.currentTimeMillis();
			
			MODEL.cursor.setPoint(lastDragWorldPoint);
			
			switch (mode) {
			case IDLE: {
				
				if (lastDragCanvasPointWasNull) {
					// first drag
					draftStart(lastPressCanvasPoint);
					draftMove(lastDragCanvasPoint);
					
					VIEW.repaint();
					
				} else {
					assert false;
				}
				
				break;
			}
			case DRAFTING:
				
				draftMove(lastDragCanvasPoint);
				
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
				
				originalViewport = VIEW.viewport;
				
			}
			
			double dx = lastDragPreviewPoint.x - lastPressPreviewPoint.x;
			double dy = lastDragPreviewPoint.y - lastPressPreviewPoint.y;
			
//			double width = (int)(1427.0 / VIEW.metersToPixels(MODEL.world.worldWidth) * 100.0);
//			double height = (int)(822.0 / VIEW.metersToPixels(MODEL.world.worldHeight) * 100.0);
//			double width = (int)(1427.0 / VIEW.metersToPixels(1.0) * 100.0);
//			double height = (int)(822.0 / VIEW.metersToPixels(1.0) * 100.0);
			
			VIEW.viewport = new AABB(
					originalViewport.x + MODEL.world.worldWidth * dx / 100.0,
					originalViewport.y + MODEL.world.worldHeight * dy / 100.0,
					originalViewport.width,
					originalViewport.height);
			
			VIEW.renderBackground();
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
				
				if (lastReleaseTime - lastPressTime < 500 && lastDragCanvasPoint == null) {
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
				
				VIEW.renderBackground();
				VIEW.repaint();
				
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
	
	
	Point lastMovedCanvasPoint;
	Point lastMovedWorldPoint;
	
	public void moved(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			VIEW.canvas.requestFocusInWindow();
			
			lastMovedCanvasPoint = ev.p;
			
			lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
			
			switch (mode) {
			case PAUSED:
			case DRAFTING:
				assert false;
				break;
			case IDLE:
			case RUNNING:
				
				Entity closest = MODEL.world.hitTest(lastMovedWorldPoint);
				MODEL.hilited = closest;
				
			case MERGERCURSOR:
			case FIXTURECURSOR:
				
				if (MODEL.cursor != null) {
					if (MODEL.grid) {
						
						Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
						MODEL.cursor.setPoint(closestGridPoint);
						
					} else {
						MODEL.cursor.setPoint(lastMovedWorldPoint);
					}
				}
				
				VIEW.repaint();
				break;
			}
			
		}
		
	}
	
	public void exited(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			switch (mode) {
			case PAUSED:
			case DRAFTING:
			case RUNNING:
				break;
			case IDLE:
			case MERGERCURSOR:
			case FIXTURECURSOR:
				
				if (MODEL.cursor != null) {
					MODEL.cursor.setPoint(null);
				}
				
				VIEW.repaint();
				break;
			}
			
		}
		
	}
	
	public void entered(InputEvent ev) {
		
		Component c = ev.c;
		
		if (c == VIEW.canvas) {
			
			Point p = ev.p;
			
			lastMovedWorldPoint = VIEW.canvasToWorld(p);
			
			switch (mode) {
			case PAUSED:
			case DRAFTING:
			case RUNNING:
				break;
			case IDLE:
			case MERGERCURSOR:
			case FIXTURECURSOR:
				
				if (MODEL.cursor != null) {
					if (MODEL.grid) {
						
						Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
						MODEL.cursor.setPoint(closestGridPoint);
						
					} else {
						MODEL.cursor.setPoint(lastMovedWorldPoint);
					}
				}
				
				VIEW.repaint();
				break;
			}
			
		}
		
	}
	
	public void qKey() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.world.completelyContains(MODEL.cursor.getShape())) {
				
				if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
					
					MODEL.world.addVertexTop(new Intersection(MODEL.cursor.getPoint()));
					
					VIEW.renderBackground();
					VIEW.repaint();
					
				}
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
			
			if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
				
				WorldSource source = new WorldSource(fc.getSourcePoint(), axis);
				WorldSink sink = new WorldSink(fc.getSinkPoint(), axis);
				
				source.matchingSink = sink;
				sink.matchingSource = source;
				
				MODEL.world.addVertexTop(source);
				
				MODEL.world.addVertexTop(sink);
				
				mode = ControlMode.IDLE;
				
				MODEL.cursor = new RegularCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				VIEW.renderBackground();
				VIEW.repaint();
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void gKey() {
		
		MODEL.grid = !MODEL.grid;
		
		VIEW.renderBackground();
		VIEW.repaint();
		
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
		
		VIEW.renderBackground();
		VIEW.repaint();
		
	}
	
	public void insertKey() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					s.setEnabled(true);
					
					VIEW.renderBackground();
					VIEW.repaint();
				}
				
			} else {
				
				mode = ControlMode.MERGERCURSOR;
				
				MODEL.cursor = new MergerCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				VIEW.repaint();
				
			}
			
			break;
		case MERGERCURSOR:
			
			if (MODEL.world.completelyContains(MODEL.cursor.getShape())) {
				
				if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
					
					MODEL.world.insertMergerTop(MODEL.cursor.getPoint());
					
					mode = ControlMode.IDLE;
					
					MODEL.cursor = new RegularCursor();
					
					MODEL.cursor.setPoint(lastMovedWorldPoint);
					
					VIEW.renderBackground();
					VIEW.repaint();
					
				}
				
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
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(Axis.NONE, EdgeDirection.STARTTOEND);
					
					VIEW.renderBackground();
					VIEW.repaint();
					
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
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(Axis.NONE, EdgeDirection.ENDTOSTART);
					
					VIEW.renderBackground();
					VIEW.repaint();
					
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
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(Axis.NONE, EdgeDirection.NONE);
				
					VIEW.renderBackground();
					VIEW.repaint();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void plusKey() {
		
		Point center = new Point(VIEW.viewport.x + VIEW.viewport.width / 2, VIEW.viewport.y + VIEW.viewport.height / 2);
		
		VIEW.PIXELS_PER_METER_DEBUG = 1.1 * VIEW.PIXELS_PER_METER_DEBUG; 
		
		double newWidth = 1427.0 / VIEW.PIXELS_PER_METER_DEBUG;
		double newHeight = 822.0 / VIEW.PIXELS_PER_METER_DEBUG;
		
		VIEW.viewport = new AABB(center.x - newWidth/2, center.y - newHeight/2, newWidth, newHeight);
		
		lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
		
		switch (mode) {
		case PAUSED:
		case DRAFTING:
			assert false;
			break;
		case IDLE:
		case RUNNING:
			
			Entity closest = MODEL.world.hitTest(lastMovedWorldPoint);
			MODEL.hilited = closest;
			
		case MERGERCURSOR:
		case FIXTURECURSOR:
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.repaint();
			break;
		}
		
		VIEW.renderBackground();
		VIEW.repaint();
		
	}
	
	public void minusKey() {
		
		Point center = new Point(VIEW.viewport.x + VIEW.viewport.width / 2, VIEW.viewport.y + VIEW.viewport.height / 2);
		
		VIEW.PIXELS_PER_METER_DEBUG = 0.9 * VIEW.PIXELS_PER_METER_DEBUG; 
		
		double newWidth = 1427.0 / VIEW.PIXELS_PER_METER_DEBUG;
		double newHeight = 822.0 / VIEW.PIXELS_PER_METER_DEBUG;
		
		VIEW.viewport = new AABB(center.x - newWidth/2, center.y - newHeight/2, newWidth, newHeight);
		
		lastMovedWorldPoint = VIEW.canvasToWorld(lastMovedCanvasPoint);
		
		switch (mode) {
		case PAUSED:
		case DRAFTING:
			assert false;
			break;
		case IDLE:
		case RUNNING:
			
			Entity closest = MODEL.world.hitTest(lastMovedWorldPoint);
			MODEL.hilited = closest;
			
		case MERGERCURSOR:
		case FIXTURECURSOR:
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.repaint();
			break;
		}
		
		VIEW.renderBackground();
		VIEW.repaint();
		
	}
	
	public void startRunning() {
		
		mode = ControlMode.RUNNING;
		
		MODEL.cursor = null;
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		
		MODEL.cursor = new RegularCursor();
		
		MODEL.cursor.setPoint(lastMovedWorldPoint);
		
		mode = ControlMode.IDLE;
		
	}
	
	public void pauseRunning() {
		
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
			
		mode = ControlMode.DRAFTING;
		
		MODEL.hilited = null;
		
		MODEL.stroke = new Stroke(Vertex.INIT_VERTEX_RADIUS);
		MODEL.stroke.add(VIEW.canvasToWorld(p));
			
	}
	
	private void draftMove(Point p) {

		MODEL.stroke.add(VIEW.canvasToWorld(p));
	}
	
	private void draftEnd() {
		
		processNewStroke();
		
		assert MODEL.world.checkConsistency();
		
		MODEL.debugStroke2 = MODEL.debugStroke;
		MODEL.debugStroke = MODEL.stroke;
		MODEL.stroke = null;
		
		mode = ControlMode.IDLE;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			VIEW.controlPanel.stopButton.setEnabled(true);
			
			CONTROLLER.startRunning();
			
		} else if (e.getActionCommand().equals("stop")) {
			
			VIEW.controlPanel.startButton.setText("Start");
			VIEW.controlPanel.startButton.setActionCommand("start");
			
			VIEW.controlPanel.stopButton.setEnabled(false);
			
			CONTROLLER.stopRunning();
		} else if (e.getActionCommand().equals("pause")) {
			
			VIEW.controlPanel.startButton.setText("Unpause");
			VIEW.controlPanel.startButton.setActionCommand("unpause");
			
			CONTROLLER.pauseRunning();
		} else if (e.getActionCommand().equals("unpause")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			CONTROLLER.unpauseRunning();
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = VIEW.controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				MODEL.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = VIEW.controlPanel.debugCheckBox.isSelected();
			
			MODEL.DEBUG_DRAW = state;
			
			VIEW.renderBackground();
			VIEW.repaint();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			MODEL.FPS_DRAW = state;
			
			VIEW.renderBackground();
			VIEW.repaint();
			
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
		
		for (int i = 0; i < s.size()-1; i++) {
			Capsule cap = new Capsule(null, s.getCircle(i), s.getCircle(i+1));
			if (!MODEL.world.completelyContains(cap)) {
				return;
			}
		}
		
		List<SweepEvent> events = s.events();
		
		/*
		 * go through and find any merger events and fixture events
		 */
		for (int i = 0; i < events.size(); i++) {
			SweepEvent e = events.get(i);
			if (e.type == SweepEventType.ENTERMERGER) {
				return;
			}
//			if (e.type == SweepEventType.ENTERVERTEX) {
//				Vertex v = (Vertex)e.shape.parent;
//				if (v instanceof Fixture && (v.roads.size() + (v.m!=null?1:0)) > 0) {
//					assert ((v.roads.size() + (v.m!=null?1:0))) == 1;
//					return;
//				}
//			}
			if (i < events.size()-1) {
				SweepEvent f = events.get(i+1);
				if (e.type == SweepEventType.EXITVERTEX && f.type == SweepEventType.ENTERVERTEX) {
					Vertex ev = (Vertex)e.shape.parent;
					Vertex fv = (Vertex)f.shape.parent;
					if (ev.m != null && ev.m == fv.m) {
						/*
						 * FIXME: this currently disallows connecting vertices in a merger, even outside the merger
						 * fix this so that it's only a road within the merger that is disallowed
						 */
						return;
					}
				}
			}
		}
		
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
				
//				assert MODEL.world.checkConsistency();
				
			} else if (e.type == SweepEventType.ENTERROADCAPSULE || e.type == SweepEventType.EXITROADCAPSULE) {
				
				Entity hit = MODEL.world.pureGraphBestHitTest(e.circle);
				
				if (hit instanceof Vertex) {
					e.setVertex((Vertex)hit);
					continue;
				}
				
				GraphPosition pos = null;
				
				/*
				 * find better place to split by checking for intersection with road
				 */
				Circle a;
				Circle b;
				int j = e.index;
				if (e.type == SweepEventType.ENTERROADCAPSULE) {
					a = e.circle;
					b = s.getCircle(j+1);
				} else {
					a = s.getCircle(j);
					b = e.circle;
				}
				
				while (true) {
					
					hit = MODEL.world.pureGraphBestHitTest(new Capsule(null, a, b));
					
					if (hit == null) {
						
					} else if (hit instanceof Vertex) {
						pos = new VertexPosition((Vertex)hit);
						break;
					} else {
						
						RoadPosition skeletonIntersection = (RoadPosition)((Road)hit).findSkeletonIntersection(a.center, b.center);
						
						if (skeletonIntersection != null) {
							
							if (DMath.lessThanEquals(Point.distance(skeletonIntersection.p, e.p), s.r + s.r)) {
								
								pos = skeletonIntersection;
								
								logger.debug("found intersection");
								
								break;
							}
						}
						
					}
					
					j += (e.type == SweepEventType.ENTERROADCAPSULE) ? 1 : -1;
					
					if (!((e.type == SweepEventType.ENTERROADCAPSULE) ? j < s.size()-1 : j >= 0)) {
						break;
					}
					
					a = s.getCircle(j);
					b = s.getCircle(j+1);
					
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
					
					assert ShapeUtils.intersect(e.circle, (Circle)v.getShape());
					
					e.setVertex(v);
				} else {
					e.setVertex((Vertex)hit2);
				}
				
//				assert MODEL.world.checkConsistency();
				
			} else if (e.type == SweepEventType.ENTERVERTEX) {
				e.setVertex((Vertex)e.shape.parent);
			} else if (e.type == SweepEventType.EXITVERTEX) {
				e.setVertex((Vertex)e.shape.parent);
			} else {
				assert false;
			}
			
			assert e.getVertex() != null;
		}
		
		/*
		 * now go through and create roads
		 */
		for (int i = 0; i < events.size()-1; i++) {
			SweepEvent e0 = events.get(i);
			SweepEvent e1 = events.get(i+1);
			
			if (e0.type == SweepEventType.ENTERVERTEX && e1.type == SweepEventType.EXITVERTEX) {
				
				logger.debug("skipping");
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			} else if (e0.type == SweepEventType.ENTERROADCAPSULE && e1.type == SweepEventType.EXITROADCAPSULE) {
				
				logger.debug("skipping");
				
				i = i+1;
				if (i == events.size()-1) {
					break;
				}
				e0 = events.get(i);
				e1 = events.get(i+1);
				
			}
			
			Vertex v0 = e0.getVertex();
			Vertex v1 = e1.getVertex();
			
			if (v0 == v1) {
				logger.debug("same vertex");
				continue;
			}
			
			List<Point> roadPts = new ArrayList<Point>();
			roadPts.add(e0.p);
			for (int j = e0.index+1; j < e1.index; j++) {
				roadPts.add(s.get(j));
			}
			roadPts.add(s.get(e1.index));
			if (!DMath.equals(e1.param, 0.0)) {
				roadPts.add(e1.p);
			}
			
			MODEL.world.createRoadTop(v0, v1, roadPts);
		}
		
	}
	
}
