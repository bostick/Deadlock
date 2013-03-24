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
			
			prevOverallPos = overallPos;
			overallPos = overallPath.forwardSearch(c.center, overallPos, false, c.length);
			
			break;
			
		case COASTING_BACKWARD:
			
			prevOverallPos = overallPos;
			overallPos = overallPath.backwardSearch(c.center, overallPos, false, c.length);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
