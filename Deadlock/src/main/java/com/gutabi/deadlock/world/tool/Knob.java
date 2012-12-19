package com.gutabi.deadlock.world.tool;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;

public abstract class Knob {
	
	public final World world;
	
	Point p;
	
	public Knob(World world) {
		this.world = world;
	}
	
	public void setPoint(Point p) {
		this.p = p;
	}
	
	private AABB aabb() {
		double pixel = 1/world.cam.pixelsPerMeter;
		return new AABB(p.x + -3 * pixel, p.y + -3 * pixel, 7 * pixel, 7 * pixel);
	}
	
	public abstract void drag(Point p);
	
	public boolean hitTest(Point p) {
		return aabb().hitTest(p);
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.setColor(Color.ORANGE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		aabb().paint(ctxt);
	}
	
}
