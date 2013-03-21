package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;

public final class InteractiveDriver extends Driver {
	
	Point goalPoint;
	
	public InteractiveDriver(InteractiveCar c) {
		super(c);
	}
	
	public void preStep(double t) {
		
		switch (c.state) {
		case IDLE:
		case DRAGGING:
			break;
		case COASTING:
			
			RoadPosition rpos = (RoadPosition)overallPos.getGraphPosition();
			double toStart = rpos.lengthToStartOfRoad;
			double toEnd = rpos.lengthToEndOfRoad;
			
			if (toStart < toEnd) {
				
				GraphPositionPathPosition next = overallPos.prevBound();
						
				goalPoint = next.p;
				
			} else {
				
				GraphPositionPathPosition next = overallPos.nextBound();
						
				goalPoint = next.p;
				
			}
			
			break;
		default:
			assert false;
			break;
		}
	}
	
	public void computeDynamicPropertiesMoving() {
		
		switch (c.state) {
		case COASTING:
			
			overallPos = overallPath.generalSearch(c.center, overallPos, c.CAR_LENGTH);
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
}
