package com.gutabi.capsloc;

public interface Integratable {
	
	/**
	 * move physics forward by dt seconds
	 */
	public static double DT = 0.0166666;
	
	void integrate(double t);
	
	double getTime();
	
}
