package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.DMath;


public class CrashSite {
	
	public final double time;
	
	private final int hash; 
	
	public CrashSite(double time) {
		this.time = time;
		
		int h = 17;
		long l = Double.doubleToLongBits(time);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
	}
	
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof CrashSite)) {
			return false;
		} else {
			CrashSite b = (CrashSite)o;
			return DMath.doubleEquals(time, b.time);
		}
	}
	
}
