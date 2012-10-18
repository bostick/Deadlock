package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import org.apache.log4j.Logger;

import com.bric.geom.AreaX;
import com.gutabi.deadlock.utils.Java2DUtils;

@SuppressWarnings("static-access")
public final class Edge extends Entity {
		
	private final List<Point> skeleton;
	
	private final double[] cumulativeDistancesFromStart;
	private final Vertex start;
	private final Vertex end;
	
	boolean adjusted;
	EdgePosition startBorder;
	EdgePosition endBorder;
	
	private final double totalLength;
	
	private final boolean standalone;
	private final boolean loop;
	
	private boolean removed = false;
	
	private final int hash;
	
	public int id;
	
	static Logger logger = Logger.getLogger(Edge.class);
	
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
		
		color = new Color(0x88, 0x88, 0x88, 0xff);
		hiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
		
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
		return start;
	}
	
	public Vertex getEnd() {
		return end;
	}
	
	public Point getPoint(int i) {
		assert i >= 0;
		return skeleton.get(i);
	}
	
	
	public boolean hitTest(Point p) {
		return path.contains(new Point2D.Double(p.x, p.y));
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
		assert !removed;
		
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void adjust() {
		
		assert !adjusted;
		
		assert !standalone;
		
		startBorder = startBorder(start.getPoint(), start.getRadius());
		
		endBorder = endBorder(end.getPoint(), end.getRadius());
		
		assert startBorder.distanceToStartOfEdge() < endBorder.distanceToStartOfEdge();
		
		computeArea();
		
		adjusted = true;
	}
	
	public EdgePosition startBorder(Point center, double radius) {
		
		EdgePosition border = null;
		
		assert center.equals(skeleton.get(0));
		
		for (int i = 0; i < skeleton.size()-1; i++) {
			Point a = skeleton.get(i);
			Point b = skeleton.get(i+1);
			if (DMath.equals(Point.distance(b, center), radius)) {
				border = new EdgePosition(this, i+1, 0.0);
				break;
			} else if (DMath.greaterThan(Point.distance(b, center), radius)) {
				assert DMath.lessThan(Point.distance(a, center), radius);
				int n;
				Point[] ints = new Point[2];
				n = Point.circleLineIntersections(center, radius, a, b, ints);
				if (n == 1) {
					Point p = ints[0];
					double u = Point.u(a, p, b);
					assert DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0);
					border = new EdgePosition(this, i, u);
					break;
				} else {
					assert n == 2;
					Point p = ints[0];
					double u = Point.u(a, p, b);
					if (DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0)) {
						border = new EdgePosition(this, i, u);
						break;
					} else {
						p = ints[1];
						u = Point.u(a, p, b);
						assert DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0);
						border = new EdgePosition(this, i, u);
						break;
					}
				}
			} else if (i+1 == skeleton.size()-1) {
				assert false : "reached end";
			}
		}
		
		assert border != null;
		return border;
	}
	
	public EdgePosition endBorder(Point center, double radius) {
		
		EdgePosition border = null;
		
		assert center.equals(skeleton.get(skeleton.size()-1));
		
		for (int i = skeleton.size()-2; i >= 0; i--) {
			Point a = skeleton.get(i);
			Point b = skeleton.get(i+1);
			if (DMath.equals(Point.distance(a, center), radius)) {
				border = new EdgePosition(this, i, 0.0);
				break;
			} else if (DMath.greaterThan(Point.distance(a, center), radius)) {
				assert DMath.lessThan(Point.distance(b, center), radius);
				int n;
				Point[] ints = new Point[2];
				n = Point.circleLineIntersections(center, radius, a, b, ints);
				if (n == 1) {
					Point p = ints[0];
					double u = Point.u(a, p, b);
					assert DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0);
					border = new EdgePosition(this, i, u);
					break;
				} else {
					assert n == 2;
					Point p = ints[0];
					double u = Point.u(a, p, b);
					if (DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0)) {
						border = new EdgePosition(this, i, u);
						break;
					} else {
						p = ints[1];
						u = Point.u(a, p, b);
						assert DMath.greaterThanEquals(u, 0.0) && DMath.lessThanEquals(u, 1.0);
						border = new EdgePosition(this, i, u);
						break;
					}
				}
			} else if (i+1 == 1) {
				assert false : "reached start";
			}
		}
		
		assert border != null;
		return border;
	}
	
	private void computeArea() {
		
//		assert area == null;
		
		AreaX area = new AreaX();
		
		if (startBorder.getIndex() == endBorder.getIndex()) {
			
			EdgePosition a = startBorder;
			EdgePosition b = endBorder;
			addToArea(area, a, b);
			
		} else {
			
			EdgePosition endBorderBound;
			if (endBorder.isBound()) {
				endBorderBound = endBorder;
			} else {
				endBorderBound = new EdgePosition(this, endBorder.getIndex(), 0.0);
			}
			
			EdgePosition a = startBorder;
			EdgePosition b = (EdgePosition)a.nextBoundForward();
			
			while (true) {
				
				addToArea(area, a, b);
				
				if (b.equals(endBorderBound)) {
					break;
				}
				
				a = b;
				b = (EdgePosition)b.nextBoundForward();
			}
			if (!endBorder.isBound()) {
				assert b.equals(endBorderBound);
				a = b;
				b = endBorder;
				
				addToArea(area, a, b);
			}
			
		}
		
		assert area.isSingular();
		
		List<Point> poly = Java2DUtils.shapeToList(area, 0.1);
		
		path = Java2DUtils.listToPath(poly);
		
	}
	
	private void addToArea(AreaX area, EdgePosition a, EdgePosition b) {
		Point aa = a.getPoint();
		Point bb = b.getPoint();

		Point diff = new Point(bb.x - aa.x, bb.y - aa.y);
		Point up = Point.ccw90(diff).multiply(MODEL.world.ROAD_RADIUS / diff.length);
		Point down = Point.cw90(diff).multiply(MODEL.world.ROAD_RADIUS / diff.length);
		
		Point p0 = aa.add(up);
		Point p1 = aa.add(down);
		Point p2 = bb.add(up);
		Point p3 = bb.add(down);
		Path2D path = new Path2D.Double();
		path.moveTo(p0.x, p0.y);
		path.lineTo(p1.x, p1.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p0.x, p0.y);
		
		Area disk1 = new Area(new Ellipse2D.Double(aa.x-MODEL.world.ROAD_RADIUS, aa.y-MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS));
		Area rect = new Area(path);
		Area disk2 = new Area(new Ellipse2D.Double(bb.x-MODEL.world.ROAD_RADIUS, bb.y-MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS, 2 * MODEL.world.ROAD_RADIUS));
		
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
		g2.fillOval((int)(startBorder.p.x * MODEL.PIXELS_PER_METER)-2, (int)(startBorder.p.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
		g2.setColor(Color.RED);
		g2.fillOval((int)(endBorder.p.x * MODEL.PIXELS_PER_METER)-2, (int)(endBorder.p.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
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
