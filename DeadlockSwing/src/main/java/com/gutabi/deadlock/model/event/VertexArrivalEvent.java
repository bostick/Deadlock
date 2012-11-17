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
public class VertexArrivalEvent extends DrivingEvent {
	
	public final StopSign sign;
	public final Vertex v;
	
	public final GraphPositionPathPosition borderMatchingPosition;
	
	public VertexArrivalEvent(GraphPositionPathPosition gppp) {
		super(gppp);
		
		GraphPositionPathPosition vertexPosition = gppp.nextBound();
		
		assert vertexPosition.gpos instanceof VertexPosition;
		if (!vertexPosition.isEndOfPath()) {
			borderMatchingPosition = vertexPosition.nextBound().travel(Car.CAR_LENGTH / 2);
		} else {
			borderMatchingPosition = null;
		}
		
		sign = ((RoadPosition)gppp.gpos).sign;
		
		v = ((VertexPosition)vertexPosition.gpos).v;
		
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof DrivingEvent)) {
			throw new IllegalArgumentException();
		} else if (!(o instanceof VertexArrivalEvent)) {
			return false;
		} else {
			VertexArrivalEvent b = (VertexArrivalEvent)o;
			return v == b.v;
		}
	}
	
}
