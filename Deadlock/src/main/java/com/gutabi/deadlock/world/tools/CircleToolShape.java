package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Ellipse;
import com.gutabi.deadlock.geom.Polyline;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleToolShape implements Shape {
	
	public final Point p;
	public final double xRadius;
	public final double yRadius;
	
	public final Ellipse c0;
	public final Ellipse c1;
	
	public final Point[] skeleton;
	public final Polyline skeletonShape;
	
	public CircleToolShape(Point p, double xRadius, double yRadius) {
		this.p = p;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		c0 = APP.platform.createEllipse(p, xRadius, yRadius);
		c1 = APP.platform.createEllipse(p, xRadius + 2 * Vertex.INIT_VERTEX_RADIUS, yRadius + 2 * Vertex.INIT_VERTEX_RADIUS);
		
		skeleton = APP.platform.createEllipse(p, xRadius + Vertex.INIT_VERTEX_RADIUS, yRadius + Vertex.INIT_VERTEX_RADIUS).skeleton();
		skeletonShape = APP.platform.createPolyline(skeleton);
		
	}
	
	public void draw(RenderingContext ctxt) {
		c0.draw(ctxt);
		c1.draw(ctxt);
	}
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}
	
}
