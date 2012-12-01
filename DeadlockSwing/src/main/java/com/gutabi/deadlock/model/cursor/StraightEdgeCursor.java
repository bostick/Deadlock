package com.gutabi.deadlock.model.cursor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.view.RenderingContext;

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
