package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	Color color;
	
	public Intersection(Point p) {
		super(p);
		color = new Color(0x44, 0x44, 0x44, 0xff);
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	public boolean supportsStopSigns() {
		return true;
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
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(color);
		
		shape.paint(g2);
		
		if (MODEL.DEBUG_DRAW) {
			
			paintAABB(g2);
			
		}
		
	}
}
