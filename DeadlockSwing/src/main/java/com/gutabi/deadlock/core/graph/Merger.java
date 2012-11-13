package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.model.fixture.MergerSink;
import com.gutabi.deadlock.model.fixture.MergerSource;

@SuppressWarnings("static-access")
public class Merger extends Edge {
	
	public static final double MERGER_WIDTH = 5.0;
	public static final double MERGER_HEIGHT = 5.0;
	
	public final Point ul;
	
	private Color color;
	private Color hiliteColor;
	
	public final MergerSink top;
	public final MergerSink left;
	public final MergerSource right;
	public final MergerSource bottom;
	
	public final Quad worldQuad;
	
	private double[] cumulativeLengthsFromTop;
	private double[] cumulativeLengthsFromLeft;
	
	public Merger(Point center) {
		
		this.ul = center.plus(new Point(-MERGER_WIDTH/2,  -MERGER_HEIGHT/2));
		
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
		
		Point p0 = ul;
		Point p1 = new Point(ul.x + MERGER_WIDTH, ul.y);
		Point p2 = new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT);
		Point p3 = new Point(ul.x, ul.y + MERGER_HEIGHT);
		worldQuad = new Quad(this, p0, p1, p2, p3);
		shape = worldQuad;
		
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
	
	public Entity decorationsHitTest(Point p) {
		return null;
	}
	
	public Entity decorationsBestHitTest(Shape s) {
		return null;
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
		;
	}
	
	public void paintDecorations(Graphics2D g2) {
		;
	}
	
	public static Rect outlineAABB(Point p) {
		return new Rect(
				p.x - Merger.MERGER_WIDTH/2 - Vertex.INIT_VERTEX_RADIUS,
				p.y - Merger.MERGER_HEIGHT/2 - Vertex.INIT_VERTEX_RADIUS,
				Merger.MERGER_WIDTH + 2 * Vertex.INIT_VERTEX_RADIUS,
				Merger.MERGER_HEIGHT + 2 * Vertex.INIT_VERTEX_RADIUS);
	}
	
}
