package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Position;

public class Path {
	
	private final List<Position> poss;
	
	private final double total;
	
	public Path(List<Position> poss) {
		
		this.poss = poss;
		
		double t = 0;
		for (int i = 0; i < poss.size()-1; i++) {
			Position a = poss.get(i);
			Position b = poss.get(i+1);
			t += a.distanceTo(b);
		}
		total = t;
		
		assert check();
	}
	
	public Position get(int i) {
		return poss.get(i);
	}
	
	public int size() {
		return poss.size();
	}
	
	public Path crash(Position pos, int pIndex) {
		
		List<Position> newPath = new ArrayList<Position>();
		Position last = null;
		for (int i = 0; i < pIndex; i++) {
			last = poss.get(i);
			newPath.add(last);
		}
		
		if (last == null || !pos.equals(last)) {
			newPath.add(pos);
		}
		
		return new Path(newPath);
	}
	
	public Path append(Position q) {
		List<Position> newPoss = new ArrayList<Position>(poss);
		newPoss.add(q);
		return new Path(newPoss);
	}
	
	public Position getLastPosition() {
		return poss.get(poss.size()-1);
	}
	
	public double totalLength() {
		return total;
	}
	
	
	private boolean check() {
		for (int i = 1; i < poss.size(); i++) {
			Position cur = poss.get(i);
			Position prev = poss.get(i-1);
			assert cur.prevPos == prev;
		}
		return true;
	}
}
