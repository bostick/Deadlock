package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class CubicCurve implements Shape {	
	
	public abstract List<Point> skeleton();
	
}
