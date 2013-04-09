package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.rushhour.RushHourWorld;

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
			((RushHourWorld)c.world).handleZooming(c);
			
			break;
			
		case COASTING_BACKWARD:
			
			setOverallPos(overallPos.backwardSearch(c.center, overallPath.startPos));
			((RushHourWorld)c.world).handleZooming(c);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
