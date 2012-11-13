package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.geom.Rect;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.Sweepable;
import com.gutabi.deadlock.core.geom.Sweeper;

@SuppressWarnings("static-access")
public abstract class Entity implements Sweepable {
	
	public Shape shape;
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
//	public final Entity bestHitTest(Point c, double r) {
//		if (shape.bestHitTest(c, r)) {
//			return this;
//		} else {
//			return null;
//		}
//	}
	
	public final Entity bestHitTest(Shape s) {
		if (shape.intersect(s)) {
			return this;
		} else {
			return null;
		}
	}
	
//	public final boolean containedIn(Entity e) {
//		return shape.containedIn(e.shape);
//	}
	
	public final void sweepStart(Sweeper s) {
		shape.sweepStart(s);
	}
	
	public final void sweep(Sweeper s, int index) {
		shape.sweep(s, index);
	}
	
	public final Rect getAABB() {
		return shape.aabb;
	}
	
	public abstract boolean isDeleteable();
	
	public abstract void preStart();
	
	public abstract void postStop();
	
	public abstract void preStep(double t);
	
	/**
	 * return true if car should persist after time step
	 */
	public abstract boolean postStep(double t);
	
	public abstract void paint(Graphics2D g2);
	
	public abstract void paintHilite(Graphics2D g2);
	
	public final void paintAABB(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);
		g2.drawRect(
				(int)(shape.aabb.x * MODEL.PIXELS_PER_METER),
				(int)(shape.aabb.y * MODEL.PIXELS_PER_METER),
				(int)(shape.aabb.width * MODEL.PIXELS_PER_METER),
				(int)(shape.aabb.height * MODEL.PIXELS_PER_METER));
		
	}
	
}
