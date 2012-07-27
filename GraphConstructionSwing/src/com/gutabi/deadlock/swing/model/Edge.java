package com.gutabi.deadlock.swing.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import com.gutabi.deadlock.swing.utils.ColinearException;
import com.gutabi.deadlock.swing.utils.Point;

public final class Edge {
		
	private List<Point> points = new LinkedList<Point>();
	
	private Vertex start;
	private Vertex end;
	
	/*
	 * both vs could be null (stand-alone loop) or both could be a vertex (shared with other edges)
	 */
	private boolean loop = false;
	
	private boolean removed = false;
	
	public String toString() {
		return "E n=" + points.size() + " " + start + " " + end;
	}
	
	public void setStart(Vertex v) {
		if (removed) {
			throw new IllegalStateException();
		}
		start = v;
		if (start == end) {
			loop = true;
		}
	}
	
	public Vertex getStart() {
		if (removed) {
			throw new IllegalStateException();
		}
		return start;
	}
	
	public void setEnd(Vertex v) {
		if (removed) {
			throw new IllegalStateException();
		}
		end = v;
		if (start == end) {
			loop = true;
		}
	}
	
	public Vertex getEnd() {
		if (removed) {
			throw new IllegalStateException();
		}
		return end;
	}
	
	public Point getPoint(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return points.get(i);
	}
	
	public void removePoint(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		points.remove(i);
	}
	
	public void addPoint(Point p) {
		if (removed) {
			throw new IllegalStateException();
		}
		if (points.contains(p) && points.indexOf(p) != 0) {
			throw new IllegalStateException();
		}
		points.add(p);
	}
	
	public int getPointsSize() {
		if (removed) {
			throw new IllegalStateException();
		}
		return points.size();
	}
	
	public void paint(Graphics2D g) {
		if (removed) {
			throw new IllegalStateException();
		}
		g.setColor(Color.BLUE);
		for (int i = 0; i < points.size()-1; i++) {
			Point prev = points.get(i);
			Point cur = points.get(i+1);
			//canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
			g.drawLine(prev.x, prev.y, cur.x, cur.y);
		}
	}
	
	public void remove() {
		if (removed) {
			throw new IllegalStateException();
		}
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void check() {
		assert !isRemoved();
		
		if (loop) {
			assert start == end;
		} else {
			assert !(start == null || end == null);
			assert !start.isRemoved();
			assert !end.isRemoved();
		}
		
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			int count = 0;
			for (Point q : points) {
				if (p.equals(q)) {
					count++;
				}
			}
			if (loop && (i == 0 || i == points.size()-1)) {
				assert count == 2;
			} else {
				assert count == 1;
			}
			
			if (i == 0) {
				if (loop) {
					if (start != null) {
						assert start.getPoint().equals(p);
						assert end.getPoint().equals(p);
					}
				} else {
					assert start.getPoint().equals(p);
				}
			} else if (i == points.size()-1) {
				if (loop) {
					if (end != null) {
						assert end.getPoint().equals(p);
					}
				} else {
					assert end.getPoint().equals(p);
				}
			}
		}
		
		checkColinearity();
	}
	
	public void checkColinearity() {
		assert !isRemoved();
		
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			/*
			 * test point p for colinearity
			 */
			if (i == 0) {
				if (loop && (start == null && end == null)) {
					/*
					 * if loop, only check if stand-alone
					 * if not stand-alone, then it is possible to have first point be colinear
					 */
					assert p.equals(points.get(points.size()-1));
					try {
						if (Point.colinear(points.get(points.size()-2), p, points.get(1))) {
							assert false : "Point " + p + " (index 0) is colinear";
						}
					} catch (ColinearException ex) {
						assert false;
					}
				}
			} else if (i == points.size()-1) {
				;
			} else {
				try {
					if (Point.colinear(points.get(i-1), p, points.get(i+1))) {
						assert false : "Point " + p + " (index " + i + ") is colinear";
					}
				} catch (ColinearException ex) {
					assert false;
				}
			}
		}
	}
}
