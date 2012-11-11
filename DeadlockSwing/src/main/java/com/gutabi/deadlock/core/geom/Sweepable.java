package com.gutabi.deadlock.core.geom;

public interface Sweepable {
	
	void sweepStart(Sweeper s, SweepEventListener l);
	
//	void sweepEnd(Stroke s, SweepEventListener l);
	
	void sweep(Sweeper s, int index, SweepEventListener l);
	
}
