package com.gutabi.core;

import com.gutabi.core.QuadTree.SegmentIndex;

public class IntersectionInfo {
	
	public final Edge edge;
	public final int index;
	public final double param;
	public final DPoint point;
	
	public IntersectionInfo(Edge e, int index, double param) {
		this.edge = e;
		this.index = index;
		this.param = param;
		
		Point c = e.getPoint(index);
		Point d = e.getPoint(index+1);
		this.point = Point.point(c, d, param);
		
	}
	
	public IntersectionInfo(SegmentIndex si, double param) {
		this(si.edge, si.index, param);
	}
	
}
