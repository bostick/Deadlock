package com.gutabi.deadlock.world.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.graph.Fixture;

//@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 2.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(Fixture s) {
		super(s);
		sheetRowStart = 64;
		sheetRowEnd = sheetRowStart + 16;
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
//	protected void computePath() {
//		
//		overallPath = source.getRandomPathToMatch();
//		
//		overallPath.currentCars.add(this);
//		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
//			path.currentCars.add(this);
//		}
//		
//	}
	
//	public void preStep(double t) {
//		
//		overallPath.precomputeHitTestData();
//		
//		super.preStep(t);
//		
//	}
	
//	public boolean postStep(double t) {
//		
//		boolean res = super.postStep(t);
//		
//		overallPath.clearHitTestData();
//		
//		return res;
//	}
	
}
