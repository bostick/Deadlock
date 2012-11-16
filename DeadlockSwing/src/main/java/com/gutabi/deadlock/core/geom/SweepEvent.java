package com.gutabi.deadlock.core.geom;

import java.util.Comparator;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Vertex;

public class SweepEvent {
	
	public enum SweepEventType {
		
		ENTERCAPSULE,
		EXITCAPSULE,
		
		ENTERCIRCLE,
		EXITCIRCLE,
		
		ENTERQUAD,
		EXITQUAD
		
	}
	
	public final SweepEventType type;
	public final Shape shape;
	public final Sweeper sweeper;
	public final int index;
	public final double param;
	public final Point p;
	public final Circle circle;
	
	private Vertex v;
	
	public SweepEvent(SweepEventType type, Shape shape, Sweeper sweeper, int index, double param) {
		this.type = type;
		this.shape = shape;
		this.sweeper = sweeper;
		this.index = index;
		this.param = param;
		
		p = sweeper.getPoint(index, param);
		circle = new Circle(sweeper, p, sweeper.radius);
		
		if (shape != null) {
			/*
			 * TODO: be more specific and test if bordering
			 */
			assert shape.intersect(circle);
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
						if (a.type == SweepEventType.ENTERCIRCLE && b.type == SweepEventType.ENTERCAPSULE) {
							return -1;
						} else if (a.type == SweepEventType.ENTERCIRCLE && b.type == SweepEventType.ENTERQUAD) {
							return -1;
						} else if (a.type == SweepEventType.ENTERQUAD && b.type == SweepEventType.ENTERCAPSULE) {
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