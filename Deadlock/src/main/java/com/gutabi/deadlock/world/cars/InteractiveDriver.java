package com.gutabi.deadlock.world.cars;

public final class InteractiveDriver extends Driver {
	
	public InteractiveDriver(InteractiveCar c) {
		super(c);
	}
	
	public void computeStartingProperties() {
		
		overallPos = overallPath.findClosestGraphPositionPathPosition(startGP);
		
	}
	
}
