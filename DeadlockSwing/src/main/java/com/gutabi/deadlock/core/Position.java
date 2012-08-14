package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

import java.util.Comparator;

public abstract class Position {
	
	protected final Point p;
	protected final Edge e;
	
	public Position(Point p, Edge e) {
		this.p = p;
		this.e = e;
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
		return distanceTo(new VertexPosition(getEdge().getEnd(), getEdge()));
	}
	
	public double distanceToStartOfEdge() {
		return distanceTo(new VertexPosition(getEdge().getStart(), getEdge()));
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
	
	public Position travelForward(double dist) throws TravelException {
		
		if (doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double distToEndOfEdge = distanceToEndOfEdge();
		if (DMath.doubleEquals(dist, distToEndOfEdge)) {
			return new VertexPosition(e.getEnd(), e);
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
	
	public Position travelForwardClamped(double dist) {
		
		try {
			return travelForward(dist);
		} catch (TravelException ex) {
			return new VertexPosition(e.getEnd(), e);
		}
		
	}
	
	public Position travelBackward(double dist) throws TravelException {
		
		if (doubleEquals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double distToStartOfEdge = distanceToStartOfEdge();
		if (DMath.doubleEquals(dist, distToStartOfEdge)) {
			return new VertexPosition(e.getStart(), e);
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
	
	public Position travelBackwardClamped(double dist) {
		
		try {
			return travelBackward(dist);
		} catch (TravelException ex) {
			return new VertexPosition(e.getStart(), e);
		}
		
	}
	
	public static Position middle(Position a, Position b) {
		assert a.getEdge() == b.getEdge();
		
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
		
	}
	
	static class PositionComparator implements Comparator<Position> {
		@Override
		public int compare(Position a, Position b) {
			
			Edge e = a.getEdge();
			
			if (a instanceof EdgePosition) {
				if (b instanceof EdgePosition) {
					if (a.getEdge() != b.getEdge()) {
						throw new IllegalArgumentException();
					}
					
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
					
					VertexPosition bb = (VertexPosition)b;
					
					Vertex bV = bb.getVertex();
					
					if (bV == e.getStart()) {
						return 1;
					} else if (bV == e.getEnd()) {
						return -1;
					} else {
						throw new IllegalArgumentException();
					}
					
				}
			} else {
				assert a instanceof VertexPosition;
				if (b instanceof EdgePosition) {
					
					VertexPosition aa = (VertexPosition)a;
					
					Vertex aV = aa.getVertex();
					
					if (aV == e.getStart()) {
						return -1;
					} else if (aV == e.getEnd()) {
						return 1;
					} else {
						throw new IllegalArgumentException();
					}
					
				} else {
					assert b instanceof VertexPosition;
					
					VertexPosition aa = (VertexPosition)a;
					VertexPosition bb = (VertexPosition)b;
					
					Vertex aV = aa.getVertex();
					Vertex bV = bb.getVertex();
					
					if (aV == bV) {
						return 0;
					} else if (aV == e.getStart() && bV == e.getEnd()) {
						return -1;
					} else if (aV == e.getEnd() && bV == e.getStart()) {
						return 1;
					} else {
						throw new IllegalArgumentException();
					}
				}
			}
		}
	}
	
	public static Comparator<Position> COMPARATOR = new PositionComparator();
	
}
