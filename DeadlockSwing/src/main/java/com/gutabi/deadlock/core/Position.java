package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

public class Position {
	
	public final Edge edge;
	public final int index;
	public final double param;
	public final Point point;
	
	public final Point segStart;
	public final Point segEnd;
	
	public Position(Edge e, int index, double param) {
		this.edge = e;
		this.index = index;
		this.param = param;
		
		segStart = e.getPoint(index);
		segEnd = e.getPoint(index+1);
		this.point = Point.point(segStart, segEnd, param);
		
	}
	
	public Position(Segment si, double param) {
		this(si.edge, si.index, param);
	}
	
	public double distToEndOfEdge() {
		double l = 0.0;
//		Point a = pts[index];
//		Point b = pts[index+1];
		l += Point.dist(point, segEnd);
		for (int i = index+1; i < edge.size()-1; i++) {
			Point a = edge.getPoint(i);
			Point b = edge.getPoint(i+1);
			l += Point.dist(a, b);
		}
		return l;
	}
	
	public double distToStartOfEdge() {
		double l = 0.0;
//		Point a = pts[index];
//		Point b = pts[index+1];
		l += Point.dist(point, segStart);
		for (int i = index-1; i >= 0; i--) {
			Point a = edge.getPoint(i);
			Point b = edge.getPoint(i+1);
			l += Point.dist(a, b);
		}
		return l;
	}
	
	public static Position travelForward(Position pos, double dist) throws TravelException {
		
		if (dist == 0.0) {
			return pos;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		Edge e = pos.edge;
		int index = pos.index;
		double param = pos.param;
		
		double distanceToTravel = dist;
		
		Point a = e.getPoint(index);
		Point b = e.getPoint(index+1);
		
		Point c = Point.point(a, b, param);
		double distanceToEndOfSegment = Point.dist(c, b);
		if (doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
			
			if (index == e.size()-2) {
				throw new TravelException();
			} else {
				return new Position(e, index+1, 0.0);
			}
			
		} else if (distanceToTravel < distanceToEndOfSegment) {
			
			double newParam = Point.travelForward(a, b, param, distanceToTravel);
			
			return new Position(e, index, newParam);
			
		} else {
			
			if (index == e.size()-2) {
				throw new TravelException();
			} else {
				return travelForward(new Position(e, index+1, 0.0), distanceToTravel-distanceToEndOfSegment);
			}
			
		}
		
	}
	
	public static Position travelBackward(Position pos, double dist) throws TravelException {
		
		if (dist == 0.0) {
			return pos;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		Edge e = pos.edge;
		int index = pos.index;
		double param = pos.param;
		
		double distanceToTravel = dist;
		
		Point a = e.getPoint(index);
		Point b = e.getPoint(index+1);
		
		Point c = Point.point(a, b, param);
		double distanceToBeginningOfSegment = Point.dist(c, a);
		if (doubleEquals(distanceToTravel, distanceToBeginningOfSegment)) {
			
			return new Position(e, index, 0.0);
			
		} else if (distanceToTravel < distanceToBeginningOfSegment) {
			
			double newParam = Point.travelBackward(a, b, param, distanceToTravel);
			
			return new Position(e, index, newParam);
			
		} else {
			
			if (index == 0) {
				throw new TravelException();
			} else {
				return travelBackward(new Position(e, index-1, 1.0), distanceToTravel-distanceToBeginningOfSegment);
			}
			
		}
		
	}

}
