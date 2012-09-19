package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

public class EdgePosition extends Position {
	
	private final Edge e;
	private final int index;
	private final double param;
	
	private final Vertex dest;
	
	public final Point segStart;
	public final Point segEnd;
	
	private double distanceToStartOfEdge = -1;
	private double distanceToEndOfEdge = -1;
	
	public EdgePosition(Edge e, int index, double param, Vertex dest) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param));
		
		if (index < 0 || index >= e.size()-1) {
			throw new IllegalArgumentException();
		}
		if (param < 0.0 || param >= 1.0) {
			throw new IllegalArgumentException();
		}
		if (index == 0 && DMath.doubleEquals(param, 0.0) && !e.isStandAlone()) {
			throw new IllegalArgumentException();
		}
		
		this.e = e;
		this.index = index;
		this.param = param;
		
		this.dest = dest;
		
		this.segStart = e.getPoint(index);
		this.segEnd = e.getPoint(index+1);
		
		double l;
		l = 0.0;
		l += Point.distance(p, segStart);
		for (int i = index-1; i >= 0; i--) {
			l += e.getSegmentLength(i);
		}
		distanceToStartOfEdge = l;
		
		distanceToEndOfEdge = e.getTotalLength() - distanceToStartOfEdge;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public Driveable getDriveable() {
		return e;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public Vertex getDest() {
		return dest;
	}
	
	public String toString() {
		return e + ": " + distanceToStartOfEdge;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof EdgePosition)) {
			return false;
		} else {
			EdgePosition b = (EdgePosition)o;
			return (e == b.e) && (index == b.index) && (param == b.param);
		}
	}
	
	
	public double distanceTo(Position b) {
		if (b instanceof Vertex) {
			Vertex bb = (Vertex)b;
			
			double aaStartPath = MODEL.distanceBetweenVertices(e.getStart(), bb);
			double aaEndPath = MODEL.distanceBetweenVertices(e.getEnd(), bb);
			
			return Math.min(aaStartPath + distanceToStartOfEdge(), aaEndPath + distanceToEndOfEdge());
		} else if (b instanceof EdgePosition) {
			EdgePosition aa = (EdgePosition)this;
			EdgePosition bb = (EdgePosition)b;
			
			if (aa.getEdge() == bb.getEdge()) {
				return Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
			}
			
			double startStartPath = MODEL.distanceBetweenVertices(aa.getEdge().getStart(), bb.getEdge().getStart());
			double startEndPath = MODEL.distanceBetweenVertices(aa.getEdge().getStart(), bb.getEdge().getEnd());
			double endStartPath = MODEL.distanceBetweenVertices(aa.getEdge().getEnd(), bb.getEdge().getStart());
			double endEndPath = MODEL.distanceBetweenVertices(aa.getEdge().getEnd(), bb.getEdge().getEnd());
			
			double startStartDistance = startStartPath + aa.distanceToStartOfEdge() + bb.distanceToStartOfEdge();
			double startEndDistance = startEndPath + aa.distanceToStartOfEdge() + bb.distanceToEndOfEdge();
			double endStartDistance = endStartPath + aa.distanceToEndOfEdge() + bb.distanceToStartOfEdge();
			double endEndDistance = endEndPath + aa.distanceToEndOfEdge() + bb.distanceToEndOfEdge();
			
			return Math.min(Math.min(startStartDistance, startEndDistance), Math.min(endStartDistance, endEndDistance));
		} else {
			SinkedPosition bb = (SinkedPosition)b;
			
			double aaStartPath = MODEL.distanceBetweenVertices(e.getStart(), bb.getSink());
			double aaEndPath = MODEL.distanceBetweenVertices(e.getEnd(), bb.getSink());
			
			return Math.min(aaStartPath + distanceToStartOfEdge(), aaEndPath + distanceToEndOfEdge());
		}
	}
	
	
	
	
	public double distanceToEndOfEdge() {
		return distanceToEndOfEdge;
	}
	
	public double distanceToStartOfEdge() {
		return distanceToStartOfEdge;
	}
	
	protected double distanceForward(EdgePosition a) {
		if (a.index == index) {
			return Point.distance(p, a.p);
		}
		double l = 0.0;
		l += Point.distance(p, segEnd);
		for (int i = index+1; i < a.index; i++) {
			l += e.getSegmentLength(i);
		}
		l += Point.distance(a.segStart, a.p);
		return l;
	}
	
	protected double distanceBackward(EdgePosition a) {
		if (a.index == index) {
			return Point.distance(a.p, p);
		}
		double l = 0.0;
		l += Point.distance(p, segStart);
		for (int i = index-1; i > a.index; i--) {
			l += e.getSegmentLength(i);
		}
		l += Point.distance(a.segEnd, a.p);
		return l;
	}
	
	/**
	 * the specific way to travel
	 */
	public Position travel(Vertex dest, double dist) {
		if (e.isLoop()) {
			throw new IllegalArgumentException();
		}
		if (!(dest == e.getStart() || dest == e.getEnd())) {
			throw new IllegalArgumentException();
		}
		if (DMath.doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		if (dest == e.getEnd()) {
			
			double distToEndOfEdge = distanceToEndOfEdge();
			if (DMath.doubleEquals(dist, distToEndOfEdge)) {
				return e.getEnd();
			} else if (dist > distToEndOfEdge) {
				throw new TravelException();
			}
			
			return travelForward(e, index, param, dist);
			
		} else {
			
			double distToStartOfEdge = distanceToStartOfEdge();
			if (DMath.doubleEquals(dist, distToStartOfEdge)) {
				return e.getStart();
			} else if (dist > distToStartOfEdge) {
				throw new TravelException();
			}
			
			return travelBackward(e, index, param, dist);
			
		}
		
	}
	
	public static Position travelFromStart(Edge e, double dist) {
		return travelForward(e, 0, 0.0, dist);
	}
	
	public static Position travelFromEnd(Edge e, double dist) {
		return travelBackward(e, e.size()-2, 1.0, dist);
	}
	
	private static Position travelForward(Edge e, int index, double param, double dist) throws TravelException {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.distance(c, b);
			
			if (DMath.doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
				return new EdgePosition(e, index+1, 0.0, e.getEnd());
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, e.getEnd());
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
	}
	
	private static Position travelBackward(Edge e, int index, double param, double dist) throws TravelException {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.distance(c, a);
			
			if (DMath.doubleEquals(distanceToTravel, distanceToStartOfSegment)) {
				return new EdgePosition(e, index, 0.0, e.getStart());
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, e.getStart());
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
}
