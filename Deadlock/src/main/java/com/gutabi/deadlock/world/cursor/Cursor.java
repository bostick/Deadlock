package com.gutabi.deadlock.world.cursor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;

//@SuppressWarnings("static-access")
public abstract class Cursor {
	
	public final WorldScreen screen;
	
	public Point p;
	
	public Cursor(WorldScreen screen) {
		this.screen = screen;
	}
	
	public abstract void setPoint(Point p);
	
	public final Point getPoint() {
		return p;
	}
	
	public abstract Shape getShape();
	
	public abstract void qKey(InputEvent ev);
	
	public abstract void wKey(InputEvent ev);
	
	public abstract void aKey(InputEvent ev);
	
	public abstract void sKey(InputEvent ev);
	
	public abstract void dKey(InputEvent ev);
	
	public abstract void insertKey(InputEvent ev);
	
	public abstract void d1Key(InputEvent ev);
	public abstract void d2Key(InputEvent ev);
	public abstract void d3Key(InputEvent ev);
	
	public abstract void plusKey(InputEvent ev);
	public abstract void minusKey(InputEvent ev);
	
	public abstract void escKey(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void exited(InputEvent ev);
	
	public abstract void draw(RenderingContext ctxt);
	
}
