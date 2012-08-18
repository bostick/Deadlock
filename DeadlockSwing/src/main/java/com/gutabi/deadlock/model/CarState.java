package com.gutabi.deadlock.model;

public enum CarState {
	
	NEW,
	
	/**
	 * moving from start of edge to end
	 */
	FORWARD,
	
	/**
	 * moving from end of edge to start
	 */
	BACKWARD,
	
	VERTEX,
	
	/**
	 * crashed
	 */
	CRASHED,
	
	SINKED;
	
}
