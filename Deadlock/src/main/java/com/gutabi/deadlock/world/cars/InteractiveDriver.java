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
			
			setOverallPos(overallPos.forwardSearch(c.center, overallPath.endPos));
			
			break;
			
		case COASTING_BACKWARD:
			
			setOverallPos(overallPos.backwardSearch(c.center, overallPath.startPos));
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
