package com.gutabi.deadlock.core.model;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;

public class Car {
	
	public Edge curEdge;
	public int curIndex;
	public double curParam;
	
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
	
	public Car copy() {
		
		Car c = new Car();
		c.curPos = getPosition();
		return c;
	}
	
}
