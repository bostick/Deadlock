package com.gutabi.deadlock.world.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.GraphPositionPath;

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
	
	protected void computePath() {
		
		overallPath = source.getShortestPathToMatch();
		
		overallPath.currentCars.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentCars.add(this);
		}
		
	}
	
}
