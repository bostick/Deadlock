package com.gutabi.deadlock.world.car;

import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.StopSign;

/**
 * arriving at a vertex
 *
 */
public class VertexArrivalEvent extends VertexEvent {
	
	public final Car c;
	public final GraphPositionPathPosition entrancePosition;
	public final StopSign sign;
	
	private int hash;
	
	public VertexArrivalEvent(Car c, GraphPositionPathPosition entrancePosition) {
		super(entrancePosition.nextBound());
		this.c = c;
		this.entrancePosition = entrancePosition;
		
		sign = ((RoadPosition)entrancePosition.getGraphPosition()).sign;
		
	}
	
	public String toString() {
		return "VertexArrivalEvent[car = " + c + ", v = " + v + "]";
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + c.hashCode();
			h = 37 * h + entrancePosition.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexArrivalEvent)) {
			return false;
		} else {
			VertexArrivalEvent b = (VertexArrivalEvent)o;
			return v == b.v;
		}
	}
	
}
