package com.gutabi.capsloc.world.physics;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.MutableAABB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class PhysicsWorld {
	
	protected double t;
	
	public PhysicsWorld() {
		
	}
	
	public boolean step() {
		return false;
	}
	
	public double getTime() {
		return t;
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
