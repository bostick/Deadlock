package com.gutabi.deadlock.core.geom;

public abstract class SweepableShape extends Shape implements Sweepable {
	
	public final Object parent;
	
	public SweepableShape(Object parent) {
		this.parent = parent;
	}
	
}
