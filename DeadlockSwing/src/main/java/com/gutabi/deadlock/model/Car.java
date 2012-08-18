package com.gutabi.deadlock.model;

import java.util.List;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.Vertex;

public class Car {
	
	private CarState state;
	
	private Position pos;
	
	public boolean special;
	
	public long startingStep;
	public Vertex startingVertex;
	
	public Path futurePath;
	
	public CarState futureState;
	
	public Edge futureEdge;
	
	public double distanceToMove;
	
	public final int id;
	
	String s;
	
	public static int carCounter;
	
	public Car() {
		state = CarState.NEW;
		id = carCounter;
		carCounter++;
		
		futurePath = new Path();
		
		s = "car " + id;
	}
	
	public String toString() {
		return s;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public void setPosition(Position pos) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		this.pos = pos;
	}
	
//	public void futurePathNewEdge() {
//		if (state == CarState.CRASHED || state == CarState.SINKED) {
//			throw new IllegalArgumentException();
//		}
//		//futurePath.add(new ArrayList<Position>());
//		futurePath.newEdge();
//	}
	
	public void futurePathAdd(Position pos) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		futurePath.add(pos);
	}
	
	/**
	 * Remove the rest of the edge after pIndex (and all other edges after eIndex) and replace the last position
	 * with pos (if they are not already equal)
	 * 
	 */
	public void futurePathCrash(Position pos, int pIndex) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		futurePath.crash(pos, pIndex);
	}
	
	public void futurePathClear() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		futurePath.clear();
		//futurePath.add(new ArrayList<Position>());
	}
	
	public List<Position> getFuturePath() {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		return futurePath.path;
	}
	
	public Position getLastFuturePosition() {
		return futurePath.getLastPosition();
	}
	
	public CarState getState() {
		return state;
	}
	
	public void setState(CarState s) {
		if (state == CarState.CRASHED || state == CarState.SINKED) {
			throw new IllegalArgumentException();
		}
		
		this.state = s;
	}
	
	public Car copy() {
		Car c = new Car();
		c.pos = getPosition();
		return c;
	}
	
//	public String toString() {
//		return "car " + pos.getPoint();
//	}
	
}
