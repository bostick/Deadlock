package com.gutabi.deadlock.core.graph;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;

public class MergerSource extends Source {
	
	public MergerSink matchingSink;
	
	public MergerSource(Point p, Axis a) {
		super(p, a);
		color = Color.GREEN;
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
