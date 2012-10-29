package com.gutabi.deadlock.core.graph;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;

public class Sink extends Vertex {
	
	public Source matchingSource;
	
	public Sink(Point p) {
		super(p);
		color = Color.RED;
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public boolean isDeleteable() {
		return false;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep() {
		return true;
	}
	
}
