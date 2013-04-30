package com.gutabi.capsloc.ui;

import com.gutabi.capsloc.math.Point;

public class InputEvent {
	
	public final Point p;
	public final Panel panel;
	
	public InputEvent(Panel panel, Point p) {
		this.panel = panel;
		this.p = p;
	}
	
}
