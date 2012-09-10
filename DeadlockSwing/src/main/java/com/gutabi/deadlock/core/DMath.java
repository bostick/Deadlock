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
		return Math.abs(a - b) < 1.0E-6;
		
		/*
		 * TestDMath.test4 has 2 doubles that are not equal when cast to floats, but that
		 * are equal using Math.abs() < 1.0E-9. They are on a rounding edge and casting to floats
		 * casts them to 2 different numbers, even though they are closer than floating point precision
		 */
		//return ((float)a) == ((float)b);
	}
	
	public static double clip(double x) {
		if (x < 0) {
			return 0;
		} else if (x > 1) {
			return 1;
		} else {
			return x;
		}
	}
	
}
