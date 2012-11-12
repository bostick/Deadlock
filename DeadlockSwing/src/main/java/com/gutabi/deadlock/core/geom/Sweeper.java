package com.gutabi.deadlock.core.geom;

import java.util.List;

public abstract class Sweeper extends CapsuleSequence {
	
	public Sweeper(Object parent, List<Capsule> caps) {
		super(parent, caps);
	}
	
	/*
	 * callbacks
	 */
	public abstract void start(SweepEvent s);
	
	public abstract void event(SweepEvent s);
	
}
