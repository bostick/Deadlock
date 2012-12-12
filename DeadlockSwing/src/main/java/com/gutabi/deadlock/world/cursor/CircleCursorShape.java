package com.gutabi.deadlock.world.cursor;


import java.awt.Color;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Ellipse;
import com.gutabi.deadlock.core.geom.Polyline;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleCursorShape extends Shape {
	
	public final Point p;
	public final double xRadius;
	public final double yRadius;
	
	public final Ellipse c0;
	public final Ellipse c1;
	
	public final List<Point> skeleton;
	public final Polyline skeletonShape;
	
	public CircleCursorShape(Point p, double xRadius, double yRadius) {
		this.p = p;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		c0 = new Ellipse(p, xRadius, yRadius);
		c1 = new Ellipse(p, xRadius + 2 * Vertex.INIT_VERTEX_RADIUS, yRadius + 2 * Vertex.INIT_VERTEX_RADIUS);
		
		skeleton = new Ellipse(p, xRadius + Vertex.INIT_VERTEX_RADIUS, yRadius + Vertex.INIT_VERTEX_RADIUS).skeleton();
		skeletonShape = new Polyline(skeleton);
		
	}
	
	@Override
	public Shape plus(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hitTest(Point p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AABB getAABB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(RenderingContext ctxt) {
		c0.draw(ctxt);
		c1.draw(ctxt);
		
		ctxt.setColor(Color.BLUE);
		skeletonShape.draw(ctxt);
	}

}
