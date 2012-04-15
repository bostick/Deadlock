package com.gutabi.deadlock.swing.utils;

import java.util.Comparator;


public class PointToBeAdded {
	
	public Point p;
	
	/**
	 * value ranging from 0..1 measuring distance between points at index and index+1, used for sorting
	 */
	Rat param;
	
	private final int hash;
	
	public PointToBeAdded(Point p, Rat param) {
		assert param.isGreaterThanOrEquals(Rat.ZERO);
		assert param.isLessThanOrEquals(Rat.ONE);
		this.p = p;
		this.param = param;
		
		int h = 17;
		h = 37 * h + p.hashCode();
		h = 37 * h + param.hashCode();
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
			return (this.p.equals(((PointToBeAdded)o).p)) && (this.param.equals(((PointToBeAdded)o).param));
		}
	}
	
	static class PTBAComparator implements Comparator<PointToBeAdded> {
		@Override
		public int compare(PointToBeAdded a, PointToBeAdded b) {
			if (a.param.isLessThan(b.param)) {
				return -1;
			} else if (a.param.isGreaterThan(b.param)) {
				return 1;
			} else {
				assert a.p.equals(b.p) && a.param.equals(b.param);
				return 0;
			}
		}
	}
	
	public static Comparator<PointToBeAdded> ptbaComparator = new PTBAComparator();
	
}

