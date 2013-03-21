package com.gutabi.deadlock.world.tools;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public interface Tool {
	
	public abstract void setPoint(Point p);
	
	public abstract Point getPoint();
	
	public abstract Shape getShape();
	
	public abstract void qKey();
	
	public abstract void wKey();
	
	public abstract void aKey();
	
	public abstract void sKey();
	
	public abstract void dKey();
	
	public abstract void fKey();
	
	public abstract void gKey();
	
	public abstract void insertKey();
	
	public abstract void d1Key();
	public abstract void d2Key();
	public abstract void d3Key();
	
	public abstract void plusKey();
	public abstract void minusKey();
	
	public abstract void escKey();
	
	public abstract void deleteKey();
	
	public abstract void ctrlSKey();
	
	public abstract void ctrlOKey();
	
	public abstract void enterKey();
	
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);	
	
	
	public abstract void draw(RenderingContext ctxt);
	
}
