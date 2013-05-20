package com.brentonbostick.capsloc.geom;

import com.brentonbostick.capsloc.world.Stroke;
import com.brentonbostick.capsloc.world.graph.Merger;
import com.brentonbostick.capsloc.world.graph.Road;
import com.brentonbostick.capsloc.world.graph.Vertex;

public enum SweepEventType {
	
	ENTERROADCAPSULE,
	EXITROADCAPSULE,
	ENTERROAD,
	EXITROAD,
	
	ENTERVERTEX,
	EXITVERTEX,
	
	ENTERMERGER,
	EXITMERGER,
	
	ENTERSTROKE,
	EXITSTROKE,
	
	ENTERBOARD,
	EXITBOARD,
	
	ENTERCAR,
	EXITCAR;
	
	public static SweepEventType enter(Object parent) {
		if (parent instanceof Vertex) {
			return SweepEventType.ENTERVERTEX;
		} else if (parent instanceof Road) {
			return SweepEventType.ENTERROADCAPSULE;
		} else if (parent instanceof Merger) {
			return SweepEventType.ENTERMERGER;
		} else if (parent instanceof Stroke) {
			return SweepEventType.ENTERSTROKE;
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
		} else if (parent instanceof Stroke) {
			return SweepEventType.EXITSTROKE;
		} else {
			assert false;
			return null;
		}
	}
	
}
