package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class ShapeEngine {
	
	public abstract Circle createCircle(Point center, double radius);
	
	public abstract Capsule createCapsule(Circle ac, Circle bc);
	
	public abstract Polyline createPolyline(List<Point> pts);
	
	public abstract OBB createOBB(Point center, double a, double xExtant, double yExtant);
	
	public abstract Ellipse createEllipse(Point center, double x, double y);
	
	public abstract Triangle createTriangle(Point p0, Point p1, Point p2);
	
	public abstract QuadCurve createQuadCurve(Point start, Point c0, Point end);
	
	public abstract CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end);
	
	
	
}
