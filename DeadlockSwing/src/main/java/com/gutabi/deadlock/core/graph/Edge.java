package com.gutabi.deadlock.core.graph;

import com.gutabi.deadlock.core.Entity;

public interface Edge extends Entity {
	
	GraphPosition travelFromConnectedVertex(Vertex v, double dist);
	
	double getTotalLength(Vertex a, Vertex b);
	
}
