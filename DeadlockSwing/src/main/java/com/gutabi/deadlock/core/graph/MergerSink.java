package com.gutabi.deadlock.core.graph;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;

public class MergerSink extends Sink {
	
	public MergerSource matchingSource;
	
	public MergerSink(Point p) {
		super(p);
		color = Color.RED;
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
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
