package com.gutabi.deadlock.model.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Fixture;


//@SuppressWarnings("static-access")
public class ReallyFastCar extends Car {
	
	private final double speed = 10.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public ReallyFastCar(Fixture s) {
		super(s);
		sheetRowStart = 64;
		sheetRowEnd = sheetRowStart + 16;
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
//	protected int getSheetRow() {
//		return 64;
//	}
	
	protected void computePath() {
		overallPath = source.getShortestPathToMatch();
		overallPath.currentCars.add(this);
	}
	
}
