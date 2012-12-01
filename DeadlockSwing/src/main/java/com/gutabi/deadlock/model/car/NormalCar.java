package com.gutabi.deadlock.model.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Fixture;


//@SuppressWarnings("static-access")
public class NormalCar extends Car {
	
	private final double speed = 2.5;
	
	static Logger logger = Logger.getLogger(NormalCar.class);
	
	public NormalCar(Fixture s) {
		super(s);
		sheetRowStart = 0;
		sheetRowEnd = sheetRowStart + 16;
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
//	protected int getSheetRow() {
//		return 0;
//	}
	
	protected void computePath() {
		overallPath = source.getShortestPathToMatch();
	}
}
