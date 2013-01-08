package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public class Geom {
	
	public static Point times(double[][] m, Point v) {
		return new Point(m[0][0] * v.x + m[0][1] * v.y, m[1][0] * v.x + m[1][1] * v.y);
	}
	
	/**
	 * 
	 * return -1 if p is to the left of <a, b>
	 * return 1 if p is to the right
	 * return 0 if p is on the line
	 */
	public static int halfPlane(Point p, Point p1, Point p2) {
		double a = -(p2.y - p1.y);
		double b = p2.x - p1.x;
		double c = -(a * p1.x + b * p1.y);
		double d = a * p.x + b * p.y + c;
		if (DMath.equals(d, 0.0)) {
			return 0;
		} else if (d > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public static Point localToWorld(Point l, double[][] m, Point t) {
		return times(m, l).plus(t);
	}
	
	public static Quad localToWorld(Quad q, double[][] m, Point t) {
		Point w0 = times(m, q.p0).plus(t);
		Point w1 = times(m, q.p1).plus(t);
		Point w2 = times(m, q.p2).plus(t);
		Point w3 = times(m, q.p3).plus(t);
		return new Quad(q.parent, w0, w1, w2, w3);
	}
	
	public static void rotationMatrix(double a, double[][] out) {
		out[0][0] = Math.cos(a);
		out[0][1] = -Math.sin(a);
		out[1][0] = Math.sin(a);
		out[1][1] = Math.cos(a);
	}
	
}
