package com.gutabi.deadlock.model.event;

import com.gutabi.deadlock.model.Car;

public class CarProximityEvent extends DrivingEvent {
	
	public final Car c;
	public final Car otherCar;
//	public final GraphPositionPathPosition gppp;
	
	public CarProximityEvent(Car c, Car otherCar) {
//		super(gppp);
		this.c = c;
		this.otherCar = otherCar;
//		this.gppp = gppp;
	}
	
//	public boolean equals(Object o) {
//		if (this == o) {
//			return true;
//		} else if (!(o instanceof DrivingEvent)) {
//			throw new IllegalArgumentException();
//		} else if (!(o instanceof CarProximityEvent)) {
//			return false;
//		} else {
//			CarProximityEvent b = (CarProximityEvent)o;
//			return gppp.equals(b.gppp);
//		}
//	}
	
	public String toString() {
		return "CarProximityEvent[car = " + c + ", otherCar = " + otherCar + "]";
	}
	
//	public GraphPositionPathPosition getGraphPositionPathPosition() {
//		return gppp;
//	}
	
}
