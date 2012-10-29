package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

//@SuppressWarnings("static-access")
public class FastCar extends Car {
	
	private final double speed = 5.0;
	
	static Logger logger = Logger.getLogger(FastCar.class);
	
	public FastCar(Source s) {
		super(s);
		overallPath = s.getShortestPathToMatchingSink();
		
		sheetCol = 64;
		sheetRow = 0;
		
		computeStartingProperties();
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
	
}
