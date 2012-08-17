package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

import java.util.Comparator;

public abstract class Position {
	
	protected final Point p;
	protected final Edge e;
	
	private final int hash;
	
	public Position(Point p, Edge e) {
		this.p = p;
		this.e = e;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		if (e != null) {
			h = 37 * h + e.hashCode();
		}
		hash = h;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public abstract int getIndex();
	public abstract double getParam();
	
	public double distanceToEndOfEdge() {
		return distanceTo(new VertexPosition(getEdge().getEnd(), getEdge(), VertexPositionType.END));
	}
	
	public double distanceToStartOfEdge() {
		return distanceTo(new VertexPosition(getEdge().getStart(), getEdge(), VertexPositionType.START));
	}
	
	public double distanceTo(Position a) {
		if (a instanceof VertexPosition) {
			return distanceToV((VertexPosition)a);
		} else {
			return distanceToE((EdgePosition)a);
		}
	}
	
	protected abstract double distanceToV(VertexPosition a);
	protected abstract double distanceToE(EdgePosition e);
	
	public Position travelTo(Position a, double dist) {
		if (a instanceof VertexPosition) {
			return travelToV((VertexPosition)a, dist);
		} else {
			return travelToE((EdgePosition)a, dist);
		}
	}
	
	protected abstract Position travelToV(VertexPosition a, double dist);
	protected abstract Position travelToE(EdgePosition e, double dist);
	
	public Position travel(int dir, double dist) throws TravelException {
		 if (dir == 1) {
			 return travelForward(dist);
		 } else if (dir == -1) {
			 return travelBackward(dist);
		 } else {
			 throw new IllegalArgumentException();
		 }
	}
	
	private Position travelForward(double dist) throws TravelException {
		
		if (doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double distToEndOfEdge = distanceToEndOfEdge();
		if (DMath.doubleEquals(dist, distToEndOfEdge)) {
			return new VertexPosition(e.getEnd(), e, VertexPositionType.END);
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
				return new EdgePosition(e, index+1, 0.0, 1);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, 1);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
	}
	
	public Position travelClamped(int dir, double dist) {
		if (dir == 1) {
			 return travelForwardClamped(dist);
		 } else if (dir == -1) {
			 return travelBackwardClamped(dist);
		 } else {
			 throw new IllegalArgumentException();
		 }
	}
	
	private Position travelForwardClamped(double dist) {
		
		try {
			return travelForward(dist);
		} catch (TravelException ex) {
			return new VertexPosition(e.getEnd(), e, VertexPositionType.END);
		}
		
	}
	
	private Position travelBackwardClamped(double dist) {
		
		try {
			return travelBackward(dist);
		} catch (TravelException ex) {
			return new VertexPosition(e.getStart(), e, VertexPositionType.START);
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
			return new VertexPosition(e.getStart(), e, VertexPositionType.START);
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
				return new EdgePosition(e, index, 0.0, 1);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new EdgePosition(e, index, newParam, 1);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
	public static Position middle(Position a, Position b) {
		assert a.getEdge() == b.getEdge() || Edge.sharedVertex(a.getEdge(), b.getEdge()) != null;
		
		if (a.getEdge() == b.getEdge()) {
			
			double d = a.distanceTo(b);
			d = d/2;
			
			try {
				switch (Position.COMPARATOR.compare(a, b)) {
				case -1:
					return a.travelForward(d);
				case 1:
					return a.travelBackward(d);
				default:
					return a;
				}
			} catch (TravelException e) {
				throw new AssertionError();
			}
			
		} else {
			
			Vertex v = Edge.sharedVertex(a.getEdge(), b.getEdge());
			
			double d = a.distanceTo(b);
			d = d/2;
			
			VertexPosition avp = new VertexPosition(v, a.getEdge());
			VertexPosition bvp = new VertexPosition(v, b.getEdge());
			
			if (DMath.doubleEquals(a.distanceTo(avp), d)) {
				/*
				 * in between two different edges, so no real orientation
				 */
				return new VertexPosition(v, null, null);
			} else if (a.distanceTo(avp) > d) {
				return a.travelTo(avp, d);
			} else {
				return bvp.travelTo(b, d-a.distanceTo(avp));
			}
			
		}
		
	}
	
//	public static boolean areComparable(Position a, Position b) {
//		return a.getEdge() != null && a.getEdge() == b.getEdge();
//	}
	
	static class PositionComparator implements Comparator<Position> {
		@Override
		public int compare(Position a, Position b) {
			
			if (a.getEdge() != b.getEdge()) {
				throw new IllegalArgumentException();
			}
			
			if (a instanceof EdgePosition) {
				if (b instanceof EdgePosition) {
					
					EdgePosition aa = (EdgePosition)a;
					EdgePosition bb = (EdgePosition)b;
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
					
					Edge e = a.getEdge();
					
					VertexPosition bb = (VertexPosition)b;
					
					Vertex bV = bb.getVertex();
					
					switch (bb.getType()) {
					case START:
						assert bV == e.getStart();
						return 1;
					case END:
						assert bV == e.getEnd();
						return -1;
					default:
						throw new AssertionError();
					}
					
				}
			} else {
				assert a instanceof VertexPosition;
				if (b instanceof EdgePosition) {
					
					Edge e = b.getEdge();
					
					VertexPosition aa = (VertexPosition)a;
					
					Vertex aV = aa.getVertex();
					
					switch (aa.getType()) {
					case START:
						assert aV == e.getStart();
						return -1;
					case END:
						assert aV == e.getEnd();
						return 1;
					default:
						throw new AssertionError();
					}
					
				} else {
					assert b instanceof VertexPosition;
					
					VertexPosition aa = (VertexPosition)a;
					VertexPosition bb = (VertexPosition)b;
					
					Vertex aV = aa.getVertex();
					Vertex bV = bb.getVertex();
					
					//Edge e = bb.getEdge();
					
					switch (aa.getType()) {
					case START:
						switch (bb.getType()) {
						case START:
							assert aV == bV;
							return 0;
						case END:
							return -1;
						default:
							throw new AssertionError();
						}
					case END:
						switch (bb.getType()) {
						case START:
							return 1;
						case END:
							assert aV == bV;
							return 0;
						default:
							throw new AssertionError();
						}
					default:
						throw new AssertionError();
					}
					
				}
			}
		}
	}
	
	public static Comparator<Position> COMPARATOR = new PositionComparator();
	
}
