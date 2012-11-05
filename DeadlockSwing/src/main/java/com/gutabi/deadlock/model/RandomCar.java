package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.graph.WorldSource;

//@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 2.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(WorldSource s) {
		super(s);
		overallPath = s.getRandomPathToMatchingSink();
		
		sheetCol = 0;
		sheetRow = 64;
		
		computeStartingProperties();
	}
	
	public double getMetersPerSecond() {
		return speed;
	}
	
}
