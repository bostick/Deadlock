package com.gutabi.deadlock.core.geom;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;

public class Rect {
	
	public final Point ul;
	public final Dim dim;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	private final double brX;
	private final double brY;
	
	public Rect(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		assert width >= 0;
		assert height >= 0;
		
		ul = new Point(x, y);
		dim = new Dim(width, height);
		
		brX = x + width;
		brY = y + height;
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + width + ", " + height + "]";
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Rect)) {
			return false;
		} else {
			Rect b = (Rect)o;
			return DMath.equals(x, b.x) && DMath.equals(y, b.y) && DMath.equals(width, b.width) && DMath.equals(height, b.height);
		}
	}
	
	public boolean hitTest(Point p) {
		if (DMath.lessThanEquals(x, p.x) && DMath.lessThanEquals(p.x, x+width) &&
				DMath.lessThanEquals(y, p.y) && DMath.lessThanEquals(p.y, y+height)) {
			
			return true;
			
		} else {
			return false;
		}
	}
	
	public static final Rect union(Rect a, Rect b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		
		boolean isA = true;
		boolean isB = true;
		
		double ulX = a.ul.x;
		double ulY = a.ul.y;
		double brX = a.brX;
		double brY = a.brY;
		
		if (b.ul.x < ulX) {
			ulX = b.ul.x;
			isA = false;
		} else {
			isB = false;
		}
		if (b.ul.y < ulY) {
			ulY = b.ul.y;
			isA = false;
		} else {
			isB = false;
		}
		if (b.brX > brX) {
			brX = b.brX;
			isA = false;
		} else {
			isB = false;
		}
		if (b.brY > brY) {
			brY = b.brY;
			isA = false;
		} else {
			isB = false;
		}
		
		if (isA) {
			return a;
		}
		if (isB) {
			return b;
		}
		
		return new Rect(ulX, ulY, brX-ulX, brY-ulY);
	}
	
	public Rect plus(Point p) {
		return new Rect(x + p.x, y + p.y, width, height);
	}
	
}
