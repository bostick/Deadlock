package com.gutabi.deadlock.core;

import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class Entity {
	
	public abstract Shape getShape();
	
	public abstract Entity hitTest(Point p);
	
	public abstract boolean isUserDeleteable();
	
	public abstract void preStart();
	
	public abstract void postStop();
	
	public abstract void preStep(double t);
	
	/**
	 * return true if car should persist after time step
	 */
	public abstract boolean postStep(double t);
		
	public abstract void paintHilite(RenderingContext ctxt);
	
}
