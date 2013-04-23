package com.gutabi.deadlock.geom;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.ui.paint.RenderingContext;


public class MutableAABB {
	
	public double x = Double.NaN;
	public double y = Double.NaN;
	public double width = Double.NaN;
	public double height = Double.NaN;
	
	public MutableAABB() {
		
	}
	
	public void reset() {
		x = Double.NaN;
		y = Double.NaN;
		width = Double.NaN;
		height = Double.NaN;
	}
	
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setDimension(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public void setShape(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public AABB copy() {
		return new AABB(x, y, width, height);
	}
	
	public void union(AABB a) {
		
		if (Double.isNaN(x)) {
			
			x = a.x;
			y = a.y;
			width = a.width;
			height = a.height;
			
		} else {
			
			double brx = x+width;
			double bry = y+height;
			
			if (a.x < x) {
				x = a.x;
			}
			if (a.y < y) {
				y = a.y;
			}
			if (a.brX > brx) {
				brx = a.brX;
			}
			if (a.brY > bry) {
				bry = a.brY;
			}
			
			width = brx-x;
			height = bry-y;
			
		}
		
	}
	
	public boolean completelyWithin(AABB parent) {
		return DMath.lessThanEquals(parent.x, x) && DMath.lessThanEquals(x+width, parent.brX) &&
				DMath.lessThanEquals(parent.y, y) && DMath.lessThanEquals(y+height, parent.brY);
	}
	
	/**
	 * returns 0.0 if completely outside parent, returns 1.0 if completely within parent
	 * 
	 * assumes only overlapping on an edge, and not on a corner
	 */
	public double fractionWithin(AABB inner, AABB outer) {
		
		if (DMath.lessThan(x, inner.x)) {
//			System.out.println(1);
			
			if (DMath.lessThan(y+height, outer.y)) {
				return 0.0;
			}
			if (DMath.greaterThan(y, outer.brY)) {
				return 0.0;
			}
			
			double diff = inner.x - outer.x;
			
			double innerLen = inner.x - x;
			
			return DMath.greaterThan(innerLen, (diff+width)) ?
					0.0 :
						1.0 - innerLen / (diff+width);
		}
		
		if (DMath.greaterThan(x+width, inner.brX)) {
//			System.out.println(2);
			
			if (DMath.lessThan(y+height, outer.y)) {
				return 0.0;
			}
			if (DMath.greaterThan(y, outer.brY)) {
				return 0.0;
			}
			
			double diff = outer.brX - inner.brX;
			
			double innerLen = x+width - inner.brX;
			
//			System.out.println(1.0 - innerLen / (diff+width));
			
			return DMath.greaterThan(innerLen, (diff+width)) ?
					0.0 :
						1.0 - innerLen / (diff+width);
		}
		
		if (DMath.lessThan(y, inner.y)) {
//			System.out.println(3);
			
			if (DMath.lessThan(x+width, outer.x)) {
				assert false;
			}
			if (DMath.greaterThan(x, outer.brX)) {
				assert false;
			}
			
			double diff = inner.y - outer.y;
			
			double innerLen = inner.y - y;
			
//			System.out.println(1.0 - innerLen / (diff+width));
			
			return DMath.greaterThan(innerLen, (diff+height)) ?
					0.0 :
						1.0 - innerLen / (diff+height);
		}
		
		if (DMath.greaterThan(y+height, inner.brY)) {
//			System.out.println(4);
			
			if (DMath.lessThan(x+width, outer.x)) {
				assert false;
			}
			if (DMath.greaterThan(x, outer.brX)) {
				assert false;
			}
			
			double diff = outer.brY - inner.brY;
			
			double innerLen = y+height - inner.brY;
			
			return DMath.greaterThan(innerLen, (diff+height)) ?
					0.0 :
						1.0 - innerLen / (diff+height);
		}
		
		return 1.0;
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.drawAABB(this);
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintAABB(this);
	}
	
}
