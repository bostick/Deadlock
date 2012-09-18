package com.gutabi.deadlock.model;


public class CrashInfo {
	
	public final double crashTime;
	public final Car i;
	public final Car j;
	
	/**
	 * 
	 * For iDir and jDir, 0 means that this car has already crashed
	 */
	public CrashInfo(double crashTime, Car i, Car j) {
		this.crashTime = crashTime;
		this.i = i;
		this.j = j;
	}
	
}
