package com.brentonbostick.capsloc.world.cars;

import com.brentonbostick.capsloc.world.graph.GraphPositionPathPosition;
import com.brentonbostick.capsloc.world.graph.Vertex;
import com.brentonbostick.capsloc.world.graph.VertexPosition;

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
