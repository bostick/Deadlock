package com.brentonbostick.capsloc.world.graph.gpp;

import com.brentonbostick.capsloc.math.Point;

public class MutableGPPAccumulator {
	
	MutableGPPP par;
	
	Point p;
	
	public final MutableGPPP closest = new MutableGPPP();
	double closestDistance;
	
	public MutableGPPAccumulator(MutableGPPP par) {
		this.par = par;
	}
	
	public void reset(Point p) {
		this.p = p;
		closest.set(par.path, par.index,  par.param);
		closestDistance = Point.distance(p, par.p);
	}
	
	
	final MutableGPPP a = new MutableGPPP();
	final MutableGPPP b = new MutableGPPP();
	
	public void apply(MutableGPPP p0, MutableGPPP p1) {
		
		assert p0.combo < p1.combo;
		
		a.set(p0);
		a.floor();
		b.set(p1);
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
			closest.set(closest.path, a.index, u);
			closestDistance = dist;
			
		}
		
	}
	
}
