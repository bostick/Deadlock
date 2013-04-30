package com.gutabi.capsloc.world.graph;

import com.gutabi.capsloc.Entity;
import com.gutabi.capsloc.math.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity entity;
	
	public GraphPosition(Point p, Entity e) {
		this.p = p;
		this.entity = e;
	}
	
	public abstract boolean isBound();
	
	public abstract GraphPosition approachNeighbor(GraphPosition p, double distance);
	
	/**
	 * given the current GPPP, return the combo for the GPPP that corresponds to goal
	 * 
	 * we need this since a Road may go in the opposite direction of the path, etc, and there is some logic needed
	 * for converting from gp's to gppp's
	 * 
	 * mainly the params, which may be the gp's param or may be 1-gp's param
	 * 
	 */
	public abstract double goalGPPPCombo(int curPathIndex, double curPathParam, boolean pathForward, GraphPosition goalGP, GraphPositionPath debugPath, GraphPositionPathPosition debugPos, double debugDist);
	
}
