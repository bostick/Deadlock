package com.gutabi.deadlock.core;


public class EdgePosition extends Position {
	
	public final int index;
	public final double param;
	public final int dir;
	
	public final Point segStart;
	public final Point segEnd;
	
	public final double distanceFromStartOfEdge;
	
	public EdgePosition(Edge e, int index, double param, int dir) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param), e);
		assert index < e.size()-1;
		assert DMath.doubleEquals(param, 0.0) || param > 0.0;
		assert !DMath.doubleEquals(param, 1.0);
		assert param < 1.0;
		assert !(index == 0 && DMath.doubleEquals(param, 0.0));
		
		this.index = index;
		this.param = param;
		this.dir = dir;
		
		this.segStart = e.getPoint(index);
		this.segEnd = e.getPoint(index+1);
		
		distanceFromStartOfEdge = distanceToStartOfEdge();
	}
	
	public EdgePosition(Segment si, double param, int dir) {
		this(si.edge, si.index, param, dir);
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public int getDir() {
		return dir;
	}
	
	public double distanceToV(VertexPosition a) {
		if (e.isLoop()) {
			assert dir != 0;
		}
		assert a.getVertex().getEdges().contains(e);
		
		int d;
		if (!e.isLoop()) {
			if (a.getVertex() == e.getStart()) {
				d = -1;
			} else if (a.getVertex() == e.getEnd()) {
				d = 1;
			} else {
				throw new AssertionError();
			}
		} else {
			d = dir;
		}
		
		if (d == 1) {
			assert a.getVertex() == e.getEnd();
			
			double l;
			l = 0.0;
			l += Point.dist(p, segEnd);
			for (int i = index+1; i < e.size()-1; i++) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			return l;
			
		} else {
			assert d == -1;
			assert a.getVertex() == e.getStart();
			
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
		
	}
	
	public double distanceToE(EdgePosition a) {
		assert a.getEdge() == e || Edge.sharedVertex(a.getEdge(), e) != null;
		
		if (a.getEdge() == e) {
			
			double l;
			switch (Position.COMPARATOR.compare(this, a)) {
			case -1:
				if (a.index == index) {
					return Point.dist(p, a.p);
				}
				l = 0.0;
				l += Point.dist(p, segEnd);
				for (int i = index+1; i < a.index; i++) {
					Point aa = e.getPoint(i);
					Point bb = e.getPoint(i+1);
					l += Point.dist(aa, bb);
				}
				l += Point.dist(a.segStart, a.p);
				return l;
			case 1:
				if (a.index == index) {
					return Point.dist(a.p, p);
				}
				l = 0.0;
				l += Point.dist(p, segStart);
				for (int i = index-1; i > a.index; i--) {
					Point aa = e.getPoint(i);
					Point bb = e.getPoint(i+1);
					l += Point.dist(aa, bb);
				}
				l += Point.dist(a.segEnd, a.p);
				return l;
			default:
				return 0.0;
			}
			
		} else {
			
			Vertex v = Edge.sharedVertex(a.getEdge(), e);
			
			return distanceTo(new VertexPosition(v, e)) + new VertexPosition(v, a.e).distanceTo(a);
			
		}
		
	}
	
	protected Position travelToE(EdgePosition a, double dist) {
		assert a.getEdge() == e || Edge.sharedVertex(a.getEdge(), e) != null;
		
		double actual = distanceToE(a);
		
		assert DMath.doubleEquals(dist, actual) || dist < actual;
		
		if (a.getEdge() == e) {
			
			switch (Position.COMPARATOR.compare(this, a)) {
			case -1:
				return travel(1, dist);
			case 1:
				return travel(-1, dist);
			default:
				throw new IllegalArgumentException();
			}
			
		} else {
			
			Vertex v = Edge.sharedVertex(a.getEdge(), e);
			
			double actualVDist = distanceTo(new VertexPosition(v, e));
			
			assert DMath.doubleEquals(dist, actualVDist) || dist > actualVDist;
			
			if (DMath.doubleEquals(dist, actualVDist)) {
				return new VertexPosition(v, null, null);
			} else {
				return new VertexPosition(v, a.getEdge()).travelToE(a, dist-actualVDist);
			}
			
		}
		
	}
	
	protected Position travelToV(VertexPosition a, double dist) {
		if (e.isLoop()) {
			assert dir != 0;
		}
		assert a.getVertex().getEdges().contains(e);
		
		double actual = distanceToV(a);
		
		assert DMath.doubleEquals(dist, actual) || dist < actual;
		
		int d;
		if (!e.isLoop()) {
			if (a.getVertex() == e.getStart()) {
				d = -1;
			} else if (a.getVertex() == e.getEnd()) {
				d = 1;
			} else {
				throw new AssertionError();
			}
		} else {
			d = dir;
		}
		
		return travel(d, dist);
		
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
	
}
