package com.brentonbostick.capsloc.world.graph.gpp;

import com.brentonbostick.capsloc.math.Point;

public class MutableGPPAccumulator {
	
	MutableGPPP par;
	
	Point p;
	
	int closestIndex;
	double closestParam;
	double closestDistance;
	
	public MutableGPPAccumulator() {
		
	}
	
	public void reset(MutableGPPP par, Point p) {
		this.par = par;
		this.p = p;
		closestIndex = par.index;
		closestParam = par.param;
		closestDistance = Point.distance(p, par.p);
	}
	
	
	final MutableGPPP a = new MutableGPPP();
	final MutableGPPP b = new MutableGPPP();
	
	public void apply(MutableGPPP p0, MutableGPPP p1) {
		apply(p0.path, p0.index, p0.param, p1.index, p1.param);
	}
	
	public void apply(GraphPositionPath path, int p0Index, double p0Param, int p1Index, double p1Param) {
		
		assert p0Index+p0Param < p1Index+p1Param;
		
		a.set(path, p0Index, p0Param);
		a.floor();
		b.set(path, p1Index, p1Param);
		b.ceil();
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
	
}
