package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bric.geom.AreaX;
import com.gutabi.deadlock.utils.Java2DUtils;

@SuppressWarnings("static-access")
public final class Edge extends Entity {
	
	public static final double ROAD_RADIUS = 0.5;
	
	private final Vertex start;
	private final Vertex end;
	private final List<Point> raw;
	
	private List<Point> skeleton;
	private Point startBorderPoint;
	private Point endBorderPoint;
	private int startBorderIndex;
	private int endBorderIndex;
	private double[] cumulativeLengthsFromStart;
	private double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	private boolean removed = false;
	
	public int id;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(Edge.class);
	
	public Edge(Vertex start, Vertex end, Graph graph, List<Point> raw) {
		super(graph);
		
		this.start = start;
		this.end = end;
		this.raw = raw;
		
		loop = (start == end);
		standalone = (loop) ? start == null : false;
		
		color = new Color(0x88, 0x88, 0x88, 0xff);
		hiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
		
		computeProperties();
		computePath();
		
		int h = 17;
		if (start != null) {
			h = 37 * h + start.hashCode();
		}
		if (end != null) {
			h = 37 * h + end.hashCode();
		}
//		h = 37 * h + graph.hashCode();
//		h = 37 * h + raw.hashCode();
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
		return start;
	}
	
	public Vertex getEnd() {
		return end;
	}
	
	public Point getPoint(int i) {
		assert i >= 0;
		return skeleton.get(i);
	}
	
	public Point getStartBorderPoint() {
		return startBorderPoint;
	}
	
	public Point getEndBorderPoint() {
		return endBorderPoint;
	}
	
	
	
	
	
	
	public boolean hitTest(Point p) {
		return path.contains(new Point2D.Double(p.x, p.y));
	}
	
	
	
	/**
	 * segment index i
	 */
	public double getLengthFromStart(int i) {
		return cumulativeLengthsFromStart[i];
	}
	
	public double getLengthFromEnd(int i) {
		return totalLength - cumulativeLengthsFromStart[i];
	}
	
