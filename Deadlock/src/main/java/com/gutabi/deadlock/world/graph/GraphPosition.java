package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.math.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity entity;
	
	public GraphPosition(Point p, Entity e) {
		this.p = p;
		this.entity = e;
	}
	
	public abstract boolean isBound();
	
	public abstract GraphPosition approachNeighbor(GraphPosition p, double distance);
	
}
