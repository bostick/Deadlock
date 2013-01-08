package com.gutabi.deadlock.world.graph;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.math.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity entity;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, Entity e) {
		this.p = p;
		this.entity = e;
	}
	
	public abstract boolean isBound();
	
	public abstract GraphPosition travelToNeighbor(GraphPosition p, double distance);
	
}
