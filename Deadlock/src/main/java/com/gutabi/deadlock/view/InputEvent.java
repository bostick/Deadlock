package com.gutabi.deadlock.view;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.Component;

public class InputEvent {
	
	public final Component c;
	public final Point p;
	
	public InputEvent(Component c, Point p) {
		this.c = c;
		this.p = p;
	}
	
}
