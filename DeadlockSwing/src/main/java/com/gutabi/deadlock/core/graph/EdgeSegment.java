package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;

public class EdgeSegment {
	
	public final Point a;
	public final Point b;
	
	public final Point aabbLoc;
	public final Dim aabbDim;
	
	public EdgeSegment(Point a, Point b) {
		this.a = a;
		this.b = b;
		
		double ulX = Math.min(a.x, b.x);
		double ulY = Math.min(a.y, b.y);
		double brX = Math.max(a.x, b.x);
		double brY = Math.max(a.y, b.y);
		
		aabbLoc = new Point(ulX, ulY);
		aabbDim = new Dim(brX - ulX, brY - ulY);
		
	}
	
	public boolean hitTest(Point p, double radius) {
		if (DMath.lessThanEquals(Point.distance(p, a, b), Edge.ROAD_RADIUS + radius)) {
			return true;
		}
		return false;
	}
}
