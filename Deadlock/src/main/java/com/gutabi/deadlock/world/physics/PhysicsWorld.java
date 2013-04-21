package com.gutabi.deadlock.world.physics;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.MutableAABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class PhysicsWorld {
	
	/**
	 * move physics forward by dt seconds
	 */
	public double DT = 0.0166666;
	
	public double t;
	
	public PhysicsWorld() {
		
	}
	
	public void step() {
		
	}
	
	public int getBodyCount() {
		assert false;
		return 0;
	}
	
	public abstract void carCrash(Point p);
	
	public void drawPhysicsDebug(RenderingContext ctxt) {
		
	}
	
	public boolean intersectsPhysicsBodies(AABB aabb) {
		assert false;
		return false;
	}
	
	public boolean intersectsPhysicsBodies(MutableAABB aabb) {
		assert false;
		return false;
	}
	
}
