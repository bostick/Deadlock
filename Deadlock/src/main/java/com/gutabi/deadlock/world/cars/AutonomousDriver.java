package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.graph.GraphPositionPath;

public final class AutonomousDriver extends Driver {
	
	public AutonomousDriver(AutonomousCar c) {
		super(c);
	}
	
	public void computeStartingProperties() {
		
		overallPath = ((AutonomousCar)c).source.getShortestPathToMatch();
		
		overallPath.currentDrivers.add(this);
		for (GraphPositionPath path : overallPath.sharedEdgesMap.keySet()) {
			path.currentDrivers.add(this);
		}
		
		startGP = overallPath.startingPos.getGraphPosition();
		
		overallPos = overallPath.findClosestGraphPositionPathPosition(startGP);
		
		vertexDepartureQueue.add(new VertexSpawnEvent(overallPos));
	}
	
}
