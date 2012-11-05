package com.gutabi.deadlock.core.graph;

import java.util.Comparator;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class SweepEvent {
	
	public enum SweepEventType {
		
		ENTERCAPSULE,
		EXITCAPSULE,
		ENTERVERTEX,
		EXITVERTEX,
		
//		CAPSULESTART,
//		VERTEXSTART,
//		NOTHINGSTART,
//		NOTHINGEND
		
	}
	
	public final SweepEventType type;
	public final Point p;
	public final int index;
	public final double param;
//	public final Object o;
	
//	private Vertex v;
	
	public SweepEvent(SweepEventType type, Point p, int index, double param) {
		this.type = type;
		this.p = p;
		this.index = index;
		this.param = param;
//		this.o = o;
	}
	
	public String toString() {
		if (type != null) {
			return type.toString();
		} else {
			return "null";
		}
	}
	
//	public void setVertex(Vertex v) {
//		this.v = v;
//	}
//	
//	public Vertex getVertex() {
//		return v;
//	}
	
	public static Comparator<SweepEvent> COMPARATOR = new SweepEventComparator();
	
	static class SweepEventComparator implements Comparator<SweepEvent> {

		public int compare(SweepEvent a, SweepEvent b) {
			if (a.index == b.index) {
				if (DMath.equals(a.param, b.param)) {
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
