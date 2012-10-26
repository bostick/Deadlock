package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

public class EdgePosition extends GraphPosition {
	
	public final Edge e;
	public final int index;
	public final double param;
	
	public final boolean bound;
	
	public final double lengthToStartOfEdge;
	public final double lengthToEndOfEdge;
	
	public final int hash;
	
	public EdgePosition(Edge e, int index, double param) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param));
		
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
		
		this.bound = DMath.equals(param, 0.0);
		
		Point segStart = e.getPoint(index);
		
		lengthToStartOfEdge = e.getLengthFromStart(index) + Point.distance(p, segStart);
		
		lengthToEndOfEdge = e.getTotalLength() - lengthToStartOfEdge;
		
		int h = 17;
		h = 37 * h + e.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		check();
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return e + " " + index + " " + param + "(" + lengthToStartOfEdge + "/" + e.getTotalLength() + ")";
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
	
	
	
	
	public Point getPoint() {
		return p;
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public Entity getEntity() {
		return e;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public GraphPosition floor() {
		if (index != 0) {
			return new EdgePosition(e, index, 0.0);
		} else {
			return new VertexPosition(e.getStart());
		}
	}
	
	public GraphPosition ceiling() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != e.size()-2) {
			return new EdgePosition(e, index+1, 0.0);
		} else {
			return new VertexPosition(e.getEnd());
		}
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			assert ge.getEdge() == e;
			
			if (lengthToStartOfEdge < ge.lengthToStartOfEdge) {
				return nextBoundForward(e, index, param);
			} else {
				return nextBoundBackward(e, index, param);
			}
			
		} else if (goal instanceof VertexPosition) {
			VertexPosition gv = (VertexPosition)goal;
			
			if (gv.getVertex() == e.getEnd()) {
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
			
			if (bb.getVertex() == e.getStart()) {
				return distanceToStartOfEdge();
			} else if (bb.getVertex() == e.getEnd()) {
				return distanceToEndOfEdge();
			}
			
			double aaStartPath = MODEL.world.distanceBetweenVertices(e.getStart(), bb.getVertex());
			double aaEndPath = MODEL.world.distanceBetweenVertices(e.getEnd(), bb.getVertex());
			
			double dist = Math.min(aaStartPath + distanceToStartOfEdge(), aaEndPath + distanceToEndOfEdge());
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		} else {
			EdgePosition aa = (EdgePosition)this;
			EdgePosition bb = (EdgePosition)b;
			
			if (aa.getEdge() == bb.getEdge()) {
				return Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
			}
			
			double startStartPath = MODEL.world.distanceBetweenVertices(aa.getEdge().getStart(), bb.getEdge().getStart());
			double startEndPath = MODEL.world.distanceBetweenVertices(aa.getEdge().getStart(), bb.getEdge().getEnd());
			double endStartPath = MODEL.world.distanceBetweenVertices(aa.getEdge().getEnd(), bb.getEdge().getStart());
			double endEndPath = MODEL.world.distanceBetweenVertices(aa.getEdge().getEnd(), bb.getEdge().getEnd());
			
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
		if (!(dest.getVertex() == e.getStart() || dest.getVertex() == e.getEnd())) {
			throw new IllegalArgumentException();
		}
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		if (dest.getVertex() == e.getEnd()) {
			
			double distToEndOfEdge = distanceToEndOfEdge();
			if (DMath.equals(dist, distToEndOfEdge)) {
				return new VertexPosition(e.getEnd());
			} else if (dist > distToEndOfEdge) {
				throw new IllegalArgumentException();
			}
			
			return travelForward(e, index, param, dist);
			
		} else {
			
			double distToStartOfEdge = distanceToStartOfEdge();
			if (DMath.equals(dist, distToStartOfEdge)) {
				return new VertexPosition(e.getStart());
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
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
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
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
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
			return new VertexPosition(e.getEnd());
		} else {
			return new EdgePosition(e, index+1, 0.0);
		}
	}
	
	private static GraphPosition nextBoundBackward(Edge e, int index, double param) {
		if (DMath.equals(param, 0.0)) {
			if (index == 0 || (index == 1 && DMath.equals(param, 0.0))) {
				return new VertexPosition(e.getStart());
			} else {
				return new EdgePosition(e, index-1, 0.0);
			}
		} else {
			if (index == 0) {
				return new VertexPosition(e.getStart());
			} else {
				return new EdgePosition(e, index, 0.0);
			}
		}
	}
	
	private void check() {
		
		double acc = 0;
		for (int i = 0; i < index; i++) {
			Point a = e.getPoint(i);
			Point b = e.getPoint(i+1);
			acc = acc + Point.distance(a, b);
		}
		acc = acc + Point.distance(e.getPoint(index), p);
		
		assert DMath.equals(lengthToStartOfEdge, acc);
		
		acc = 0;
		acc = acc + Point.distance(p, e.getPoint(index+1));
		for (int i = index+1; i < e.size()-1; i++) {
			Point a = e.getPoint(i);
			Point b = e.getPoint(i+1);
			acc = acc + Point.distance(a, b);
		}
		
		assert DMath.equals(lengthToEndOfEdge, acc);
		
	}
	
}
