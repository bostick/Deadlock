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
			
			this.prevOverallPos.set(this.overallPos);
			overallPos.forwardSearch(c.center, overallPos.lengthToEndOfPath);
			if (!c.driver.toolCoastingGoal.isEndOfPath()) {
				((BypassWorld)c.world).handlePanning(c, c.center);
			}
			
			break;
			
		case COASTING_BACKWARD:
			
			this.prevOverallPos.set(this.overallPos);
			overallPos.backwardSearch(c.center, overallPos.lengthToStartOfPath);
			((BypassWorld)c.world).handlePanning(c, c.center);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
