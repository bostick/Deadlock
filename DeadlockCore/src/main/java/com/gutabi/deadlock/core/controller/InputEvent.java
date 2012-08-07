package com.gutabi.deadlock.core.controller;

import com.gutabi.core.DPoint;

public class InputEvent {
	
	private DPoint p;
	
	public InputEvent(DPoint p) {
		this.p = p;
	}
	
	public DPoint getDPoint() {
		return p;
	}
	
}
