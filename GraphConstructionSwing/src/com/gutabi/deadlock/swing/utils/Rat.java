package com.gutabi.deadlock.swing.utils;

public class Rat {
	
	public static final Rat ZERO = new Rat(0, 1);
	public static final Rat ONE = new Rat(1, 1);
	
//	private final int d;
//	private final int n;
	
//	private final int dSimple;
//	private final int nSimple;
	
	/*
	 * computed value, only used for printing/debugging
	 */
	private final float val;
	
	private final int hash;
	private final String s;
	
//	public Rat(int n) {
//		this(n, 1);
//	}
	
	public Rat(int n, int d) {
//		this.d = d;
//		this.n = n;
		if (d == 0) {
			throw new IllegalArgumentException("d is 0");
		}
		
		int nn = n;
		int dd = d;
		
//		if (Math.abs(nn) >= 65536) {
//			throw new IllegalArgumentException("nn");
//		}
//		if (Math.abs(dd) >= 65536) {
//			throw new IllegalArgumentException("dd");
//		}
		
//		while (Math.abs(nn) >= 65536 || Math.abs(dd) >= 65536) {
//			if (Math.abs(nn) > 1) {
//				nn = nn / 2;
//			}
//			if (Math.abs(dd) > 1) {
//				dd = dd / 2;
//			}
//		}
		
		if (dd == 0) {
			throw new IllegalArgumentException("d is 0");
		}
		
		int g = gcd(nn, dd);
		//assert g > 0;
		
		int sig;
		if (nn > 0) {
			if (dd > 0) {
				sig = 1;
			} else {
				sig = -1;
			}
		} else if (nn == 0) {
			sig = 0;
		} else {
			if (dd > 0) {
				sig = -1;
			} else {
				sig = 1;
			}
		}
		
		nn = sig * Math.abs(nn / g);
		dd = Math.abs(dd / g);
		
		val = ((float)nn) / ((float)dd);
		
		int h = 17;
		h = 37 * h + ((int)val);
		hash = h;
		
		s = Double.toString(val);
		
	}
	
	private Rat(float val) {
		
		this.val = val;
		
		int h = 17;
		h = 37 * h + ((int)val);
		hash = h;
		
		s = Double.toString(val);
		
	}
	
	public double getN() {
		return val;
	}
	
	public int getD() {
		return 1;
	}
	
	public double getVal() {
		return val;
	}
	
	public boolean isZero() {
		return (val == 0.0);
	}
	
	public boolean isOne() {
		return (val == 1.0);
	}
	
	public boolean isPositive() {
		return val > 0.0;
	}
	
	public boolean isNegative() {
		return val < 0.0;
	}
	
	public boolean isLessThan(Rat a) {
		return this.minus(a).isNegative();
	}
	
	public boolean isGreaterThan(Rat a) {
		return this.minus(a).isPositive();
	}
	
	public boolean isLessThanOrEquals(Rat a) {
		Rat b = this.minus(a);
		return b.isNegative() || b.isZero();
	}
	
	public boolean isGreaterThanOrEquals(Rat a) {
		Rat b = this.minus(a);
		return b.isPositive() || b.isZero();
	}
	
	public Rat minus(Rat a) {
//		int nn = (nSimple, a.dSimple) - Rat.safeMultiply(dSimple, a.nSimple);
//        int dd = dSimple * a.dSimple;
//        return new Rat(nn, dd);
		return new Rat(val - a.val);
	}
	
    public Rat plus(Rat a) {
//    	if (dSimple == a.dSimple) {
//    		int nn = Rat.safeAdd(nSimple, a.nSimple);
//            int dd = dSimple;
//            return new Rat(nn, dd);
//    	} else {
//    		int nn = Rat.safeAdd(Rat.safeMultiply(nSimple, a.dSimple), Rat.safeMultiply(dSimple, a.nSimple));
//            int dd = Rat.safeMultiply(dSimple, a.dSimple);
//            return new Rat(nn, dd);
//    	}
    	return new Rat(val + a.val);
    }
	
    public Rat times(Rat a) {
        //return new Rat(Rat.safeMultiply(nSimple, a.nSimple), Rat.safeMultiply(dSimple, a.dSimple));
    	return new Rat(val * a.val);
    }
    
    public Rat reciprocal() {
    	//return new Rat(dSimple, nSimple);
    	return new Rat((float) (1.0 / val));
    }
    
    public Rat over(Rat a) {
    	//return times(a.reciprocal());
    	return new Rat(val / a.val);
    }
    
	@Override
	public boolean equals(Object r) {
		if (this == r) {
			return true;
		} else if (!(r instanceof Rat)) {
			return false;
		} else {
			//return (dSimple == ((Rat)r).dSimple) && (nSimple == ((Rat)r).nSimple);
			return Math.abs(val - ((Rat)r).val) < 0.0001;
		}
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	private static int gcd(int m, int n) {
        if (0 == n) {
        	return m;
        } else  {
        	return gcd(n, m % n);
        }
    }
	
	static final int safeAdd(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE - right : left < Integer.MIN_VALUE - right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left + right;
	}
	
	static final int safeSubtract(int left, int right) throws ArithmeticException {
		if (right > 0 ? left < Integer.MIN_VALUE + right : left > Integer.MAX_VALUE + right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left - right;
	}
	
	static final int safeMultiply(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE/right || left < Integer.MIN_VALUE/right : (right < -1 ? left > Integer.MIN_VALUE/right || left < Integer.MAX_VALUE/right : right == -1 && left == Integer.MIN_VALUE) ) {
			throw new ArithmeticException("Integer overflow");
		}
		return left * right;
	}
	
	static final int safeDivide(int left, int right) throws ArithmeticException {
		if ((left == Integer.MIN_VALUE) && (right == -1)) {
			throw new ArithmeticException("Integer overflow");
		}
		return left / right;
	}
	
	static final int safeNegate(int a) throws ArithmeticException {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return -a;
	}
	
	static final int safeAbs(int a) throws ArithmeticException {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return Math.abs(a);
	}
	
}
