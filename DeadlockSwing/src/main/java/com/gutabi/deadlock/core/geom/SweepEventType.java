package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.Vertex;

public enum SweepEventType {
	
	ENTERROADCAPSULE,
	EXITROADCAPSULE,
	
	ENTERVERTEX,
	EXITVERTEX,
	
	ENTERMERGER,
	EXITMERGER;
	
	public static SweepEventType enter(Object parent) {
		if (parent instanceof Vertex) {
			return SweepEventType.ENTERVERTEX;
		} else if (parent instanceof Road) {
			return SweepEventType.ENTERROADCAPSULE;
		} else if (parent instanceof Merger) {
			return SweepEventType.ENTERMERGER;
		} else {
			assert false;
			return null;
		}
	}
	
	public static SweepEventType exit(Object parent) {
		if (parent instanceof Vertex) {
			return SweepEventType.EXITVERTEX;
		} else if (parent instanceof Road) {
			return SweepEventType.EXITROADCAPSULE;
		} else if (parent instanceof Merger) {
			return SweepEventType.EXITMERGER;
		} else {
			assert false;
			return null;
		}
	}
	
}
