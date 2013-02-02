package com.gutabi.deadlock.geom;

import java.util.List;

public interface Sweepable {
	
	List<SweepEvent> sweepStart(CapsuleSequence s);
	
	List<SweepEvent> sweep(CapsuleSequence s, int index);
	
}
