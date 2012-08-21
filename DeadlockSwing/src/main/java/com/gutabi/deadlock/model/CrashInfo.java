package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.Position;

public class CrashInfo {
	
	public final CrashSite crashSite;
	public final Car i;
	public final Car j;
//	public final Position ipa;
//	public final Position jpa;
	public final Position ip;
	public final Position jp;
	public final int iDir;
	public final int jDir;
	//public final double dist;
	//public final int ie;
	public final int ik;
	//public final int je;
	public final int jl;
	
	/**
	 * 
	 * For iDir and jDir, 0 means that this car has already crashed
	 */
	public CrashInfo(CrashSite crashSite, Car i, Car j, Position ip, Position jp, int iDir, int jDir, double dist, int ik, int jl) {
		this.crashSite = crashSite;
		this.i = i;
		this.j = j;
//		this.ipa = ipa;
//		this.jpa = jpa;
		this.ip = ip;
		this.jp = jp;
		this.iDir = iDir;
		this.jDir = jDir;
		//this.dist = dist;
		//this.ie = ie;
		this.ik = ik;
		//this.je = je;
		this.jl = jl;
	}
	
//	public String toString() {
//		return i + " " + j;
//	}
	
}
