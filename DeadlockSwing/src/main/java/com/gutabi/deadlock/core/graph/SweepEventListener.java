package com.gutabi.deadlock.core.graph;

public interface SweepEventListener {
	
	void enter(SweepEvent e);
	
	void exit(SweepEvent e);
	
	void intersect(SweepEvent e);
	
}
