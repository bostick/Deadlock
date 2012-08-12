package com.gutabi.deadlock.core;

public class DMath {
	
	public static boolean doubleEquals(double a, double b) {
		/*
		 * 1.0E-12 seems to be fine for the math we do here
		 * 1.0E-13 gives StackOverflowErrors when it is expecting some points to be equal
		 */
		return Math.abs(a - b) < 1.0E-12;
	}
	
}
