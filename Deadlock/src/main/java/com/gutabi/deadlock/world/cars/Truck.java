package com.gutabi.deadlock.world.cars;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Fixture;

public class Truck extends Car {
	
	private final double speed = 2.5;
	
	static Logger logger = Logger.getLogger(Truck.class);
	
	public Truck(World w, Fixture s) {
		super(w, s);
		
		sheetColStart = 0;
		sheetColEnd = sheetColStart + 96;
		sheetRowStart = 192;
		sheetRowEnd = sheetRowStart + 32;
		
		CAR_LENGTH = 3.0;
		CAR_WIDTH = 1.0;
		
		computeCtorProperties();
		computeStartingProperties();
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
}
