package com.brentonbostick.capsloc.world.cars;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.world.graph.GraphPosition;
import com.brentonbostick.capsloc.world.graph.GraphPositionPath;
import com.brentonbostick.capsloc.world.graph.GraphPositionPathPosition;
import com.brentonbostick.capsloc.world.graph.Side;

public abstract class Driver {
	
	public final Car c;
	
	public GraphPosition startGP;
	public Side startSide;
	
	public GraphPositionPath overallPath;
	
	public GraphPositionPathPosition overallPos;
	public GraphPositionPathPosition prevOverallPos;
	
	Point goalPoint;
	
	public GraphPositionPathPosition toolOrigOverallPos;
	public GraphPositionPathPosition toolOrigExitingVertexPos;
	public GraphPositionPathPosition toolLastExitingVertexPos;
	
	public GraphPositionPathPosition toolCoastingGoal;
	
	public Driver(Car c) {
		this.c = c;
	}
	
	public void setOverallPos(GraphPositionPathPosition pos) {
		assert pos != null;
		
		prevOverallPos = overallPos;
		overallPos = pos;
	}
	
}
