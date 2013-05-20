package com.brentonbostick.capsloc.world.tools;

import com.brentonbostick.capsloc.geom.Capsule;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.World;
import com.brentonbostick.capsloc.world.graph.Vertex;

public class StraightEdgeToolShape {
	
	public final Circle firstCircle;
	public final Circle pCircle;
	public final Capsule cap;
	
//	private final AABB aabb;
//	GeometryPath path = APP.platform.createGeometryPath();
	
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
