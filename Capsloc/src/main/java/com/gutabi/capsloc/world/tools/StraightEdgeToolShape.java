package com.gutabi.capsloc.world.tools;

import com.gutabi.capsloc.geom.Capsule;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.Shape;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.graph.Vertex;

public class StraightEdgeToolShape implements Shape {
	
	public final Circle firstCircle;
	public final Circle pCircle;
	public final Capsule cap;
	
//	private final AABB aabb;
	
	public StraightEdgeToolShape(World world, Point first, Point p) {
		
		this.firstCircle = new Circle(first, Vertex.INIT_VERTEX_RADIUS);
		this.pCircle = new Circle(p, Vertex.INIT_VERTEX_RADIUS);
		
		this.cap = new Capsule(firstCircle, pCircle);
		
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
