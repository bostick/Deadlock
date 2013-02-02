package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public interface SweeperShape {
	
	Point getPoint(int index, double param);
	
	double getRadius();
	
}
