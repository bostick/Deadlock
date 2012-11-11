package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;
import com.gutabi.deadlock.core.geom.SweepEvent;
import com.gutabi.deadlock.core.geom.SweepEvent.SweepEventType;
import com.gutabi.deadlock.core.geom.SweepEventListener;
import com.gutabi.deadlock.core.geom.SweepUtils;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.model.Stroke;

@SuppressWarnings("static-access")
public class Merger extends Edge {
	
	public static final double MERGER_WIDTH = 5.0;
	public static final double MERGER_HEIGHT = 5.0;
	
	public final Point ul;
	
	private Rect aabb;
	private Color color;
	private Color hiliteColor;
	
	public final MergerSink top;
	public final MergerSink left;
	public final MergerSource right;
	public final MergerSource bottom;
	
	private Point p0;
	private Point p1;
	private Point p2;
	private Point p3;
	
	private double[] cumulativeLengthsFromTop;
	private double[] cumulativeLengthsFromLeft;
	
	public Merger(Point center) {
		
		this.ul = center.plus(new Point(-MERGER_WIDTH/2,  -MERGER_HEIGHT/2));
		
		aabb = new Rect(ul.x, ul.y, MERGER_WIDTH, MERGER_HEIGHT);
		color = Color.BLUE;
		hiliteColor = Color.ORANGE;
		
		top = new MergerSink(new Point(ul.x + MERGER_WIDTH/2, ul.y), Axis.TOPBOTTOM);
		left = new MergerSink(new Point(ul.x, ul.y + MERGER_HEIGHT/2), Axis.LEFTRIGHT);
		right = new MergerSource(new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT/2), Axis.LEFTRIGHT);
		bottom = new MergerSource(new Point(ul.x + MERGER_WIDTH/2, ul.y+MERGER_HEIGHT), Axis.TOPBOTTOM);
		
		top.matchingSource = bottom;
		bottom.matchingSink = top;
		
		left.matchingSource = right;
		right.matchingSink = left;
		
		p0 = ul;
		p1 = new Point(ul.x + MERGER_WIDTH, ul.y);
		p2 = new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT);
		p3 = new Point(ul.x, ul.y + MERGER_HEIGHT);
		
		top.m = this;
		left.m = this;
		right.m = this;
		bottom.m = this;
		
		computeLengths();
		
	}
	
	public void destroy() {
		top.m = null;
		left.m = null;
		right.m = null;
		bottom.m = null;
	}
	
	public String toString() {
		return "Merger[" + top.id + " " + left.id + " " + right.id + " " + bottom.id + "]";
	}
	
	public double getTotalLength(Vertex a, Vertex b) {
		if (a == top || a == bottom) {
			return MERGER_HEIGHT;
		} else {
			return MERGER_WIDTH;
		}
	}
	
	public void enterDistancesMatrix(double[][] distances) {
		distances[top.id][bottom.id] = Merger.MERGER_HEIGHT;
		distances[bottom.id][top.id] = Merger.MERGER_HEIGHT;
		distances[left.id][right.id] = Merger.MERGER_WIDTH;
		distances[right.id][left.id] = Merger.MERGER_WIDTH;
	}
	
	
	
	public void sweepStart(Stroke s, SweepEventListener l) {
		
		Point c = s.pts.get(0);
		
		if (bestHitTest(c, s.r) != null) {
			l.start(new SweepEvent(SweepEventType.ENTERMERGER, this, s, 0, 0.0));
		}
		
	}
	
