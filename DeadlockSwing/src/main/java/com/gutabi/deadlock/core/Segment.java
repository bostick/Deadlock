package com.gutabi.deadlock.core;


public class Segment {
	
	public final Edge edge;
	public final int index;
	public final Point start;
	public final Point end;
	
	public Segment(Edge e, int index) {
		this.edge = e;
		this.index = index;
		this.start = e.getPoint(index);
		this.end = e.getPoint(index+1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Segment)) {
			return false;
		} else {
			Segment b = (Segment)o;
			return edge == b.edge && index == b.index;
		}
	}
	
}
