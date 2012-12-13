package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;

public abstract class Knob {
	
	Point p;
	
	public Knob() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
	}
	
	private AABB aabb() {
		double pixel = 1/APP.world.PIXELS_PER_METER_DEBUG;
		return new AABB(p.x + -3 * pixel, p.y + -3 * pixel, 7 * pixel, 7 * pixel);
	}
	
	public abstract void drag(Point p);
	
	public boolean hitTest(Point p) {
		return aabb().hitTest(p);
	}
	
	public void draw(RenderingContext ctxt) {
		aabb().paint(ctxt);
	}
	
}
