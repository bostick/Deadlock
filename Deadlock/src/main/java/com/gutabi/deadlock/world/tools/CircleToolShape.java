package com.gutabi.deadlock.world.tools;

import java.util.List;


import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.math.geom.Ellipse;
import com.gutabi.deadlock.math.geom.Polyline;
import com.gutabi.deadlock.math.geom.Shape;
import com.gutabi.deadlock.math.geom.ShapeUtils;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleToolShape implements Shape {
	
	public final Point p;
	public final double xRadius;
	public final double yRadius;
	
	public final Ellipse c0;
	public final Ellipse c1;
	
	public final List<Point> skeleton;
	public final Polyline skeletonShape;
	
	public CircleToolShape(Point p, double xRadius, double yRadius) {
		this.p = p;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		c0 = new Ellipse(p, xRadius, yRadius);
		c1 = new Ellipse(p, xRadius + 2 * Vertex.INIT_VERTEX_RADIUS, yRadius + 2 * Vertex.INIT_VERTEX_RADIUS);
		
		skeleton = ShapeUtils.skeleton(new Ellipse(p, xRadius + Vertex.INIT_VERTEX_RADIUS, yRadius + Vertex.INIT_VERTEX_RADIUS));
		skeletonShape = new Polyline(skeleton);
		
	}
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
		c0.draw(ctxt);
		c1.draw(ctxt);
	}

}
