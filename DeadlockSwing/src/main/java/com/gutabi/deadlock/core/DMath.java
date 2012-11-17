package com.gutabi.deadlock.core;

import java.util.Comparator;

public class DMath {
	
//	public static boolean equals(double a, double b) {
//		return equals(a, b, 1.0E-8);
//	}
	
	public static boolean equals(double a, double b) {
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
		return Math.abs(a - b) < 1.0E-7;
		
		/*
		 * TestDMath.test4 has 2 doubles that are not equal when cast to floats, but that
		 * are equal using Math.abs() < 1.0E-9. They are on a rounding edge and casting to floats
		 * casts them to 2 different numbers, even though they are closer than floating point precision
		 */
		//return ((float)a) == ((float)b);
	}
	
	public static boolean lessThanEquals(double a, double b) {
		return a < b || equals(a, b);
	}
	
	public static boolean greaterThanEquals(double a, double b) {
		return a > b || equals(a, b);
	}
	
	public static boolean lessThan(double a, double b) {
		return a < b && !equals(a, b);
	}
	
	public static boolean greaterThan(double a, double b) {
		return a > b && !equals(a, b);
	}
	
	public static Comparator<Double> COMPARATOR = new DoubleComparator();
	
	static class DoubleComparator implements Comparator<Double> {

		public int compare(Double a, Double b) {
			if (DMath.equals(a, b)) {
				return 0;
			}
			if (a < b) {
				return -1;
			} else {
				return 1;
			}
		}
		
	}
	
	/**
	 * clip x between 0 and 1
	 */
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
		
		return lessThanEquals(aa, dd) && lessThanEquals(cc , bb);
	}
	
	public static double sgn(double x) {
		if (DMath.equals(x, 0.0)) {
			return 1;
		} else if (x < 0.0) {
			return -1;
		} else {
			return 1;
		}
	}
	
	
	public static double quadraticSolve(double a, double b, double c, double[] roots) {
		
		double discriminant = b*b - 4*a*c;
		
		if (DMath.equals(discriminant, 0.0)) {
			
			roots[0] = -b / (2*a);
			
			return discriminant;
			
		} else if (discriminant > 0) {
			
			roots[0] = (-b + Math.sqrt(discriminant)) / (2 * a);
			roots[1] = (-b - Math.sqrt(discriminant)) / (2 * a);
			
			return discriminant;
			
		} else {
			
			return discriminant;
			
		}
	}
}
