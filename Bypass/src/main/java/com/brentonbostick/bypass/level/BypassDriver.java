package com.brentonbostick.bypass.level;

import com.brentonbostick.capsloc.world.cars.Driver;

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
				((BypassWorld)c.world).handlePanning(c, c.center);
			}
			
			break;
			
		case COASTING_BACKWARD:
			
			setOverallPos(overallPos.backwardSearch(c.center, overallPos.lengthToStartOfPath));
			((BypassWorld)c.world).handlePanning(c, c.center);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
