package com.gutabi.deadlock.world.cars;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Fixture;

public class NormalCar extends Car {
	
	private final double speed = 2.5;
	
	static Logger logger = Logger.getLogger(NormalCar.class);
	
	public NormalCar(World w, Fixture s) {
		super(w, s);
		sheetColStart = 0;
		sheetColEnd = sheetColStart + 64;
		sheetRowStart = 0;
		sheetRowEnd = sheetRowStart + 32;
		
		CAR_LENGTH = 1.0;
		CAR_WIDTH = 0.5;
		
		computeCtorProperties();
		computeStartingProperties();
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
}
