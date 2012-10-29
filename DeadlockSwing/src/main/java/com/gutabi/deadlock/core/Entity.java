package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.apache.log4j.Logger;

@SuppressWarnings("static-access")
public abstract class Entity {
	
	static Logger logger = Logger.getLogger(Entity.class);
	
	public abstract boolean hitTest(Point p, double radius);
	
	public boolean isDeleteable() {
		return true;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep() {
		return true;
	}
	
	
	protected Color color;
	protected Color hiliteColor;
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
	protected Rect aabb;
	public final Rect getAABB() {
		return aabb;
	}
	
	public final boolean hitTest(Point p) {
		if (DMath.lessThanEquals(aabb.x, p.x) && DMath.lessThanEquals(p.x, aabb.x+aabb.width) &&
				DMath.lessThanEquals(aabb.y, p.y) && DMath.lessThanEquals(p.y, aabb.y+aabb.height)) {
			return hitTest(p, 0.0);
		} else {
			return false;
		}
	}
	
	protected void paintAABB(Graphics2D g2) {
		
//		logger.debug(aabb);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabb.x * MODEL.PIXELS_PER_METER),
				(int)(aabb.y * MODEL.PIXELS_PER_METER),
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
}
