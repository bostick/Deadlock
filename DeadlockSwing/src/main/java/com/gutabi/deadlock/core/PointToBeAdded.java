package com.gutabi.deadlock.core;

import java.util.Comparator;


public class PointToBeAdded {
	
	public final DPoint p;
	
	/**
	 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
	 */
	private final double param;
	
	private final int hash;
	
	public PointToBeAdded(DPoint p, double param) {
		assert param >= 0.0;
		assert param <= 1.0;
		this.p = p;
		this.param = param;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		h = 37 * h + (int)param;
		hash = h;
		
	}
	
	@Override
	public String toString() {
		return "PTBA: " + p + " " + param;
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
			return (this.p.equals(((PointToBeAdded)o).p)) && Point.doubleEquals(this.param, (((PointToBeAdded)o).param));
		}
	}
	
	static class PTBAComparator implements Comparator<PointToBeAdded> {
		@Override
		public int compare(PointToBeAdded a, PointToBeAdded b) {
			if (a.param < (b.param)) {
				return -1;
			} else if (a.param > (b.param)) {
				return 1;
			} else {
				assert DPoint.equals(a.p, b.p) && Point.doubleEquals(a.param, b.param);
				return 0;
			}
		}
	}
	
	public static Comparator<PointToBeAdded> ptbaComparator = new PTBAComparator();
	
}

