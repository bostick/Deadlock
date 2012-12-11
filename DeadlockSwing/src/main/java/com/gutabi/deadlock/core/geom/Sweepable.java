package com.gutabi.deadlock.core.geom;

import java.util.List;


public interface Sweepable {
	
	List<SweepEvent> sweepStart(Circle c);
	
	List<SweepEvent> sweep(Capsule cap);
	
}
