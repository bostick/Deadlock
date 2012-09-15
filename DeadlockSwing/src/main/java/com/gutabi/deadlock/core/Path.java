package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.List;

public class Path {
	
	private final List<STPosition> poss;
	
	private double totalDistance;
	private double totalTime;
	
	public Path(List<STPosition> poss) {
		
		this.poss = poss;
		
		calculateTotalDistanceAndTime(poss);
		
		//assert check();
	}
	
	private void calculateTotalDistanceAndTime(List<STPosition> poss) {
		
		double accDist = 0.0;
		double accTime = 0.0;
		
		for (int i = 0; i < poss.size()-1; i++) {
			STPosition a = poss.get(i);
			STPosition b = poss.get(i+1);
			
			double dist;
			double time = b.t - a.t;
			
			if (a.s instanceof VertexPosition) {
				if (b.s instanceof VertexPosition) {
					
					VertexPosition aa = (VertexPosition)a.s;
					VertexPosition bb = (VertexPosition)b.s;
					
					List<Connector> cons = Vertex.commonConnectors(aa.getVertex(), bb.getVertex());
					assert cons.size() == 1;
					
					Edge e = (Edge)cons.get(0);
					assert !e.isLoop();
					
					dist = e.getTotalLength();
					
				} else {
					VertexPosition aa = (VertexPosition)a.s;
					EdgePosition bb = (EdgePosition)b.s;
					
					Edge e = bb.getEdge();
					if (aa.getVertex() == e.getStart()) {
						dist = bb.distanceToStartOfEdge();
					} else {
						assert aa.getVertex() == e.getEnd();
						dist = bb.distanceToEndOfEdge();
					}
				}
			} else {
				if (b.s instanceof VertexPosition) {
					EdgePosition aa = (EdgePosition)a.s;
					VertexPosition bb = (VertexPosition)b.s;
					
					Edge e = aa.getEdge();
					if (bb.getVertex() == e.getStart()) {
						dist = aa.distanceToStartOfEdge();
					} else {
						assert bb.getVertex() == e.getEnd();
						dist = aa.distanceToEndOfEdge();
					}
					
				} else {
					EdgePosition aa = (EdgePosition)a.s;
					EdgePosition bb = (EdgePosition)b.s;
					
					Edge ae = aa.getEdge();
					Edge be = bb.getEdge();
					assert ae == be;
					
					dist = Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
				}
			}
			
			accDist += dist;
			accTime += time;
		}
		
		totalDistance = accDist;
		totalTime = accTime;
	}
	
	public STPosition get(int i) {
		return poss.get(i);
	}
	
	public int size() {
		return poss.size();
	}
	
	public Path crash(STPosition pos, int pIndex) {
		
		List<STPosition> newPath = new ArrayList<STPosition>();
		STPosition last = null;
		for (int i = 0; i < pIndex; i++) {
			last = poss.get(i);
			newPath.add(last);
		}
		
		if (last == null || !pos.equals(last)) {
			newPath.add(pos);
		}
		
		return new Path(newPath);
	}
	
	public Path append(STPosition q) {
		List<STPosition> newPoss = new ArrayList<STPosition>(poss);
		newPoss.add(q);
		return new Path(newPoss);
	}
	
	public STPosition getLastPosition() {
		return poss.get(poss.size()-1);
	}
	
	public double totalDistance() {
		return totalDistance;
	}
	
	public double totalTime() {
		return totalTime;
	}
	
//	private boolean check() {
//		for (int i = 1; i < poss.size(); i++) {
//			Position cur = poss.get(i);
//			Position prev = poss.get(i-1);
//			assert cur.prevPos == prev;
//		}
//		return true;
//	}
}
