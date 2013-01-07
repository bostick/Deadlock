package com.gutabi.deadlock.math.geom;

import java.util.Comparator;


import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.graph.Vertex;

public class SweepEvent {
	
	public final SweepEventType type;
	public final SweepableShape still;
	
	public final SweeperShape moving;
	
	public final int index;
	public final double param;
	
	public final double combo;
	
	public final Point p;
	public final Circle circle;
	
	private Vertex v;
	
	public SweepEvent(SweepEventType type, SweepableShape still, SweeperShape moving, int index, double param) {
		this.type = type;
		this.still = still;
		this.moving = moving;
		this.index = index;
		this.param = param;
		
		this.combo = index+param;
		
		p = moving.getPoint(param);
		circle = new Circle(null, p, moving.getRadius());
		
		if (still != null) {
			/*
			 * TODO: be more specific and test if bordering
			 */
			assert ShapeUtils.intersect(still, circle);
		}
		
	}
	
	public String toString() {
		if (type != null) {
			return type.toString();
		} else {
			return "null";
		}
	}
	
	public void setVertex(Vertex v) {
		this.v = v;
	}
	
	public Vertex getVertex() {
		return v;
	}
	
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
						} else if (a.type == SweepEventType.ENTERVERTEX && b.type == SweepEventType.ENTERMERGER) {
							return -1;
						} else if (a.type == SweepEventType.ENTERMERGER && b.type == SweepEventType.ENTERROADCAPSULE) {
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
