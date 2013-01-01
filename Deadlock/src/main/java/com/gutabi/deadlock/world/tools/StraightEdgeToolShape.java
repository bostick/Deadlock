package com.gutabi.deadlock.world.tools;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Vertex;

public class StraightEdgeToolShape implements Shape {
	
	public final Circle firstCircle;
	public final Circle pCircle;
	public final Capsule cap;
	
//	private final AABB aabb;
	
	public StraightEdgeToolShape(World world, Point first, Point p) {
		
		this.firstCircle = new Circle(null, first, Vertex.INIT_VERTEX_RADIUS);
		this.pCircle = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		
		this.cap = new Capsule(null, firstCircle, pCircle, -1);
		
//		aabb = cap.aabb;
	}
	
	public java.awt.Shape java2D() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (!firstCircle.center.equals(pCircle.center)) {
			cap.draw(ctxt);
		} else {
			/*
			 * the overlapping causes XOR mode to draw nothing, so handle it here
			 */
			firstCircle.draw(ctxt);
		}
		
	}
}
