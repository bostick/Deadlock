package com.gutabi.deadlock.model.event;

import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.core.graph.VertexPosition;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.StopSign;

/**
 * arriving at a vertex
 *
 */
public class VertexEvent {
	
	public final StopSign sign;
	public final Vertex v;
	
	public final GraphPositionPathPosition borderPosition;
	public final GraphPositionPathPosition borderMatchingPosition;
	
	public VertexEvent(GraphPositionPathPosition borderPosition) {
		
		this.borderPosition = borderPosition;
		
		GraphPositionPathPosition vertexPosition = borderPosition.nextBound();
		
		assert vertexPosition.gpos instanceof VertexPosition;
		if (!vertexPosition.isEndOfPath()) {
			borderMatchingPosition = vertexPosition.nextBound().travel(Car.CAR_LENGTH / 2);
		} else {
			borderMatchingPosition = null;
		}
		
		sign = ((RoadPosition)borderPosition.gpos).sign;
		
		v = ((VertexPosition)vertexPosition.gpos).v;
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexEvent)) {
			return false;
		} else {
			VertexEvent b = (VertexEvent)o;
			return borderPosition.equals(b.borderPosition);
		}
	}
	
}
