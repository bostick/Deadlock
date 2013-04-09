package com.gutabi.deadlock.world.tools;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Knob {
	
	Point p;
	
	public AABB aabb;
	
	public Knob() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		double pixelGuess = 1.0/32.0;
		aabb = new AABB(p.x + -3 * pixelGuess, p.y + -3 * pixelGuess, 7 * pixelGuess, 7 * pixelGuess);
	}
	
	public abstract void drag(Point p);
	
	public boolean hitTest(Point p) {
		return aabb.hitTest(p);
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.setColor(Color.ORANGE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		aabb.paint(ctxt);
		
		ctxt.clearXORMode();
	}
	
}
