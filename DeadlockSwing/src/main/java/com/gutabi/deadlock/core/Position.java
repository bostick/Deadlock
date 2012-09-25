package com.gutabi.deadlock.core;

public abstract class Position {
	
	Point p;
	
	Position(Point p) {
		this.p = p;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public abstract Driveable getDriveable();
	
	/**
	 * A position is a bound if it is not interpolated, i.e., it is on a well-defined point on the graph
	 */
	public abstract boolean isBound();
	
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) {
//			return true;
//		} else if (!(o instanceof Position)) {
//			throw new IllegalArgumentException();
//		} else {
//			if (this instanceof Vertex) {
//				return ((Vertex)this).equalsP((GraphPosition)o);
//			} else if (this instanceof EdgePosition) {
//				return ((EdgePosition)this).equalsP((GraphPosition)o);
//			} else {
////				return ((SinkedPosition)this).equalsP((Position)o);
//				throw new AssertionError();
//			}
//		}
//	}

}
