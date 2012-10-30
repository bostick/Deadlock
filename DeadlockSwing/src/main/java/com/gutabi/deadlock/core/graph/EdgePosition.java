package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public class EdgePosition extends GraphPosition {
	
	public final Edge e;
	public final int index;
	public final double param;
	
	public final boolean bound;
	
	public final double lengthToStartOfEdge;
	public final double lengthToEndOfEdge;
	
	public final List<StopSign> events;
	
	public final int hash;
	
	public EdgePosition(Edge e, int index, double param) {
		super(Point.point(e.get(index), e.get(index+1), param));
		
		if (index < 0 || index >= e.size()-1) {
			throw new IllegalArgumentException();
		}
		if (DMath.lessThan(param, 0.0) || DMath.greaterThanEquals(param, 1.0)) {
			throw new IllegalArgumentException();
		}
		if (index == 0 && DMath.equals(param, 0.0) && !e.isStandAlone()) {
			throw new IllegalArgumentException();
		}
		
		this.e = e;
		this.index = index;
		this.param = param;
		
		events = new ArrayList<StopSign>();
		if (DMath.equals(param, 0.0)) {
			bound = true;
			
			if (index == 1) {
				if (e.startSign != null) {
					assert p.equals(e.startSign.p);
					events.add(e.startSign);
				}
			} else if (index == e.size()-2) {
				if (e.endSign != null) {
					assert p.equals(e.endSign.p);
					events.add(e.endSign);
				}
			}
			
		} else {
			bound = false;
		}
		
		Point segStart = e.get(index);
		
		lengthToStartOfEdge = e.getLengthFromStart(index) + Point.distance(p, segStart);
		
		lengthToEndOfEdge = e.getTotalLength() - lengthToStartOfEdge;
		
		int h = 17;
		h = 37 * h + e.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		assert check();
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return e + " " + index + " " + param + "(" + lengthToStartOfEdge + "/" + e.getTotalLength() + ")";
	}
	
	public List<StopSign> getEvents() {
		return events;
	}
	
	public boolean equalsP(GraphPosition o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof EdgePosition)) {
			return false;
		} else {
			EdgePosition b = (EdgePosition)o;
			return (e == b.e) && (index == b.index) && DMath.equals(param, b.param);
		}
	}
	
	
	public Entity getEntity() {
		return e;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public GraphPosition floor() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != 0) {
			return new EdgePosition(e, index, 0.0);
		} else {
			return new VertexPosition(e.start);
		}
	}
	
	public GraphPosition ceiling() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != e.size()-2) {
			return new EdgePosition(e, index+1, 0.0);
		} else {
			return new VertexPosition(e.end);
		}
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			assert ge.e == e;
			
			if (lengthToStartOfEdge < ge.lengthToStartOfEdge) {
				return nextBoundForward(e, index, param);
			} else {
				return nextBoundBackward(e, index, param);
			}
			
		} else if (goal instanceof VertexPosition) {
			VertexPosition gv = (VertexPosition)goal;
			
			if (gv.v == e.end) {
				return nextBoundForward(e, index, param);
			} else {
				return nextBoundBackward(e, index, param);
			}
			
		} else {
			throw new AssertionError();
		}
		
	}
	
	public GraphPosition nextBoundBackward() {
		return nextBoundBackward(e, index, param);
	}
	
	public GraphPosition nextBoundForward() {
		return nextBoundForward(e, index, param);
	}
	
	public double distanceTo(GraphPosition b) {
		if (b instanceof VertexPosition) {
			VertexPosition bb = (VertexPosition)b;
			
			if (bb.v == e.start) {
				return distanceToStartOfEdge();
			} else if (bb.v == e.end) {
				return distanceToEndOfEdge();
			}
			
			double aaStartPath = MODEL.world.distanceBetweenVertices(e.start, bb.v);
			double aaEndPath = MODEL.world.distanceBetweenVertices(e.end, bb.v);
			
			double dist = Math.min(aaStartPath + distanceToStartOfEdge(), aaEndPath + distanceToEndOfEdge());
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		} else {
			EdgePosition aa = (EdgePosition)this;
			EdgePosition bb = (EdgePosition)b;
			
			if (aa.e == bb.e) {
				return Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
			}
			
			double startStartPath = MODEL.world.distanceBetweenVertices(aa.e.start, bb.e.start);
			double startEndPath = MODEL.world.distanceBetweenVertices(aa.e.start, bb.e.end);
			double endStartPath = MODEL.world.distanceBetweenVertices(aa.e.end, bb.e.start);
			double endEndPath = MODEL.world.distanceBetweenVertices(aa.e.end, bb.e.end);
			
			double startStartDistance = startStartPath + aa.distanceToStartOfEdge() + bb.distanceToStartOfEdge();
			double startEndDistance = startEndPath + aa.distanceToStartOfEdge() + bb.distanceToEndOfEdge();
			double endStartDistance = endStartPath + aa.distanceToEndOfEdge() + bb.distanceToStartOfEdge();
			double endEndDistance = endEndPath + aa.distanceToEndOfEdge() + bb.distanceToEndOfEdge();
			
			double dist = Math.min(Math.min(startStartDistance, startEndDistance), Math.min(endStartDistance, endEndDistance));
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		}
	}
	
	
	
	
	public double distanceToEndOfEdge() {
		return lengthToEndOfEdge;
	}
	
	public double distanceToStartOfEdge() {
		return lengthToStartOfEdge;
	}
	
	protected double distanceForward(EdgePosition a) {
		return a.lengthToStartOfEdge - lengthToStartOfEdge;
	}
	
	protected double distanceBackward(EdgePosition a) {
		return lengthToStartOfEdge - a.lengthToStartOfEdge;
	}
	
	/**
	 * the specific way to travel
	 */
	public GraphPosition travel(VertexPosition dest, double dist) {
		if (e.isLoop()) {
			throw new IllegalArgumentException();
		}
		if (!(dest.v == e.start || dest.v == e.end)) {
			throw new IllegalArgumentException();
		}
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		if (dest.v == e.end) {
			
			double distToEndOfEdge = distanceToEndOfEdge();
			if (DMath.equals(dist, distToEndOfEdge)) {
				return new VertexPosition(e.end);
			} else if (dist > distToEndOfEdge) {
				throw new IllegalArgumentException();
			}
			
			return travelForward(e, index, param, dist);
			
		} else {
			
			double distToStartOfEdge = distanceToStartOfEdge();
			if (DMath.equals(dist, distToStartOfEdge)) {
				return new VertexPosition(e.start);
			} else if (dist > distToStartOfEdge) {
				throw new IllegalArgumentException();
			}
			
			return travelBackward(e, index, param, dist);
			
		}
		
	}
	
	public static GraphPosition travelFromStart(Edge e, double dist) {
		return travelForward(e, 0, 0.0, dist);
	}
	
	public static GraphPosition travelFromEnd(Edge e, double dist) {
		return travelBackward(e, e.size()-2, 1.0, dist);
	}
	
	private static GraphPosition travelForward(Edge e, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.get(index);
			Point b = e.get(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.distance(c, b);
			
			if (DMath.equals(distanceToTravel, distanceToEndOfSegment)) {
				return new EdgePosition(e, index+1, 0.0);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
	}
	
	private static GraphPosition travelBackward(Edge e, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.get(index);
			Point b = e.get(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.distance(c, a);
			
			if (DMath.equals(distanceToTravel, distanceToStartOfSegment)) {
				return new EdgePosition(e, index, 0.0);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
	public static GraphPosition nextBoundfromStart(Edge e) {
		return nextBoundForward(e, 0, 0.0);
	}
	
	public static GraphPosition nextBoundfromEnd(Edge e) {
		return nextBoundBackward(e, e.size()-2, 1.0);
	}
	
	private static GraphPosition nextBoundForward(Edge e, int index, double param) {
		if (index == e.size()-2) {
			return new VertexPosition(e.end);
		} else {
			return new EdgePosition(e, index+1, 0.0);
		}
	}
	
	private static GraphPosition nextBoundBackward(Edge e, int index, double param) {
		if (DMath.equals(param, 0.0)) {
			if (index == 0 || (index == 1 && DMath.equals(param, 0.0))) {
				return new VertexPosition(e.start);
			} else {
				return new EdgePosition(e, index-1, 0.0);
			}
		} else {
			if (index == 0) {
				return new VertexPosition(e.start);
			} else {
				return new EdgePosition(e, index, 0.0);
			}
		}
	}
	
	private boolean check() {
		
		double acc = 0;
		for (int i = 0; i < index; i++) {
			Point a = e.get(i);
			Point b = e.get(i+1);
			acc = acc + Point.distance(a, b);
		}
		acc = acc + Point.distance(e.get(index), p);
		
		assert DMath.equals(lengthToStartOfEdge, acc);
		
		acc = 0;
		acc = acc + Point.distance(p, e.get(index+1));
		for (int i = index+1; i < e.size()-1; i++) {
			Point a = e.get(i);
			Point b = e.get(i+1);
			acc = acc + Point.distance(a, b);
		}
		
		assert DMath.equals(lengthToEndOfEdge, acc);
		
		return true;
	}
	
}
