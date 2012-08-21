package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.DMath;


public class CrashSite {
	
	//public final Position p;
	public final double time;
	
	private final int hash; 
	
	public CrashSite(double time) {
		//this.p = p;
		this.time = time;
		
		int h = 17;
		//h = 37 * h + p.hashCode();
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
			//return (p.equals(b.p)) && (time == b.time);
			return DMath.doubleEquals(time, b.time);
		}
	}
	
//	static class CrashSiteComparator implements Comparator<CrashSite> {
//		@Override
//		public int compare(CrashSite a, CrashSite b) {
//			if (DMath.doubleEquals(a.time, b.time)) {
//				return 0;
//			} else if (a.time < b.time) {
//				return -1;
//			} else {
//				return 1;
//			}
//		}
//	}
//	
//	public static Comparator<CrashSite> COMPARATOR = new CrashSiteComparator();
	
}
