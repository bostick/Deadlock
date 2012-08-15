package com.gutabi.deadlock.core;

public class VertexPosition extends Position {
	
	public final Vertex v;
	
	private final int index;
	private final double param;
	
	private final VertexPositionType type;
	
	public VertexPosition(Vertex v, Edge e, VertexPositionType type) {
		super(v.getPoint(), e);
		this.v = v;
		
		this.type = type;
		
		if (type == VertexPositionType.START) {
			index = 0;
			param = 0.0;
		} else {
			index = e.size()-2;
			param = 1.0;
		}
		
	}
	
	public Vertex getVertex() {
		return v;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public VertexPositionType getType() {
		return type;
	}
	
	public double distanceToV(VertexPosition a) {
		assert a.getEdge() == e;
		
		switch (Position.COMPARATOR.compare(this, a)) {
		case -1:
		case 1:
			return e.getTotalLength();
		default:
			return 0.0;
		}
		
	}
	
	public double distanceToE(EdgePosition a) {
		assert a.getEdge() == e;
		
		double l;
		switch (Position.COMPARATOR.compare(this, a)) {
		case -1:
			l = 0.0;
			for (int i = 0; i < a.index; i++) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			l += Point.dist(a.segStart, a.p);
			return l;
		case 1:
			l = 0.0;
			for (int i = e.size()-2; i > a.index; i--) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			l += Point.dist(a.segEnd, a.p);
			return l;
		default:
			throw new AssertionError();
		}
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (v == b.v && e == b.e && type == b.type);
		}
	}
	
}
