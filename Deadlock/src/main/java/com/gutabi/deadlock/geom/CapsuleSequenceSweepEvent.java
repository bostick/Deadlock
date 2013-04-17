package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.DMath;

public class CapsuleSequenceSweepEvent extends SweepEvent {
	
	public final Circle circle;
	
	public CapsuleSequenceSweepEvent(SweepEventType type, Object stillParent, Shape still, CapsuleSequence moving, int index, double param, int offset) {
		super(type, stillParent, still, moving, index, param, offset);
		
		circle = new Circle(p, moving.getRadius());
		
		if (still != null && !(index == 0 && DMath.equals(param, 0.0))) {
			/*
			 * if regular sweep (not sweepStart), then assert touching
			 */
			assert ShapeUtils.touch(still, circle);
		}
		
	}
	
	public CapsuleSequenceSweepEvent(SweepEventType type, Object stillParent, Shape still, MutableCapsuleSequence moving, int index, double param, int offset) {
		super(type, stillParent, still, moving, index, param, offset);
		
		circle = new Circle(p, moving.getRadius());
		
		if (still != null && !(index == 0 && DMath.equals(param, 0.0))) {
			/*
			 * if regular sweep (not sweepStart), then assert touching
			 */
			assert ShapeUtils.touch(still, circle);
		}
		
	}
	
}
