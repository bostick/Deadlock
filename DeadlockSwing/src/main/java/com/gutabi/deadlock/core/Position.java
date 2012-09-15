package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.Comparator;

public abstract class Position {
	
	protected final Point p;
	
	protected final Driveable d;
	
	private final int hash;
	
	public Position(Point p, Driveable d) {
		this.p = p;
		this.d = d;
		
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
	
	public double distanceTo(Position b) {
		
		if (this instanceof VertexPosition) {
			if (b instanceof VertexPosition) {
				VertexPosition aa = (VertexPosition)this;
				VertexPosition bb = (VertexPosition)b;
				
				return MODEL.distanceTo(aa.getVertex(), bb.getVertex());
			} else {
				VertexPosition aa = (VertexPosition)this;
				EdgePosition bb = (EdgePosition)b;
				
				double bbStartPath = MODEL.distanceTo(aa.getVertex(), bb.getEdge().getStart());
				double bbEndPath = MODEL.distanceTo(aa.getVertex(), bb.getEdge().getEnd());
				
				return Math.min(bbStartPath + bb.distanceToStartOfEdge(), bbEndPath + bb.distanceToEndOfEdge());
			}
		} else {
			if (b instanceof VertexPosition) {
				EdgePosition aa = (EdgePosition)this;
				VertexPosition bb = (VertexPosition)b;
				
				double aaStartPath = MODEL.distanceTo(aa.getEdge().getStart(), bb.getVertex());
				double aaEndPath = MODEL.distanceTo(aa.getEdge().getEnd(), bb.getVertex());
				
				return Math.min(aaStartPath + aa.distanceToStartOfEdge(), aaEndPath + aa.distanceToEndOfEdge());
			} else {
				EdgePosition aa = (EdgePosition)this;
				EdgePosition bb = (EdgePosition)b;
				
				if (aa.getEdge() == bb.getEdge()) {
					return Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
				}
				
				double startStartPath = MODEL.distanceTo(aa.getEdge().getStart(), bb.getEdge().getStart());
				double startEndPath = MODEL.distanceTo(aa.getEdge().getStart(), bb.getEdge().getEnd());
				double endStartPath = MODEL.distanceTo(aa.getEdge().getEnd(), bb.getEdge().getStart());
				double endEndPath = MODEL.distanceTo(aa.getEdge().getEnd(), bb.getEdge().getEnd());
				
				double startStartDistance = startStartPath + aa.distanceToStartOfEdge() + bb.distanceToStartOfEdge();
				double startEndDistance = startEndPath + aa.distanceToStartOfEdge() + bb.distanceToEndOfEdge();
				double endStartDistance = endStartPath + aa.distanceToEndOfEdge() + bb.distanceToStartOfEdge();
				double endEndDistance = endEndPath + aa.distanceToEndOfEdge() + bb.distanceToEndOfEdge();
				
				return Math.min(Math.min(startStartDistance, startEndDistance), Math.min(endStartDistance, endEndDistance));
			}
		}
	}
	
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
					
					if (aa.getIndex() < bb.getIndex()) {
						return -1;
					} else if (aa.getIndex() > bb.getIndex()) {
						return 1;
					} else if (aa.getParam() < bb.getParam()) {
						return -1;
					} else if (aa.getParam() > bb.getParam()) {
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
					
					throw new IllegalArgumentException();
					
				}
			}
		}
	}
	
	public static Comparator<Position> COMPARATOR = new PositionComparator();
	
}
