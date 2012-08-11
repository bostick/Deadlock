package com.gutabi.deadlock.core.model;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;

public class Car {
	
	public Edge curEdge;
	public int curIndex;
	public double curParam;
	
	public Vertex curVertex;
	public boolean travelingForward;
	//public double distanceToMove;
	
	public DPoint curPos;
	
	public DPoint getPosition() {
		
		if (curPos != null) {
			return curPos;
		}
		
		Point a = curEdge.getPoint(curIndex);
		Point b = curEdge.getPoint(curIndex+1);
		
		DPoint pos = Point.point(a.toDPoint(), b.toDPoint(), curParam);
		
		return pos;
	}
	
	@Override
	public String toString() {
		return "car: " + "curIndex: " + curIndex + " curParam: " + curParam;
	}
	
	public Car copy() {
		
		Car c = new Car();
		c.curPos = getPosition();
		return c;
	}
	
}
