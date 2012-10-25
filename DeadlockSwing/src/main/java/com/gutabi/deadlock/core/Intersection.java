package com.gutabi.deadlock.core;

import java.awt.Color;

public class Intersection extends Vertex {
	
	public Intersection(Point p) {
		super(p);
		color = new Color(0x44, 0x44, 0x44, 0xff);
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep() {
		return true;
	}
	
}
