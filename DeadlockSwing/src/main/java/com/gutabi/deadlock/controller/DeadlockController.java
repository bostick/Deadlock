package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

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
import com.gutabi.deadlock.model.StopSign;
import com.gutabi.deadlock.model.Stroke;
import com.gutabi.deadlock.model.car.Car;
import com.gutabi.deadlock.model.graph.EdgePosition;
import com.gutabi.deadlock.model.graph.GraphPosition;
import com.gutabi.deadlock.model.graph.Intersection;
import com.gutabi.deadlock.model.graph.Merger;
import com.gutabi.deadlock.model.graph.Road;
import com.gutabi.deadlock.model.graph.RoadPosition;
import com.gutabi.deadlock.model.graph.Vertex;
import com.gutabi.deadlock.model.graph.VertexPosition;

//@SuppressWarnings("static-access")
public class DeadlockController {
	
	public static DeadlockController CONTROLLER = new DeadlockController();
	
	public ControlMode mode;
	
	public MouseController mc;
	public KeyboardController kc;
	
	Logger logger = Logger.getLogger(DeadlockController.class);
//	Logger reportingLogger = Logger.getLogger("com.gutabi.deadlock.controller.DeadlockController.reporting");
	
	public DeadlockController() {
		mc = new MouseController();
		kc = new KeyboardController();
	}
	
//	public void init() {
//		
//		
//		
//		mc.init();
//		
//		kc.init();
//		
//	}
	
	public void startRunning() {
		
		mode = ControlMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		
		mode = ControlMode.IDLE;
		
	}
	
	public void pauseRunning() {
		
		mode = ControlMode.PAUSED;
	}
	
	public void unpauseRunning() {
		
		mode = ControlMode.RUNNING;
		
		synchronized (MODEL.pauseLock) {
			MODEL.pauseLock.notifyAll();
		}
	}
	
	
	
	public void draftStart(Point p) {
			
		mode = ControlMode.DRAFTING;
		
		MODEL.hilited = null;
		
		MODEL.stroke = new Stroke();
		MODEL.stroke.add(VIEW.canvasToWorld(p));
			
	}
	
	public void draftMove(Point p) {

		MODEL.stroke.add(VIEW.canvasToWorld(p));
	}
	
	public void draftEnd() {
		
		processNewStroke(MODEL.stroke);
		
		assert MODEL.world.checkConsistency();
		
		MODEL.debugStroke2 = MODEL.debugStroke;
		MODEL.debugStroke = MODEL.stroke;
		MODEL.stroke = null;
		
		mode = ControlMode.IDLE;
		
	}
	
	public void removeVertexTop(Vertex v) {
		MODEL.world.removeVertexTop(v);
	}
	
	public void removeRoadTop(Road e) {
		MODEL.world.removeRoadTop(e);
	}
	
	public void removeMergerTop(Merger m) {
		MODEL.world.removeMergerTop(m);
	}
	
	public void removeStopSignTop(StopSign s) {
		MODEL.world.removeStopSignTop(s);
	}
	
	public void removeCarTop(Car c) {
		MODEL.world.removeCarTop(c);
	}
	
	public void processNewStroke(Stroke s) {
		
		/*
		 * TODO:
		 * fix this and turn on
		 * a stroke that starts/ends on a fixture may technically be outside of the quadrant, but it should still count
		 */
//		for (int i = 0; i < s.size()-1; i++) {
//			Capsule cap = new Capsule(null, s.getCircle(i), s.getCircle(i+1));
//			if (!MODEL.world.completelyContains(cap)) {
//				return;
//			}
//		}
		
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
				
			} else if (e.type == SweepEventType.ENTERROADCAPSULE || e.type == SweepEventType.EXITROADCAPSULE) {
				
				Entity hit = MODEL.world.pureGraphBestHitTestCircle(e.circle);
				
				if (hit instanceof Vertex) {
					e.setVertex((Vertex)hit);
					continue;
				}
				
				GraphPosition pos = null;
				
				double nextEventCombo;
				if (e.type == SweepEventType.ENTERROADCAPSULE) {
					if (i < events.size()-1) {
						nextEventCombo = events.get(i+1).combo;
					} else {
						nextEventCombo = Double.POSITIVE_INFINITY;
					}
				} else {
					if (i >= 1) {
						nextEventCombo = events.get(i-1).combo;
					} else {
						nextEventCombo = Double.NEGATIVE_INFINITY;
					}
				}
				
				/*
				 * find better place to split by checking for intersection with road
				 */
				Circle a;
				Circle b;
				int j = e.index;
				if (e.type == SweepEventType.ENTERROADCAPSULE) {
					a = e.circle;
					if (DMath.lessThan(nextEventCombo, j+1)) {
						/*
						 * next event is before next stroke point, so use the event circle
						 */
						b = events.get(i+1).circle;
					} else {
						b = s.getCircle(j+1);
					}
				} else {
					if (DMath.greaterThan(nextEventCombo, j)) {
						a = events.get(i-1).circle;
					} else {
						a = s.getCircle(j);
					}
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
							
							if (DMath.lessThanEquals(Point.distance(skeletonIntersection.p, e.p), Stroke.STROKE_RADIUS + Stroke.STROKE_RADIUS)) {
								
								pos = skeletonIntersection;
								
								logger.debug("found intersection");
								
								break;
							}
						}
						
					}
					
					j += (e.type == SweepEventType.ENTERROADCAPSULE) ? 1 : -1;
					
					if (!((e.type == SweepEventType.ENTERROADCAPSULE) ? j < Math.min(nextEventCombo, s.size()-1) : j >= Math.max(nextEventCombo, 0))) {
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
			if (e1.index >= e0.index+1) {
				roadPts.add(s.get(e1.index));
				if (!DMath.equals(e1.param, 0.0)) {
					roadPts.add(e1.p);
				}
			} else {
				assert e1.index == e0.index;
				roadPts.add(e1.p);
			}
			
			MODEL.world.createRoadTop(v0, v1, roadPts);
		}
		
	}
	
}
