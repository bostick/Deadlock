package com.gutabi.deadlock.model.car;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.fixture.WorldSource;


//@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 2.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(WorldSource s) {
		super(s);
	}
	
	public double getMaxSpeed() {
		return speed;
	}
	
	protected int getSheetRow() {
		return 64;
	}
	
	protected void computePath() {
		overallPath = source.getRandomPathToMatchingSink();
	}
	
	public void preStep(double t) {
		
		overallPath.precomputeHitTestData();
		
		super.preStep(t);
		
	}
	
	public boolean postStep(double t) {
		
		boolean res = super.postStep(t);
		
		overallPath.clearHitTestData();
		
		return res;
	}
}
