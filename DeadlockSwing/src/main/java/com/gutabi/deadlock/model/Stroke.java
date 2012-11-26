package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEventType;
import com.gutabi.deadlock.core.geom.Sweeper;
import com.gutabi.deadlock.core.geom.tree.AABB;

@SuppressWarnings("static-access")
public class Stroke {
	
	public List<Point> pts;
	
	public final double r;
	
	private 
	
	static Logger logger = Logger.getLogger(Stroke.class);
	
	public Stroke(double r) {
		pts = new ArrayList<Point>();
		this.r = r;
	}
	
	
	private AABB aabb;
	
	public final AABB getAABB() {
		return aabb;
	}
	
	public double getRadius() {
		return r;
	}
	
	public void add(Point p) {
		
		pts.add(p);
		
		computeAABB();
	}
	
	List<SweepEvent> events;
	Sweeper sweeper;
	int vertexCount;
	int capsuleCount;
	int mergerCount;
	List<SweepEvent> vertexEvents;
	
	public List<SweepEvent> events() {
		
		vertexCount = 0;
		capsuleCount = 0;
		mergerCount = 0;
		vertexEvents = new ArrayList<SweepEvent>();
		
		List<Circle> circs = new ArrayList<Circle>();
		for (Point p : pts) {
			circs.add(new Circle(null, p, r));
		}
		
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < pts.size()-1; i++) {
			Circle a = circs.get(i);
			Circle b = circs.get(i+1);
			caps.add(new Capsule(null, a, b));
		}
		
		sweeper = new Sweeper(null, caps) {
			public void start(SweepEvent e) {
				events.add(e);
			}
			
			public void event(SweepEvent e) {
				events.add(e);
			}
		};
		
		events = new ArrayList<SweepEvent>();
		
		MODEL.world.sweepStart(sweeper);
		
		Collections.sort(events, SweepEvent.COMPARATOR);
		
		for (SweepEvent e : events) {
			startSorted(e);
		}
		
		if ((vertexCount + capsuleCount + mergerCount) == 0) {
			logger.debug("start in nothing");
			vertexEvents.add(new SweepEvent(null, null, sweeper, 0, 0.0));
		} else {
			logger.debug("end counts: " + vertexCount + " " + capsuleCount + " " + mergerCount);
		}
		
		for (int i = 0; i < pts.size()-1; i++) {
			
			events = new ArrayList<SweepEvent>();
			
			MODEL.world.sweep(sweeper, i);
			
			Collections.sort(events, SweepEvent.COMPARATOR);
			
			for (SweepEvent e : events) {
				eventSorted(e);
			}
		}
		
		if ((vertexCount + capsuleCount + mergerCount) == 0) {
			logger.debug("end in nothing");
			vertexEvents.add(new SweepEvent(null, null, sweeper, pts.size()-1, 0.0));
		}
		
		
		List<SweepEvent> adj = new ArrayList<SweepEvent>();
		
		for (int i = 0; i < vertexEvents.size(); i++) {
			SweepEvent e = vertexEvents.get(i);
			if (e.type == SweepEventType.EXITMERGER) {
				/*
				 * if there is an EXITMERGER event, then ignore this stroke
				 */
				return adj;
			}
		}
		
		/*
		 * go through and give vertex events higher precedence over capsule events
		 * remove any capsule events between matching vertex events
		 * and also remove the matching capsule event
		 */
		
		boolean insideCircle = false;
		boolean lookingForExitCapsule = false;
		boolean lookingForExitQuad = false;
		for (int i = 0; i < vertexEvents.size(); i++) {
			SweepEvent e = vertexEvents.get(i);
			if (!insideCircle) {
				if (e.type == SweepEventType.ENTERVERTEX) {
					insideCircle = true;
					adj.add(e);
				} else if (e.type == SweepEventType.EXITVERTEX) {
					assert false;
				} else {
					if (!lookingForExitCapsule && !lookingForExitQuad) {
						adj.add(e);
					} else {
						if (lookingForExitCapsule) {
							if (e.type == SweepEventType.EXITROADCAPSULE) {
								lookingForExitCapsule = false;
							}
						}
						if (lookingForExitQuad) {
							if (e.type == SweepEventType.EXITMERGER) {
								lookingForExitQuad = false;
							}
						}
					}
				}
			} else {
				if (e.type == SweepEventType.ENTERVERTEX) {
					assert false;
				} else if (e.type == SweepEventType.EXITVERTEX) {
					insideCircle = false;
					adj.add(e);
				} else if (e.type == SweepEventType.ENTERROADCAPSULE) {
					lookingForExitCapsule = true;
				} else if (e.type == SweepEventType.EXITROADCAPSULE) {
					assert lookingForExitCapsule;
					lookingForExitCapsule = false;
				} else if (e.type == SweepEventType.ENTERMERGER) {
					lookingForExitQuad = true;
				} else if (e.type == SweepEventType.EXITMERGER) {
					assert lookingForExitQuad;
					lookingForExitQuad = false;
				} else {
					/*
					 * finish implementing
					 */
					assert false;
				}
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("vertexEvents:");
			logger.debug(adj);
		}
		
		return adj;
	}
	
