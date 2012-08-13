package com.gutabi.deadlock.core;


public class EdgePosition extends Position {
	
	public final int index;
	public final double param;
	
	public final Point segStart;
	public final Point segEnd;
	
	public EdgePosition(Edge e, int index, double param) {
		super(Point.point(e.getPoint(index), e.getPoint(index+1), param), e);
		assert index < e.size()-1;
		assert DMath.doubleEquals(param, 0.0) || param > 0.0;
		assert param < 1.0;
		assert !(index == 0 && param == 0.0);
		assert !(index == e.size()-2 && param == 1.0);
		
		this.index = index;
		this.param = param;
		
		this.segStart = e.getPoint(index);
		this.segEnd = e.getPoint(index+1);
	}
	
	public EdgePosition(Segment si, double param) {
		this(si.edge, si.index, param);
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public double distanceToV(VertexPosition a) {
		assert a.getEdge() == e;
		
		double l;
		switch (Position.COMPARATOR.compare(this, a)) {
		case -1:
			l = 0.0;
			l += Point.dist(p, segEnd);
			for (int i = index+1; i < e.size()-1; i++) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			return l;
		case 1:
			l = 0.0;
			l += Point.dist(p, segStart);
			for (int i = index-1; i >= 0; i--) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			return l;
		default:
			throw new AssertionError();
		}
		
	}
	
	public double distanceToE(EdgePosition a) {
		assert a.getEdge() == e;
		
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
