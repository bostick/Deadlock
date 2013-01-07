package com.gutabi.deadlock.math.geom;

public abstract class SweepableShape implements Sweepable, Shape {
	
	public final Object parent;
	
	public SweepableShape(Object parent) {
		this.parent = parent;
	}
	
	public abstract AABB getAABB();
	
}
