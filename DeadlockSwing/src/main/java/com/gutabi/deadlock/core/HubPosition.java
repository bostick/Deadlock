package com.gutabi.deadlock.core;

public class HubPosition extends Position {
	
	private final Hub h;
	
	public HubPosition(Hub h, Point p) {
		super(p);
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

	@Override
	public Position nextToward(Position goal) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
