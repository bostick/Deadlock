package com.gutabi.deadlock.world.car;

import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;

public class Driver {
	
	public final Car c;
	
	public GraphPositionPath overallPath;
	
	public GraphPositionPathPosition overallPos;
	
	public Driver(Car c) {
		this.c = c;
	}
	
	public void computeStartingProperties() {
		
		computePath();
		
		overallPos = overallPath.startingPos;
	}
	
	private void computePath() {
		
		overallPath = c.source.getShortestPathToMatch();
		
		overallPath.currentDrivers.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.add(this);
		}
		
	}
	
	public void computeDynamicPropertiesMoving() {
		overallPos = overallPath.findClosestGraphPositionPathPosition(c.p, overallPos);
	}
	
	public void clear() {
		
		overallPos = null;
		
		overallPath.currentDrivers.remove(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.remove(this);
		}
		
	}
	
}
