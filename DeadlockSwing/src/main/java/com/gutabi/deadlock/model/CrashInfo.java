package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.Position;

public class CrashInfo {
	
	public final CrashSite crashSite;
	public final Car i;
	public final Car j;
	public final Position ip;
	public final Position jp;
	public final int iDir;
	public final int jDir;
	public final int ik;
	public final int jl;
	
	/**
	 * 
	 * For iDir and jDir, 0 means that this car has already crashed
	 */
	public CrashInfo(CrashSite crashSite, Car i, Car j, Position ip, Position jp, int iDir, int jDir, double dist, int ik, int jl) {
		this.crashSite = crashSite;
		this.i = i;
		this.j = j;
		this.ip = ip;
		this.jp = jp;
		this.iDir = iDir;
		this.jDir = jDir;
		this.ik = ik;
		this.jl = jl;
	}
	
}
