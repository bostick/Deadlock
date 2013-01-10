package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.ui.paint.RenderingContext;

public interface Shape {
	
	public void paint(RenderingContext ctxt);
	
	public void draw(RenderingContext ctxt);
	
}