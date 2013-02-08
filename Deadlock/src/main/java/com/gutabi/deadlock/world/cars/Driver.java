package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPath;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;

public abstract class Driver {
	
	public final Car c;
	
	public GraphPosition startGP;
//	public Side overallSide;
	
	public GraphPositionPath overallPath;
	public GraphPositionPathPosition overallPos;
	
//	public int directionInTrack;
	
	public Driver(Car c) {
		this.c = c;
	}
	
//	public abstract void computeStartingProperties();
	
}
