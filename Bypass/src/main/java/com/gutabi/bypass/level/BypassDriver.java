package com.gutabi.bypass.level;

import com.gutabi.deadlock.world.cars.Driver;

public final class BypassDriver extends Driver {
	
	public BypassDriver(BypassCar c) {
		super(c);
	}
	
	public void postStep(double t) {
		
		switch (c.state) {
		case IDLE:
			break;
			
		case COASTING_FORWARD:
			
			setOverallPos(overallPos.forwardSearch(c.center, overallPath.endPos));
			((BypassWorld)c.world).handleZooming(c);
			
			break;
			
		case COASTING_BACKWARD:
			
			setOverallPos(overallPos.backwardSearch(c.center, overallPath.startPos));
			((BypassWorld)c.world).handleZooming(c);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
