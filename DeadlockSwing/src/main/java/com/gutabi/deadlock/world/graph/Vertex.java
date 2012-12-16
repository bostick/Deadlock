package com.gutabi.deadlock.world.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.car.Driver;

//@SuppressWarnings("static-access")
public abstract class Vertex extends Entity {
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Road.ROAD_RADIUS * Road.ROAD_RADIUS);
	
	public final World world;
	public final Point p;
	
	public final List<Road> roads = new ArrayList<Road>();
	public Merger m;
	
	public int id;
	
	protected double r;
	
	public final List<Driver> driverQueue = new ArrayList<Driver>();
	
	protected Color hiliteColor;
	
	protected Circle shape;
	
	private int hash;
	
	static Logger logger = Logger.getLogger(Vertex.class);
	
	public Vertex(World world, Point p) {
		
		this.world = world;
		this.p = p;
		
		r = INIT_VERTEX_RADIUS;
		
		shape = new Circle(this, p, r);
		
	}
	
	public int hashCode() {
		if (hash == 0) {
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
	
	public Circle getShape() {
		return shape;
	}
	
	public List<Edge> getEdges() {
		List<Edge> eds = new ArrayList<Edge>();
		eds.addAll(roads);
		if (m != null) {
			eds.add(m);
		}
		return eds;
	}
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
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
			logger.debug("aabb before: " + shape.getAABB());
		}
		
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
			logger.debug("aabb after: " + shape.getAABB());
		}
		
	}
	
	
	public static List<Edge> commonEdges(Vertex a, Vertex b) {
		
		List<Edge> common = new ArrayList<Edge>();
		
		if (a == b) {
			
			for (Road r : a.roads) {
				if (r.start == a && r.end == a) {
					common.add(r);
				}
			}
			
		} else {
			
			for (Road r1 : a.roads) {
				for (Road r2 : b.roads) {
					if (r1 == r2) {
						common.add(r1);
					}
				}
			}
			
		}
		
		if (a.m != null && a.m == b.m) {
			common.add(a.m);
		}
		
		return common;
	}
	

	
	public abstract void paint(RenderingContext ctxt);
	
	public abstract void paintScene(RenderingContext ctxt);
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(hiliteColor);
		shape.paint(ctxt);
	}
	
	public void paintID(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		
		ctxt.paintString(p.x-r, p.y, 1.0 / world.pixelsPerMeter, id + " " + driverQueue.size());
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
