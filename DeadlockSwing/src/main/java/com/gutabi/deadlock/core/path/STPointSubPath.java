package com.gutabi.deadlock.core.path;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;


public class STPointSubPath {
	
	public STPoint start;
	public STPoint end;
	
	public STPointSubPath(STPoint start, STPoint end) {
		this.start = start;
		this.end = end;
	}
	
	public Point getPoint(double time) {
		if (DMath.equals(time, start.getTime())) {
			return start.getSpace();
		} else if (DMath.equals(time, end.getTime())) {
			return end.getSpace();
		} else {
			if (time < start.getTime()) {
				throw new IllegalArgumentException();
			}
			if (time > end.getTime()) {
				throw new IllegalArgumentException();
			}
			if (start.getSpace().equals(end.getSpace())) {
				return start.getSpace();
			} else {
				double p = (time - start.getTime()) / (end.getTime() - start.getTime());
				return Point.point(start.getSpace(), end.getSpace(), p);
			}
		}
	}
}
