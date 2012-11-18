package com.gutabi.deadlock.model.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.fixture.WorldSource;

//@SuppressWarnings("static-access")
public class FastCar extends Car {
	
	private final double speed = 5.0;
	
	static Logger logger = Logger.getLogger(FastCar.class);
	
	public FastCar(WorldSource s) {
		super(s);
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
	protected int getSheetRow() {
		return 32;
	}
	
	protected void computePath() {
		overallPath = source.getShortestPathToMatchingSink();
	}
}
