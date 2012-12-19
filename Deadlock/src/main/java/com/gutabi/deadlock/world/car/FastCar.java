package com.gutabi.deadlock.world.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;
import com.gutabi.deadlock.world.graph.Fixture;

//@SuppressWarnings("static-access")
public class FastCar extends Car {
	
	private final double speed = 5.0;
	
	static Logger logger = Logger.getLogger(FastCar.class);
	
	public FastCar(WorldCamera cam, World w, Fixture s) {
		super(cam, w, s);
		sheetRowStart = 32;
		sheetRowEnd = sheetRowStart + 16;
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
}
