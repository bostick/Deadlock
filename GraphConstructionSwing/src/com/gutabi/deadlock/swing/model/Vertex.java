package com.gutabi.deadlock.swing.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.swing.utils.Point;


public class Vertex {
	
	private final Point p;
	
	private List<Edge> eds = new ArrayList<Edge>();
	
	private Edge lastEdgeAdded;
	
	private boolean removed = false;
	
	public Vertex(Point p) {
		this.p = p;
	}
	
	public String toString() {
		return "V " + p;
	}
	
	public void add(Edge ed) {
		if (removed) {
			throw new IllegalStateException();
		}
		if (!(ed.getStart() == this && ed.getEnd() == this)) {
			assert !eds.contains(ed);
		}
		eds.add(ed);
		lastEdgeAdded = ed;
	}
	
	public void remove(Edge ed) {
		if (removed) {
			throw new IllegalStateException();
		}
		assert eds.contains(ed);
		eds.remove(ed);
	}
	
	public List<Edge> getEdges() {
		if (removed) {
			throw new IllegalStateException();
		}
		return eds;
	}
	
	public Edge getOnlyEdge() {
		assert eds.size() == 1;
		return lastEdgeAdded;
	}
	
	public Point getPoint() {
		if (removed) {
			throw new IllegalStateException();
		}
		return p;
	}
	
	public void paint(Graphics2D g) {
		if (removed) {
			throw new IllegalStateException();
		}
		g.setColor(Color.BLUE);
		g.fillOval(p.x-3, p.y-3, 6, 6);
	}
	
	public void remove() {
		if (removed) {
			throw new IllegalStateException();
		}
		removed = true;
	}
	
	boolean isRemoved() {
		return removed;
	}
	
	public void check() {
		
		assert !isRemoved();
		
		assert getPoint() != null;
		
		int edgeCount = getEdges().size();
		
		/*
		 * edgeCount cannot be 0, why have some free-floating vertex?
		 */
		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
		assert edgeCount != 2;
		
		/*
		 * all edges in v should be unique
		 */
		int count;
		for (Edge e : getEdges()) {
			
			assert !e.isRemoved();
			
			count = 0;
			for (Edge f : getEdges()) {
				if (e == f) {
					count++;
				}
			}
			if (e.getStart() == this && e.getEnd() == this) {
				assert count == 2;
			} else {
				assert count == 1;
			}
		}
		
	}
	
}
