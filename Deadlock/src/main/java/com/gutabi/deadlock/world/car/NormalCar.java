package com.gutabi.deadlock.world.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;
import com.gutabi.deadlock.world.graph.Fixture;

//@SuppressWarnings("static-access")
public class NormalCar extends Car {
	
	private final double speed = 2.5;
	
	static Logger logger = Logger.getLogger(NormalCar.class);
	
	public NormalCar(WorldCamera cam, World w, Fixture s) {
		super(cam, w, s);
		sheetRowStart = 0;
		sheetRowEnd = sheetRowStart + 16;
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
}
