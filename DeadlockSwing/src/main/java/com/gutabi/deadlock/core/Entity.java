package com.gutabi.deadlock.core;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public abstract class Entity {
	
	public abstract boolean hitTest(Point p);
	
	
	protected Body b2dBody;
	protected Shape b2dShape;
	protected Fixture b2dFixture;
	protected boolean b2dInited;
	
	public abstract void b2dInit();
	
	public abstract void b2dCleanup();
	
	
	
	protected Color color;
	protected Color hiliteColor;
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
}
