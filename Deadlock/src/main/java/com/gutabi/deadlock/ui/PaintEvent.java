package com.gutabi.deadlock.ui;

public class PaintEvent {
	
	public final Component c;
	public final RenderingContext ctxt;
	
	public PaintEvent(Component c, RenderingContext ctxt) {
		this.c = c;
		this.ctxt = ctxt;
	}
	
}