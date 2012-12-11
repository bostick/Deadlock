package com.gutabi.deadlock.world.cursor;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;

public class CircleCursor extends Cursor {

	CircleCursorShape shape;
	
	public CircleCursor() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new CircleCursorShape(p);
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
		
		ctxt.setColor(Color.GRAY);
		ctxt.setWorldPixelStroke(1);
		
		shape.draw(ctxt);
		
	}

}
