package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.graph.VertexPosition;

public abstract class VertexEvent extends DrivingEvent {
	
	public final GraphPositionPathPosition vertexPosition;
	public final Vertex v;
	public final GraphPositionPathPosition carPastExitPosition;
	
	public VertexEvent(GraphPositionPathPosition vertexPosition) {
		this.vertexPosition = vertexPosition;
		
		v = ((VertexPosition)vertexPosition.getGraphPosition()).v;
		
		if (!vertexPosition.isEndOfPath()) {
			carPastExitPosition = vertexPosition.nextBound();
		} else {
			carPastExitPosition = null;
		}
		
		
	}
	
}
