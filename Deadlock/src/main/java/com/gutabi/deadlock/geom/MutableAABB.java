package com.gutabi.deadlock.geom;

import java.io.Serializable;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MutableAABB implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public double x = Double.NaN;
	public double y = Double.NaN;
	public double width = Double.NaN;
	public double height = Double.NaN;
	
	public final Point n01 = Point.UP;
	public final Point n12 = Point.RIGHT;
	
	private transient double[] n01Projection;
	private transient double[] n12Projection;
	
	public MutableAABB() {
		
	}
	
	public void reset() {
		x = Double.NaN;
		y = Double.NaN;
		width = Double.NaN;
		height = Double.NaN;
		
		n01Projection = null;
		n12Projection = null;
	}
	
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		
		n01Projection = null;
		n12Projection = null;
	}
	
	public void setDimension(double width, double height) {
		this.width = width;
		this.height = height;
		
		n01Projection = null;
		n12Projection = null;
	}
	
	public void setShape(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		n01Projection = null;
		n12Projection = null;
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
			
			if (DMath.lessThan(y+height, outer.y)) {
				return 0.0;
			}
			if (DMath.greaterThan(y, outer.brY)) {
				return 0.0;
			}
			
			double diff = outer.brX - inner.brX;
			
			double innerLen = x+width - inner.brX;
			
			return DMath.greaterThan(innerLen, (diff+width)) ?
					0.0 :
						1.0 - innerLen / (diff+width);
		}
		
		if (DMath.lessThan(y, inner.y)) {
			
			if (DMath.lessThan(x+width, outer.x)) {
				assert false;
			}
			if (DMath.greaterThan(x, outer.brX)) {
				assert false;
			}
			
			double diff = inner.y - outer.y;
			
			double innerLen = inner.y - y;
			
			return DMath.greaterThan(innerLen, (diff+height)) ?
					0.0 :
						1.0 - innerLen / (diff+height);
		}
		
		if (DMath.greaterThan(y+height, inner.brY)) {
			
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
	
	public void project(Point axis, double[] out) {
		
		Point p0 = new Point(x, y);
		Point p1 = new Point(x + width, y);
		Point p2 = new Point(x+width, y+height);
		Point p3 = new Point(x, y + height);
		
		double min = Point.dot(axis, p0);
		double max = min;
		
		double p = Point.dot(axis, p1);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		p = Point.dot(axis, p2);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		p = Point.dot(axis, p3);
		if (p < min) {
			min = p;
		} else if (p > max) {
			max = p;
		}
		
		out[0] = min;
		out[1] = max;
	}
	
	public Point getN01() {
		return n01;
	}
	
	public Point getN12() {
		return n12;
	}
	
	public void projectN01(double[] out) {
		if (n01Projection == null) {
			computeProjections();
		}
		out[0] = n01Projection[0];
		out[1] = n01Projection[1];
	}
	
	public void projectN12(double[] out) {
		if (n12Projection == null) {
			computeProjections();
		}
		out[0] = n12Projection[0];
		out[1] = n12Projection[1];
	}
	
	private void computeProjections() {
		
		n01Projection = new double[2];
		project(n01, n01Projection);
		
		n12Projection = new double[2];
		project(n12, n12Projection);
	}
	
	public void draw(RenderingContext ctxt) {
		ctxt.drawAABB(this);
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintAABB(this);
	}
	
}
