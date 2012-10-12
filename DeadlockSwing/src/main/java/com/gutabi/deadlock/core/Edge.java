package com.gutabi.deadlock.core;


import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;

public final class Edge implements Hilitable {
		
	private final List<Point> skeleton;
	
	public Area area;
	
	private final double[] cumulativeDistancesFromStart;
	private final Vertex start;
	private final Vertex end;
	
	private final double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	private boolean removed = false;
	
	private final int hash;
	
	int id;
	
	public Edge(Vertex start, Vertex end, List<Point> pts) {
		this.start = start;
		this.end = end;
		loop = (start == end);
		standalone = (loop) ? start == null : false;
//		this.pts = pts.toArray(new Point[0]);
		
		this.skeleton = pts;
		
		cumulativeDistancesFromStart = new double[pts.size()];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < pts.size(); i++) {
			if (i == 0) {
				cumulativeDistancesFromStart[i] = 0;
			} else {
				Point a  = pts.get(i-1);
				Point b = pts.get(i);
				length = Point.distance(a, b);
				l += length;
				cumulativeDistancesFromStart[i] = cumulativeDistancesFromStart[i-1] + length;
			}
		}
		
		totalLength = l;
		
		int h = 17;
		if (start != null) {
			h = 37 * h + start.hashCode();
		}
		if (end != null) {
			h = 37 * h + end.hashCode();
		}
		h = 37 * h + pts.hashCode();
		hash = h;
		
		check();
	}
	
	public int hashCode() {
		return hash;
	}
	
	
	
	public boolean isStandAlone() {
		return standalone;
	}
	
	public boolean isLoop() {
		return loop;
	}
	
	public int size() {
		return skeleton.size();
	}
	
	public double getTotalLength() {
		return totalLength;
	}
	
	public Vertex getStart() {
		if (removed) {
			throw new IllegalStateException("edge has been removed");
		}
		return start;
	}
	
	public Vertex getEnd() {
		if (removed) {
			throw new IllegalStateException();
		}
		return end;
	}
	
	public Point getPoint(int i) {
		if (i >= 0) {
			return skeleton.get(i);
		} else {
			return skeleton.get(i + skeleton.size());
		}
	}
	
	/**
	 * segment index i
	 */
	public double getDistanceFromStart(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return cumulativeDistancesFromStart[i];
	}
	
	public double getDistanceFromEnd(int i) {
		if (removed) {
			throw new IllegalStateException();
		}
		return totalLength - cumulativeDistancesFromStart[i];
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
	
	public void computeArea() {
		
		assert area == null;
		
		area = new Area();
		for (int i = 0; i < skeleton.size()-1; i++) {
			Point a = skeleton.get(i);
			Point b = skeleton.get(i+1);

			Point diff = new Point(b.x - a.x, b.y - a.y);
			Point up = Point.ccw90(diff).multiply(MODEL.world.ROAD_RADIUS / diff.length);
			Point down = Point.cw90(diff).multiply(MODEL.world.ROAD_RADIUS / diff.length);
			
			Point p0 = a.add(up);
			Point p1 = a.add(down);
			Point p2 = b.add(up);
			Point p3 = b.add(down);
			Path2D path = new Path2D.Double();
			path.moveTo(p0.x, p0.y);
			path.lineTo(p1.x, p1.y);
			path.lineTo(p3.x, p3.y);
			path.lineTo(p2.x, p2.y);
			path.lineTo(p0.x, p0.y);
			
			Area disk1 = new Area(new Ellipse2D.Double(a.getX()-MODEL.world.ROAD_RADIUS, a.getY()-MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS));
			Area rect = new Area(path);
			Area disk2 = new Area(new Ellipse2D.Double(b.getX()-MODEL.world.ROAD_RADIUS, b.getY()-MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS));
			
			Area capsule = new Area();
			capsule.add(disk1);
			capsule.add(rect);
			capsule.add(disk2);
			
			area.add(capsule);
		}
		
	}
	
	
	
	
	
	
	
	public static boolean haveExactlyOneSharedIntersection(Edge a, Edge b) {
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			return (ae != be);
		} else if (as == be) {
			return (ae != bs);
		} else if (ae == bs) {
			return (as != be);
		} else if (ae == be) {
			return (as != bs);
		} else {
			return false;
		}
	}
	
	public static boolean haveTwoSharedIntersections(Edge a, Edge b) {
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			return (ae == be);
		} else if (as == be) {
			return (ae == bs);
		} else if (ae == bs) {
			return (as == be);
		} else if (ae == be) {
			return (as == bs);
		} else {
			return false;
		}
	}
	
	/**
	 * returns the single shared intersection between edges a and b
	 */
	public static Vertex sharedIntersection(Edge a, Edge b) throws SharedVerticesException {
		assert a != b;
		Vertex as = a.getStart();
		Vertex ae = a.getEnd();
		Vertex bs = b.getStart();
		Vertex be = b.getEnd();
		if (as == bs) {
			if (ae == be) {
				throw new SharedVerticesException(as, ae);
			}
			return as;
		} else if (as == be) {
			if (ae == bs) {
				throw new SharedVerticesException(as, ae);
			}
			return as;
		} else if (ae == bs) {
			if (as == be) {
				throw new SharedVerticesException(as, ae);
			}
			return ae;
		} else if (ae == be) {
			if (as == bs) {
				throw new SharedVerticesException(as, ae);
			}
			return be;
		} else {
			return null;
		}
	}
	
	public void paintHilite(Graphics2D g2) {
		int size = size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = VIEW.worldToPanel(getPoint(i));
			xPoints[i] = (int)p.getX();
			yPoints[i] = (int)p.getY();
		}
		g2.setColor(Color.RED);
		g2.setStroke(new Road1Stroke());
		g2.drawPolyline(xPoints, yPoints, size);
	}
	
	public void paintEdge1(Graphics2D g2) {
//		int size = size();
//		int[] xPoints = new int[size];
//		int[] yPoints = new int[size];
//		
//		g2.setColor(new Color(0x88, 0x88, 0x88, 0xff));
//		g2.setStroke(new Road1Stroke());
//		
//		for (int i = 0; i < size; i++) {
//			Point p = VIEW.worldToPanel(getPoint(i));
//			xPoints[i] = (int)p.getX();
//			yPoints[i] = (int)p.getY();
//		}
//		
//		g2.drawPolyline(xPoints, yPoints, size);
		
		
		
//		int size = polygonTop.size() + polygonBottom.size();
//		int[] xPoints = new int[size];
//		int[] yPoints = new int[size];
//		
//		g2.setColor(Color.WHITE);
//		g2.setStroke(new PolyStroke());
//		
//		int index = 0;
//		for (int i = 0; i < polygonTop.size(); i++) {
//			Point p = VIEW.worldToPanel(polygonTop.get(i));
//			xPoints[index] = (int)p.getX();
//			yPoints[index] = (int)p.getY();
//			index++;
//		}
//		for (int i = polygonBottom.size()-1; i >= 0; i--) {
//			Point p = VIEW.worldToPanel(polygonBottom.get(i));
//			xPoints[index] = (int)p.getX();
//			yPoints[index] = (int)p.getY();
//			index++;
//		}
//		
//		g2.drawPolygon(xPoints, yPoints, size);
		
		AffineTransform origTransform = g2.getTransform();
		
		AffineTransform trans = (AffineTransform)VIEW.worldToPanelTransform.clone();
		
		g2.setTransform(trans);
		
		g2.setColor(Color.WHITE);
		g2.fill(area);
		
		g2.setTransform(origTransform);
		
	}
	
