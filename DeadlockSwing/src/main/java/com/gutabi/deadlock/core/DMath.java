package com.gutabi.deadlock.core;

public class DMath {
	
	public static boolean doubleEquals(double a, double b) {
		/*
		 * 1.0E-12 seems to be fine for the math we do here
		 * 1.0E-13 gives StackOverflowErrors when it is expecting some points to be equal
		 * 
		 * TestDMath.test3 is a case that came up that shows 1.0E-12 is insufficient
		 * 
		 * TestDragging.testBug16 shows that 1.0E-11 is insufficient
		 * 
		 * A time of 5.00000000012918 should be equal to 5, so 1.0E-11 is insufficient
		 */
		return Math.abs(a - b) < 1.0E-9;
		
		//return ((float)a) == ((float)b);
	}
	
}
