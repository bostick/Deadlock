package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.model.graph.Vertex;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class StraightEdgeCursorShape extends Shape {
	
	public final Circle first;
	public final Circle pCircle;
//	public final Line line;
	public final Capsule cap;
	
	private final AABB aabb;
	
	public StraightEdgeCursorShape(Point first, Point p) {
		
		this.first = new Circle(null, first, Vertex.INIT_VERTEX_RADIUS);
		this.pCircle = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		
		this.cap = new Capsule(null, this.first, pCircle);
		
		aabb = cap.aabb;
		
	}
	
	public boolean hitTest(Point p) {
		assert false;
		return false;
	}
	
	public Shape plus(Point p) {
		assert false;
		return null;
	}
	
	public AABB getAABB() {
		assert false;
		return null;
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.setColor(Color.GRAY);
		ctxt.setWorldPixelStroke(1);
		
		cap.draw(ctxt);
		
		if (MODEL.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
	}
}
