package com.brentonbostick.bypass.solver;

public class CarInfo {
	
	public byte o;
	public int row;
	public int col;
	public int size;
	
	public int hashCode() {
		int h = 17;
		h = 37 * h + o;
		h = 37 * h + row;
		h = 37 * h + col;
		h = 37 * h + size;
		return h;
	}
	
	public boolean equals(Object o) {
		assert false;
		return false;
	}
	
}
