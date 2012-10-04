package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.List;

public class STPointPath {
	
	private List<STPoint> poss;
	public STPoint start;
	public STPoint end;
	public List<Double> times;
	int hash;
	
	public STPointPath(List<STPoint> poss) {
		
		assert poss.size() >= 2;
		
		this.poss = poss;
		this.start = poss.get(0);
		this.end = poss.get(poss.size()-1);
		
		times = new ArrayList<Double>();
		for (STPoint pos : poss) {
			times.add(pos.getTime());
		}
		
		int h = 17;
		h = 37 * h + poss.hashCode();
		hash = h;
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public int size() {
		return poss.size();
	}
	
}
