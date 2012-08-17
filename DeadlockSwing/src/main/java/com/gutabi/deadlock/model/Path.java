package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.VertexPosition;

public class Path {
	
	public List<List<Position>> path = new ArrayList<List<Position>>();
	
	public void newEdge() {
		path.add(new ArrayList<Position>());
	}
	
	public void add(Position pos) {
		List<Position> last = path.get(path.size()-1);
		last.add(pos);
		assert isConsistent();
	}
	
	public void crash(Position pos, int eIndex, int pIndex) {
		
		List<List<Position>> newFuturePath = new ArrayList<List<Position>>();
		for (int i = 0; i < eIndex; i++) {
			newFuturePath.add(path.get(i));
		}
		
		List<Position> oldE = path.get(eIndex);
		
		List<Position> newE = new ArrayList<Position>();
		newFuturePath.add(newE);
		Position last = null;
		for (int i = 0; i < pIndex; i++) {
			last = oldE.get(i);
			newE.add(last);
		}
		
		if (last == null || !pos.equals(last)) {
			newE.add(pos);
		}
		
		path = newFuturePath;
		
		assert isConsistent();
	}
	
//	public List<Position> get(int i) {
//		return path.get(i);
//	}
	
	public void clear() {
		path.clear();
	}
	
	public Position getLastPosition() {
		int l = path.size()-1;
		List<Position> last = path.get(l);
		return last.get(last.size()-1);
	}
	
	private boolean isConsistent() {
		VertexPosition last = null;
		for (List<Position> ePath : path) {
			
			Edge e = null;
			for (int i = 0; i < ePath.size()-1; i++) {
				Position a = ePath.get(i);
				Position b = ePath.get(i+1);
				if (i == 0) {
					e = a.getEdge();
					if (a instanceof VertexPosition) {
						if (last != null) {
							assert last.getVertex() == ((VertexPosition)a).getVertex();
							
							/*
							 * could be one edge on vertex
							 */
							//assert last.getEdge() != ((VertexPosition)a).getEdge();
							last = null;
						}
					}
				} else {
					assert a instanceof EdgePosition;
				}
				assert b.getEdge() == e;
				if (i == ePath.size()-2) {
					if (b instanceof VertexPosition) {
						last = ((VertexPosition)b);
					}
				}
				assert !a.equals(b);
//				if (a instanceof EdgePosition) {
//					if (b instanceof EdgePosition) {
//						assert !a.equals(b);
//					} else {
//						assert !a.equals(b);
//					}
//				} else {
//					if (b instanceof EdgePosition) {
//						assert !a.equals(b);
//					} else {
//						assert !a.equals(b);
//						//assert ((VertexPosition)a).getEdge() != ((VertexPosition)b).getEdge(); 
//					}
//				}
			}
		}
		return true;
	}
	
}
