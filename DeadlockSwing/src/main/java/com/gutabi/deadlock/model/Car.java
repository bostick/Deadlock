package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Vertex;

public class Car {
	
//	public Edge curEdge;
//	public int curIndex;
//	public double curParam;
	
	public Vertex curVertex;
	public boolean travelingForward;
	
	//public Point curPos;
	private Position pos;
	
	public Position getPosition() {
		
//		if (curPos != null) {
//			return curPos;
//		}
//		
//		Point a = curEdge.getPoint(curIndex);
//		Point b = curEdge.getPoint(curIndex+1);
//		
//		Point pos = Point.point(a, b, curParam);
		
		return pos;
	}
	
	public void setPosition(Position pos) {
		this.pos = pos;
	}
	
	//@Override
//	public String toString() {
//		return "car: " + "curIndex: " + curIndex + " curParam: " + curParam;
//	}
	
	public Car copy() {
		Car c = new Car();
		c.pos = getPosition();
		return c;
	}
	
}
