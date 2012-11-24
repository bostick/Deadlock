package com.gutabi.deadlock.model.event;

import com.gutabi.deadlock.model.Car;

public class CarProximityEvent extends DrivingEvent {
	
	public final Car c;
	public final Car otherCar;
	
	public CarProximityEvent(Car c, Car otherCar) {
		this.c = c;
		this.otherCar = otherCar;
	}
	
	public String toString() {
		return "CarProximityEvent[car = " + c + ", otherCar = " + otherCar + "]";
	}
	
}
