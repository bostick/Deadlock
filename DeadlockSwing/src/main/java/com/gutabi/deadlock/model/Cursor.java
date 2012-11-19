package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.geom.Shape;

@SuppressWarnings("static-access")
public abstract class Cursor {
	
	protected Point p;
	
	protected Rect aabb;
	
	public abstract void setPoint(Point p);
	
	public final Point getPoint() {
		return p;
	}
	
	public final Rect getAABB() {
		return aabb;
	}
	
	public abstract boolean intersect(Shape s);
	
	public abstract void paint(Graphics2D g2);
	
	protected final void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(aabb.x * MODEL.PIXELS_PER_METER),
				(int)(aabb.y * MODEL.PIXELS_PER_METER),
				(int)(aabb.width * MODEL.PIXELS_PER_METER),
				(int)(aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
}
