package com.gutabi.deadlock.quadranteditor;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;

public abstract class Button {
	
	public boolean enabled = true;
	
	public final AABB aabb;
	
	public Button(double x, double y, double width, double height) {
		aabb = new AABB(x, y, width, height);
	}
	
	public boolean hitTest(Point p) {
		return aabb.hitTest(p);
	}
	
	public abstract void action();
	
	public abstract void paint(RenderingContext ctxt);
//	public void paint(RenderingContext ctxt) {
//		ctxt.setColor(Color.BLUE);
//		aabb.paint(ctxt);
//	}
	
}
