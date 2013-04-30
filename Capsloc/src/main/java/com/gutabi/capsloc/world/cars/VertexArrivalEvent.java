package com.gutabi.capsloc.world.cars;

import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.graph.RoadPosition;
import com.gutabi.capsloc.world.graph.StopSign;

/**
 * arriving at a vertex
 *
 */
public class VertexArrivalEvent extends VertexEvent {
	
	public final Driver d;
	public final GraphPositionPathPosition entrancePosition;
	public final StopSign sign;
	
	private int hash;
	
	public VertexArrivalEvent(Driver d, GraphPositionPathPosition entrancePosition) {
		super(entrancePosition.nextBound());
		this.d = d;
		this.entrancePosition = entrancePosition;
		
		sign = ((RoadPosition)entrancePosition.gp).sign;
		
	}
	
	public String toString() {
		return "VertexArrivalEvent[driver = " + d + ", v = " + v + "]";
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + d.hashCode();
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
