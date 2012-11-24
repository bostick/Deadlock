package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.model.StopSign;

public class RoadPosition extends EdgePosition {
	
	public final Road r;
	public final int index;
	public final double param;
	public final double combo;
	
	public final boolean bound;
	
	public final double lengthToStartOfRoad;
	public final double lengthToEndOfRoad;
	
	public final StopSign sign;
	
	private int hash;
	
	public RoadPosition(Road r, int index, double param) {
		super(Point.point(r.getPoint(index), r.getPoint(index+1), param), r, Axis.NONE);
		
		if (index < 0 || index >= r.pointCount()-1) {
			throw new IllegalArgumentException();
		}
		if (DMath.lessThan(param, 0.0) || DMath.greaterThanEquals(param, 1.0)) {
			throw new IllegalArgumentException();
		}
		if (index == 0 && DMath.equals(param, 0.0) && !r.isStandAlone()) {
			throw new IllegalArgumentException();
		}
		
		this.r = r;
		this.index = index;
		this.param = param;
		
		this.combo = index + param;
		
		if (DMath.equals(param, 0.0)) {
			bound = true;
			
			if (index == 1) {
				sign = r.startSign;
			} else if (index == r.pointCount()-2) {
				sign = r.endSign;
			} else {
				sign = null;
			}
			
		} else {
			bound = false;
			sign = null;
		}
		
		Point segStart = r.getPoint(index);
		
		lengthToStartOfRoad = r.getLengthFromStart(index) + Point.distance(p, segStart);
		
		lengthToEndOfRoad = r.getTotalLength(r.start, r.end) - lengthToStartOfRoad;
		
		assert check();
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + r.hashCode();
			h = 37 * h + index;
			long l = Double.doubleToLongBits(param);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
	}
	
	public String toString() {
		return r + " " + index + Double.toString(param).substring(1) + " (" + lengthToStartOfRoad + "/" + r.getTotalLength(r.start, r.end) + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof RoadPosition)) {
			return false;
		} else {
			RoadPosition b = (RoadPosition)o;
			return (r == b.r) && (index == b.index) && DMath.equals(param, b.param);
		}
	}
	
	
	public Entity getEntity() {
		return r;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public double getCombo() {
		return combo;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof RoadPosition) {
			RoadPosition ge = (RoadPosition)goal;
			assert ge.r == r;
			assert !equals(ge);
			
			if (lengthToStartOfRoad < ge.lengthToStartOfRoad) {
				return nextBoundForward(r, index, param);
			} else {
				return nextBoundBackward(r, index, param);
			}
			
		} else {
			VertexPosition gv = (VertexPosition)goal;
			
			if (gv.v == r.end) {
				return nextBoundForward(r, index, param);
			} else {
				return nextBoundBackward(r, index, param);
			}
			
		}
		
	}
	
	public GraphPosition nextBoundBackward() {
		return nextBoundBackward(r, index, param);
	}
	
	public GraphPosition nextBoundForward() {
		return nextBoundForward(r, index, param);
	}
	
	public double distanceToConnectedVertex(Vertex v) {
		if (v == r.start) {
			return lengthToStartOfRoad;
		} else {
			return lengthToEndOfRoad;
		}
	}
	
	public GraphPosition travelToConnectedVertex(Vertex v, double dist) {
		
		if (v == r.start) {
			
			return travelBackward(r, index, param, dist);
			
		} else {
			return travelForward(r, index, param, dist);
		}
		
	}
	
	public double distanceToStartOfRoad() {
		return lengthToStartOfRoad;
	}
	
	protected double distanceForward(RoadPosition a) {
		return a.lengthToStartOfRoad - lengthToStartOfRoad;
	}
	
	protected double distanceBackward(RoadPosition a) {
		return lengthToStartOfRoad - a.lengthToStartOfRoad;
	}
	
	public static GraphPosition travelFromStart(Road e, double dist) {
		return travelForward(e, 0, 0.0, dist);
	}
	
	public static GraphPosition travelFromEnd(Road e, double dist) {
		return travelBackward(e, e.pointCount()-2, 1.0, dist);
	}
	
	private static GraphPosition travelForward(Road e, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.distance(c, b);
			
			if (DMath.equals(distanceToTravel, distanceToEndOfSegment)) {
				return new RoadPosition(e, index+1, 0.0);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new RoadPosition(e, index, newParam);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
	}
	
	private static GraphPosition travelBackward(Road e, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.distance(c, a);
			
			if (DMath.equals(distanceToTravel, distanceToStartOfSegment)) {
				return new RoadPosition(e, index, 0.0);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new RoadPosition(e, index, newParam);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
	public static GraphPosition nextBoundfromStart(Road e) {
		return nextBoundForward(e, 0, 0.0);
	}
	
	public static GraphPosition nextBoundfromEnd(Road e) {
		return nextBoundBackward(e, e.pointCount()-2, 1.0);
	}
	
	private static GraphPosition nextBoundForward(Road e, int index, double param) {
		if (index == e.pointCount()-2) {
			return new VertexPosition(e.end);
		} else {
			return new RoadPosition(e, index+1, 0.0);
		}
	}
	
	private static GraphPosition nextBoundBackward(Road e, int index, double param) {
		if (DMath.equals(param, 0.0)) {
			if (index == 0 || (index == 1 && DMath.equals(param, 0.0))) {
				return new VertexPosition(e.start);
			} else {
				return new RoadPosition(e, index-1, 0.0);
			}
		} else {
			if (index == 0) {
				return new VertexPosition(e.start);
			} else {
				return new RoadPosition(e, index, 0.0);
			}
		}
	}
	
	private boolean check() {
		
		double acc = 0;
		for (int i = 0; i < index; i++) {
			Point a = r.getPoint(i);
			Point b = r.getPoint(i+1);
			acc = acc + Point.distance(a, b);
		}
		acc = acc + Point.distance(r.getPoint(index), p);
		
		assert DMath.equals(lengthToStartOfRoad, acc);
		
		acc = 0;
		acc = acc + Point.distance(p, r.getPoint(index+1));
		for (int i = index+1; i < r.pointCount()-1; i++) {
			Point a = r.getPoint(i);
			Point b = r.getPoint(i+1);
			acc = acc + Point.distance(a, b);
		}
		
		assert DMath.equals(lengthToEndOfRoad, acc);
		
		return true;
	}
	
}
