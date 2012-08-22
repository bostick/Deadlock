package com.gutabi.deadlock.core;

import java.util.Comparator;

import static com.gutabi.deadlock.core.DMath.doubleEquals;

public class PointToBeAdded {
	
	public final Point p;
	
	public final int index;
	/**
	 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
	 */
	public final double param;
	
	private final int hash;
	
	public PointToBeAdded(Point p, int index, double param) {
		assert param >= 0.0;
		assert param <= 1.0;
		this.p = p;
		this.index = index;
		this.param = param;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		h = 37 * h + (int)index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof PointToBeAdded)) {
			return false;
		} else {
			PointToBeAdded b = (PointToBeAdded)o;
			boolean res = (index == b.index && doubleEquals(param, b.param));
			if (res) {
				assert Point.equals(p, b.p);
			}
			return res;
		}
	}
	
	static class PTBAComparator implements Comparator<PointToBeAdded> {
		@Override
		public int compare(PointToBeAdded a, PointToBeAdded b) {
			if (a.index < b.index) {
				return -1;
			} else if (a.index > b.index) {
				return 1;
			} else {
				if (doubleEquals(a.param, b.param)) {
					return 0;
				} else if (a.param < b.param) {
					return -1;
				} else {
					return 1;
				}
			}
		}
	}
	
	public static Comparator<PointToBeAdded> COMPARATOR = new PTBAComparator();
	
}
