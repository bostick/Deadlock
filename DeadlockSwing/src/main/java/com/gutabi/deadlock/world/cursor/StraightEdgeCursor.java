package com.gutabi.deadlock.world.cursor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

public class StraightEdgeCursor extends Cursor {
	
	public final Vertex first;
	
	StraightEdgeCursorShape shape;
	
	public StraightEdgeCursor(Vertex first) {
		this.first = first;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new StraightEdgeCursorShape(first.p, p);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		shape.draw(ctxt);
		
	}
}
