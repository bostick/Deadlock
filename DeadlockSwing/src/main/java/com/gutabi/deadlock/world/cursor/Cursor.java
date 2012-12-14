package com.gutabi.deadlock.world.cursor;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;

//@SuppressWarnings("static-access")
public abstract class Cursor {
	
	public final World world;
	
	public Point p;
	
	public Cursor(World world) {
		this.world = world;
	}
	
	public abstract void setPoint(Point p);
	
	public final Point getPoint() {
		return p;
	}
	
	public abstract Shape getShape();
	
	public abstract void qKey();
	
	public abstract void wKey();
	
	public abstract void aKey();
	
	public abstract void sKey();
	
	public abstract void dKey();
	
	public abstract void insertKey();
	
	public abstract void d1Key();
	public abstract void d2Key();
	public abstract void d3Key();
	
	public abstract void plusKey();
	public abstract void minusKey();
	
	public abstract void escKey();
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void exited(InputEvent ev);
	
	public abstract void draw(RenderingContext ctxt);
	
}
