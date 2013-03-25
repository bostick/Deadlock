package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;

public abstract class Driver {
	
	public final Car c;
	
	public GraphPosition startGP;
	
	public GraphPositionPath overallPath;
	
	public GraphPositionPathPosition overallPos;
	public GraphPositionPathPosition prevOverallPos;
	
	Point goalPoint;
	
	public GraphPositionPathPosition toolOrigOverallPos;
	public GraphPositionPathPosition toolOrigExitingVertexPos;
	
	public GraphPositionPathPosition toolCoastingGoal;
	
	public Driver(Car c) {
		this.c = c;
	}
	
	public void setOverallPos(GraphPositionPathPosition pos) {
		assert pos != null;
		
//		System.out.println(pos.getGraphPosition());
		
		prevOverallPos = overallPos;
		overallPos = pos;
	}
	
}
