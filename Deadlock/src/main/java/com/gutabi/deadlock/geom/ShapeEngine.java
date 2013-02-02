package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class ShapeEngine {
	
	public abstract AABB createAABB(Object parent, double x, double y, double width, double height);
	
	public abstract Circle createCircle(Object parent, Point center, double radius);
	
	public abstract Capsule createCapsule(Object parent, Circle ac, Circle bc);
	
	public abstract Line createLine(Point p0, Point p1);
	
	public abstract Polyline createPolyline(List<Point> pts);
	
	public abstract Quad createQuad(Object parent, Point p0, Point p1, Point p2, Point p3);
	
	public abstract Ellipse createEllipse(Point center, double x, double y);
	
	public abstract Triangle createTriangle(Point p0, Point p1, Point p2);
	
	public abstract QuadCurve createQuadCurve(Point start, Point c0, Point end);
	
	public abstract CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end);
	
	
	
}
