package com.gutabi.deadlock.geom;

import java.util.Comparator;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.graph.Vertex;

public abstract class SweepEvent {
	
	/*
	 * not final, used in changing ROADCAPSULE -> ROAD
	 */
	public SweepEventType type;
	public final Object stillParent;
	public final Shape still;
	
	public final SweeperShape moving;
	
	public final int index;
	public final double param;
	
	public final double combo;
	
	public final Point p;
	
	public Vertex v;
	public boolean inRoad;
	
	protected SweepEvent(SweepEventType type, Object stillParent, Shape still, CapsuleSequence moving, int index, double param, int offset) {
		this.type = type;
		this.stillParent = stillParent;
		this.still = still;
		this.moving = moving;
		this.index = index+offset;
		this.param = param;
		
		this.combo = index+offset+param;
		
		p = moving.getPoint(index, param);
		
	} 
	
	protected SweepEvent(SweepEventType type, Object stillParent, Shape still, MutableCapsuleSequence moving, int index, double param, int offset) {
		this.type = type;
		this.stillParent = stillParent;
		this.still = still;
		this.moving = moving;
		this.index = index+offset;
		this.param = param;
		
		this.combo = index+offset+param;
		
		p = moving.getPoint(index, param);
		
	}
	
	public String toString() {
		if (type != null) {
			return type + " " + stillParent;
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
					if (a.type == b.type) {
						return 0;
					} else {
						if (a.type == SweepEventType.ENTERVERTEX && b.type == SweepEventType.ENTERROADCAPSULE) {
							return -1;
						} else if (a.type == SweepEventType.ENTERROADCAPSULE && b.type == SweepEventType.ENTERVERTEX) {
							return 1;
						} else if (a.type == SweepEventType.ENTERVERTEX && b.type == SweepEventType.ENTERMERGER) {
							return -1;
						} else if (a.type == SweepEventType.ENTERMERGER && b.type == SweepEventType.ENTERROAD) {
							return 0;
						} else {
							/*
							 * this is where Vertices take precedence over capsules
							 * order vertices before capsules
							 */
							throw new IllegalStateException("finish implementing");
						}
					}
				} else if (a.param < b.param) {
					return -1;
				} else {
					return 1;
				}
			} else if (a.index < b.index) {
				return -1;
			} else {
				return 1;
			}
		}
		
	}
	
}
