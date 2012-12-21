package com.gutabi.deadlock.core.geom;

public interface CompoundShape extends Shape {
	
	boolean intersect(Shape s);
	
}
