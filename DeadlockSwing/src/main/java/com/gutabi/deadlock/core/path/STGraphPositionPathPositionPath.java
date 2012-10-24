package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Graph;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Sink;
import com.gutabi.deadlock.core.VertexPosition;

public class STGraphPositionPathPositionPath {
	
	private List<STGraphPositionPathPosition> poss;
	
	private Graph graph;
	
	private List<Double> times;
	
	private double startTime;
	private double endTime;
	
	private double sinkTime = -1;
	
	private int hash;
	
	static Logger logger = Logger.getLogger(STGraphPositionPathPositionPath.class);
	
	private STGraphPositionPathPositionPath(List<STGraphPositionPathPosition> poss, Graph graph) {
		
		assert poss.size() >= 2;
		
		this.poss = poss;
		this.graph = graph;
		
		times = new ArrayList<Double>();
		
		for (STGraphPositionPathPosition pos : poss) {
			times.add(pos.getTime());
			GraphPosition gpos = pos.getSpace().getGraphPosition();
			if (gpos instanceof VertexPosition && ((VertexPosition)gpos).getVertex() instanceof Sink && sinkTime == -1) {
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
		
		logger.debug("advanceOneTimeStep");
		
		assert start != null;
		
		List<STGraphPositionPathPosition> poss = new ArrayList<STGraphPositionPathPosition>();
		
		GraphPositionPathPosition curPos = start;
		double traveledDist = 0.0;
		
		double speed = dist / 1.0;
		
		poss.add(new STGraphPositionPathPosition(curPos, 0.0));
		
		if (!DMath.equals(dist, 0.0)) {
			
			int iterations = 0;
			while (true) {
				
				logger.debug("iteration: " + iterations);
				
				if (iterations == 100) {
					String.class.getName();
				}
				
				if (curPos.isEndOfPath()) {
					
					break;
					
				} else {
					
					GraphPositionPathPosition nextPos = curPos.nextBound();
					double distanceToNextPos = curPos.distanceTo(nextPos);
					
					if (DMath.equals(traveledDist + distanceToNextPos, dist)) {
						logger.debug("1");
						
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
						logger.debug("2");
						
						double inc = distanceToNextPos;
						traveledDist = traveledDist + inc;
						
						assert DMath.greaterThan(traveledDist / speed, poss.get(poss.size()-1).getTime());
						poss.add(new STGraphPositionPathPosition(nextPos, traveledDist / speed));
						
						curPos = nextPos;
						
					} else {
						logger.debug("3");
						
						double toTravel = dist - traveledDist;
						
						traveledDist = traveledDist + toTravel;
						curPos = curPos.travel(toTravel);
						
						assert DMath.greaterThan(traveledDist / speed, poss.get(poss.size()-1).getTime());
						poss.add(new STGraphPositionPathPosition(curPos, traveledDist / speed));
						break;
						
					}
					
				}
				
				iterations++;
			}
			logger.debug("done while");
			
		}
		
		STGraphPositionPathPosition last = poss.get(poss.size()-1);
		if (DMath.lessThan(poss.get(poss.size()-1).getTime(), 1.0)) {
			
			poss.add(new STGraphPositionPathPosition(last.getSpace(), 1.0));
			
		} else {
			assert DMath.equals(poss.get(poss.size()-1).getTime(), 1.0);
		}
		
		logger.debug("done advanceOneTimeStep");
		
		return new STGraphPositionPathPositionPath(poss, start.getGraphPosition().graph);
		
	}
	
	public static STGraphPositionPathPositionPath crashOneTimeStep(GraphPositionPathPosition start) {
		
		List<STGraphPositionPathPosition> poss = new ArrayList<STGraphPositionPathPosition>();
		
		poss.add(new STGraphPositionPathPosition(start, 0.0));
		poss.add(new STGraphPositionPathPosition(start, 1.0));
		
		return new STGraphPositionPathPositionPath(poss, start.getGraphPosition().graph);
	}
	
	public STGraphPositionPath toSTGraphPositionPath() {
		List<STGraphPosition> newPath = new ArrayList<STGraphPosition>();
		for (STGraphPositionPathPosition p : poss) {
			newPath.add(new STGraphPosition(p.getSpace().getGraphPosition(), p.getTime()));
		}
		return new STGraphPositionPath(newPath, graph);
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
