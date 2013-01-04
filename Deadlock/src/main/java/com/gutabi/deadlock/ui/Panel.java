package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.core.geom.AABB;

public abstract class Panel {
	
	public  AABB aabb = new AABB(0, 0, 0, 0);
	
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);
	
//	public abstract void entered(InputEvent ev);
	
//	public abstract void exited(InputEvent ev);
	
	public abstract void postDisplay();
	
	public abstract void paint(RenderingContext ctxt);
	
}
