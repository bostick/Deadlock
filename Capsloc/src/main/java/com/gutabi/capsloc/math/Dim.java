package com.gutabi.capsloc.math;

import java.io.Serializable;

public class Dim implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final double width;
	public final double height;
	
	public Dim(double width, double height) {
		assert DMath.greaterThanEquals(width, 0.0);
		assert DMath.greaterThanEquals(height, 0.0);
		this.width = width;
		this.height = height;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Dim)) {
			return false;
		} else {
			Dim b = (Dim)o;
			return DMath.equals(width, b.width) && DMath.equals(height, b.height);
		}
	}
	
	public Dim multiply(double scale) {
		return new Dim(width * scale, height * scale);
	}
	
}
