package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldScreen;

public abstract class Knob {
	
	public final World world;
	
	Point p;
	
	WorldScreen worldScreen;
	
	public Knob(World world) {
		this.world = world;
		
		worldScreen = (WorldScreen)APP.appScreen;
	}
	
	public void setPoint(Point p) {
		this.p = p;
	}
	
	private AABB aabb() {
		double pixel = 1/worldScreen.pixelsPerMeter;
		return new AABB(p.x + -3 * pixel, p.y + -3 * pixel, 7 * pixel, 7 * pixel);
	}
	
	public abstract void drag(Point p);
	
	public boolean hitTest(Point p) {
		return aabb().hitTest(p);
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.setColor(Color.ORANGE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		aabb().paint(ctxt);
		
		ctxt.setPaintMode();
	}
	
}
