package com.gutabi.capsloc.world.graph;

import com.gutabi.capsloc.math.Point;

public interface GPPAccumulator {
	
	void reset(Point p);
	
	void apply(GraphPositionPathPosition p0, GraphPositionPathPosition p1);
	
	GraphPositionPathPosition result();
	
}
