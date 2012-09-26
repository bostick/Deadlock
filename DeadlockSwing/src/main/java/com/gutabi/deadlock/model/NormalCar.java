package com.gutabi.deadlock.model;

import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.STPath;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.Source;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

public class NormalCar extends Car {
	
	public double DISTANCE_PER_TIMESTEP = 1;
	
	public NormalCar(Source s) {
		super(s);
	}

	@Override
	BufferedImage image() {
		return VIEW.normalCar;
	}
	
	/**
	 * Returns true if car moved in this update
	 */
	public boolean updateNext() {
		
		assert nextPath == null;
		assert nextState == null;
		
		switch (state) {
		case RUNNING:
			
			nextPath = STPath.advanceOneTimeStep(pos, DISTANCE_PER_TIMESTEP);
			
//			logger.debug("last nextPath: " + nextPath.getLastPosition());
			
			if (nextPath.getLastPosition().getSpace().getGraphPosition() instanceof Sink) {
				nextState = CarState.SINKED;
			} else {
				nextState = CarState.RUNNING;
			}
			break;
		case CRASHED:
			nextPath = STPath.crashOneTimeStep(pos);
			nextState = CarState.CRASHED;
			break;
		default:
			assert false;
		}
		
		
		return nextState == CarState.RUNNING || nextState == CarState.SINKED;
	}

}
