package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Panel {
	
	public AABB aabb = new AABB(0, 0, 0, 0);
	
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);
	
	public abstract void postDisplay();
	
	public abstract void paint(RenderingContext ctxt);
	
}
