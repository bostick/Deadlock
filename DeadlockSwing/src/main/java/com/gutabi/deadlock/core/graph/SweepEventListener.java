package com.gutabi.deadlock.core.graph;

public interface SweepEventListener {
	
	void start(SweepEvent e);
	
	void end(SweepEvent e);
	
	void event(SweepEvent e);
	
}
