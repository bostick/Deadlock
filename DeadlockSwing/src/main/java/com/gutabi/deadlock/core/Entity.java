package com.gutabi.deadlock.core;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.geom.Shape;

//@SuppressWarnings("static-access")
public abstract class Entity {
	
	public abstract Shape getShape();
	
	public abstract Entity hitTest(Point p);
	
//	public Shape shape;
//	
//	public final Entity hitTest(Point p) {
//		if (shape.hitTest(p)) {
//			return this;
//		} else {
//			return null;
//		}
//	}
	
//	public final Entity intersect(Shape s) {
//		if (ShapeUtils.intersect(shape, s)) {
//			return this;
//		}
//		return null;
//	}
	
//	public final void sweepStart(Sweeper s) {
//		shape.sweepStart(s);
//	}
//	
//	public final void sweep(Sweeper s, int index) {
//		shape.sweep(s, index);
//	}
//	
//	public final AABB getAABB() {
//		return shape.aabb;
//	}
	
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
	
}
