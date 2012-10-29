package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;

//@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 6.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(Source s) {
		super(s);
		overallPath = s.getRandomPathToMatchingSink();
		
		sheetCol = 0;
		sheetRow = 32;
		
		computeStartingProperties();
	}
	
	public double getMetersPerSecond() {
		return speed;
	}

}
