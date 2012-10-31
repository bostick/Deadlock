package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Rect;


public class EdgeSegment {
	
	public final Point a;
	public final Point b;
	
	public final Rect aabb;
	
	public EdgeSegment(Point a, Point b) {
		this.a = a;
		this.b = b;
		
		double ulX = Math.min(a.x, b.x) - Edge.ROAD_RADIUS;
		double ulY = Math.min(a.y, b.y) - Edge.ROAD_RADIUS;
		double brX = Math.max(a.x, b.x) + Edge.ROAD_RADIUS;
		double brY = Math.max(a.y, b.y) + Edge.ROAD_RADIUS;
		
		aabb = new Rect(ulX, ulY, brX - ulX, brY - ulY);
		
	}
	
	public boolean hitTest(Point p, double radius) {
		if (DMath.lessThanEquals(Point.distance(p, a, b), Edge.ROAD_RADIUS + radius)) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("static-access")
	public void paint(Graphics2D g2) {
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		Point diff = new Point(b.x - a.x, b.y - a.y);
		Point up = Point.ccw90(diff).multiply(Edge.ROAD_RADIUS / diff.length);
		Point down = Point.cw90(diff).multiply(Edge.ROAD_RADIUS / diff.length);
		
		Point p0 = a.plus(up);
		Point p1 = b.plus(up);
		Point p2 = b.plus(down);
		Point p3 = a.plus(down);
		
		xPoints[0] = (int)(p0.x * MODEL.PIXELS_PER_METER);
		xPoints[1] = (int)(p1.x * MODEL.PIXELS_PER_METER);
		xPoints[2] = (int)(p2.x * MODEL.PIXELS_PER_METER);
		xPoints[3] = (int)(p3.x * MODEL.PIXELS_PER_METER);
		yPoints[0] = (int)(p0.y * MODEL.PIXELS_PER_METER);
		yPoints[1] = (int)(p1.y * MODEL.PIXELS_PER_METER);
		yPoints[2] = (int)(p2.y * MODEL.PIXELS_PER_METER);
		yPoints[3] = (int)(p3.y * MODEL.PIXELS_PER_METER);
		
		g2.fillOval((int)((a.x - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)((a.y - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER));
		g2.fillPolygon(xPoints, yPoints, 4);
		g2.fillOval((int)((b.x - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)((b.y - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER));
	}
	
	@SuppressWarnings("static-access")
	public void draw(Graphics2D g2) {
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		Point diff = new Point(b.x - a.x, b.y - a.y);
		Point up = Point.ccw90(diff).multiply(Edge.ROAD_RADIUS / diff.length);
		Point down = Point.cw90(diff).multiply(Edge.ROAD_RADIUS / diff.length);
		
		Point p0 = a.plus(up);
		Point p1 = b.plus(up);
		Point p2 = b.plus(down);
		Point p3 = a.plus(down);
		
		xPoints[0] = (int)(p0.x * MODEL.PIXELS_PER_METER);
		xPoints[1] = (int)(p1.x * MODEL.PIXELS_PER_METER);
		xPoints[2] = (int)(p2.x * MODEL.PIXELS_PER_METER);
		xPoints[3] = (int)(p3.x * MODEL.PIXELS_PER_METER);
		
		yPoints[0] = (int)(p0.y * MODEL.PIXELS_PER_METER);
		yPoints[1] = (int)(p1.y * MODEL.PIXELS_PER_METER);
		yPoints[2] = (int)(p2.y * MODEL.PIXELS_PER_METER);
		yPoints[3] = (int)(p3.y * MODEL.PIXELS_PER_METER);
		
		g2.drawOval((int)((a.x - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)((a.y - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER));
		g2.drawPolygon(xPoints, yPoints, 4);
		g2.drawOval((int)((b.x - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)((b.y - Edge.ROAD_RADIUS) * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER), (int)(2 * Edge.ROAD_RADIUS * MODEL.PIXELS_PER_METER));
		
	}
}
