package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.graph.WorldSource;

//@SuppressWarnings("static-access")
public class ReallyFastCar extends Car {
	
	private final double speed = 10.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public ReallyFastCar(WorldSource s) {
		super(s);
		overallPath = s.getShortestPathToMatchingSink();
		
		sheetCol = 0;
		sheetRow = 64;
		
		computeStartingProperties();
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
	
}
