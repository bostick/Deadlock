package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public class GraphPositionPathPosition {
	
	public final GraphPositionPath path;
	public final int index;
	public final double param;
	
	public final double combo;
	public final boolean bound;
	
	private GraphPosition gpos;
	
	public final Point p;
	
	public final double lengthToStartOfPath;
	public final double lengthToEndOfPath;
	
	private int hash;
	
	public GraphPositionPathPosition(GraphPositionPath path, int index, double param) {
		
		if (index < 0 || index >= path.size) {
			throw new IllegalArgumentException();
		}
		if (DMath.lessThan(param, 0.0) || DMath.greaterThanEquals(param, 1.0)) {
			throw new IllegalArgumentException();
		}
		
		this.path = path;
		this.index = index;
		this.param = param;
		
		combo = index+param;
		
		this.bound = DMath.equals(param, 0.0);
		
		if (bound) {
			p = path.get(index).p;
		} else {
			p = Point.point(path.get(index).p, path.get(index+1).p, param);
		}
		
		double acc = path.cumulativeDistancesFromStart[index];
		acc += Point.distance(path.get(index).p, p);
		
		lengthToStartOfPath = acc;
		lengthToEndOfPath = path.totalLength - lengthToStartOfPath;
		
		assert lengthToStartOfPath <= path.totalLength;
		assert lengthToEndOfPath >= 0;
		
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + path.hashCode();
			h = 37 * h + index;
			long l = Double.doubleToLongBits(param);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
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
		return "GPPP[...] " + index + " " + param;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public boolean isStartOfPath() {
		return (index == 0) && DMath.equals(param, 0.0);
	}
	
	public boolean isEndOfPath() {
		return (index == path.size-1) && DMath.equals(param, 0.0);
	}
	
	private void computeGraphPosition() {
		
		GraphPosition p1 = path.get(index);
		if (DMath.equals(param, 0.0)) {
			gpos = p1;
			return;
		}
		
		GraphPosition p2 = path.get(index+1);
		
		if (DMath.equals(param, 1.0)) {
			gpos = p2;
			return;
		}
		
		double dist = Point.distance(p1.p, p2.p);
		
		gpos = p1.approachNeighbor(p2, dist * param);
		
	}
	
	public GraphPosition getGraphPosition() {
		if (gpos == null) {
			computeGraphPosition();
		}
		return gpos;
	}
	
	public double distanceTo(GraphPositionPathPosition p) {
		
		assert p.path.equals(path);
		
		if (DMath.equals(p.combo, combo)) {
			return 0.0;
		} else if (DMath.greaterThanEquals(p.combo, combo)) {
			return p.lengthToStartOfPath - lengthToStartOfPath;
		} else {
			return lengthToStartOfPath - p.lengthToStartOfPath;
		}
		
	}
	
	private GraphPositionPathPosition travel(double dist, boolean forward) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		
		assert dist > 0.0;
		
		double traveled = 0.0;
		
		int curPathIndex = index;
		double curPathParam = param;
		GraphPosition curGraphPosition = getGraphPosition();
		double curLengthToStartOfPath = lengthToStartOfPath;
		
		while (true) {
			
			/*
			 * try to go to the next vertex first
			 */
			
			int nextVertexIndex = forward ? path.nextVertexIndex(curPathIndex, curPathParam) : path.prevVertexIndex(curPathIndex, curPathParam);
			GraphPosition nextVertexGP;
			double nextVertexLengthToStartOfPath;
			double distanceToNextVertexPosition;
			if (nextVertexIndex == -1) {
				/*
				 * there is no next vertex
				 */
				nextVertexGP = null;
				nextVertexLengthToStartOfPath = Double.POSITIVE_INFINITY;
				distanceToNextVertexPosition = Double.POSITIVE_INFINITY;
			} else {
				nextVertexGP = path.get(nextVertexIndex);
				nextVertexLengthToStartOfPath = path.cumulativeDistancesFromStart[nextVertexIndex];
				distanceToNextVertexPosition = forward ? nextVertexLengthToStartOfPath - curLengthToStartOfPath : curLengthToStartOfPath - nextVertexLengthToStartOfPath;
			}
			
			if (DMath.equals(traveled + distanceToNextVertexPosition, dist)) {
				
				return new GraphPositionPathPosition(path, nextVertexIndex, 0.0);
				
			} else if (traveled + distanceToNextVertexPosition < dist) {
				
				traveled += distanceToNextVertexPosition;
				
				curPathIndex = nextVertexIndex;
				curPathParam = 0.0;
				curGraphPosition = nextVertexGP;
				curLengthToStartOfPath = nextVertexLengthToStartOfPath;
				
			} else {
				/* 
				 * distanceToNextVertexPosition > toTravel == dist - traveled
				 */
				
				while (true) {
					
					/*
					 * try to go to the next bound
					 */
					int nextBoundIndex = forward ? (curPathIndex < path.size-1 ? curPathIndex+1 : -1) : (DMath.equals(curPathParam, 0.0) ? (curPathIndex > 0 ? curPathIndex-1 : -1) : curPathIndex);
					GraphPosition nextBoundGP;
					double nextBoundLengthToStartOfPath;
					double distanceToNextBound;
					if (nextBoundIndex == -1) {
						/*
						 * there is no next bound
						 */
						throw new IllegalArgumentException();
					} else {
						nextBoundGP = path.get(nextBoundIndex);
						nextBoundLengthToStartOfPath = path.cumulativeDistancesFromStart[nextBoundIndex];
						distanceToNextBound = forward ? nextBoundLengthToStartOfPath - curLengthToStartOfPath : curLengthToStartOfPath - nextBoundLengthToStartOfPath;
					}
					
					if (DMath.equals(traveled + distanceToNextBound, dist)) {

						return new GraphPositionPathPosition(path, nextBoundIndex, 0.0);
						
					} else if (traveled + distanceToNextBound < dist) {
						
						traveled += distanceToNextBound;
						
						curPathIndex = nextBoundIndex;
						curPathParam = 0.0;
						curGraphPosition = nextBoundGP;
						curLengthToStartOfPath = nextBoundLengthToStartOfPath;
						
					} else {
						/* 
						 * distanceToNextBound > toTravel == dist - traveled
						 */
						
						double toTravel = dist - traveled;
						
						GraphPosition ggoal = curGraphPosition.approachNeighbor(nextBoundGP, toTravel);
						
						double retCombo = curGraphPosition.goalGPPPCombo(curPathIndex, curPathParam, forward, ggoal);
						
						int retIndex = (int)Math.floor(retCombo);
						double retParam = retCombo - retIndex;
						
						GraphPositionPathPosition ret = new GraphPositionPathPosition(path, retIndex, retParam);
						double distToRet = this.distanceTo(ret);
						assert DMath.equals(distToRet, dist);
						
						return ret;
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public GraphPositionPathPosition travelForward(double dist) {
		return travel(dist, true);
	}
	
	public GraphPositionPathPosition travelBackward(double dist) {
		return travel(dist, false);
	}
	
	public GraphPositionPathPosition nextBound() {
		assert index < path.size-1;
		return new GraphPositionPathPosition(path, index+1, 0.0);
	}
	
	public GraphPositionPathPosition prevBound() {
		if (DMath.equals(param, 0.0)) {
			assert index > 0;
			return new GraphPositionPathPosition(path, index-1, 0.0);
		} else {
			return new GraphPositionPathPosition(path, index, 0.0);
		}
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
