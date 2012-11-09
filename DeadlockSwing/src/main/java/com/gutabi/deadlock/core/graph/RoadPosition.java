package com.gutabi.deadlock.core.graph;

import java.util.ArrayList;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public class RoadPosition extends EdgePosition {
	
	public final Road r;
	public final int index;
	public final double param;
	
	public final boolean bound;
	
	public final double lengthToStartOfRoad;
	public final double lengthToEndOfRoad;
	
	public final StopSign sign;
	
	private final int hash;
	
	public RoadPosition(Road r, int index, double param) {
		super(Point.point(r.get(index), r.get(index+1), param));
		
		if (index < 0 || index >= r.size()-1) {
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
		
		int h = 17;
		h = 37 * h + r.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		vs = new ArrayList<Vertex>();
		vs.add(r.start);
		vs.add(r.end);
		
		if (DMath.equals(param, 0.0)) {
			bound = true;
			
			if (index == 1) {
				sign = r.startSign;
			} else if (index == r.size()-2) {
				sign = r.endSign;
			} else {
				sign = null;
			}
			
		} else {
			bound = false;
			sign = null;
		}
		
		Point segStart = r.get(index);
		
		lengthToStartOfRoad = r.getLengthFromStart(index) + Point.distance(p, segStart);
		
		lengthToEndOfRoad = r.getTotalLength() - lengthToStartOfRoad;
		
		assert check();
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return r + " " + index + " " + param + "(" + lengthToStartOfRoad + "/" + r.getTotalLength() + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPosition)) {
			throw new IllegalArgumentException();
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
	
	public GraphPosition floor() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != 0) {
			return new RoadPosition(r, index, 0.0);
		} else {
			return new VertexPosition(r.start);
		}
	}
	
	public GraphPosition ceiling() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != r.size()-2) {
			return new RoadPosition(r, index+1, 0.0);
		} else {
			return new VertexPosition(r.end);
		}
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof RoadPosition) {
			RoadPosition ge = (RoadPosition)goal;
			assert ge.r == r;
			
			if (lengthToStartOfRoad < ge.lengthToStartOfRoad) {
				return nextBoundForward(r, index, param);
			} else {
				return nextBoundBackward(r, index, param);
			}
			
		} else if (goal instanceof VertexPosition) {
			VertexPosition gv = (VertexPosition)goal;
			
			if (gv.v == r.end) {
				return nextBoundForward(r, index, param);
			} else {
				return nextBoundBackward(r, index, param);
			}
			
		} else {
			throw new AssertionError();
		}
		
	}
	
	public GraphPosition nextBoundBackward() {
		return nextBoundBackward(r, index, param);
	}
	
	public GraphPosition nextBoundForward() {
		return nextBoundForward(r, index, param);
	}
	
	public double distanceToConnectedVertex(Vertex v) {
		assert vs.contains(v);
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
	
//	public double distanceTo(GraphPosition b) {
//		if (b instanceof VertexPosition) {
//			VertexPosition bb = (VertexPosition)b;
//			
//			if (bb.v == r.start) {
//				return distanceToStartOfRoad();
//			} else if (bb.v == r.end) {
//				return distanceToEndOfRoad();
//			}
//			
//			double aaStartPath = MODEL.world.distanceBetweenVertices(r.start, bb.v);
//			double aaEndPath = MODEL.world.distanceBetweenVertices(r.end, bb.v);
//			
//			double dist = Math.min(aaStartPath + distanceToStartOfRoad(), aaEndPath + distanceToEndOfRoad());
//			
//			assert DMath.greaterThanEquals(dist, 0.0);
//			
//			return dist;
//		} else {
//			RoadPosition aa = (RoadPosition)this;
//			RoadPosition bb = (RoadPosition)b;
//			
//			if (aa.r == bb.r) {
//				return Math.abs(aa.distanceToStartOfRoad() - bb.distanceToStartOfRoad());
//			}
//			
//			double startStartPath = MODEL.world.distanceBetweenVertices(aa.r.start, bb.r.start);
//			double startEndPath = MODEL.world.distanceBetweenVertices(aa.r.start, bb.r.end);
//			double endStartPath = MODEL.world.distanceBetweenVertices(aa.r.end, bb.r.start);
//			double endEndPath = MODEL.world.distanceBetweenVertices(aa.r.end, bb.r.end);
//			
//			double startStartDistance = startStartPath + aa.distanceToStartOfRoad() + bb.distanceToStartOfRoad();
//			double startEndDistance = startEndPath + aa.distanceToStartOfRoad() + bb.distanceToEndOfRoad();
//			double endStartDistance = endStartPath + aa.distanceToEndOfRoad() + bb.distanceToStartOfRoad();
//			double endEndDistance = endEndPath + aa.distanceToEndOfRoad() + bb.distanceToEndOfRoad();
//			
//			double dist = Math.min(Math.min(startStartDistance, startEndDistance), Math.min(endStartDistance, endEndDistance));
//			
//			assert DMath.greaterThanEquals(dist, 0.0);
//			
//			return dist;
//		}
//	}
	
	
	
	
	public double distanceToEndOfRoad() {
		return lengthToEndOfRoad;
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
	
	/**
	 * the specific way to travel
	 */
//	public GraphPosition travel(VertexPosition dest, double dist) {
//		if (r.isLoop()) {
//			throw new IllegalArgumentException();
//		}
//		if (!(dest.v == r.start || dest.v == r.end)) {
//			throw new IllegalArgumentException();
//		}
//		if (DMath.equals(dist, 0.0)) {
//			return this;
//		}
//		if (dist < 0.0) {
//			throw new IllegalArgumentException();
//		}
//		
//		if (dest.v == r.end) {
//			
////			double distToEndOfEdge = distanceToEndOfRoad();
//			if (DMath.equals(dist, lengthToEndOfRoad)) {
//				return new VertexPosition(r.end);
//			} else if (dist > lengthToEndOfRoad) {
//				throw new IllegalArgumentException();
//			}
//			
//			return travelForward(r, index, param, dist);
//			
//		} else {
//			
////			double distToStartOfEdge = distanceToStartOfRoad();
//			if (DMath.equals(dist, lengthToStartOfRoad)) {
//				return new VertexPosition(r.start);
//			} else if (dist > lengthToStartOfRoad) {
//				throw new IllegalArgumentException();
//			}
//			
//			return travelBackward(r, index, param, dist);
//			
//		}
//		
//	}
	
	public static GraphPosition travelFromStart(Road e, double dist) {
		return travelForward(e, 0, 0.0, dist);
	}
	
	public static GraphPosition travelFromEnd(Road e, double dist) {
		return travelBackward(e, e.size()-2, 1.0, dist);
	}
	
	private static GraphPosition travelForward(Road e, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.get(index);
			Point b = e.get(index+1);
			
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
			Point a = e.get(index);
			Point b = e.get(index+1);
			
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
		return nextBoundBackward(e, e.size()-2, 1.0);
	}
	
	private static GraphPosition nextBoundForward(Road e, int index, double param) {
		if (index == e.size()-2) {
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
			Point a = r.get(i);
			Point b = r.get(i+1);
			acc = acc + Point.distance(a, b);
		}
		acc = acc + Point.distance(r.get(index), p);
		
		assert DMath.equals(lengthToStartOfRoad, acc);
		
		acc = 0;
		acc = acc + Point.distance(p, r.get(index+1));
		for (int i = index+1; i < r.size()-1; i++) {
			Point a = r.get(i);
			Point b = r.get(i+1);
			acc = acc + Point.distance(a, b);
		}
		
		assert DMath.equals(lengthToEndOfRoad, acc);
		
		return true;
	}
	
}