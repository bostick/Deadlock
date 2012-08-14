package com.gutabi.deadlock.model;

public enum CarState {
	
	/**
	 * moving from start of edge to end
	 */
	FORWARD,
	
	/**
	 * moving from end of edge to start
	 */
	BACKWARD,
	
	/**
	 * crashed
	 */
	CRASHED,
	
	SINKED;
	
}
