package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;

public class RoadSegment extends Capsule {
	
	public final Road r;
	
	public RoadSegment(Road r, Point a, Point b) {
		super(a, b, Road.ROAD_RADIUS);
		this.r = r;
	}
	
}
