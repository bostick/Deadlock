package com.gutabi.deadlock.core;

import java.util.Comparator;
import java.util.List;

public abstract class Position {
	
	protected final Point p;
	//protected final Edge e;
	
	public final Position prevPos;
	public final Edge prevDirEdge;
	public final int prevDir;
	
	private final int hash;
	
	public Position(Point p, Position prevPos, Edge prevDirEdge, int prevDir) {
		this.p = p;
		//this.e = e;
		
		this.prevPos = prevPos;
		this.prevDirEdge = prevDirEdge;
		this.prevDir = prevDir;
		
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
	
	/**
	 * this is the generic travel method.
	 * 
	 * when there is ambiguity in the path, throws TravelException
	 * 
	 * More specific methods are in EdgePosition and VertexPosition
	 */
//	public Position travel(Position a, double dist) {
//		return travel(this, a, dist);
//	}
	
	public static Position travel(Position a, Position b, double dist) {
		if (a instanceof EdgePosition) {
			EdgePosition aa = (EdgePosition)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				if (aa.getEdge() == bb.getEdge()) {
					if (!aa.getEdge().isLoop()) {
						
						switch (Position.COMPARATOR.compare(aa, bb)) {
						case -1:
							return aa.travel(1, dist);
						case 1:
							return aa.travel(-1, dist);
						default:
							throw new IllegalArgumentException();
						}
						
					} else {
						
						throw new TravelException("ambiguous");
						
						/*
						 * possible ambiguity, so figure out path to take
						 */
//						switch (Position.COMPARATOR.compare(aa, bb)) {
//						case -1: {
//							double fDist = aa.distanceForward(bb);
//							double bDist = aa.distanceToStartOfEdge() + bb.distanceToEndOfEdge();
//							if (DMath.doubleEquals(fDist, bDist)) {
//								throw new TravelException("ambiguous");
//							} else if (fDist < bDist) {
//								return aa.travel(1, dist);
//							} else {
//								
//								break;
//							}
//						}
//						case 1: {
//							double fDist = aa.distanceToEndOfEdge() + bb.distanceToStartOfEdge();
//							double bDist = aa.distanceBackward(bb);
//							if (DMath.doubleEquals(fDist, bDist)) {
//								throw new TravelException("ambiguous");
//							} else if (fDist < bDist) {
//								break;
//							} else {
//								return aa.travel(-1, dist);
//							}
//						}
//						default:
//							throw new IllegalArgumentException();
//						}
						
					}
				} else if (Edge.haveExactlyOneSharedVertex(aa.getEdge(), bb.getEdge())) {
					
					if (!aa.getEdge().isLoop()) {
						
						Vertex v = Edge.sharedVertex(aa.getEdge(), bb.getEdge());
						VertexPosition vp = new VertexPosition(v, aa, aa.getEdge(), (v == aa.getEdge().getEnd()) ? 1 : -1);
						
						double distToV = aa.distanceTo(vp);
						
						if (DMath.doubleEquals(distToV, dist)) {
							return vp;
						} else if (distToV > dist) {
							if (v == aa.getEdge().getEnd()) {
								return aa.travel(1, dist);
							} else {
								return aa.travel(-1, dist);
							}
						} else {
							return travel(vp, bb, dist-distToV);
						}
						
					} else {
						throw new TravelException("ambiguous");
					}
					
				} else if (Edge.haveTwoSharedVertices(aa.getEdge(), bb.getEdge())) {
					throw new TravelException("ambiguous");
				} else {
					throw new TravelException("no choice");
				}
				
			} else {
				VertexPosition bb = (VertexPosition)b;
				Vertex v = bb.getVertex();
				
				Edge e = aa.getEdge();
				
				if (v == e.getStart() || v == e.getEnd()) {
					
					if (!e.isLoop()) {
						
						if (v == e.getEnd()) {
							return aa.travel(1, dist);
						} else {
							return aa.travel(-1, dist);
						}
						
					} else {
						throw new TravelException("ambiguous");
					}
					
				} else {
					throw new TravelException("no choice");
				}
				
			}
		} else {
			VertexPosition aa = (VertexPosition)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				Vertex v = aa.getVertex();
				
				Edge e = bb.getEdge();
				
				if (v == e.getStart() || v == e.getEnd()) {
					
					if (!e.isLoop()) {
						
						if (v == e.getStart()) {
							return aa.travel(e, 1, dist);
						} else {
							return aa.travel(e, -1, dist);
						}
						
					} else {
						throw new TravelException("ambiguous");
					}
					
				} else {
					throw new TravelException("no choice");
				}
				
			} else {
				VertexPosition bb = (VertexPosition)b;
				
				Vertex v1 = aa.getVertex();
				Vertex v2 = bb.getVertex();
				
				if (v1 == v2) {
					throw new TravelException("ambiguous");
				} else {
					List<Edge> common = Vertex.commonEdges(v1, v2);
					
					if (common.size() == 1) {
						
						double distToV = aa.distanceTo(bb);
						
						if (DMath.doubleEquals(distToV, dist)) {
							return bb;
						} else {
							
							Edge e = common.get(0);
							
							if (v1 == e.getStart()) {
								return aa.travel(e, 1, dist);
							} else {
								return aa.travel(e, -1, dist);
							}
							
						}
						
					} else if (common.size() > 1) {
						throw new TravelException("ambiguous");
					} else {
						throw new TravelException("no choice");
					}
					
				}
				
			}
		}
	}
	
//	public Position travelTo(Vertex v, double dist) {
//		return travelTo(new VertexPosition(v), dist);
//	}
	
//	protected abstract Position travelV(VertexPosition a, double dist);
//	protected abstract Position travelE(EdgePosition e, double dist);
	
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
