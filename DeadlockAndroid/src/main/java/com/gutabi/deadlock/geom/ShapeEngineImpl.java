package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public class ShapeEngineImpl extends ShapeEngine {

	public AABB createAABB(double x, double y, double width, double height) {
		return new AABBImpl(x, y, width, height);
	}

	public Circle createCircle(Object parent, Point center, double radius) {
		return new CircleImpl(parent, center, radius);
	}

	public Line createLine(Point p0, Point p1) {
		return new LineImpl(p0, p1);
	}

	public Polyline createPolyline(List<Point> pts) {
		return new PolylineImpl(pts);
	}

	public Quad createQuad(Object parent, Point p0, Point p1, Point p2, Point p3) {
		return new QuadImpl(parent, p0, p1, p2, p3);
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
