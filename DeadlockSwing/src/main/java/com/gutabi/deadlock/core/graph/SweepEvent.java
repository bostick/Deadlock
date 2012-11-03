package com.gutabi.deadlock.core.graph;

import java.util.Comparator;

import com.gutabi.deadlock.core.DMath;

public class SweepEvent {
	
	enum SweepEventType {
		ENTERSIDE,
		EXITSIDE,
		ENTERCAP,
		EXITCAP,
		INTERSECTIONCAPSULE,
		ENTERVERTEX,
		EXITVERTEX,
		INTERSECTIONVERTEX
	}
	
	public final SweepEventType type;
	public final int index;
	public final double param;
	public final Object o;
	
	public SweepEvent(SweepEventType type, int index, double param, Object o) {
		this.type = type;
		this.index = index;
		this.param = param;
		this.o = o;
	}
	
	public static Comparator<SweepEvent> COMPARATOR = new SweepEventComparator();
	
	static class SweepEventComparator implements Comparator<SweepEvent> {

		public int compare(SweepEvent a, SweepEvent b) {
			if (a.index == b.index) {
				if (DMath.equals(a.param, b.param)) {
					assert a.o == b.o;
					assert a.type == b.type;
					return 0;
				}
				if (a.param < b.param) {
					return -1;
				} else {
					return 1;
				}
			}
			if (a.index < b.index) {
				return -1;
			} else {
				return 1;
			}
		}
		
	}
	
}
