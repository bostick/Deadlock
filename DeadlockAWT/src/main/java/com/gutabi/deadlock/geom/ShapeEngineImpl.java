package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public class ShapeEngineImpl extends ShapeEngine {

	public Circle createCircle(Point center, double radius) {
		return new CircleImpl(center, radius);
	}
	
	public Capsule createCapsule(Circle ac, Circle bc) {
		return new CapsuleImpl(ac, bc);
	}

	public Polyline createPolyline(List<Point> pts) {
		return new PolylineImpl(pts);
	}

	public OBB createOBB(Point center, double angle, double xExtant, double yExtant) {
		return new OBBImpl(center, angle, xExtant, yExtant);
	}

	public Ellipse createEllipse(Point center, double x, double y) {
		return new EllipseImpl(center, x, y);
	}

	public Triangle createTriangle(Point p0, Point p1, Point p2) {
		return new TriangleImpl(p0, p1, p2);
	}

	public QuadCurve createQuadCurve(Point start, Point c0, Point end) {
		return new QuadCurveImpl(start, c0, end);
	}

	public CubicCurve createCubicCurve(Point start, Point c0, Point c1, Point end) {
		return new CubicCurveImpl(start, c0, c1, end);
	}
	
}
