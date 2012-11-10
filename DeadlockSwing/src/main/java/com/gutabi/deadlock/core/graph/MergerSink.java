package com.gutabi.deadlock.core.graph;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;

public class MergerSink extends Sink {
	
	public MergerSource matchingSource;
	
	public MergerSink(Point p, Axis a) {
		super(p, a);
		hiliteColor = new Color(0, 255, 255);
	}

	@Override
	public boolean isDeleteable() {
		return false;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		carQueue.clear();
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
}
