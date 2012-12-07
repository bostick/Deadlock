package com.gutabi.deadlock.model.car;


public class CarProximityEvent extends DrivingEvent {
	
	public final Car c;
	public final Car otherCar;
	
	private int hash;
	
	public CarProximityEvent(Car c, Car otherCar) {
		this.c = c;
		this.otherCar = otherCar;
		
//		if (otherCar.curDrivingEvent != null && (otherCar.curDrivingEvent instanceof CarProximityEvent && ((CarProximityEvent)otherCar.curDrivingEvent).otherCar == c)) {
//			c.deadlocked = true;
//			otherCar.deadlocked = true;
//		}
		
	}
	
	public String toString() {
		return "CarProximityEvent[car = " + c + ", otherCar = " + otherCar + "]";
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + c.hashCode();
			h = 37 * h + otherCar.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof CarProximityEvent)) {
			return false;
		} else {
			CarProximityEvent b = (CarProximityEvent)o;
			return c == b.c && otherCar == b.otherCar;
		}
	}
}
