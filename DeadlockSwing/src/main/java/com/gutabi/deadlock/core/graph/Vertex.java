package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;
import com.gutabi.deadlock.core.geom.SweepEventListener;
import com.gutabi.deadlock.core.geom.SweepUtils;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public abstract class Vertex implements Entity, Sweepable {
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Road.ROAD_RADIUS * Road.ROAD_RADIUS);
	
	public final Point p;
	
	protected final List<Road> roads = new ArrayList<Road>();
	protected Merger m;
	
	public int id;
	
	protected double r;
	
	public final List<Car> carQueue = new ArrayList<Car>();
	
	protected Color hiliteColor;
		
	private final int hash;
	
	public Vertex(Point p) {
		
		this.p = p;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
		
		r = INIT_VERTEX_RADIUS;
		
		computeAABB();
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return "Vertex[" + id + "]";
	}
	
	public double getRadius() {
		return r;
	}
	
	
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		
		Point c = s.pts.get(0);
		
		if (bestHitTest(c, s.r) != null) {
			SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, this, s, 0, 0.0);
			l.start(e);
		}
		
	}
	
//	public void sweepEnd(Stroke s, SweepEventListener l) {
//		
//		Point d = s.pts.get(s.pts.size()-1);
//		
//		if (bestHitTest(d, s.r)) {
//			SweepEvent e = new SweepEvent(SweepEventType.EXITVERTEX, this, s, s.pts.size()-1, 0.0);
//			l.end(e);
//		}
//		
//	}
	
	public void sweep(Stroke s, int index, SweepEventListener l) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (bestHitTest(c, s.r) != null) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = SweepUtils.sweepCircleCircle(p, c, d, s.r, r, params);
		
		Arrays.sort(params);
		
		if (paramCount == 2) {
			String.class.getName();
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			if (DMath.lessThan(param, 1.0) || index == s.pts.size()-1) {
				if (outside) {
					l.event(new SweepEvent(SweepEventType.ENTERVERTEX, this, s, index, param));
				} else {
					l.event(new SweepEvent(SweepEventType.EXITVERTEX, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}


	
	
	
	public Entity hitTest(Point p) {
		if (aabb.hitTest(p)) {
			
			if (DMath.lessThanEquals(Point.distance(p, this.p), r)) {
				return this;
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}
	
	public Entity bestHitTest(Point p, double radius) {
		if (DMath.lessThanEquals(Point.distance(p, this.p), r + radius)) {
			return this;
		} else {
			return null;
		}
	}
	
	public VertexPosition skeletonHitTest(Point p) {
		if (p.equals(this.p)) {
			return new VertexPosition(this);
		}
		return null;
	}
	
	private void computeAABB() {
		aabb = new Rect(p.x-r, p.y-r, 2*r, 2*r);
	}
	
	protected Rect aabb;
	public final Rect getAABB() {
		return aabb;
	}
	
	protected void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabb.x * MODEL.PIXELS_PER_METER),
				(int)(aabb.y * MODEL.PIXELS_PER_METER),
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
	public void computeRadius(double maximumRadius) {
		
		r = INIT_VERTEX_RADIUS;
		for (Road e : roads) {
			e.computeProperties();
		}
		
		loop:
		while (true) {
			
			boolean tooClose = false;
			
			for (int i = 0; i < roads.size()-1; i++) {
				Road ei = roads.get(i);
				
				Point borderi;
				if (ei.start == this) {
					borderi = ei.getStartBorderPoint();
					
					for (int j = i+1; j < roads.size(); j++) {
						Road ej = roads.get(j);
						
						Point borderj;
						if (ej.start == this && ei != ej) {
							borderj = ej.getStartBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Road.ROAD_RADIUS + Road.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
						if (ej.end == this) {
							borderj = ej.getEndBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Road.ROAD_RADIUS + Road.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
					}
					
				}
				
				if (ei.end == this) {
					borderi = ei.getEndBorderPoint();
					
					for (int j = i+1; j < roads.size(); j++) {
						Road ej = roads.get(j);
						
						Point borderj;
						if (ej.start == this) {
							borderj = ej.getStartBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Road.ROAD_RADIUS + Road.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
						if (ej.end == this && ei != ej) {
							borderj = ej.getEndBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Road.ROAD_RADIUS + Road.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
					}
				}
				
			}
			
			if (!tooClose) {
				break loop;
			} else if (r + 0.1 > maximumRadius) {
				break loop;
			} else {
				r = r + 0.1;
			}
			
			for (Road e : roads) {
				e.computeProperties();
			}
			
		}
		
		computeAABB();
		
		for (Road e : roads) {
			e.computeProperties();
		}
	}
	
	
	public static List<Edge> commonEdges(Vertex a, Vertex b) {
		
		if (a == b) {
			throw new IllegalArgumentException();
		}
		
		List<Edge> common = new ArrayList<Edge>();
		
		for (Road r1 : a.roads) {
			for (Road r2 : b.roads) {
				if (r1 == r2) {
					common.add(r1);
				}
			}
		}
		
		if (a.m != null && a.m == b.m) {
			common.add(a.m);
		}
		
		return common;
	}
	

	
	/**
	 * @param g2 in world coords
	 */
	public abstract void paint(Graphics2D g2);
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(hiliteColor);
		
		g2.fillOval(
				(int)((p.x - r) * MODEL.PIXELS_PER_METER),
				(int)((p.y - r) * MODEL.PIXELS_PER_METER),
				(int)((2 * r) * MODEL.PIXELS_PER_METER),
				(int)((2 * r) * MODEL.PIXELS_PER_METER));
		
		g2.setTransform(origTransform);
		
	}
	
	/**
	 * 
	 * @param g2 in pixels, <0, 0> is world origin
	 */
	public void paintID(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		Point worldPoint = p.minus(new Point(r, 0));
		Point panelPoint = worldPoint.multiply(MODEL.PIXELS_PER_METER);
		
		g2.drawString(Integer.toString(id), (int)(panelPoint.x), (int)(panelPoint.y));
		
		g2.drawString(Integer.toString(carQueue.size()), (int)(panelPoint.x + 10), (int)(panelPoint.y));
	}
	
	public void check() {
		assert p != null;
		
//		int edgeCount = eds.size();
		
//		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
//		assert edgeCount != 2;
		
		int count;
		for (Road e : roads) {
			
			count = 0;
			for (Road f : roads) {
				if (e == f) {
					count++;
				}
			}
			if (e.start == this && e.end == this) {
				/*
				 * loop with one intersection, so count of roads is 2 for this road
				 */
				assert count == 2;
			} else {
				/*
				 * otherwise, all roads in v should be unique
				 */
				assert count == 1;
			}
		}
		
	}
	
}
