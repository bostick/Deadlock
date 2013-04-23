package com.gutabi.deadlock.math;

import java.util.Comparator;

public class DMath {
	
	/**
	 * if the angle is within RIGHT_ANGLE_TOLERANCE of a right angle,
	 * then it is treated as a right angle
	 */
	public static final double RIGHT_ANGLE_TOLERANCE = 1.0E-4;
	
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
		return (a - b) < 1.0E-7;
	}
	
	public static boolean greaterThanEquals(double a, double b) {
		return (b - a) < 1.0E-7;
	}
	
	public static boolean lessThan(double a, double b) {
		return (b - a) >= 1.0E-7;
	}
	
	public static boolean greaterThan(double a, double b) {
		return (a - b) >= 1.0E-7;
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
	
	public static boolean rangesOverlap(double[] r0, double[] r1) {
		
		double a = r0[0];
		double b = r0[1];
		double c = r1[0];
		double d = r1[1];
		
		if (a < b) {
			if (c < d) {
				return lessThanEquals(a, d) && lessThanEquals(c, b);
			} else {
				return lessThanEquals(a, c) && lessThanEquals(d, b);
			}
		} else {
			if (c < d) {
				return lessThanEquals(b, d) && lessThanEquals(c, a);
			} else {
				return lessThanEquals(b, c) && lessThanEquals(d, a);
			}
		}
	}
	
	public static boolean rangesOverlapArea(double[] r0, double[] r1) {
		
		double a = r0[0];
		double b = r0[1];
		double c = r1[0];
		double d = r1[1];
		
		if (a < b) {
			if (c < d) {
				return lessThan(a, d) && lessThan(c, b);
			} else {
				return lessThan(a, c) && lessThan(d, b);
			}
		} else {
			if (c < d) {
				return lessThan(b, d) && lessThan(c, a);
			} else {
				return lessThan(b, c) && lessThan(d, a);
			}
		}
	}
	
	public static boolean rangesOverlapArea(double a, double b, double c, double d) {
		
		if (a < b) {
			if (c < d) {
				return lessThan(a, d) && lessThan(c, b);
			} else {
				return lessThan(a, c) && lessThan(d, b);
			}
		} else {
			if (c < d) {
				return lessThan(b, d) && lessThan(c, a);
			} else {
				return lessThan(b, c) && lessThan(d, a);
			}
		}
	}
	
	public static boolean rangesTouch(double[] r0, double[] r1) {
		
		double a = r0[0];
		double b = r0[1];
		double c = r1[0];
		double d = r1[1];
		
		if (a < b) {
			if (c < d) {
				return equals(a, d) && equals(c, b);
			} else {
				return equals(a, c) && equals(d, b);
			}
		} else {
			if (c < d) {
				return equals(b, d) && equals(c, a);
			} else {
				return equals(b, c) && equals(d, a);
			}
		}
	}
	
	public static boolean rangeContains(double[] r0, double[] r1) {
		
		double a = r0[0];
		double b = r0[1];
		double c = r1[0];
		double d = r1[1];
		
		if (a < b) {
			if (c < d) {
				return lessThanEquals(a, c) && lessThanEquals(d, b);
			} else {
				return lessThanEquals(a, d) && lessThanEquals(c, b);
			}
		} else {
			if (c < d) {
				return lessThanEquals(b, c) && lessThanEquals(d, a);
			} else {
				return lessThanEquals(b, d) && lessThanEquals(c, a);
			}
		}
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
	
	public static double lerp(double a, double b, double param) {
		
		if (DMath.equals(param, 0.0)) {
			
			return a;
			
		} else if (DMath.equals(param, 1.0)) {
			
			return b;
			
		} else {
			return a + param * (b - a);
		}
		
	}
	
	public static double tryAdjustToRightAngle(double preA) {
		
		double adjA = preA;
		while (DMath.greaterThanEquals(adjA, 2*Math.PI)) {
			adjA -= 2*Math.PI;
		}
		while (DMath.lessThan(adjA, 0.0)) {
			adjA += 2*Math.PI;
		}
		
		if (Math.abs(adjA - 0.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return 0.0 * Math.PI;
		} else if (Math.abs(adjA - 0.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return 0.5 * Math.PI;
		} else if (Math.abs(adjA - 1.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return 1.0 * Math.PI;
		} else if (Math.abs(adjA - 1.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return 1.5 * Math.PI;
		} else {
			return adjA;
		}
		
	}
	
	public static double tryAdjustToReducedRightAngle(double preA) {
		
		double adjA = preA;
		while (DMath.greaterThanEquals(adjA, Math.PI)) {
			adjA -= Math.PI;
		}
		while (DMath.lessThan(adjA, 0.0)) {
			adjA += Math.PI;
		}
		
		if (Math.abs(adjA - 0.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return 0.0 * Math.PI;
		} else if (Math.abs(adjA - 0.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return 0.5 * Math.PI;
		} else {
			return adjA;
		}
		
	}
	
	public static boolean isRightAngle(double pre) {
		
		double adjA = pre;
		while (DMath.greaterThanEquals(adjA, 2*Math.PI)) {
			adjA -= 2*Math.PI;
		}
		while (DMath.lessThan(adjA, 0.0)) {
			adjA += 2*Math.PI;
		}
		
		if (Math.abs(adjA - 0.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return true;
		} else if (Math.abs(adjA - 0.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return true;
		} else if (Math.abs(adjA - 1.0 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return true;
		} else if (Math.abs(adjA - 1.5 * Math.PI) < DMath.RIGHT_ANGLE_TOLERANCE) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean anglesCompatible(double a, double b) {
		double aa = tryAdjustToReducedRightAngle(a);
		double bb = tryAdjustToReducedRightAngle(b);
		return DMath.equals(aa, bb);
	}
	
}
