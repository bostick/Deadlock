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
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Direction;
import com.gutabi.deadlock.core.graph.EdgePosition;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.Intersection;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Side;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.core.graph.VertexPosition;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Fixture;
import com.gutabi.deadlock.model.FixtureType;
import com.gutabi.deadlock.model.StopSign;
import com.gutabi.deadlock.model.Stroke;
import com.gutabi.deadlock.model.cursor.FixtureCursor;
import com.gutabi.deadlock.model.cursor.MergerCursor;
import com.gutabi.deadlock.model.cursor.RegularCursor;
import com.gutabi.deadlock.model.cursor.StraightEdgeCursor;

@SuppressWarnings("static-access")
public class DeadlockController implements ActionListener {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public ControlMode mode;
	
	public MouseController mc;
	public KeyboardController kc;
	
	Logger logger = Logger.getLogger(DeadlockController.class);
	
	public DeadlockController() {
		
	}
	
	public void init() {
		
		mc = new MouseController();
		kc = new KeyboardController();
		
		mc.init();
		
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
	
//	AABB originalViewport;
	
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
					
					VIEW.repaintCanvas();
					
				} else {
					assert false;
				}
				
				break;
			}
			case DRAFTING:
				
				draftMove(lastDragCanvasPoint);
				
				VIEW.repaintCanvas();
				break;
				
			case RUNNING:
			case PAUSED:
			case MERGERCURSOR:
			case FIXTURECURSOR:
			case STRAIGHTEDGECURSOR:
			case MENU:
				;
				break;
			}
			
		} else if (c == VIEW.previewPanel) {
			
			VIEW.previewPanel.requestFocusInWindow();
			
			Point penDragPreviewPoint = lastDragPreviewPoint;
			lastDragPreviewPoint = p;
			lastDragTime = System.currentTimeMillis();
			
			if (penDragPreviewPoint != null) {
				
				double dx = lastDragPreviewPoint.x - penDragPreviewPoint.x;
				double dy = lastDragPreviewPoint.y - penDragPreviewPoint.y;
				
				VIEW.pan(new Point(dx, dy));
				
				VIEW.renderWorldBackground();
				VIEW.repaintCanvas();
				VIEW.repaintControlPanel();
				
			}
			
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
				
				VIEW.renderWorldBackground();
				VIEW.repaintCanvas();
				VIEW.repaintControlPanel();
				
				break;
			case RUNNING:
			case PAUSED:
			case MERGERCURSOR:
			case FIXTURECURSOR:
			case STRAIGHTEDGECURSOR:
			case MENU:
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
			
//			if (logger.isDebugEnabled()) {
//				logger.debug("moved. canvas: " + ev.p + " " + " world: " + lastMovedWorldPoint + " viewport: " + VIEW.worldViewport.ul);
//			}
			
			switch (mode) {
			case PAUSED:
			case DRAFTING:
				assert false;
				break;
			case IDLE:
			case RUNNING:
				
				Entity closest = MODEL.world.hitTest(lastMovedWorldPoint);
				MODEL.hilited = closest;
				
				if (MODEL.cursor != null) {
					if (MODEL.grid) {
						
						Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
						MODEL.cursor.setPoint(closestGridPoint);
						
					} else {
						MODEL.cursor.setPoint(lastMovedWorldPoint);
					}
				}
				
				VIEW.repaintCanvas();
				break;
				
			case MERGERCURSOR:
			case FIXTURECURSOR:
			case STRAIGHTEDGECURSOR:
				
				MODEL.hilited = null;
				
				if (MODEL.cursor != null) {
					if (MODEL.grid) {
						
						Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
						MODEL.cursor.setPoint(closestGridPoint);
						
					} else {
						MODEL.cursor.setPoint(lastMovedWorldPoint);
					}
				}
				
				VIEW.repaintCanvas();
				break;
			case MENU:
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
			case MENU:
				break;
			case IDLE:
			case MERGERCURSOR:
			case FIXTURECURSOR:
			case STRAIGHTEDGECURSOR:
				
				if (MODEL.cursor != null) {
					MODEL.cursor.setPoint(null);
				}
				
				VIEW.repaintCanvas();
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
			case MENU:
				break;
			case IDLE:
			case MERGERCURSOR:
			case FIXTURECURSOR:
			case STRAIGHTEDGECURSOR:
				if (MODEL.cursor != null) {
					if (MODEL.grid) {
						
						Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
						MODEL.cursor.setPoint(closestGridPoint);
						
					} else {
						MODEL.cursor.setPoint(lastMovedWorldPoint);
					}
				}
				
				VIEW.repaintCanvas();
				break;
			}
			
		}
		
	}
	
	public void qKey() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.world.completelyContains(MODEL.cursor.getShape())) {
				
				Entity hit = MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape());
				if (hit == null) {
					
					MODEL.world.addVertexTop(new Intersection(MODEL.cursor.getPoint()));
					
					VIEW.renderWorldBackground();
					VIEW.repaintControlPanel();
					VIEW.repaintCanvas();
					
				} else if (hit instanceof Vertex) {
					
					mode = ControlMode.STRAIGHTEDGECURSOR;
					
					MODEL.cursor = new StraightEdgeCursor((Vertex)hit);
					
					MODEL.cursor.setPoint(lastMovedWorldPoint);
					
					VIEW.repaintCanvas();
					
				}
			}
			
			break;
			
		case STRAIGHTEDGECURSOR:
			
			Vertex first = ((StraightEdgeCursor)MODEL.cursor).first;
			Vertex second = null;
			
			Entity hit = MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape());
			if (hit == null) {
				
				second = new Intersection(MODEL.cursor.getPoint());
				
				MODEL.world.addVertexTop(second);
				
			} else if (hit instanceof Vertex) {
				
				second = (Vertex)hit;
				
			} else if (hit instanceof Road) {
				
				RoadPosition pos = MODEL.world.findClosestRoadPosition(MODEL.cursor.getPoint(), Vertex.INIT_VERTEX_RADIUS);
				
				assert pos != null;
				
				second = MODEL.world.splitRoadTop(pos);
				
			}
			
			if (second != null) {
				
				List<Point> roadPts = new ArrayList<Point>();
				roadPts.add(first.p);
				roadPts.add(second.p);
				MODEL.world.createRoadTop(first, second, roadPts);
				
				VIEW.renderWorldBackground();
				VIEW.repaintControlPanel();
				
			}
			
			mode = ControlMode.IDLE;
			
			MODEL.cursor = new RegularCursor();
			
			MODEL.cursor.setPoint(lastMovedWorldPoint);
			
			VIEW.repaintCanvas();
			
			break;
			
		case RUNNING:
		case PAUSED:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case DRAFTING:
		case MENU:
			break;
		}
		
	}
	
	public void wKey() {
		
		switch (mode) {
		case IDLE:
			
			mode = ControlMode.FIXTURECURSOR;
			
			MODEL.cursor = new FixtureCursor();
			
			MODEL.cursor.setPoint(lastMovedWorldPoint);
			
			VIEW.repaintCanvas();
			
			break;
		case FIXTURECURSOR:
			FixtureCursor fc = (FixtureCursor)MODEL.cursor;
			Axis axis = fc.getAxis();
			
			if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
				
				Fixture source = new Fixture(fc.getSourcePoint(), axis);
				Fixture sink = new Fixture(fc.getSinkPoint(), axis);
				
				source.type = FixtureType.SOURCE;
				sink.type = FixtureType.SINK;
				
				source.match = sink;
				sink.match = source;
				
				switch (axis) {
				case TOPBOTTOM:
					source.s = Side.BOTTOM;
					sink.s = Side.BOTTOM;
					break;
				case LEFTRIGHT:
					source.s = Side.RIGHT;
					sink.s = Side.RIGHT;
					break;
				}
				
				MODEL.world.addVertexTop(source);
				
				MODEL.world.addVertexTop(sink);
				
				mode = ControlMode.IDLE;
				
				MODEL.cursor = new RegularCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				VIEW.renderWorldBackground();
				VIEW.repaintCanvas();
				VIEW.repaintControlPanel();
				
			}
			
			break;
		case RUNNING:
		case PAUSED:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case DRAFTING:
		case MENU:
			break;
		}
		
	}
	
	public void gKey() {
		
		MODEL.grid = !MODEL.grid;
		
		VIEW.renderWorldBackground();
		VIEW.repaintCanvas();
		
	}
	
	public void deleteKey() {
		
		if (MODEL.hilited != null) {
			
			if (MODEL.hilited.isUserDeleteable()) {
				
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
		
		VIEW.renderWorldBackground();
		VIEW.repaintCanvas();
		VIEW.repaintControlPanel();
		
	}
	
	public void insertKey() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					s.setEnabled(true);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
				}
				
			} else {
				
				mode = ControlMode.MERGERCURSOR;
				
				MODEL.cursor = new MergerCursor();
				
				MODEL.cursor.setPoint(lastMovedWorldPoint);
				
				VIEW.repaintCanvas();
				
			}
			
			break;
		case MERGERCURSOR:
			
			if (MODEL.world.completelyContains(MODEL.cursor.getShape())) {
				
				if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
					
					MODEL.world.insertMergerTop(MODEL.cursor.getPoint());
					
					mode = ControlMode.IDLE;
					
					MODEL.cursor = new RegularCursor();
					
					MODEL.cursor.setPoint(lastMovedWorldPoint);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					VIEW.repaintControlPanel();
					
				}
				
			}
			
			break;
		case RUNNING:
		case PAUSED:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case DRAFTING:
		case MENU:
			break;
		}
		
	}
	
	public void escKey() {
		
		switch (mode) {
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			mode = ControlMode.IDLE;
			
			MODEL.cursor = new RegularCursor();
			
			MODEL.cursor.setPoint(lastMovedWorldPoint);
			
			VIEW.repaintCanvas();
			
			break;
		case RUNNING:
		case PAUSED:
		case DRAFTING:
		case IDLE:
		case MENU:
			break;
		}
		
	}
	
	public void d1Key() {
		
		switch (mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(null, Direction.STARTTOEND);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
				} else if (MODEL.hilited instanceof Fixture) {
					Fixture f = (Fixture)MODEL.hilited;
					
					Fixture g = f.match;
					
					if (f.type != null) {
						f.type = f.type.other();
					}
					if (g.type != null) {
						g.type = g.type.other();
					}
					
					f.s = f.s.other();
					g.s = g.s.other();
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
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
					
					r.setDirection(null, Direction.ENDTOSTART);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
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
					
					r.setDirection(null, null);
				
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void plusKey() {
		
		VIEW.zoom(1.1);
		
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
		case STRAIGHTEDGECURSOR:
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case MENU:
			break;
		}
		
	}
	
	public void minusKey() {
		
		VIEW.zoom(0.9);
		
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
		case STRAIGHTEDGECURSOR:
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * lastMovedWorldPoint.x), 2 * Math.round(0.5 * lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(lastMovedWorldPoint);
				}
			}
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case MENU:
			break;
		}
		
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
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			MODEL.FPS_DRAW = state;
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			
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
				
				Entity hit = MODEL.world.pureGraphBestHitTestCircle(e.circle);
				assert hit == null;
				logger.debug("create");
				Intersection v = new Intersection(e.p);
				MODEL.world.addVertexTop(v);
				
				e.setVertex(v);
				
//				assert MODEL.world.checkConsistency();
				
			} else if (e.type == SweepEventType.ENTERROADCAPSULE || e.type == SweepEventType.EXITROADCAPSULE) {
				
				Entity hit = MODEL.world.pureGraphBestHitTestCircle(e.circle);
				
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
					
					hit = MODEL.world.pureGraphBestHitTestCapsule(new Capsule(null, a, b));
					
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
					hit2 = MODEL.world.pureGraphBestHitTestCircle(new Circle(null, pos.p, e.circle.radius));
				} else {
					hit2 = ((VertexPosition)pos).v;
				}
				
				if (hit2 instanceof Road) {
					Vertex v = MODEL.world.splitRoadTop((RoadPosition)pos);
					
					assert ShapeUtils.intersectCC(e.circle, v.getShape());
					
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
