package com.gutabi.deadlock.model.menu;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public abstract class Menu {
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	public void click(Point p) {
		
		MenuItem item = hitTest(p);
		
		if (item != null) {
			item.action();
		}
		
	}
	
	public abstract MenuItem hitTest(Point p);
	
	public abstract void render(RenderingContext ctxt);
	
	public abstract void paint(RenderingContext ctxt);
	
}