//	public void sweepEnd(Stroke s, SweepEventListener l) {
//		
//		Point d = s.pts.get(s.pts.size()-1);
//		
//		if (bestHitTest(d, s.r)) {
//			l.end(new SweepEvent(SweepEventType.EXITMERGER, this, s, s.pts.size()-1, 0.0));
//		}
//		
//	}
	
	public void sweep(Stroke s, int index, SweepEventListener l) {
		
		Point c = s.pts.get(index);
		Point d = s.pts.get(index+1);
		
		boolean outside;
		if (bestHitTest(c, s.r) != null) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleLine(p0, p1, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p1, p2, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p2, p3, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleLine(p3, p0, c, d, s.r);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			if (DMath.lessThan(param, 1.0) || index == s.pts.size()-1) {
				if (outside) {
					l.event(new SweepEvent(SweepEventType.ENTERMERGER, this, s, index, param));
				} else {
					l.event(new SweepEvent(SweepEventType.EXITMERGER, this, s, index, param));
				}
				outside = !outside;
			}
		}
		
	}
	
	
	
	public GraphPosition travelFromConnectedVertex(Vertex v, double dist) {
		if (v == top) {
			assert DMath.lessThan(dist, MERGER_HEIGHT);
			
			return MergerPosition.travelFromTop(this, dist);
			
		} else if (v == left) {
			assert DMath.lessThan(dist, MERGER_WIDTH);
			
			return MergerPosition.travelFromLeft(this, dist);
			
		} else if (v == right) {
			assert DMath.lessThan(dist, MERGER_WIDTH);
			
			return MergerPosition.travelFromRight(this, dist);
			
		} else {
			assert v == bottom;
			assert DMath.lessThan(dist, MERGER_HEIGHT);
			
			return MergerPosition.travelFromBottom(this, dist);
			
		}
	}
	
	public Entity hitTest(Point p) {
		if (DMath.lessThanEquals(ul.x, p.x) && DMath.lessThanEquals(p.x, ul.x+MERGER_WIDTH) &&
				DMath.lessThanEquals(ul.y, p.y) && DMath.lessThanEquals(p.y, ul.y+MERGER_HEIGHT)) {
			return this;
		} else {
			return null;
		}
	}
	
	public Entity decorationsHitTest(Point p) {
		return null;
	}
	
	public Entity bestHitTest(Point p, double r) {
		if (hitTest(p) != null) {
			return this;
		} else {
			
//			if (DMath.equals(Point.distance(p, p0, p1), r)) {
//				String.class.getName();
//			} else if (DMath.equals(Point.distance(p, p1, p2), r)) {
//				String.class.getName();
//			} else if (DMath.equals(Point.distance(p, p2, p3), r)) {
//				String.class.getName();
//			} else if (DMath.equals(Point.distance(p, p3, p0), r)) {
//				String.class.getName();
//			}
			
			if (DMath.lessThanEquals(Point.distance(p, p0, p1), r)) {
				return this;
			} else if (DMath.lessThanEquals(Point.distance(p, p1, p2), r)) {
				return this;
			} else if (DMath.lessThanEquals(Point.distance(p, p2, p3), r)) {
				return this;
			} else if (DMath.lessThanEquals(Point.distance(p, p3, p0), r)) {
				return this;
			}
			return null;
		}
	}
	
	
	
	@Override
	public boolean isDeleteable() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	@Override
	public void preStep(double t) {
		;
	}

	@Override
	public boolean postStep(double t) {
		return true;
	}
	
	public Point get(int index, Axis a) {
		if (index == 0 || index == 2) {
			if (a == Axis.TOPBOTTOM) {
				if (index == 0) {
					return ul.plus(new Point(Merger.MERGER_WIDTH/2, 0));
				} else {
					return ul.plus(new Point(Merger.MERGER_WIDTH/2, Merger.MERGER_HEIGHT));
				}
			} else {
				if (index == 0) {
					return ul.plus(new Point(0, Merger.MERGER_HEIGHT/2));
				} else {
					return ul.plus(new Point(Merger.MERGER_WIDTH, Merger.MERGER_HEIGHT/2));
				}
			}
		} else {
			assert index == 1;
			return ul.plus(new Point(Merger.MERGER_WIDTH/2, Merger.MERGER_HEIGHT/2));
		}
	}
	
	public double getLengthFromLeft(int i) {
		return cumulativeLengthsFromLeft[i];
	}
	
	public double getLengthFromTop(int i) {
		return cumulativeLengthsFromTop[i];
	}
	
	private void computeLengths() {
		
		cumulativeLengthsFromTop = new double[3];
		cumulativeLengthsFromLeft = new double[3];
		
		double length;
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				cumulativeLengthsFromTop[i] = 0;
			}
			length = Merger.MERGER_HEIGHT/2;
			cumulativeLengthsFromTop[i+1] = cumulativeLengthsFromTop[i] + length;
		}
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				cumulativeLengthsFromLeft[i] = 0;
			}
			length = Merger.MERGER_HEIGHT/2;
			cumulativeLengthsFromLeft[i+1] = cumulativeLengthsFromLeft[i] + length;
		}
	}
	
	@Override
	public Rect getAABB() {
		return aabb;
	}
	
	@Override
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(color);
		
		g2.fillRect(
				(int)(ul.x * MODEL.PIXELS_PER_METER),
				(int)(ul.y * MODEL.PIXELS_PER_METER),
				(int)(MERGER_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(MERGER_HEIGHT * MODEL.PIXELS_PER_METER));
		
		if (MODEL.DEBUG_DRAW) {
			paintSkeleton(g2);
		}
		
		g2.setTransform(origTransform);
		
//		top.paint(g2);
//		left.paint(g2);
//		right.paint(g2);
//		bottom.paint(g2);
		
	}

	@Override
	public void paintHilite(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(hiliteColor);
		
		g2.fillRect(
				(int)(ul.x * MODEL.PIXELS_PER_METER),
				(int)(ul.y * MODEL.PIXELS_PER_METER),
				(int)(MERGER_WIDTH * MODEL.PIXELS_PER_METER),
				(int)(MERGER_HEIGHT * MODEL.PIXELS_PER_METER));
		
		g2.setTransform(origTransform);
		
	}
	
	void paintSkeleton(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		
		g2.drawLine(
				(int)((top.p.x) * MODEL.PIXELS_PER_METER),
				(int)((top.p.y + Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((bottom.p.x) * MODEL.PIXELS_PER_METER),
				(int)((bottom.p.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
		
		g2.drawLine(
				(int)((left.p.x + Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((left.p.y) * MODEL.PIXELS_PER_METER),
				(int)((right.p.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((right.p.y) * MODEL.PIXELS_PER_METER));
		
	}
	
	public void paintBorders(Graphics2D g2) {
		
//		g2.setColor(Color.GREEN);
//		g2.fillOval((int)(startBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(startBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
//		
//		g2.setColor(Color.RED);
//		g2.fillOval((int)(endBorderPoint.x * MODEL.PIXELS_PER_METER)-2, (int)(endBorderPoint.y * MODEL.PIXELS_PER_METER)-2, 4, 4);
		
	}
	
	public void paintDecorations(Graphics2D g2) {
		
	}
	
	//java.awt.Stroke mergerOutlineStroke = new BasicStroke(float width, int cap, int join, float miterlimit, float[] dash, float dash_phase);
	
	public static void paintOutline(Point p, Graphics2D g2) {
		
		java.awt.Stroke origStroke = g2.getStroke();
		
		g2.setStroke(Cursor.dashedOutlineStroke);
		g2.setColor(Color.GRAY);
		
		g2.drawRect(
				(int)((p.x - Merger.MERGER_WIDTH/2) * MODEL.PIXELS_PER_METER),
				(int)((p.y - Merger.MERGER_WIDTH/2) * MODEL.PIXELS_PER_METER),
				(int)((Merger.MERGER_WIDTH) * MODEL.PIXELS_PER_METER),
				(int)((Merger.MERGER_HEIGHT) * MODEL.PIXELS_PER_METER));
		
		Point top = p.plus(new Point(0, -Merger.MERGER_HEIGHT/2));
		Point left = p.plus(new Point(-Merger.MERGER_WIDTH/2, 0));
		Point right = p.plus(new Point(Merger.MERGER_WIDTH/2, 0));
		Point bottom = p.plus(new Point(0, Merger.MERGER_HEIGHT/2));
		
		g2.drawOval(
				(int)((top.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((top.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
		
		g2.drawOval(
				(int)((left.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((left.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
		
		g2.drawOval(
				(int)((right.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((right.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
		
		g2.drawOval(
				(int)((bottom.x - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((bottom.y - Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER),
				(int)((2*Vertex.INIT_VERTEX_RADIUS) * MODEL.PIXELS_PER_METER));
		
		g2.setStroke(origStroke);
		
	}
	
	public static Rect outlineAABB(Point p) {
		return new Rect(
				p.x - Merger.MERGER_WIDTH/2 - Vertex.INIT_VERTEX_RADIUS,
				p.y - Merger.MERGER_HEIGHT/2 - Vertex.INIT_VERTEX_RADIUS,
				Merger.MERGER_WIDTH + 2 * Vertex.INIT_VERTEX_RADIUS,
				Merger.MERGER_HEIGHT + 2 * Vertex.INIT_VERTEX_RADIUS);
	}
	
}
