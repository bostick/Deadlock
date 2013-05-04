package com.gutabi.capsloc;

public interface Integratable {
	
	/**
	 * move physics forward by dt seconds
	 */
//	public static double DT = 0.0166666;
	public static double DT = 1.0 / 100.0;
	
	boolean integrate(double t);
	
	double getTime();
	
}
