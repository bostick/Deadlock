package com.gutabi.deadlock.core;

import java.awt.Graphics2D;

import org.apache.log4j.Logger;

//@SuppressWarnings("static-access")
public abstract class Entity {
	
	static Logger logger = Logger.getLogger(Entity.class);
	
	public abstract boolean hitTest(Point p, double radius);
	
	public abstract boolean isDeleteable();
	
	public abstract void preStep(double t);
	
	public abstract boolean postStep();
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
	public abstract Rect getAABB();
	
}