//	public void paintEdge2(Graphics2D g2) {
//		int size = size();
//		int[] xPoints = new int[size];
//		int[] yPoints = new int[size];
//		for (int i = 0; i < size; i++) {
//			Point p = VIEW.worldToPanel(getPoint(i));
//			xPoints[i] = (int)p.getX();
//			yPoints[i] = (int)p.getY();
//		}
//		g2.setColor(Color.YELLOW);
//		g2.setStroke(new Road2Stroke());
//		g2.drawPolyline(xPoints, yPoints, size);
//	}
	
	public static class Road1Stroke extends BasicStroke {
		
		public Road1Stroke() {
			super((int)(2 * MODEL.world.ROAD_RADIUS * MODEL.world.PIXELS_PER_METER), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		}
		
	}
	
	public static class PolyStroke extends BasicStroke {
		
		public PolyStroke() {
			super(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		}
		
	}
	
//	public static class Road2Stroke extends BasicStroke {
//		
//		public Road2Stroke() {
//			super(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
//		}
//
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void check() {
		
		assert skeleton.size() >= 2;
		
		if (loop) {
			assert start == end;
		} else {
			assert !(start == null || end == null);
			assert !start.isRemoved();
			assert !end.isRemoved();
		}
		
		for (int i = 0; i < skeleton.size(); i++) {
			Point p = skeleton.get(i);
			
			int count = 0;
			for (Point q : skeleton) {
				if (p.equals(q)) {
					count++;
				}
			}
			if (loop && (i == 0 || i == skeleton.size()-1)) {
				assert count == 2;
			} else {
				assert count == 1;
			}
			
//			if (i < pts.length-1) {
//				Point q = pts[i+1];
//				if (Math.abs(p.getX() - q.getX()) < 1.0E-3) {
//					assert DMath.equals(p.getX(), q.getX());
//				}
//				if (Math.abs(p.getY() - q.getY()) < 1.0E-3) {
//					assert DMath.equals(p.getY(), q.getY());
//				}
//			}
			
			if (i == 0) {
				if (loop) {
					if (start != null) {
						assert start.getPoint().equals(p);
						assert end.getPoint().equals(p);
					}
				} else {
					assert start.getPoint().equals(p);
				}
			} else if (i == skeleton.size()-1) {
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
	
	private void checkColinearity() {
		assert !isRemoved();
		
		for (int i = 0; i < skeleton.size(); i++) {
			Point p = skeleton.get(i);
			/*
			 * test point p for colinearity
			 */
			if (i == 0) {
				if (loop && (start == null && end == null)) {
					/*
					 * if loop, only check if stand-alone
					 * if not stand-alone, then it is possible to have first point be colinear
					 */
					assert p.equals(skeleton.get(skeleton.size()-1));
					try {
						if (Point.colinear(skeleton.get(skeleton.size()-2), p, skeleton.get(1))) {
							assert false : "Point " + p + " (index 0) is colinear";
						}
					} catch (ColinearException ex) {
						assert false;
					}
				}
			} else if (i == skeleton.size()-1) {
				;
			} else {
				try {
					if (Point.colinear(skeleton.get(i-1), p, skeleton.get(i+1))) {
						assert false : "Point " + p + " (index " + i + ") is colinear";
					}
				} catch (ColinearException ex) {
					assert false;
				}
			}
		}
	}
}
