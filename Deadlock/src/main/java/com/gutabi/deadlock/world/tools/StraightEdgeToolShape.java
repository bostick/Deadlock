package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Capsule;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Vertex;

public class StraightEdgeToolShape implements Shape {
	
	public final Circle firstCircle;
	public final Circle pCircle;
	public final Capsule cap;
	
//	private final AABB aabb;
	
	public StraightEdgeToolShape(World world, Point first, Point p) {
		
		this.firstCircle = APP.platform.createShapeEngine().createCircle(null, first, Vertex.INIT_VERTEX_RADIUS);
		this.pCircle = APP.platform.createShapeEngine().createCircle(null, p, Vertex.INIT_VERTEX_RADIUS);
		
		this.cap = APP.platform.createShapeEngine().createCapsule(null, firstCircle, pCircle);
		
//		aabb = cap.aabb;
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
	
	public void paint(RenderingContext ctxt) {
		assert false;
	}
	
}
