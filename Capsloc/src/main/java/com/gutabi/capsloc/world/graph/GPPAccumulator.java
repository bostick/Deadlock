package com.gutabi.capsloc.world.graph;

import com.gutabi.capsloc.math.Point;

public class GPPAccumulator {
	
	GraphPositionPathPosition par;
	
	Point p;
	
	int closestIndex;
	double closestParam;
	double closestDistance;
	
	public GPPAccumulator(GraphPositionPathPosition par) {
		this.par = par;
	}
	
	public void reset(Point p) {
		this.p = p;
		closestIndex = par.index;
		closestParam = par.param;
		closestDistance = Point.distance(p, par.p);
	}
	
	public void apply(GraphPositionPathPosition p0, GraphPositionPathPosition p1) {
		
		assert p0.combo < p1.combo;
		
		GraphPositionPathPosition a = p0.floor();
		GraphPositionPathPosition b = p1.ceil();
		Point aa = a.p;
		Point bb = b.p;
		
		double u = Point.u(aa, p, bb);
		if (u < 0.0) {
			u = 0.0;
		} else if (u > 1.0) {
			u = 1.0;
		}
		
		Point pOnPath = Point.point(aa, bb, u);
			
		double dist = Point.distance(p, pOnPath);
		if (dist < closestDistance) {
			closestIndex = a.index;
			closestParam = u;
			
			closestDistance = dist;
			
		}
		
	}
	
	public GraphPositionPathPosition result() {
		return new GraphPositionPathPosition(par.path, closestIndex, closestParam);
	}
	
}
