package com.gutabi.deadlock.core;

import java.util.Comparator;

public class DMath {
	
	public static boolean doubleEquals(double a, double b) {
		return doubleEquals(a, b, 1.0E-8);
	}
	
	public static boolean doubleEquals(double a, double b, double delta) {
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
		return Math.abs(a - b) < delta;
		
		/*
		 * TestDMath.test4 has 2 doubles that are not equal when cast to floats, but that
		 * are equal using Math.abs() < 1.0E-9. They are on a rounding edge and casting to floats
		 * casts them to 2 different numbers, even though they are closer than floating point precision
		 */
		//return ((float)a) == ((float)b);
	}
	
	static Comparator<Double> COMPARATOR = new DoubleComparator();
	
	static class DoubleComparator implements Comparator<Double> {

		public int compare(Double a, Double b) {
			if (doubleEquals(a, b)) {
				return 0;
			}
			if (a < b) {
				return -1;
			} else {
				return 1;
			}
		}
		
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
	
	public static boolean rangesOverlap(double a, double b, double c, double d) {
		
		double aa;
		double bb;
		double cc;
		double dd;
		
		if (a < b) {
			aa = a;
			bb = b;
		} else {
			aa = b;
			bb = a;
		}
		
		if (c < d) {
			cc = c;
			dd = d;
		} else {
			cc = d;
			dd = c;
		}
		
		return (DMath.doubleEquals(aa, dd) || aa < dd) && (DMath.doubleEquals(cc , bb) || cc < bb);
		
	}
	
}
