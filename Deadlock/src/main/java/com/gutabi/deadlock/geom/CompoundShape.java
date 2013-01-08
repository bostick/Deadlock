package com.gutabi.deadlock.geom;

public interface CompoundShape extends Shape {
	
	boolean intersect(Shape s);
	
}