	public void remove() {
		assert !removed;
		
		graph.segTree.removeEdge(this);
		
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
//	public void computeBorders() {
//		assert !standalone;
//		
//		/*
//		 * invalidate path, so it has to be recomputed also
//		 */
//		path = null;
//		
//		double startBorderCombo = startBorderCombo(start, skeleton);
//		
//		double endBorderCombo = endBorderCombo(end, skeleton);
//		
//		assert startBorderCombo < endBorderCombo;
//		
//		startBorderIndex = (int)Math.floor(startBorderCombo);
//		endBorderIndex = (int)Math.floor(endBorderCombo);
//		
//		startBorderParam = startBorderCombo - startBorderIndex;
//		endBorderParam = endBorderCombo - endBorderIndex;
//		
//		if (startBorderCombo < 0) {
//			assert startBorderIndex == -1;
//			startBorderPoint = Point.point(start.p, skeleton.get(0), startBorderParam);
//		} else {
//			startBorderPoint = Point.point(skeleton.get(startBorderIndex), skeleton.get(startBorderIndex+1), startBorderParam);
//		}
//		
//		if (endBorderCombo >= skeleton.size()-1) {
//			assert endBorderIndex == skeleton.size()-1;
//			endBorderPoint = Point.point(skeleton.get(endBorderIndex), end.p, endBorderParam);
//		} else {
//			endBorderPoint = Point.point(skeleton.get(endBorderIndex), skeleton.get(endBorderIndex+1), endBorderParam);
//		}
//		
//	}
	
	public void computeProperties() {
		
		List<Point> adj = raw;
		
		adj = removeColinear(adj);
		
		computeBorders(adj);
		
		adj = adjustToBorders(adj);
		
		skeleton = adj;
		
		graph.segTree.removeEdge(this);
		graph.segTree.addEdge(this);
		
		/*
		 * after adjustToBorders, the border properties are set to predictable values
		 */
		if (!standalone) {
			startBorderIndex = 1;
			endBorderIndex = skeleton.size()-2;
			startBorderPoint = skeleton.get(startBorderIndex);
			endBorderPoint = skeleton.get(endBorderIndex);
			assert DMath.equals(Point.distance(startBorderPoint, start.p), start.radius);
			assert DMath.equals(Point.distance(endBorderPoint, end.p), end.radius);
		} else {
			startBorderIndex = -1;
			endBorderIndex = -1;
			startBorderPoint = null;
			endBorderPoint = null;
		}
		
		computeLengths();
		
	}
	
	private static List<Point> removeColinear(List<Point> stroke) {
		List<Point> adj = new ArrayList<Point>();
		for (int i = 0; i < stroke.size(); i++) {
			Point cur = stroke.get(i);
			if (adj.size() < 2) {
				adj.add(cur);
			} else {
				int n = adj.size();
				Point last = adj.get(n-1);
				Point pen = adj.get(n-2);
				try {
					if (!Point.colinear(pen, last, cur)) {
						adj.add(cur);
					} else {
						adj.remove(n-1);
						adj.add(cur);
					}
				} catch (ColinearException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return adj;
	}
	
	private void computeBorders(List<Point> pts) {
		
		double c = startBorderCombo(start, pts);
		if (c < 0) {
			startBorderIndex = (int)Math.floor(c);
			assert startBorderIndex == -1;
			double startBorderParam = c-startBorderIndex;
			assert 0 <= startBorderParam && startBorderParam <= 1;
			startBorderPoint = Point.point(start.p, pts.get(startBorderIndex+1), startBorderParam);
		} else {
			startBorderIndex = (int)Math.floor(c);
			double startBorderParam = c-startBorderIndex;
			assert 0 <= startBorderParam && startBorderParam <= 1;
			startBorderPoint = Point.point(pts.get(startBorderIndex), pts.get(startBorderIndex+1), startBorderParam);
		}
		
		c = endBorderCombo(end, pts);
		if (c >= pts.size()-1) {
			endBorderIndex = (int)Math.floor(c);
			assert endBorderIndex == pts.size()-1;
			double endBorderParam = c-endBorderIndex;
			assert 0 <= endBorderParam && endBorderParam <= 1;
			endBorderPoint = Point.point(pts.get(endBorderIndex), end.p, endBorderParam);
		} else {
			endBorderIndex = (int)Math.floor(c);
			double endBorderParam = c-endBorderIndex;
			assert 0 <= endBorderParam && endBorderParam <= 1;
			endBorderPoint = Point.point(pts.get(endBorderIndex), pts.get(endBorderIndex+1), endBorderParam);
		}
		
	}
	
	private List<Point> adjustToBorders(List<Point> pts) {
		
		List<Point> adj = new ArrayList<Point>();
		adj.add(start.p);
		adj.add(startBorderPoint);
		for (int i = startBorderIndex+1; i < endBorderIndex; i++) {
			adj.add(pts.get(i));
		}
		adj.add(endBorderPoint);
		adj.add(end.p);
		
		return adj;
	}
	
	/**
	 * returns index + param, returns -1 if first point is already outside of radius
	 */
	private static double startBorderCombo(Vertex start, List<Point> pts) {
		
		Point center = start.getPoint();
		double radius = start.getRadius();
		
		for (int i = 0; i < pts.size()-1; i++) {
			Point a = pts.get(i);
			Point b = pts.get(i+1);
			if (DMath.greaterThan(Point.distance(a, center), radius)) {
				assert i == 0;
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, center, a, ints);
				assert n == 1;
				
				return -1 + Point.param(ints[0], center, a);
				
			} else if (DMath.equals(Point.distance(b, center), radius)) {
				return (i+1) + 0.0;
			} else if (DMath.greaterThan(Point.distance(b, center), radius)) {
				assert DMath.lessThanEquals(Point.distance(a, center), radius);
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, a, b, ints);
				assert n == 1;
				
				return i + Point.param(ints[0], a, b);
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * returns index + param, returns -1 if last point is already outside of radius
	 */
	private static double endBorderCombo(Vertex end, List<Point> pts) {
		
		Point center = end.getPoint();
		double radius = end.getRadius();
		
		for (int i = pts.size()-2; i >= 0; i--) {
			Point a = pts.get(i);
			Point b = pts.get(i+1);
			if (DMath.greaterThan(Point.distance(b, center), radius)) {
				assert i+1 == pts.size()-1;
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, b, center, ints);
				assert n == 1;
				
				return i+1 + Point.param(ints[0], b, center);
				
			} else if (DMath.equals(Point.distance(a, center), radius)) {
				return i + 0.0;
			} else if (DMath.greaterThan(Point.distance(a, center), radius)) {
				assert DMath.lessThanEquals(Point.distance(b, center), radius);
				
				Point[] ints = new Point[2];
				int n = Point.circleSegmentIntersections(center, radius, a, b, ints);
				assert n == 1;
				
				return i + Point.param(ints[0], a, b);
			}
		}
		
		throw new IllegalArgumentException();
	}
	
	private void computeLengths() {
		
		cumulativeLengthsFromStart = new double[skeleton.size()];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < skeleton.size(); i++) {
			if (i == 0) {
				cumulativeLengthsFromStart[i] = 0;
			} else {
				Point a  = skeleton.get(i-1);
				Point b = skeleton.get(i);
				length = Point.distance(a, b);
				l += length;
				cumulativeLengthsFromStart[i] = cumulativeLengthsFromStart[i-1] + length;
			}
		}
		
		totalLength = l;
		
	}
	
	/**
	 * computes path, and also renderingUpperLeft and renderingBottomRight
	 */
	public void computePath() {
		
//		assert area == null;
		assert !standalone;
		
		AreaX area = new AreaX();
		
		if (startBorderIndex == endBorderIndex) {
			
			addToArea(area, startBorderPoint, endBorderPoint);
			
		} else {
			
			for (int i = startBorderIndex; i < endBorderIndex; i++) {
				Point a = skeleton.get(i);
				Point b = skeleton.get(i+1);
				addToArea(area, a, b);
			}
			
		}
		
		assert area.isSingular();
		
		List<Point> poly = Java2DUtils.shapeToList(area, 0.1);
		
		path = Java2DUtils.listToPath(poly);
		
		Rectangle2D bound = path.getBounds2D();
		
		renderingUpperLeft = new Point(bound.getX(), bound.getY());
		renderingBottomRight = new Point(bound.getX() + bound.getWidth(), bound.getY() + bound.getHeight());
		
	}
	
	private void addToArea(AreaX area, Point a, Point b) {

		Point diff = new Point(b.x - a.x, b.y - a.y);
		Point up = Point.ccw90(diff).multiply(ROAD_RADIUS / diff.length);
		Point down = Point.cw90(diff).multiply(ROAD_RADIUS / diff.length);
		
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
		
		Area disk1 = new Area(new Ellipse2D.Double(a.x-ROAD_RADIUS, a.y-ROAD_RADIUS, 2 * ROAD_RADIUS, 2 * ROAD_RADIUS));
		Area rect = new Area(path);
		Area disk2 = new Area(new Ellipse2D.Double(b.x-ROAD_RADIUS, b.y-ROAD_RADIUS, 2 * ROAD_RADIUS, 2 * ROAD_RADIUS));
		
		Area capsule = new Area();
		capsule.add(disk1);
		capsule.add(rect);
		capsule.add(disk2);
		
		area.add(capsule);
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
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		if (!MODEL.DEBUG_DRAW) {
			g2.setColor(hiliteColor);
			g2.fill(path);
		} else {
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(0.05f));
			g2.draw(path);
		}
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		if (!MODEL.DEBUG_DRAW) {
			g2.setColor(color);
			g2.fill(path);
		} else {
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(0.05f));
			g2.draw(path);
			
//			paintBorders(g2);
			
		}
	}
	
	/**
	 * @param g2 in pixels
	 */
	public void paintSkeleton(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(1.0f));
		
		int[] xs = new int[skeleton.size()];
		int[] ys = new int[skeleton.size()];
		for (int i = 0; i < skeleton.size(); i++) {
			Point p = skeleton.get(i);
			xs[i] = (int)(p.x * MODEL.PIXELS_PER_METER);
			ys[i] = (int)(p.y * MODEL.PIXELS_PER_METER);
		}
		g2.drawPolyline(xs, ys, skeleton.size());
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintBorders(Graphics2D g2) {
		
		g2.setColor(Color.GREEN);
		g2.fillOval((int)(startBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(startBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
		g2.setColor(Color.RED);
		g2.fillOval((int)(endBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(endBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
	}
	
	
	
	
	
	
	
	
	
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
				if (standalone) {
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
			} else if (i == 1) {
				// don't test if border point (border points may be colinear)
			} else if (i == skeleton.size()-2) {
				// don't test if border point (border points may be colinear)
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
