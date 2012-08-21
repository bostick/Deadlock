package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Position;

public class Path {
	
	public List<Position> path = new ArrayList<Position>();
	
	public void add(Position pos) {
		path.add(pos);
		//assert isConsistent();
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
		
		//assert isConsistent();
	}
	
	public void clear() {
		path.clear();
	}
	
	public Position getLastPosition() {
		return path.get(path.size()-1);
	}
	
//	private boolean isConsistent() {
//		for (int i = 0; i < path.size()-1; i++) {
//			Position a = path.get(i);
//			Position b = path.get(i+1);
//			
//			/*
//			 * exactly 1 way to get from a to b?
//			 */
//			
//			if (a instanceof EdgePosition) {
//				if (b instanceof EdgePosition) {
//					d;
//				} else {
//					d;
//				}
//			} else {
//				if (b instanceof EdgePosition) {
//					d;
//				} else {
//					d;
//				}
//			}
//			
//		}
//		return true;
//	}
	
}
