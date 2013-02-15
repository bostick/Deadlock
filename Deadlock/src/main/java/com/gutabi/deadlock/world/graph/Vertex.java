package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.AutonomousDriver;

public abstract class Vertex extends Entity {
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Road.ROAD_RADIUS * Road.ROAD_RADIUS);
	
	public World world;
	public final Point p;
	
	public final List<Road> roads = new ArrayList<Road>();
	public Merger m;
	
	public int id;
	
	protected double r;
	
	public final List<AutonomousDriver> driverQueue = new ArrayList<AutonomousDriver>();
	
	protected Circle shape;
	
	private int hash;
	
	public Vertex(World world, Point p) {
		this.world = world;
		this.p = p;
		
		r = INIT_VERTEX_RADIUS;
		
		shape = APP.platform.createShapeEngine().createCircle(p, r);
		
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
	
	public Road bestMatchingRoad(Road r, Road hint) {
		assert !roads.isEmpty();
		if (r == null) {
			assert roads.size() == 1;
			return roads.get(0);
		} else if (hint != null && roads.contains(hint)) {
			
			return hint;
			
		} else {
			
			double angle;
			if (r.start == this) {
				Point rp = r.getPoint(1);
				angle = Math.atan2(rp.y - p.y, rp.x - p.x);
			} else {
				Point rp = r.getPoint(r.pointCount()-2);
				angle = Math.atan2(rp.y - p.y, rp.x - p.x);
			}
			
			Road bestRoad = null;
			double bestAngleDiff = Double.POSITIVE_INFINITY;
			for (Road rr : roads) {
				if (rr == r) {
					continue;
				}
				double testAngle;
				if (rr.start == this) {
					Point rp = rr.getPoint(1);
					testAngle = Math.atan2(rp.y - p.y, rp.x - p.x);
				} else {
					Point rp = rr.getPoint(rr.pointCount()-2);
					testAngle = Math.atan2(rp.y - p.y, rp.x - p.x);
				}
				
				double testAngleDiff = testAngle - (angle + Math.PI);
				while (testAngleDiff >= Math.PI) {
					testAngleDiff = testAngleDiff - 2 * Math.PI;
				}
				while (testAngleDiff < -Math.PI) {
					testAngleDiff = testAngleDiff + 2 * Math.PI;
				}
				
				if (DMath.lessThan(Math.abs(testAngleDiff), Math.abs(bestAngleDiff))) {
					bestAngleDiff = testAngleDiff;
					bestRoad = rr;
				}
				
			}
			
			assert bestRoad != null;
			return bestRoad;
		}
		
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
		
		r = INIT_VERTEX_RADIUS;
		shape = APP.platform.createShapeEngine().createCircle(p, r);
		
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
				shape = APP.platform.createShapeEngine().createCircle(p, r);
			}
			
			for (Road e : roads) {
				e.computeProperties();
			}
			
		}
		
		for (Road e : roads) {
			e.computeProperties();
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
	

	public abstract String toFileString();
	
	public abstract void paint_panel(RenderingContext ctxt);
	
	public abstract void paint_preview(RenderingContext ctxt);
	
	public abstract void paintScene(RenderingContext ctxt);
	
	public void paintID(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		
		ctxt.paintString(p.x-r, p.y, 1.0/world.screen.pixelsPerMeter, id + " " + driverQueue.size());
	}
	
	public void check() {
		assert p != null;
		
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
