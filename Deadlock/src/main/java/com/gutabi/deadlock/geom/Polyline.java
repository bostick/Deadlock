package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public abstract class Polyline implements Shape {
	
	public List<Point> pts;
	
	public Polyline(List<Point> pts) {
		this.pts = pts;
	}
	
}
