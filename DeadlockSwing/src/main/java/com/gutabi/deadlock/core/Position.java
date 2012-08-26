package com.gutabi.deadlock.core;

import java.util.Comparator;

public abstract class Position {
	
	protected final Point p;
	
	public final Position prevPos;
	public final Edge prevDirEdge;
	public final int prevDir;
	
	private final int hash;
	
	public Position(Point p, Position prevPos, Edge prevDirEdge, int prevDir) {
		this.p = p;
		
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
	
	public double distanceTo(Position a) {
		if (a instanceof VertexPosition) {
			return distanceToV((VertexPosition)a);
		} else {
			return distanceToE((EdgePosition)a);
		}
	}
	
	protected abstract double distanceToV(VertexPosition a);
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
