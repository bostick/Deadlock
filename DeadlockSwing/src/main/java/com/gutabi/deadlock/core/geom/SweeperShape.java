package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.Point;

public interface SweeperShape {
	
	Point getPoint(double param);
	
	double getRadius();
	
}
