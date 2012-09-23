package com.gutabi.deadlock.controller;

import java.awt.Component;

import com.gutabi.deadlock.core.Point;

public class InputEvent {
	
	Component c;
	Point p;
	
	public InputEvent(Component c, Point p) {
		this.c = c;
		this.p = p;
	}
	
	public Component getComponent() {
		return c;
	}
	
	public Point getPoint() {
		return p;
	}
	
}
