package com.gutabi.deadlock.world.car;

import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.Vertex;
import com.gutabi.deadlock.world.graph.VertexPosition;

public abstract class VertexEvent extends DrivingEvent {
	
	public final GraphPositionPathPosition vertexPosition;
	public final Vertex v;
	public final GraphPositionPathPosition carPastExitPosition;
	
	public VertexEvent(GraphPositionPathPosition vertexPosition) {
		this.vertexPosition = vertexPosition;
		
//		assert vertexPosition.gpos instanceof VertexPosition;
		
		v = ((VertexPosition)vertexPosition.getGraphPosition()).v;
		
		if (!vertexPosition.isEndOfPath()) {
			carPastExitPosition = vertexPosition.nextBound().travel(Car.CAR_LENGTH/2);
		} else {
			carPastExitPosition = null;
		}
		
		
	}
	
}
