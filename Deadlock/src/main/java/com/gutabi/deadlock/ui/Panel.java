package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Panel {
	
	public AABB aabb = APP.platform.createShapeEngine().createAABB(null, 0, 0, 0, 0);
	
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);
	
	public abstract void postDisplay();
	
	public abstract void paint(RenderingContext ctxt);
	
}
