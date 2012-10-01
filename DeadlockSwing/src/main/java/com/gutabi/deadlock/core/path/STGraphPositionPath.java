package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.List;

public class STGraphPositionPath {
	
	private List<STGraphPosition> poss;
	
	public STGraphPositionPath(List<STGraphPosition> poss) {
		this.poss = poss;
	}
	
	public STPointPath toSTPointPath() {
		List<STPoint> newPath = new ArrayList<STPoint>();
		for (STGraphPosition p : poss) {
			newPath.add(new STPoint(p.getSpace().getPoint(), p.getTime()));
		}
		return new STPointPath(newPath);
	}
	
}
