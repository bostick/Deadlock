package com.gutabi.deadlock.geom;


public abstract class SweepableShape implements Shape, Sweepable {
	
	public final Object parent;
	
	public SweepableShape(Object parent) {
		this.parent = parent;
	}
	
	public abstract AABB getAABB();
	
}
