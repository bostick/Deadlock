package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Sink;

public class STGraphPositionPathPositionPath {
	
	private List<STGraphPositionPathPosition> poss;
	
	private List<Double> times;
	
	private double startTime;
	private double endTime;
	
	private double sinkTime = -1;
	
	private int hash;
	
	private STGraphPositionPathPositionPath(List<STGraphPositionPathPosition> poss) {
		
		assert poss.size() >= 2;
		
		this.poss = poss;
		
		times = new ArrayList<Double>();
		
		for (STGraphPositionPathPosition pos : poss) {
			times.add(pos.getTime());
			if (pos.getSpace().getGraphPosition() instanceof Sink && sinkTime == -1) {
				sinkTime = pos.getTime();
			}
		}
		
		int h = 17;
		h = 37 * h + poss.hashCode();
		hash = h;
		
		startTime = times.get(0);
		endTime = times.get(times.size()-1);
		
		assert check();
		
	}
	
	private boolean check() {
		for (int i = 1; i < poss.size(); i++) {
			STGraphPositionPathPosition cur = poss.get(i);
			STGraphPositionPathPosition prev = poss.get(i-1);
			assert cur.getTime() > prev.getTime();
		}
		return true;
	}
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return poss.toString();
	}
	
	public static STGraphPositionPathPositionPath advanceOneTimeStep(GraphPositionPathPosition start, double dist) {
		
		assert start != null;
		
		List<STGraphPositionPathPosition> poss = new ArrayList<STGraphPositionPathPosition>();
		
		GraphPositionPathPosition curPos = start;
		double traveledDist = 0.0;
		
		double speed = dist / 1.0;
		
		poss.add(new STGraphPositionPathPosition(curPos, 0.0));
		
		if (!DMath.equals(dist, 0.0)) {
			
			while (true) {
				
				if (curPos.isEndOfPath()) {
					
					break;
					
				} else {
					
					GraphPositionPathPosition nextPos = curPos.nextBound();
					double distanceToNextPos = curPos.distanceTo(nextPos);
					
					if (DMath.equals(traveledDist + distanceToNextPos, dist)) {
						
						traveledDist = traveledDist + distanceToNextPos;
						curPos = nextPos;
						
						assert DMath.greaterThan(traveledDist / speed, poss.get(poss.size()-1).getTime());
						double time = traveledDist / speed;
						if (DMath.equals(time, 1.0)) {
							time = 1.0;
						}
						poss.add(new STGraphPositionPathPosition(curPos, time));
						break;
						
					} else if (traveledDist + distanceToNextPos < dist) {
						
						double inc = distanceToNextPos;
						traveledDist = traveledDist + inc;
						
						assert DMath.greaterThan(traveledDist / speed, poss.get(poss.size()-1).getTime());
						poss.add(new STGraphPositionPathPosition(nextPos, traveledDist / speed));
						
						curPos = nextPos;
						
					} else {
						
						double toTravel = dist - traveledDist;
						
						traveledDist = traveledDist + toTravel;
						curPos = curPos.travel(toTravel);
						
						assert DMath.greaterThan(traveledDist / speed, poss.get(poss.size()-1).getTime());
						poss.add(new STGraphPositionPathPosition(curPos, traveledDist / speed));
						break;
						
					}
					
				}
				
			}
			
		}
		
		STGraphPositionPathPosition last = poss.get(poss.size()-1);
		if (DMath.lessThan(poss.get(poss.size()-1).getTime(), 1.0)) {
			
			poss.add(new STGraphPositionPathPosition(last.getSpace(), 1.0));
			
		} else {
			assert DMath.equals(poss.get(poss.size()-1).getTime(), 1.0);
		}
		
		return new STGraphPositionPathPositionPath(poss);
		
	}
	
	public static STGraphPositionPathPositionPath crashOneTimeStep(GraphPositionPathPosition start) {
		
		List<STGraphPositionPathPosition> poss = new ArrayList<STGraphPositionPathPosition>();
		
		poss.add(new STGraphPositionPathPosition(start, 0.0));
		poss.add(new STGraphPositionPathPosition(start, 1.0));
		
		return new STGraphPositionPathPositionPath(poss);
	}
	
	public STGraphPositionPath toSTGraphPositionPath() {
		List<STGraphPosition> newPath = new ArrayList<STGraphPosition>();
		for (STGraphPositionPathPosition p : poss) {
			newPath.add(new STGraphPosition(p.getSpace().getGraphPosition(), p.getTime()));
		}
		return new STGraphPositionPath(newPath);
	}
	
	
	public STGraphPositionPathPosition get(int i) {
		return poss.get(i);
	}
	
	public GraphPositionPathPosition getPosition(double time) {
		if (time < startTime) {
			throw new IllegalArgumentException();
		}
		if (time > endTime) {
			throw new IllegalArgumentException();
		}
		
		int key = Collections.binarySearch(times, time, DMath.COMPARATOR);
		if (key >= 0) {
			// found
			return poss.get(key).getSpace();
		} else {
			int insertionPoint = -(key+1);
			//start is between insertionPoint-1 and insertionPoint
			STGraphPositionPathPosition a = poss.get(insertionPoint-1);
			STGraphPositionPathPosition b = poss.get(insertionPoint);
			assert DMath.lessThan(a.getTime(), time) && DMath.lessThan(time, b.getTime());
			
			if (a.getSpace().equals(b.getSpace())) {
				return a.getSpace();
			} else {
				double p = (time - a.getTime()) / (b.getTime() - a.getTime());
				double d = a.getSpace().distanceTo(b.getSpace());
				return a.getSpace().travel(p * d);
			}
		}
	}
	
	public int size() {
		return poss.size();
	}
	
}
