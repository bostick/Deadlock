package com.gutabi.deadlock.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public abstract class Entity {
	
	protected Path2D path;
	
	public abstract boolean hitTest(Point p);
	
	protected Color color;
	protected Color hiliteColor;
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
}
