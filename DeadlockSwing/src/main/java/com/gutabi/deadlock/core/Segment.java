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
}
