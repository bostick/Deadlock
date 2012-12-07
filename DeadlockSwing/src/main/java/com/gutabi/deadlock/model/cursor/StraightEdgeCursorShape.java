package com.gutabi.deadlock.model.cursor;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class StraightEdgeCursorShape extends Shape {
	
	public final Point first;
	public final Circle pCircle;
	public final Line line;
	
	public final AABB aabb;
	
	public StraightEdgeCursorShape(Point first, Point p) {
		
		this.first = first;
		this.pCircle = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		
		this.line = new Line(first.x, first.y, p.x, p.y);
		
		aabb = pCircle.aabb;
		
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
		return aabb;
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.setColor(Color.GRAY);
		ctxt.setWorldPixelStroke(1);
		
		pCircle.draw(ctxt);
		line.draw(ctxt);
		
		if (MODEL.DEBUG_DRAW) {
			
			aabb.draw(ctxt);
			
		}
	}
}
