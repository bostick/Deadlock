package com.gutabi.deadlock.geom;


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
	
}
