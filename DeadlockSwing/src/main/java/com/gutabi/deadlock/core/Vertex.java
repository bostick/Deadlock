package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.utils.Java2DUtils;

public class Vertex extends Entity {
	
	private final Point p;
	private final int hash;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	
	protected boolean removed = false;
	
	public int id;
	
	List<Point> poly;
	Path2D path;
	double radius;
	
	public Vertex(Point p) {
		
		this.p = p;
		
		radius = 3 * Math.sqrt(2 * MODEL.world.ROAD_RADIUS * MODEL.world.ROAD_RADIUS);
		
		computeArea();
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return "V " + p;
	}
	
	public double getRadius() {
		return radius;
	}
	
	
	public boolean hitTest(Point p) {
		return DMath.lessThanEquals(Point.distance(p, this.p), radius);
	}
	
	
	private void computeArea() {
		Shape s = new Ellipse2D.Double(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
		
		poly = Java2DUtils.shapeToList(s);
		
		path = Java2DUtils.listToPath(poly);
	}	
	
	
	public void adjustRadius() {
		
//		while (all borders are not at least 0.1 + ROAD_RADIUS away from each other) {
//			add 0.1 to radius
//		}
		
		List<Point> borderPoints = new ArrayList<Point>();
		
		for (Edge e : eds) {
			if (e.getStart() == this) {
				borderPoints.add(e.startBorder(p, radius).getPoint());
			}
			if (e.getEnd() == this) {
				borderPoints.add(e.endBorder(p, radius).getPoint());
			}
		}
		
		double minimumDist = Double.POSITIVE_INFINITY;
		for (int i = 0; i < borderPoints.size()-1; i++) {
			Point a = borderPoints.get(i);
			for (int j = i+1; j < borderPoints.size(); j++) {
				Point b = borderPoints.get(j);
				double dist = Point.distance(a, b);
				if (DMath.lessThan(dist, minimumDist)) {
					minimumDist = dist;
				}
			}
		}
		
		while (true) {
			
			if (DMath.greaterThan(minimumDist, 2 * MODEL.world.ROAD_RADIUS + 0.1)) {
				break;
			}
			
			radius = radius + 0.1;
			
			borderPoints.clear();
			
			for (Edge e : eds) {
				e.adjusted = false;
				if (e.getStart() == this) {
					borderPoints.add(e.startBorder(p, radius).getPoint());
				}
				if (e.getEnd() == this) {
					borderPoints.add(e.endBorder(p, radius).getPoint());
				}
			}
			
			minimumDist = Double.POSITIVE_INFINITY;
			for (int i = 0; i < borderPoints.size()-1; i++) {
				Point a = borderPoints.get(i);
				for (int j = i+1; j < borderPoints.size(); j++) {
					Point b = borderPoints.get(j);
					double dist = Point.distance(a, b);
					if (DMath.lessThan(dist, minimumDist)) {
						minimumDist = dist;
					}
				}
			}
			
		}
		
		computeArea();
		
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
	
	public void addEdge(Edge e) {
		assert e != null;
		if (removed) {
			throw new IllegalStateException("vertex has been removed");
		}
		eds.add(e);
	}
	
	public void removeEdge(Edge e) {
		if (removed) {
			throw new IllegalStateException();
		}
		assert eds.contains(e);
		eds.remove(e);
	}
	
	public List<Edge> getEdges() {
		if (removed) {
			throw new IllegalStateException();
		}
		return eds;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public void remove() {
		assert !removed;
		assert eds.size() == 0;
		
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void paint(Graphics2D g2) {
		
//		g2.setColor(color);
//		
//		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-radius, -radius)));
//		
//		g2.fillOval((int)loc.getX(), (int)loc.getY(), (int)(radius * 2 * MODEL.world.PIXELS_PER_METER), (int)(radius * 2 * MODEL.world.PIXELS_PER_METER));
//		
//		g2.setColor(Color.WHITE);
//		
//		g2.drawString(Integer.toString(id), (int)loc.getX(), (int)(loc.getY() + radius * MODEL.world.PIXELS_PER_METER));
		
		AffineTransform origTransform = g2.getTransform();
		AffineTransform trans = (AffineTransform)VIEW.worldToPanelTransform.clone();
		g2.setTransform(trans);
		g2.setColor(color);
		g2.fill(path);
		
		g2.setTransform(origTransform);
		
		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-radius, -radius)));
		g2.setColor(Color.WHITE);
		g2.drawString(Integer.toString(id), (int)loc.getX(), (int)(loc.getY() + radius * MODEL.world.PIXELS_PER_METER));
		
	}
	
	public void paintHilite(Graphics2D g2) {
		
//		g2.setColor(hiliteColor);
//		
//		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-radius, -radius)));
//		
//		g2.fillOval((int)loc.getX(), (int)loc.getY(), (int)(radius * 2 * MODEL.world.PIXELS_PER_METER), (int)(radius * 2 * MODEL.world.PIXELS_PER_METER));
		
		
		
		AffineTransform origTransform = g2.getTransform();
		AffineTransform trans = (AffineTransform)VIEW.worldToPanelTransform.clone();
		g2.setTransform(trans);
		g2.setColor(hiliteColor);
		g2.fill(path);
		g2.setTransform(origTransform);
		
	}
	
	public void check() {
		assert !isRemoved();
		assert getPoint() != null;
		
		int edgeCount = eds.size();
		
		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
		assert edgeCount != 2;
		
		int count;
		for (Edge e : eds) {
			
			assert !e.isRemoved();
			
			count = 0;
			for (Edge f : getEdges()) {
				if (e == f) {
					count++;
				}
			}
			if (e.getStart() == this && e.getEnd() == this) {
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
