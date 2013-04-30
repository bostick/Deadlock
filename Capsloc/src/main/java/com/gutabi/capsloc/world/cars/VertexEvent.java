package com.gutabi.capsloc.world.cars;

import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.graph.Vertex;
import com.gutabi.capsloc.world.graph.VertexPosition;

public abstract class VertexEvent extends DrivingEvent {
	
	public final GraphPositionPathPosition vertexPosition;
	public final Vertex v;
	public final GraphPositionPathPosition carPastExitPosition;
	
	public VertexEvent(GraphPositionPathPosition vertexPosition) {
		this.vertexPosition = vertexPosition;
		
		v = ((VertexPosition)vertexPosition.gp).v;
		
		if (!vertexPosition.isEndOfPath()) {
			carPastExitPosition = vertexPosition.nextBound();
		} else {
			carPastExitPosition = null;
		}
		
		
	}
	
}
