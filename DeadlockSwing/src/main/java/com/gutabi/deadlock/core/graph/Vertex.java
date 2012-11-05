package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.graph.SweepEvent.SweepEventType;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public abstract class Vertex implements Entity {
	
	public final Point p;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	protected Merger m;
	
	public int id;
	
	protected double r;
	
	public final List<Car> carQueue = new ArrayList<Car>();
	
	protected Color color;
	protected Color hiliteColor;
	
	SweepEventListener l;
	
	private final int hash;
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Edge.ROAD_RADIUS * Edge.ROAD_RADIUS);
	
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
		return Integer.toString(id);
	}
	
	public double getRadius() {
		return r;
	}
	
//	public void addEdge(Edge e) {
//		assert e != null;
//		eds.add(e);
//	}
//	
//	public void removeEdge(Edge e) {
//		assert eds.contains(e);
//		eds.remove(e);
//	}
	
	
	
	public void setSweepEventListener(SweepEventListener l) {
		this.l = l;
	}
	
	public void sweepStart(Stroke s) {
		
		Point c = s.pts.get(0);
		
		if (hitTest(c, s.r)) {
			SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, c, 0, 0.0);
//			e.setVertex(this);
			l.start(e);
		}
		
	}
	
	public void sweep(Stroke s, int index) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (hitTest(c, s.r)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double aCoeff = ((d.x - c.x)*(d.x - c.x) + (d.y - c.y)*(d.y - c.y));
		double bCoeff = -2 * ((d.x - c.x)*(p.x - c.x) + (d.y - c.y)*(p.y - c.y));
		double cCoeff = ((p.x - c.x)*(p.x - c.x) + (p.y - c.y)*(p.y - c.y) - (s.r + r)*(s.r + r));
		double[] roots = new double[2];
		double discriminant = DMath.quadraticSolve(aCoeff, bCoeff, cCoeff, roots);
		if (DMath.equals(discriminant, 0.0)) {
			/*
			 * 1 event
			 */
			double cdParam = roots[0];
			if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
				Point p = Point.point(c, d, cdParam);
				assert DMath.equals(Point.distance(p, this.p), r + s.r);
				if (outside) {
					SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, p, index, cdParam);
//					e.setVertex(this);
					l.event(e);
				} else {
					l.event(new SweepEvent(SweepEventType.EXITVERTEX, p, index, cdParam));
				}
			}

		} else if (discriminant > 0) {
			/*
			 * 2 events
			 */
			double cdParam0 = roots[0];
			if (DMath.greaterThanEquals(cdParam0, 0.0) && DMath.lessThanEquals(cdParam0, 1.0)) {
				Point p0 = Point.point(c, d, cdParam0);
				assert DMath.equals(Point.distance(p0, this.p), r + s.r);
				if (outside) {
					SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, p0, index, cdParam0);
//					e.setVertex(this);
					l.event(e);
				} else {
					l.event(new SweepEvent(SweepEventType.EXITVERTEX, p0, index, cdParam0));
				}
			}
			double cdParam1 = roots[1];
			if (DMath.greaterThanEquals(cdParam1, 0.0) && DMath.lessThanEquals(cdParam1, 1.0)) {
				Point p1 = Point.point(c, d, cdParam1);
				assert DMath.equals(Point.distance(p1, this.p), r + s.r);
				if (outside) {
					SweepEvent e = new SweepEvent(SweepEventType.ENTERVERTEX, p1, index, cdParam1);
//					e.setVertex(this);
					l.event(e);
				} else {
					l.event(new SweepEvent(SweepEventType.EXITVERTEX, p1, index, cdParam1));
				}
			}
			
		} else {
			/*
			 * 0 events
			 */
		}
		
		
	}


	
	
	
	public final boolean hitTest(Point p) {
		if (aabb.hitTest(p)) {
			
			return DMath.lessThanEquals(Point.distance(p, this.p), r);
			
		} else {
			return false;
		}
	}
	
	public final boolean hitTest(Point p, double radius) {
		return DMath.lessThanEquals(Point.distance(p, this.p), r + radius);
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
		for (Edge e : eds) {
			e.computeProperties();
		}
		
		loop:
		while (true) {
			
			boolean tooClose = false;
			
			for (int i = 0; i < eds.size()-1; i++) {
				Edge ei = eds.get(i);
				
				Point borderi;
				if (ei.start == this) {
					borderi = ei.getStartBorderPoint();
					
					for (int j = i+1; j < eds.size(); j++) {
						Edge ej = eds.get(j);
						
						Point borderj;
						if (ej.start == this && ei != ej) {
							borderj = ej.getStartBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Edge.ROAD_RADIUS + Edge.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
						if (ej.end == this) {
							borderj = ej.getEndBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Edge.ROAD_RADIUS + Edge.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
					}
					
				}
				
				if (ei.end == this) {
					borderi = ei.getEndBorderPoint();
					
					for (int j = i+1; j < eds.size(); j++) {
						Edge ej = eds.get(j);
						
						Point borderj;
						if (ej.start == this) {
							borderj = ej.getStartBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Edge.ROAD_RADIUS + Edge.ROAD_RADIUS + 0.1)) {
								tooClose = true;
							}
						}
						
						if (ej.end == this && ei != ej) {
							borderj = ej.getEndBorderPoint();
							if (DMath.lessThan(Point.distance(borderi, borderj), Edge.ROAD_RADIUS + Edge.ROAD_RADIUS + 0.1)) {
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
			
			for (Edge e : eds) {
				e.computeProperties();
			}
			
		}
		
		computeAABB();
		
		for (Edge e : eds) {
			e.computeProperties();
		}
	}
	
	
	public static List<EdgeX> commonEdgesX(Vertex a, Vertex b) {
		
		if (a == b) {
			throw new IllegalArgumentException();
		}
		
		List<Edge> common = new ArrayList<Edge>();
		for (Edge e1 : a.eds) {
			for (Edge e2 : b.eds) {
				if (e1 == e2) {
					common.add(e1);
				}
			}
		}
		
		return common;
	}
	
//	public static EdgeX commonEdgeX(Vertex a, Vertex b) {
//		
//		List<Edge> common = commonEdges(a, b);
//		
//		if (common.size() != 1) {
//			throw new IllegalArgumentException();
//		} else {
//			return common.get(0);
//		}
//		
//	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(color);
		
		g2.fillOval(
				(int)((p.x - r) * MODEL.PIXELS_PER_METER),
				(int)((p.y - r) * MODEL.PIXELS_PER_METER),
				(int)((2 * r) * MODEL.PIXELS_PER_METER),
				(int)((2 * r) * MODEL.PIXELS_PER_METER));
		
		g2.setTransform(origTransform);
		
		if (MODEL.DEBUG_DRAW) {
			
			g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
			
			paintAABB(g2);
			
			g2.setTransform(origTransform);
			
		}
		
	}
	
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
		for (Edge e : eds) {
			
			count = 0;
			for (Edge f : eds) {
				if (e == f) {
					count++;
				}
			}
			if (e.start == this && e.end == this) {
				/*
				 * loop with one intersection, so count of edges is 2 for this edge
				 */
				assert count == 2;
			} else {
				/*
				 * otherwise, all edges in v should be unique
				 */
				assert count == 1;
			}
		}
		
	}
	
}
