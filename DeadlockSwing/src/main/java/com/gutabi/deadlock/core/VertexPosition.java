package com.gutabi.deadlock.core;

public class VertexPosition extends Position {
	
	public final Vertex v;
	
	private final int index;
	private final double param;
	
	private final VertexPositionType type;
	
	public VertexPosition(Vertex v) {
		this(v, (v.getEdges().size() == 1) ? v.getEdges().get(0) : badEdge());
	}
	
	public VertexPosition(Vertex v, Edge e) {
		this(v, e, (e.isLoop()) ? badVertexPositionType() : (v == e.getStart()) ? VertexPositionType.START : VertexPositionType.END);
	}
	
	private static VertexPositionType badVertexPositionType() {
		throw new IllegalArgumentException();
	}
	
	private static Edge badEdge() {
		throw new IllegalArgumentException();
	}
	
	public VertexPosition(Vertex v, Edge e, VertexPositionType type) {
		super(v.getPoint(), e);
		this.v = v;
		
		this.type = type;
		
		if (type == null) {
			index = -1;
			param = -1;
		} else if (type == VertexPositionType.START) {
			assert v == e.getStart();
			index = 0;
			param = 0.0;
		} else {
			assert v == e.getEnd();
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
		assert e != null;
		assert a.getVertex() == v || a.getVertex() == e.otherVertex(v);
		
		if (a.getVertex() == v) {
			assert !e.isLoop(); // need to handle
			return 0.0;
		} else {
			return e.getTotalLength();
		}
		
	}
	
	public double distanceToE(EdgePosition a) {
		assert a.getEdge() == e;
		if (a.getEdge().isLoop()) {
			assert type != null;
		}
		assert getVertex().getEdges().contains(a.getEdge());
		
		VertexPositionType t;
		if (!a.getEdge().isLoop()) {
			if (v == a.getEdge().getStart()) {
				t = VertexPositionType.START;
			} else if (v == a.getEdge().getEnd()) {
				t = VertexPositionType.END;
			} else {
				throw new AssertionError();
			}
		} else {
			t = type;
		}
		
		double l;
		switch (t) {
		case START:
			l = 0.0;
			for (int i = 0; i < a.index; i++) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			l += Point.dist(a.segStart, a.p);
			return l;
		case END:
			l = 0.0;
			l += Point.dist(a.p, a.segEnd);
			for (int i = a.index+1; i < e.size()-1; i++) {
			//for (int i = e.size()-2; i > a.index; i--) {
				Point aa = e.getPoint(i);
				Point bb = e.getPoint(i+1);
				l += Point.dist(aa, bb);
			}
			return l;
		default:
			throw new AssertionError();
		}
		
	}
	
	protected Position travelToE(EdgePosition a, double dist) {
		assert a.getEdge() == e;
		if (a.getEdge().isLoop()) {
			assert type != null;
		}
		assert getVertex().getEdges().contains(a.getEdge());
		
		double actual = distanceToE(a);
		
		assert DMath.doubleEquals(dist, actual) || dist < actual;
		
		VertexPositionType t;
		if (!a.getEdge().isLoop()) {
			if (v == a.getEdge().getStart()) {
				t = VertexPositionType.START;
			} else if (v == a.getEdge().getEnd()) {
				t = VertexPositionType.END;
			} else {
				throw new AssertionError();
			}
		} else {
			t = type;
		}
		
		//double l;
		switch (t) {
		case START:
			return travel(1, dist);
		case END:
			return travel(-1, dist);
		default:
			throw new AssertionError();
		}
		
	}
	
	protected Position travelToV(VertexPosition a, double dist) {
		assert e != null;
		assert a.getVertex() == v || a.getVertex() == e.otherVertex(v);
		
		double actual = distanceToV(a);
		
		assert DMath.doubleEquals(dist, actual) || dist < actual;
		
		if (a.getVertex() == v) {
			assert !e.isLoop(); // need to handle
			return this;
		} else {
			
			if (v == e.getStart()) {
				return travel(1, dist);
			} else {
				return travel(-1, dist);
			}
			
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
