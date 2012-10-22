package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.utils.Java2DUtils;

@SuppressWarnings("static-access")
public class Vertex extends Entity {
	
	public final Point p;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	
	protected boolean removed = false;
	
	public int id;
	
	List<Point> poly;
	Path2D path;
	double radius;
	
	private final int hash;
	
	public static final double INIT_VERTEX_RADIUS = Math.sqrt(2 * Edge.ROAD_RADIUS * Edge.ROAD_RADIUS);
	
	public Vertex(Point p, Graph graph) {
		super(graph);
		
		this.p = p;
		
		radius = INIT_VERTEX_RADIUS;
		
		computePath();
		
		int h = 17;
		h = 37 * h + p.hashCode();
		h = 37 * h + graph.hashCode();
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
	
	
	private void computePath() {
		Shape s = new Ellipse2D.Double(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
		
		poly = Java2DUtils.shapeToList(s, 0.01);
		
		path = Java2DUtils.listToPath(poly);
		
		Rectangle2D bound = path.getBounds2D();
		
		renderingUpperLeft = new Point(bound.getX(), bound.getY());
		renderingBottomRight = new Point(bound.getX() + bound.getWidth(), bound.getY() + bound.getHeight());
	}	
	
	
	public void computeRadius(double maximumRadius) {
		
		radius = INIT_VERTEX_RADIUS;
		for (Edge e : eds) {
			e.computeProperties();
		}
		
		loop:
		while (true) {
			
			boolean tooClose = false;
			
			for (int i = 0; i < eds.size()-1; i++) {
				Edge ei = eds.get(i);
				
				Point borderi;
				if (ei.getStart() == this) {
					borderi = ei.getStartBorderPoint();
				} else {
					borderi = ei.getEndBorderPoint();
				}
				
				for (int j = i+1; j < eds.size(); j++) {
					Edge ej = eds.get(j);
					
					Point borderj;
					if (ei == ej) {
						// loop, make sure to get the other point
						borderj = ej.getEndBorderPoint();
					} else if (ej.getStart() == this) {
						borderj = ej.getStartBorderPoint();
					} else {
						borderj = ej.getEndBorderPoint();
					}
					
					if (DMath.lessThan(Point.distance(borderi, borderj), Edge.ROAD_RADIUS + Edge.ROAD_RADIUS + 0.1)) {
						tooClose = true;
					}
					
				}
			}
			
			if (!tooClose) {
				break loop;
			}
			
			if (radius + 0.1 > maximumRadius) {
				radius = maximumRadius;
				break loop;
			} else {
				radius = radius + 0.1;
			}
			
			for (Edge e : eds) {
				e.computeProperties();
			}
			
		}
		
		computePath();
		
		for (Edge e : eds) {
			e.computeProperties();
			e.computePath();
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
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		g2.setColor(color);
		g2.fill(path);
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		g2.setColor(hiliteColor);
		g2.fill(path);
	}
	
	/**
	 * 
	 * @param g2 in pixels, <0, 0> is world origin
	 */
	public void paintID(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		Point worldPoint = p.minus(new Point(radius, 0));
		Point panelPoint = worldPoint.multiply(MODEL.PIXELS_PER_METER);
		g2.drawString(Integer.toString(id), (int)(panelPoint.x), (int)(panelPoint.y));
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
