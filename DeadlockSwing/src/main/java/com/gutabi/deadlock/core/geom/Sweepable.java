package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.model.Stroke;

public interface Sweepable {
	
	void sweepStart(Stroke s, SweepEventListener l);
	
//	void sweepEnd(Stroke s, SweepEventListener l);
	
	void sweep(Stroke s, int index, SweepEventListener l);
	
}
