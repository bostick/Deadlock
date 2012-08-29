package com.gutabi.deadlock.core;

import java.util.Comparator;

public abstract class Position {
	
	protected final Point p;
	
	protected final Driveable d;
	
	public final Position prevPos;
	public final Edge prevDirEdge;
	public final int prevDir;
	
	private final int hash;
	
	public Position(Point p, Driveable d, Position prevPos, Edge prevDirEdge, int prevDir) {
		this.p = p;
		this.d = d;
		
		this.prevPos = prevPos;
		this.prevDirEdge = prevDirEdge;
		this.prevDir = prevDir;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public Driveable getDriveable() {
		return d;
	}
	
	public double distanceTo(Position a) {
		if (a instanceof IntersectionPosition) {
			return distanceToV((IntersectionPosition)a);
		} else {
			return distanceToE((EdgePosition)a);
		}
	}
	
	protected abstract double distanceToV(IntersectionPosition a);
	protected abstract double distanceToE(EdgePosition e);
	
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
					assert b instanceof IntersectionPosition;
					
					EdgePosition aa = (EdgePosition)a;
					IntersectionPosition bb = (IntersectionPosition)b;
					
					Edge e = aa.getEdge();
					Intersection bV = bb.getVertex();
					
					if (bV == e.getStart()) {
						return 1;
					} else if (bV == e.getEnd()) {
						return -1;
					} else {
						throw new AssertionError();
					}
					
				}
			} else {
				assert a instanceof IntersectionPosition;
				if (b instanceof EdgePosition) {
					
					IntersectionPosition aa = (IntersectionPosition)a;
					EdgePosition bb = (EdgePosition)b;
					
					Intersection aV = aa.getVertex();
					Edge e = bb.getEdge();
					
					if (aV == e.getStart()) {
						return -1;
					} else if (aV == e.getEnd()) {
						return 1;
					} else {
						throw new AssertionError();
					}
					
				} else {
					assert b instanceof IntersectionPosition;
					
					IntersectionPosition aa = (IntersectionPosition)a;
					IntersectionPosition bb = (IntersectionPosition)b;
					
					Intersection aV = aa.getVertex();
					Intersection bV = bb.getVertex();
					
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
