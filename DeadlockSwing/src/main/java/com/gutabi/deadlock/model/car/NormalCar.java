package com.gutabi.deadlock.model.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.model.graph.Fixture;
import com.gutabi.deadlock.model.graph.GraphPositionPath;

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
	
	protected void computePath() {
		
		overallPath = source.getShortestPathToMatch();
		
		overallPath.currentCars.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentCars.add(this);
		}
		
	}
}
