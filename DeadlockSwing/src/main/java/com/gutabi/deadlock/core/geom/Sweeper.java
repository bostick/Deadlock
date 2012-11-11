package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.Point;

public interface Sweeper {
	
	Point get(int i);
	
	int size();
	
	double getRadius();
	
}
