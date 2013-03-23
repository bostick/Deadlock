package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.VertexPosition;

public final class InteractiveDriver extends Driver {
	
	public InteractiveDriver(InteractiveCar c) {
		super(c);
	}
	
	public void preStep(double t) {
		
		switch (c.state) {
		case IDLE:
		case DRAGGING:
			break;
		case COASTING_FORWARD: {
			
			GraphPosition gpos = overallPos.getGraphPosition();
			
			if (gpos instanceof RoadPosition) {
				
				goalPoint = overallPos.nextBound().p;
				
//				System.out.println("goal: " + overallPos.nextBound());
				
			} else if (gpos instanceof VertexPosition) {
				
				
				assert false;
				
				
			} else if (gpos instanceof RushHourBoardPosition) {
				
//				GraphPosition pgpos = prevOverallPos.getGraphPosition();
//				assert pgpos instanceof RoadPosition;
//				assert false;
				goalPoint = overallPos.nextBound().p;
				
			} else {
				assert false;
			}
			break;
		}
			
		case COASTING_BACKWARD: {
			
			GraphPosition gpos = overallPos.getGraphPosition();
			
			if (gpos instanceof RoadPosition) {
				
				goalPoint = overallPos.prevBound().p;
				
//				System.out.println("goal: " + overallPos.prevBound());
				
			} else if (gpos instanceof VertexPosition) {
				
				
				assert false;
				
				
			} else if (gpos instanceof RushHourBoardPosition) {
				
//				GraphPosition pgpos = prevOverallPos.getGraphPosition();
//				assert pgpos instanceof RoadPosition;
//				assert false;
				goalPoint = overallPos.prevBound().p;
				
			} else {
				assert false;
			}
			
			break;
		}
		
		default:
			assert false;
			break;
		}
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
