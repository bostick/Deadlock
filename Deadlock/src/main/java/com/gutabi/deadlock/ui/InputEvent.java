package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.math.Point;

public class InputEvent {
	
	public final Point p;
	public final Panel panel;
	
	public InputEvent(Panel panel, Point p) {
		this.panel = panel;
		this.p = p;
	}
	
}
