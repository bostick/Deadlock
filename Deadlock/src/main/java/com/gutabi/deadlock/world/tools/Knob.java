package com.gutabi.deadlock.world.tools;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public abstract class Knob {
	
	WorldScreen screen;
	public final World world;
	
	Point p;
	
	public Knob(WorldScreen screen, World world) {
		this.screen = screen;
		this.world = world;
	}
	
	public void setPoint(Point p) {
		this.p = p;
	}
	
	private AABB aabb() {
		double pixel = 1/screen.pixelsPerMeter;
		return new AABB(p.x + -3 * pixel, p.y + -3 * pixel, 7 * pixel, 7 * pixel);
	}
	
	public abstract void drag(Point p);
	
	public boolean hitTest(Point p) {
		return aabb().hitTest(p);
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.setColor(Color.ORANGE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1.0);
		
		aabb().paint(ctxt);
		
		ctxt.setPaintMode();
	}
	
}
