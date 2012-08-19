package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;


public class EdgePosition extends Position {
	
	public final Edge e;
	public final int index;
	public final double param;
	//public final int dir;
	
	public final Point segStart;
	public final Point segEnd;
	
	public final double distanceFromStartOfEdge;
	
	public EdgePosition(Edge e, int index, double param) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param));
		assert index < e.size()-1;
		assert DMath.doubleEquals(param, 0.0) || param > 0.0;
		assert !DMath.doubleEquals(param, 1.0);
		assert param < 1.0;
		assert !(index == 0 && DMath.doubleEquals(param, 0.0));
		
		if (Point.equals(p, new Point(1245.4733103626659, 25.0))) {
			String.class.getName();
		}
		
		this.e = e;
		this.index = index;
		this.param = param;
		//this.dir = dir;
		
		this.segStart = e.getPoint(index);
		this.segEnd = e.getPoint(index+1);
		
		distanceFromStartOfEdge = distanceToStartOfEdge();
	}
	
	public EdgePosition(Segment si, double param) {
		this(si.edge, si.index, param);
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
//	public int getDir() {
//		return dir;
//	}
	
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
	
	public double distanceToEndOfEdge() {
		
		/*
		 * may be stand-alone loop, so start and end are null
		 */
		
		double l;
		l = 0.0;
		l += Point.dist(p, segEnd);
		for (int i = index+1; i < e.size()-1; i++) {
			Point aa = e.getPoint(i);
			Point bb = e.getPoint(i+1);
			l += Point.dist(aa, bb);
		}
		return l;
		
	}
	
	public double distanceToStartOfEdge() {
		
		/*
		 * may be stand-alone loop, so start and end are null
		 */
		
		double l;
		l = 0.0;
		l += Point.dist(p, segStart);
		for (int i = index-1; i >= 0; i--) {
			Point aa = e.getPoint(i);
			Point bb = e.getPoint(i+1);
			l += Point.dist(aa, bb);
		}
		return l;
		
	}
	
	public double distanceToV(VertexPosition a) {
		
//		if (e.isLoop()) {
//			throw new IllegalArgumentException();
//		}
		
		Vertex v = a.getVertex();
		
		if (!(v == e.getStart() || v == e.getEnd())) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (v == e.getEnd()) {
			
			return distanceToEndOfEdge();
			
		} else {
			
			return distanceToStartOfEdge();
			
		}
		
	}
	
	public double distanceToE(EdgePosition a) {
		
		if (e.isLoop()) {
			throw new IllegalArgumentException();
		}
		
		try {
			if (!(a.getEdge() == e || Edge.sharedVertex(a.getEdge(), e) != null)) {
				return Double.POSITIVE_INFINITY;
			}
			
			if (a.getEdge() == e) {
				
				switch (Position.COMPARATOR.compare(this, a)) {
				case -1 : {
					if (a.index == index) {
						return Point.dist(p, a.p);
					}
					double l = 0.0;
					l += Point.dist(p, segEnd);
					for (int i = index+1; i < a.index; i++) {
						Point aa = e.getPoint(i);
						Point bb = e.getPoint(i+1);
						l += Point.dist(aa, bb);
					}
					l += Point.dist(a.segStart, a.p);
					return l;
				}
				case 1: {
					if (a.index == index) {
						return Point.dist(a.p, p);
					}
					double l = 0.0;
					l += Point.dist(p, segStart);
					for (int i = index-1; i > a.index; i--) {
						Point aa = e.getPoint(i);
						Point bb = e.getPoint(i+1);
						l += Point.dist(aa, bb);
					}
					l += Point.dist(a.segEnd, a.p);
					return l;
				}
				default: {
					return 0;
				}
				}
				
			} else {
				
				Vertex v = Edge.sharedVertex(a.getEdge(), e);
				VertexPosition vp = new VertexPosition(v);
				
				return distanceTo(vp) + vp.distanceTo(a);
			}
		} catch (SharedVerticesException ex) {
			
			Vertex v1 = ex.v1;
			VertexPosition vp1 = new VertexPosition(v1);
			Vertex v2 = ex.v2;
			VertexPosition vp2 = new VertexPosition(v2);
			
			double d1 = distanceTo(vp1) + vp1.distanceTo(a);
			double d2 = distanceTo(vp2) + vp2.distanceTo(a);
			
			return Math.min(d1, d2);
		}
		
	}
	
//	public Position travel(int dir, double dist) throws TravelException {
//		 if (dir == 1) {
//			 return travelForward(dist);
//		 } else if (dir == -1) {
//			 return travelBackward(dist);
//		 } else {
//			 throw new IllegalArgumentException();
//		 }
//	}
	
	private Position travelForward(double dist) throws TravelException {
		
		if (doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double distToEndOfEdge = distanceToEndOfEdge();
		if (DMath.doubleEquals(dist, distToEndOfEdge)) {
			return new VertexPosition(e.getEnd());
		} else if (dist > distToEndOfEdge) {
			throw new TravelException();
		}
		
		int index = getIndex();
		double param = getParam();
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.dist(c, b);
			
			if (doubleEquals(distanceToTravel, distanceToEndOfSegment)) {
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
	
	private Position travelBackward(double dist) throws TravelException {
		
		if (doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double distToStartOfEdge = distanceToStartOfEdge();
		if (DMath.doubleEquals(dist, distToStartOfEdge)) {
			return new VertexPosition(e.getStart());
		} else if (dist > distToStartOfEdge) {
			throw new TravelException();
		}
		
		int index = getIndex();
		double param = getParam();
		double distanceToTravel = dist;
		
		while (true) {
			Point a = e.getPoint(index);
			Point b = e.getPoint(index+1);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.dist(c, a);
			
			if (doubleEquals(distanceToTravel, distanceToStartOfSegment)) {
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
	
	public Position travel(Vertex v, double dist) {
		return travel(new VertexPosition(v), dist);
	}
	
	protected Position travelE(EdgePosition a, double dist) {
		
		if (e.isLoop()) {
			throw new IllegalArgumentException();
		}
		
		try {
			assert a.getEdge() == e || Edge.sharedVertex(a.getEdge(), e) != null;
			
			double actual = distanceToE(a);
			
			assert DMath.doubleEquals(dist, actual) || dist < actual;
			
			if (a.getEdge() == e) {
				
				switch (Position.COMPARATOR.compare(this, a)) {
				case -1:
					return travelForward(dist);
				case 1:
					return travelBackward(dist);
				default:
					return this;
				}
				
			} else {
				
				Vertex v = Edge.sharedVertex(a.getEdge(), e);
				VertexPosition vp = new VertexPosition(v);
				
				double actualVDist = distanceTo(vp);
				
				if (DMath.doubleEquals(dist, actualVDist)) {
					return vp;
				} else if (dist < actualVDist) {
					return travelV(vp, dist);
				} else {
					return vp.travelE(a, dist-actualVDist);
				}
				
			}
		} catch (SharedVerticesException ex) {
			
			Vertex v1 = ex.v1;
			VertexPosition vp1 = new VertexPosition(v1);
			Vertex v2 = ex.v2;
			VertexPosition vp2 = new VertexPosition(v2);
			
			double d1 = distanceTo(vp1) + vp1.distanceTo(a);
			double d2 = distanceTo(vp2) + vp2.distanceTo(a);
			
			if (d1 < d2) {
				
				double actualVDist = distanceTo(vp1);
				
				if (DMath.doubleEquals(dist, actualVDist)) {
					return vp1;
				} else if (dist < actualVDist) {
					return travelV(vp1, dist);
				} else {
					return vp1.travelE(a, dist-actualVDist);
				}
				
			} else {
				
				double actualVDist = distanceTo(vp2);
				
				if (DMath.doubleEquals(dist, actualVDist)) {
					return vp2;
				} else if (dist < actualVDist) {
					return travelV(vp2, dist);
				} else {
					return vp2.travelE(a, dist-actualVDist);
				}
				
			}
		}
		
	}
	
	protected Position travelV(VertexPosition a, double dist) {
		
		if (e.isLoop()) {
			throw new IllegalArgumentException();
		}
		
		//assert a.getVertex().getEdges().contains(e);
		
		Vertex v = a.getVertex();
		
		assert v == e.getStart() || v == e.getEnd();
		
		if (v == e.getEnd()) {
			return travelForward(dist);
		} else {
			return travelBackward(dist);
		}
		
	}
	
}
