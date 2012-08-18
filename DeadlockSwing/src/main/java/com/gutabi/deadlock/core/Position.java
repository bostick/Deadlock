package com.gutabi.deadlock.core;

import java.util.Comparator;

public abstract class Position {
	
	protected final Point p;
	//protected final Edge e;
	
	private final int hash;
	
	public Position(Point p) {
		this.p = p;
		//this.e = e;
		
		int h = 17;
		h = 37 * h + p.hashCode();
//		if (e != null) {
//			h = 37 * h + e.hashCode();
//		}
		hash = h;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	public Point getPoint() {
		return p;
	}
	
//	public Edge getEdge() {
//		return e;
//	}
	
//	public double distanceTo(Vertex v) {
//		return distanceTo(new VertexPosition(v));
//	}
	
	public double distanceTo(Position a) {
		if (a instanceof VertexPosition) {
			return distanceToV((VertexPosition)a);
		} else {
			return distanceToE((EdgePosition)a);
		}
	}
	
	protected abstract double distanceToV(VertexPosition a);
	protected abstract double distanceToE(EdgePosition e);
	
	public Position travel(Position a, double dist) {
		if (a instanceof VertexPosition) {
			return travelV((VertexPosition)a, dist);
		} else {
			return travelE((EdgePosition)a, dist);
		}
	}
	
//	public Position travelTo(Vertex v, double dist) {
//		return travelTo(new VertexPosition(v), dist);
//	}
	
	protected abstract Position travelV(VertexPosition a, double dist);
	protected abstract Position travelE(EdgePosition e, double dist);
	
	public static Position middle(Position a, Position b) {
		
		double d = a.distanceTo(b);
		d = d/2;
		
		return a.travel(b, d);
		
//		if (a instanceof EdgePosition) {
//			if (b instanceof EdgePosition) {
//				
//				EdgePosition aa = (EdgePosition)a;
//				EdgePosition bb = (EdgePosition)b;
//				
//				double d = aa.distanceTo(bb);
//				d = d/2;
//				
//				assert aa.getEdge() == bb.getEdge() || Edge.sharedVertex(aa.getEdge(), bb.getEdge()) != null;
//				
//				if (aa.getEdge() == bb.getEdge()) {
//					
//					return aa.travel(bb, d);
//					
//				} else {
//					
//					Vertex v = Edge.sharedVertex(aa.getEdge(), bb.getEdge());
//					
//					VertexPosition vp = new VertexPosition(v);
//					
//					if (DMath.doubleEquals(aa.distanceTo(vp), d)) {
//						return vp;
//					} else {
//						return aa.travel(vp, d);
//					}
//					
//				}
//				
//				
//			} else {
//				assert b instanceof VertexPosition;
//				
//				EdgePosition aa = (EdgePosition)a;
//				VertexPosition bb = (VertexPosition)b;
//				
//				Edge e = aa.getEdge();
//				Vertex bV = bb.getVertex();
//				
//				//VertexPosition vp = new VertexPosition(bV);
//				
//				double d = aa.distanceTo(bb);
//				d = d/2;
//				
//				assert bV == e.getStart() || bV == e.getEnd();
//				
//				if (DMath.doubleEquals(aa.distanceTo(bb), d)) {
//					return bb;
//				} else {
//					return aa.travel(bb, d);
//				}
//				
//			}
//		} else {
//			assert a instanceof VertexPosition;
//			if (b instanceof EdgePosition) {
//				
//				VertexPosition aa = (VertexPosition)a;
//				EdgePosition bb = (EdgePosition)b;
//				
//				Vertex aV = aa.getVertex();
//				Edge e = bb.getEdge();
//				
//				//VertexPosition vp = new VertexPosition(aV);
//				
//				double d = aa.distanceTo(bb);
//				d = d/2;
//				
//				assert aV == e.getStart() || aV == e.getEnd();
//				
//				if (DMath.doubleEquals(aa.distanceTo(bb), d)) {
//					return bb;
//				} else {
//					return aa.travel(bb, d);
//				}
//				
//			} else {
//				assert b instanceof VertexPosition;
//				
//				VertexPosition aa = (VertexPosition)a;
//				VertexPosition bb = (VertexPosition)b;
//				
//				Vertex aV = aa.getVertex();
//				Vertex bV = bb.getVertex();
//				
//				if (aV == bV) {
//					return 0;
//				}
//				
//				Edge e = Edge.commonEdge(aV, bV);
//				
//				
//			}
//		}
		
	}
	
//	public static boolean areComparable(Position a, Position b) {
//		return a.getEdge() != null && a.getEdge() == b.getEdge();
//	}
	
	static class PositionComparator implements Comparator<Position> {
		@Override
		public int compare(Position a, Position b) {
			
			if (a instanceof EdgePosition) {
				if (b instanceof EdgePosition) {
					
					EdgePosition aa = (EdgePosition)a;
					EdgePosition bb = (EdgePosition)b;
					
					if (aa.getEdge() != bb.getEdge()) {
						throw new IllegalArgumentException();
					}
					
					if (aa.index < bb.index) {
						return -1;
					} else if (aa.index > bb.index) {
						return 1;
					} else if (aa.param < bb.param) {
						return -1;
					} else if (aa.param > bb.param) {
						return 1;
					} else {
						return 0;
					}
				} else {
					assert b instanceof VertexPosition;
					
					EdgePosition aa = (EdgePosition)a;
					VertexPosition bb = (VertexPosition)b;
					
					Edge e = aa.getEdge();
					Vertex bV = bb.getVertex();
					
					if (bV == e.getStart()) {
						return 1;
					} else if (bV == e.getEnd()) {
						return -1;
					} else {
						throw new AssertionError();
					}
					
				}
			} else {
				assert a instanceof VertexPosition;
				if (b instanceof EdgePosition) {
					
					VertexPosition aa = (VertexPosition)a;
					EdgePosition bb = (EdgePosition)b;
					
					Vertex aV = aa.getVertex();
					Edge e = bb.getEdge();
					
					if (aV == e.getStart()) {
						return -1;
					} else if (aV == e.getEnd()) {
						return 1;
					} else {
						throw new AssertionError();
					}
					
				} else {
					assert b instanceof VertexPosition;
					
					VertexPosition aa = (VertexPosition)a;
					VertexPosition bb = (VertexPosition)b;
					
					Vertex aV = aa.getVertex();
					Vertex bV = bb.getVertex();
					
					if (aV == bV) {
						return 0;
					}
					
					Edge e = Edge.commonEdge(aV, bV);
					
					if (e.isLoop()) {
						throw new IllegalArgumentException();
					}
					
					if (aV == e.getStart()) {
						return -1;
					} else {
						return 1;
					}
					
				}
			}
		}
	}
	
	public static Comparator<Position> COMPARATOR = new PositionComparator();
	
}
