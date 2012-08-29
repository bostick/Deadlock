package com.gutabi.deadlock.core;

@SuppressWarnings("serial")
public class SharedIntersectionsException extends RuntimeException {
	
	public Intersection v1;
	public Intersection v2;
	
	public SharedIntersectionsException(Intersection v1, Intersection v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
}
