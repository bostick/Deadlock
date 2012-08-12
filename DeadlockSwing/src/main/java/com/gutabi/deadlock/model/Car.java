package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<Position> futurePath = new ArrayList<Position>();
	
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
	
	public Position getLastFuturePosition() {
		return futurePath.get(futurePath.size()-1);
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
