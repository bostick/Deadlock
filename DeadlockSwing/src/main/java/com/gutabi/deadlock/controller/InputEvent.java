package com.gutabi.deadlock.controller;

import java.awt.Component;

import com.gutabi.deadlock.core.Point;

public class InputEvent {
	
	public final Component c;
	public final Point p;
	
	public InputEvent(Component c, Point p) {
		this.c = c;
		this.p = p;
	}
	
}
