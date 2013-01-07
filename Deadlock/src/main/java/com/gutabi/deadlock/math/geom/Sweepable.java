package com.gutabi.deadlock.math.geom;

import java.util.List;


public interface Sweepable {
	
	List<SweepEvent> sweepStart(Circle c);
	
	List<SweepEvent> sweep(Capsule cap);
	
}
