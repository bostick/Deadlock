package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.Point;

public class CapsuleSequencePosition {
	
	public final CapsuleSequence seq;
	public final int index;
	public final double param;
	
	public final Point p;
	
	public CapsuleSequencePosition(CapsuleSequence seq, int index, double param) {
		
		this.seq = seq;
		this.index = index;
		this.param = param;
		
		Capsule cap = seq.getCapsule(index);
		
		this.p = Point.point(cap.a, cap.b, param);
		
	}
	
	
}
