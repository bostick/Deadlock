package com.gutabi.deadlock.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public abstract class Entity {
	
	public abstract boolean hitTest(Point p);
	public abstract boolean hitTest(Point p, double radius);
	
	public abstract boolean isDeleteable();
	
	public abstract void preStep(double t);
	public abstract boolean postStep();
	
	
	
	protected Path2D path;
	public abstract void computePath();
	
	protected Color color;
	protected Color hiliteColor;
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
	public Point aabbLoc;
	public Dim aabbDim;
	
}
