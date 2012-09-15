package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.STPosition;

public class CrashInfo {
	
	public final CrashSite crashSite;
	public final Car i;
	public final Car j;
	public final STPosition ip;
	public final STPosition jp;
	public final boolean iCrashed;
	public final boolean jCrashed;
	public final int ik;
	public final int jl;
	
	/**
	 * 
	 * For iDir and jDir, 0 means that this car has already crashed
	 */
	public CrashInfo(CrashSite crashSite, Car i, Car j, STPosition ip, STPosition jp, boolean iCrashed, boolean jCrashed, double dist, int ik, int jl) {
		this.crashSite = crashSite;
		this.i = i;
		this.j = j;
		this.ip = ip;
		this.jp = jp;
		this.iCrashed = iCrashed;
		this.jCrashed = jCrashed;
		this.ik = ik;
		this.jl = jl;
	}
	
}
