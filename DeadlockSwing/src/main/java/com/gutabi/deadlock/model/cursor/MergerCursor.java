package com.gutabi.deadlock.model.cursor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.model.Cursor;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class MergerCursor extends Cursor {
	
	MergerCursorShape shape;
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new MergerCursorShape(p);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void paint(RenderingContext ctxt) {
	
		if (p == null) {
			return;
		}
		
		shape.paint(ctxt);
		
	}

}
