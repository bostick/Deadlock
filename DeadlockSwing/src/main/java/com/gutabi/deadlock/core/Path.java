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
			
			double dist;
			if (a instanceof VertexPosition) {
				if (b instanceof VertexPosition) {
					VertexPosition aa = (VertexPosition)a;
					VertexPosition bb = (VertexPosition)b;
					
					Edge e = bb.prevDirEdge;
					assert (aa.getVertex() == e.getStart() && bb.getVertex() == e.getEnd()) || (aa.getVertex() == e.getEnd() && bb.getVertex() == e.getStart());
					
					dist = e.getTotalLength();
					
				} else {
					VertexPosition aa = (VertexPosition)a;
					EdgePosition bb = (EdgePosition)b;
					
					Edge e = bb.getEdge();
					if (aa.getVertex() == e.getStart()) {
						dist = bb.distanceToStartOfEdge();
					} else {
						assert aa.getVertex() == e.getEnd();
						dist = bb.distanceToEndOfEdge();
					}
				}
			} else {
				if (b instanceof VertexPosition) {
					EdgePosition aa = (EdgePosition)a;
					VertexPosition bb = (VertexPosition)b;
					
					Edge e = aa.getEdge();
					if (bb.getVertex() == e.getStart()) {
						dist = aa.distanceToStartOfEdge();
					} else {
						assert bb.getVertex() == e.getEnd();
						dist = aa.distanceToEndOfEdge();
					}
					
				} else {
					EdgePosition aa = (EdgePosition)a;
					EdgePosition bb = (EdgePosition)b;
					
					Edge ae = aa.getEdge();
					Edge be = bb.getEdge();
					assert ae == be;
					
					dist = Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
				}
			}
			
			t += dist;
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
