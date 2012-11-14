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
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;
import com.gutabi.deadlock.core.geom.Sweeper;

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
	
	
	private Rect aabb;
	
	public final Rect getAABB() {
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
			circs.add(new Circle(this, p, r));
		}
		
		List<Capsule> caps = new ArrayList<Capsule>();
		for (int i = 0; i < pts.size()-1; i++) {
			Circle a = circs.get(i);
			Circle b = circs.get(i+1);
			caps.add(new Capsule(this, a, b));
		}
		
		sweeper = new Sweeper(this, caps) {
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
		
		/*
		 * go through and give vertex events higher precedence over capsule events
		 * remove any capsule events between matching vertex events
		 * and also remove the matching capsule event
		 */
		
		List<SweepEvent> adj = new ArrayList<SweepEvent>();
		boolean insideCircle = false;
		boolean lookingForExitCapsule = false;
		for (int i = 0; i < vertexEvents.size(); i++) {
			SweepEvent e = vertexEvents.get(i);
			if (!insideCircle) {
				if (e.type == SweepEventType.ENTERCIRCLE) {
					insideCircle = true;
					adj.add(e);
				} else if (e.type == SweepEventType.EXITCIRCLE) {
					assert false;
				} else {
					if (lookingForExitCapsule) {
						if (e.type == SweepEventType.EXITCAPSULE) {
							lookingForExitCapsule = false;
						} else {
							/*
							 * finish implementing
							 */
							assert false;
						}
					} else {
						adj.add(e);
					}
				}
			} else {
				if (e.type == SweepEventType.ENTERCIRCLE) {
					assert false;
				} else if (e.type == SweepEventType.EXITCIRCLE) {
					insideCircle = false;
					adj.add(e);
				} else if (e.type == SweepEventType.ENTERCAPSULE) {
					lookingForExitCapsule = true;
				} else if (e.type == SweepEventType.EXITCAPSULE) {
					assert lookingForExitCapsule;
					lookingForExitCapsule = false;
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
		case ENTERCAPSULE:
			capsuleCount++;
//			if ((vertexCount + capsuleCount + mergerCount) == 1) {
//				vertexEvents.add(e);
//			}
			if (capsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERCIRCLE:
			vertexCount++;
//			if ((vertexCount + capsuleCount + mergerCount) == 1) {
//				vertexEvents.add(e);
//			}
			if (vertexCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case ENTERQUAD:
			mergerCount++;
//			if ((vertexCount + capsuleCount + mergerCount) == 1) {
//				vertexEvents.add(e);
//			}
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
		case ENTERCIRCLE:
			vertexCount++;
//			if ((vertexCount + capsuleCount + mergerCount) == 1) {
//				vertexEvents.add(e);
//			}
			if (vertexCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITCIRCLE:
			vertexCount--;
			assert vertexCount >= 0;
//			if ((vertexCount + capsuleCount + mergerCount) == 0) {
//				vertexEvents.add(e);
//			}
			if (vertexCount == 0) {
				vertexEvents.add(e);
			}
			break;
		case ENTERCAPSULE:
			capsuleCount++;
//			if ((vertexCount + capsuleCount + mergerCount) == 1) {
//				vertexEvents.add(e);
//			}
			if (capsuleCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITCAPSULE:
			capsuleCount--;
			assert capsuleCount >= 0;
//			if ((vertexCount + capsuleCount + mergerCount) == 0) {
//				vertexEvents.add(e);
//			}
			if (capsuleCount == 0) {
				vertexEvents.add(e);
			}
			break;
		case ENTERQUAD:
			mergerCount++;
//			if ((vertexCount + capsuleCount + mergerCount) == 1) {
//				vertexEvents.add(e);
//			}
			if (mergerCount == 1) {
				vertexEvents.add(e);
			}
			break;
		case EXITQUAD:
			mergerCount--;
			assert mergerCount >= 0;
//			if ((vertexCount + capsuleCount + mergerCount) == 0) {
//				vertexEvents.add(e);
//			}
			if (mergerCount == 0) {
				vertexEvents.add(e);
			}
			break;
		}
	}
	
	
	private void computeAABB() {
		
		aabb = null;
		
		for (Point p : pts) {
			aabb = Rect.union(aabb, new Rect(p.x-r, p.y-r, 2*r, 2*r));
		}
		
	}
	
	public void paint(Graphics2D g2) {
		
		if (!MODEL.DEBUG_DRAW) {
			
			paintStroke(g2);
			
		} else {
			
			paintStroke(g2);
			paintAABB(g2);
			
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
	
	private void paintAABB(Graphics2D g2) {
		
		if (!pts.isEmpty()) {
			
			g2.setColor(Color.BLACK);
			g2.drawRect(
					(int)(aabb.x * MODEL.PIXELS_PER_METER),
					(int)(aabb.y * MODEL.PIXELS_PER_METER),
					(int)(aabb.width * MODEL.PIXELS_PER_METER),
					(int)(aabb.height * MODEL.PIXELS_PER_METER));
			
		}
		
	}
	
}
