package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

//@SuppressWarnings("static-access")
public class NormalCar extends Car {
	
	private final double speed = 2.5;
	
	static Logger logger = Logger.getLogger(NormalCar.class);
	
	public NormalCar(Source s) {
		super(s);
		overallPath = s.getShortestPathToMatchingSink();
		
		sheetCol = 0;
		sheetRow = 0;
		
		computeStartingProperties();
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
	
}
