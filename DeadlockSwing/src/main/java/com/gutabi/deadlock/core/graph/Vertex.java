package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.model.Car;

@SuppressWarnings("static-access")
public abstract class Vertex extends GraphEntity {
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Road.ROAD_RADIUS * Road.ROAD_RADIUS);
	
	public final Point p;
	
	protected final List<Road> roads = new ArrayList<Road>();
	protected Merger m;
	
	public int id;
	
	protected double r;
	
	public final List<Car> carQueue = new ArrayList<Car>();
	
	protected Color hiliteColor;
		
	private final int hash;
	
	private final List<Vertex> vs;
	
	static Logger logger = Logger.getLogger(Vertex.class);
	
	public Vertex(Point p) {
		
		this.p = p;
		
		r = INIT_VERTEX_RADIUS;
		
		shape = new Circle(this, p, r);
		
		vs = new ArrayList<Vertex>();
		vs.add(this);
	}
	
	public int hashCode() {
		if () {
			int h = 17;
			h = 37 * h + p.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public String toString() {
		return "Vertex[" + id + "]";
	}
	
	public double getRadius() {
		return r;
	}
	
	public List<Vertex> getVertices(Axis a) {
		return vs;
	}
	
	public abstract boolean supportsStopSigns();
	
	public VertexPosition skeletonHitTest(Point p) {
		if (p.equals(this.p)) {
			return new VertexPosition(this);
		}
		return null;
	}
	
	public void computeRadius(double maximumRadius) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("aabb before: " + shape.aabb);
		}
		
		double oldR = r;
		
		r = INIT_VERTEX_RADIUS;
		shape = new Circle(this, p, r);
		
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
				shape = new Circle(this, p, r);
			}
			
			for (Road e : roads) {
				e.computeProperties();
			}
			
		}
		
		for (Road e : roads) {
			e.computeProperties();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("aabb after: " + shape.aabb);
		}
		
		if (r != oldR) {
			String.class.getName();
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
		
		g2.setColor(hiliteColor);
		
		shape.paint(g2);
		
	}
	
	/**
	 * 
	 * @param g2 in pixels, <0, 0> is world origin
	 */
	public void paintID(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		Point worldPoint = p.minus(new Point(r, 0));
		Point panelPoint = worldPoint.multiply(MODEL.PIXELS_PER_METER);
		
		g2.drawString(id + " " + carQueue.size(), (int)(panelPoint.x), (int)(panelPoint.y));
		
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
