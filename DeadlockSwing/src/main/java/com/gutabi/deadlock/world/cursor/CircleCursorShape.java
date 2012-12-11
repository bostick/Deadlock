package com.gutabi.deadlock.world.cursor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

public class CircleCursorShape extends Shape {
	
	public final Point p;
	
	private final Circle c0;
	private final Circle c1;
	
	public CircleCursorShape(Point p) {
		this.p = p;
		
		c0 = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		c1 = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS + 2 * Vertex.INIT_VERTEX_RADIUS);
		
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
	}

}
