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
import com.gutabi.deadlock.model.Car;

@SuppressWarnings("static-access")
public abstract class Vertex extends Entity {
	
	public final Point p;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	
	public int id;
	
	protected double r;
	
	public final List<Car> queue = new ArrayList<Car>();
	
	protected Color color;
	protected Color hiliteColor;
	
	private final int hash;
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Edge.ROAD_RADIUS * Edge.ROAD_RADIUS);
	
	public Vertex(Point p) {
		
		this.p = p;
		
		r = INIT_VERTEX_RADIUS;
		
		computeAABB();
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
		
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
	
	public void addEdge(Edge e) {
		assert e != null;
		eds.add(e);
	}
	
	public void removeEdge(Edge e) {
		assert eds.contains(e);
		eds.remove(e);
	}
	
	
	
	public boolean hitTest(Point p, double radius) {
		return DMath.lessThanEquals(Point.distance(p, this.p), radius + r);
	}
	
	public VertexPosition findClosestVertexPosition(Point a, double radius) {
		double dist = Point.distance(a, p);
		if (DMath.lessThanEquals(dist, radius + r)) {
			return new VertexPosition(this);
		} else {
			return null;
		}
	}
	
	public void postStop() {
		queue.clear();
	}
	
	private void computeAABB() {
		aabb = new Rect(p.x-r, p.y-r, 2*r, 2*r);
	}
	
	protected Rect aabb;
	public final Rect getAABB() {
		return aabb;
	}
	
	public final boolean hitTest(Point p) {
		if (DMath.lessThanEquals(aabb.x, p.x) && DMath.lessThanEquals(p.x, aabb.x+aabb.width) &&
				DMath.lessThanEquals(aabb.y, p.y) && DMath.lessThanEquals(p.y, aabb.y+aabb.height)) {
			return hitTest(p, 0.0);
		} else {
			return false;
		}
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
	
	
	public static List<Edge> commonEdges(Vertex a, Vertex b) {
		
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
	
	public static Edge commonEdge(Vertex a, Vertex b) {
		
		List<Edge> common = commonEdges(a, b);
		
		if (common.size() != 1) {
			throw new IllegalArgumentException();
		} else {
			return common.get(0);
		}
		
	}
	
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
		
		g2.drawString(Integer.toString(queue.size()), (int)(panelPoint.x + 10), (int)(panelPoint.y));
	}
	
	public void check() {
		assert p != null;
		
		int edgeCount = eds.size();
		
		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
		assert edgeCount != 2;
		
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
