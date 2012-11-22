package com.gutabi.deadlock.model.event;

import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.model.StopSign;

/**
 * arriving at a vertex
 *
 */
public class VertexArrivalEvent extends VertexEvent {
	
	public final GraphPositionPathPosition entrancePosition;
	public final StopSign sign;
	
//	public final GraphPositionPathPosition borderMatchingPosition;
	
	private int hash;
	
	public VertexArrivalEvent(GraphPositionPathPosition entrancePosition) {
		super(entrancePosition.nextBound());
//		super(((VertexPosition)(gppp.nextBound()).gpos).v);
		
//		this.gppp = gppp;
		this.entrancePosition = entrancePosition;
		
		sign = ((RoadPosition)entrancePosition.gpos).sign;
		
	}
	
	@Override
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + entrancePosition.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o.hashCode() != hashCode()) {
			return false;
		} else if (!(o instanceof VertexArrivalEvent)) {
			return false;
		} else {
			VertexArrivalEvent b = (VertexArrivalEvent)o;
			return v == b.v;
		}
	}
	
//	public GraphPositionPathPosition getGraphPositionPathPosition() {
//		return gppp;
//	}
	
}
