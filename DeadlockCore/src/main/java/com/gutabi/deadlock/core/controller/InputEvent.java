package com.gutabi.deadlock.core.controller;

import com.gutabi.core.Point;

public class InputEvent {
	
	private Point p;
	
	public InputEvent(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
}
