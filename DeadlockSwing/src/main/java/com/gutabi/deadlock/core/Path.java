package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Position;

public class Path {
	
	public List<Position> path = new ArrayList<Position>();
	
	public void add(Position pos) {
		path.add(pos);
	}
	
	public void crash(Position pos, int pIndex) {
		
		List<Position> newPath = new ArrayList<Position>();
		Position last = null;
		for (int i = 0; i < pIndex; i++) {
			last = path.get(i);
			newPath.add(last);
		}
		
		if (last == null || !pos.equals(last)) {
			newPath.add(pos);
		}
		
		path = newPath;
	}
	
	public void clear() {
		path.clear();
	}
	
	public Position getLastPosition() {
		return path.get(path.size()-1);
	}
	
	public double totalLength() {
		double total = 0;
		for (int i = 0; i < path.size()-1; i++) {
			Position a = path.get(i);
			Position b = path.get(i+1);
			total += a.distanceTo(b);
		}
		return total;
	}
}
