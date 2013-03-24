package com.gutabi.deadlock.world.cars;


public final class InteractiveDriver extends Driver {
	
	public InteractiveDriver(InteractiveCar c) {
		super(c);
	}
	
	public void postStep(double t) {
		
		switch (c.state) {
		case IDLE:
			break;
			
		case COASTING_FORWARD:
			
			setOverallPos(overallPath.forwardSearch(c.center, overallPos, false, Double.POSITIVE_INFINITY));
			
			break;
			
		case COASTING_BACKWARD:
			
			setOverallPos(overallPath.backwardSearch(c.center, overallPos, false, c.length));
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
