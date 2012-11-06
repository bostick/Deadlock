package com.gutabi.deadlock.core.graph;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;

public class GraphPositionPathPosition {
	
	public final GraphPositionPath path;
	public final int index;
	public final double param;
	
	public final double combo;
	public final boolean bound;
	
	public final GraphPosition gpos;
	
	public final double lengthToStartOfPath;
	public final double lengthToEndOfPath;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(GraphPositionPathPosition.class);
	
	public GraphPositionPathPosition(GraphPositionPath path, int index, double param) {
		
		this.path = path;
		this.index = index;
		this.param = param;
		
		combo = index+param;
		
		int h = 17;
		h = 37 * h + path.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		this.bound = DMath.equals(param, 0.0);
		gpos = path.getGraphPosition(index, param);
		
		double acc = path.cumulativeDistancesFromStart[index];
		acc += path.get(index).distanceTo(gpos);
		
		lengthToStartOfPath = acc;
		lengthToEndOfPath = path.totalLength - lengthToStartOfPath;
		
		assert lengthToStartOfPath <= path.totalLength;
		assert lengthToEndOfPath >= 0;
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPositionPathPosition)) {
			return false;
		} else {
			GraphPositionPathPosition b = (GraphPositionPathPosition)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public String toString() {
		return "GPPP " + index + " " + param + " (" + lengthToStartOfPath + "/" + path.totalLength + ")";
	}
	
	
	
	public boolean isBound() {
		return bound;
	}
	
	public boolean isEndOfPath() {
		return (index == path.size-1) && DMath.equals(param, 0.0);
	}
	
	public double distanceTo(GraphPositionPathPosition p) {
		
		int goalIndex = p.index;
		double goalParam = p.param;
		
		if (index == goalIndex || (goalIndex == index+1 && DMath.equals(goalParam, 0.0))) {
			
			if (gpos.equals(p.gpos)) {
				String.class.getName();
			}
			
			return gpos.distanceTo(p.gpos);
			
		} else {
			
			double acc = 0.0;
			GraphPositionPathPosition curPos = this;
			GraphPositionPathPosition nextPos = curPos.nextBound();
			
			acc += curPos.distanceTo(nextPos);
			
			while (true) {
				
				if (nextPos.index == goalIndex) {
					break;
				}
				
				curPos = nextPos;
				nextPos = curPos.nextBound();
				
				acc += curPos.distanceTo(nextPos);
				
			}
			
			if (DMath.equals(nextPos.param, goalParam)) {
				return acc;
			} else {
				
				return acc + nextPos.gpos.distanceTo(p.gpos);
				
			}
			
		}
	}
	
	public GraphPositionPathPosition travel(double dist) {
		
		if (DMath.equals(dist, 0)) {
			return this;
		}
		
		if (dist < 0) {
			throw new IllegalArgumentException();
		}
		
		double traveled = 0.0;
		
		GraphPositionPathPosition curPos = this;
		GraphPositionPathPosition nextPos;
		
		while (true) {
			
			nextPos = curPos.nextBound();
			double distanceToNextPos = curPos.distanceTo(nextPos);
			
			if (DMath.equals(traveled + distanceToNextPos, dist)) {

				return nextPos;
				
			} else if (traveled + distanceToNextPos < dist) {
				
				traveled += distanceToNextPos;
				curPos = nextPos;
				
			} else {
				
				double toTravel = dist - traveled;
				
				EdgePosition g = (EdgePosition)(curPos.gpos.travelTo(nextPos.gpos, toTravel));
				
				if (curPos.gpos instanceof EdgePosition) {
					EdgePosition curPosE = (EdgePosition)curPos.gpos;
					
					if (curPosE.getIndex() < g.getIndex() || (curPosE.getIndex() == g.getIndex() && curPosE.getParam() < g.getParam())) {
						// same direction as edge
						//assert curPosE.getIndex() == g.getIndex();
						return new GraphPositionPathPosition(path, curPos.index, g.getParam());
					} else {
						return new GraphPositionPathPosition(path, curPos.index, 1-g.getParam());
					}
					
				} else {
					VertexPosition curPosE = (VertexPosition)curPos.gpos;
					
					if (g.vs.get(0) == curPosE.vs.get(0)) {
						// same direction as edge
						return new GraphPositionPathPosition(path, curPos.index, g.getParam());
					} else {
						return new GraphPositionPathPosition(path, curPos.index, 1-g.getParam());
					}
					
				}
				
			}
			
		}
		
	}
	
	public GraphPositionPathPosition nextBound() {
		assert index < path.size-1;
		return new GraphPositionPathPosition(path, index+1, 0.0);
	}
	
	public GraphPositionPathPosition floor() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else {
			return new GraphPositionPathPosition(path, index, 0.0);
		}
	}
	
	public GraphPositionPathPosition ceiling() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else {
			return new GraphPositionPathPosition(path, index+1, 0.0);
		}
	}
	
}
