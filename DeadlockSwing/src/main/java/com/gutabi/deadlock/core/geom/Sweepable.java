package com.gutabi.deadlock.core.geom;


public interface Sweepable {
	
	void sweepStart(Sweeper s);
	
	void sweep(Sweeper s, int index);
	
}
