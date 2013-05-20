package com.brentonbostick.capsloc;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

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