	private void startSorted(SweepEvent e) {
		if (logger.isDebugEnabled()) {
			logger.debug("startSorted: " + e + " " + e.shape + " " + e.index + " " + e.param);
		}
		switch (e.type) {
		case ENTERROADCAPSULE:
			capsuleCount++;
			if (capsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERVERTEX:
			vertexCount++;
			if (vertexCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERMERGER:
			mergerCount++;
			if (mergerCount == 1) {
				vertexEvents.add(e);
			}
			break;
		default:
			assert false;
			break;
		}
	}
	
	private void eventSorted(SweepEvent e) {
		if (logger.isDebugEnabled()) {
			logger.debug("eventSorted: " + e + " " + e.shape + " " + e.index + " " + e.param);
		}
		switch (e.type) {
		case ENTERVERTEX:
			vertexCount++;
			if (vertexCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITVERTEX:
			vertexCount--;
			assert vertexCount >= 0;
			if (vertexCount == 0) {
				vertexEvents.add(e);
			}
			break;
		case ENTERROADCAPSULE:
			capsuleCount++;
			if (capsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITROADCAPSULE:
			capsuleCount--;
			assert capsuleCount >= 0;
			if (capsuleCount == 0) {
				vertexEvents.add(e);
			}
			break;
		case ENTERMERGER:
			mergerCount++;
			if (mergerCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITMERGER:
			mergerCount--;
			assert mergerCount >= 0;
			if (mergerCount == 0) {
				vertexEvents.add(e);
			}
			break;
		}
	}
	
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Point p : pts) {
			aabb = AABB.union(aabb, new AABB(null, p.x-r, p.y-r, 2*r, 2*r));
		}
		
	}
	
	public void paint(Graphics2D g2) {
			
		paintStroke(g2);
		
		if (MODEL.DEBUG_DRAW) {
			
//			paintAABB(g2);
			aabb.draw(g2);
			
		}
		
	}
	
	private void paintStroke(Graphics2D g2) {
		
		if (!pts.isEmpty()) {
			
			g2.setColor(Color.RED);
			
			int size = pts.size();
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			for (int i = 0; i < size; i++) {
				Point p = pts.get(i);
				xPoints[i] = (int)(p.x * MODEL.PIXELS_PER_METER);
				yPoints[i] = (int)(p.y * MODEL.PIXELS_PER_METER);
			}
			
			g2.drawPolyline(xPoints, yPoints, size);
			
			Point start = pts.get(0);
			g2.drawOval(
					(int)((start.x - r) * MODEL.PIXELS_PER_METER),
					(int)((start.y - r) * MODEL.PIXELS_PER_METER),
					(int)((2 * r) * MODEL.PIXELS_PER_METER),
					(int)((2 * r) * MODEL.PIXELS_PER_METER));
			
			Point end = pts.get(pts.size()-1);
			g2.drawOval(
					(int)((end.x - r) * MODEL.PIXELS_PER_METER),
					(int)((end.y - r) * MODEL.PIXELS_PER_METER),
					(int)((2 * r) * MODEL.PIXELS_PER_METER),
					(int)((2 * r) * MODEL.PIXELS_PER_METER));
			
		}
		
	}
	
//	private void paintAABB(Graphics2D g2) {
//		
//		if (!pts.isEmpty()) {
//			
//			g2.setColor(Color.BLACK);
//			g2.drawRect(
//					(int)(aabb.x * MODEL.PIXELS_PER_METER),
//					(int)(aabb.y * MODEL.PIXELS_PER_METER),
//					(int)(aabb.width * MODEL.PIXELS_PER_METER),
//					(int)(aabb.height * MODEL.PIXELS_PER_METER));
//			
//		}
//		
//	}
	
}
