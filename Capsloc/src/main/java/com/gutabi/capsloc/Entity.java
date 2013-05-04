package com.gutabi.capsloc;

import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class Entity {
	
	public abstract Entity hitTest(Point p);
	
	public abstract boolean isUserDeleteable();
	
	public abstract void preStart();
	
	public abstract void postStop();
	
	public abstract boolean preStep(double t);
	
	/**
	 * return true if car should persist after time step
	 */
	public abstract boolean postStep(double t);
		
	public abstract void paintHilite(RenderingContext ctxt);
	
}
