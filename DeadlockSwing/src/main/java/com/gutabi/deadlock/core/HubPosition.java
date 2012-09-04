package com.gutabi.deadlock.core;

public class HubPosition extends Position {
	
	private final Hub h;
	
	public HubPosition(Hub h, Point p) {
		super(p, h);
		this.h = h;
	}
	
	public Hub getHub() {
		return h;
	}
	
}
