package com.brentonbostick.capsloc.world.cars;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.world.graph.GraphPosition;
import com.brentonbostick.capsloc.world.graph.Side;
import com.brentonbostick.capsloc.world.graph.gpp.GraphPositionPath;
import com.brentonbostick.capsloc.world.graph.gpp.MutableGPPAccumulator;
import com.brentonbostick.capsloc.world.graph.gpp.MutableGPPP;

public abstract class Driver {
	
	public final Car c;
	
	public GraphPosition startGP;
	public Side startSide;
	
	public GraphPositionPath overallPath;
	
	public MutableGPPP overallPos = new MutableGPPP();
	public MutableGPPAccumulator acc = new MutableGPPAccumulator();
	final public MutableGPPP prevOverallPos = new MutableGPPP();
	
	Point goalPoint;
	
	final public MutableGPPP toolOrigOverallPos = new MutableGPPP();
	public MutableGPPP toolOrigExitingVertexPos = new MutableGPPP();
	final public MutableGPPP toolLastExitingVertexPos = new MutableGPPP();
	
	public MutableGPPP toolCoastingGoal = new MutableGPPP();
	
	public Driver(Car c) {
		this.c = c;
	}
	
//	public void setOverallPos(GraphPositionPathPosition pos) {
//		assert pos != null;
//		
//		prevOverallPos.set(overallPos);
//		overallPos.set(pos);
//	}
	
}
