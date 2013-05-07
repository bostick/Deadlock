package com.gutabi.bypass.level;

import com.gutabi.capsloc.world.cars.Driver;

public final class BypassDriver extends Driver {
	
	public BypassDriver(BypassCar c) {
		super(c);
	}
	
	public void postStep(double t) {
		
		switch (c.state) {
		case IDLE:
			break;
			
		case COASTING_FORWARD:
			
			setOverallPos(overallPos.forwardSearch(c.center, overallPos.lengthToEndOfPath));
			if (!c.driver.toolCoastingGoal.isEndOfPath()) {
				((BypassWorld)c.world).handleZooming(c);
			}
			
			break;
			
		case COASTING_BACKWARD:
			
			setOverallPos(overallPos.backwardSearch(c.center, overallPos.lengthToStartOfPath));
			((BypassWorld)c.world).handleZooming(c);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
