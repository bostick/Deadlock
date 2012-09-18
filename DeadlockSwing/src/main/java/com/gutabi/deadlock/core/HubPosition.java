package com.gutabi.deadlock.core;

public class HubPosition extends Position {
	
	private final Point p;
	private final Hub h;
	
	public HubPosition(Hub h, Point p) {
		this.p = p;
		this.h = h;
	}
	
	public Hub getHub() {
		return h;
	}

	@Override
	public Point getPoint() {
		return p;
	}
	
	public Driveable getDriveable() {
		return h;
	}
	
	@Override
	public double distanceTo(Position b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
