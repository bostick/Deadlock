package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Position;

public class Car {
	
	private CarState state;
	
	private Position pos;
	
	public List<Position> futurePath = new ArrayList<Position>();
	public CarState futureState;
	
	public Position getPosition() {
		return pos;
	}
	
	public void setPosition(Position pos) {
		this.pos = pos;
	}
	
	public Position getLastFuturePosition() {
		return futurePath.get(futurePath.size()-1);
	}
	
	public CarState getState() {
		return state;
	}
	
	public void setState(CarState state) {
		this.state = state;
	}
	
	public Car copy() {
		Car c = new Car();
		c.pos = getPosition();
		return c;
	}
	
}
