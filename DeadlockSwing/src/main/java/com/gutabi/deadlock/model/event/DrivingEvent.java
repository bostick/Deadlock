package com.gutabi.deadlock.model.event;

import java.util.Comparator;

import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;

public abstract class DrivingEvent {
	
	public final GraphPositionPathPosition gppp;
	
	public DrivingEvent(GraphPositionPathPosition gppp) {
		this.gppp = gppp;
	}
	
	public GraphPositionPathPosition getGraphPositionPathPosition() {
		return gppp;
	}
	
	public static Comparator<DrivingEvent> COMPARATOR = new DrivingEventComparator();
	
	static class DrivingEventComparator implements Comparator<DrivingEvent> {

		public int compare(DrivingEvent a, DrivingEvent b) {
			return GraphPositionPathPosition.COMPARATOR.compare(a.gppp, b.gppp);
		}
		
	}
}
