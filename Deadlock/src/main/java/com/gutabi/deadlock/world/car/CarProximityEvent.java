package com.gutabi.deadlock.world.car;


public class CarProximityEvent extends DrivingEvent {
	
	public final Driver d;
	public final Driver otherDriver;
	
	private int hash;
	
	public CarProximityEvent(Driver d, Driver otherDriver) {
		this.d = d;
		this.otherDriver = otherDriver;
	}
	
	public String toString() {
		return "CarProximityEvent[driver = " + d + ", otherDriver = " + otherDriver + "]";
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + d.hashCode();
			h = 37 * h + otherDriver.hashCode();
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
			return d == b.d && otherDriver == b.otherDriver;
		}
	}
}