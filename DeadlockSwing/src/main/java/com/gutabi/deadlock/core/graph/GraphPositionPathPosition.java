package com.gutabi.deadlock.core.graph;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class GraphPositionPathPosition {
	
	public final GraphPositionPath path;
	public final int index;
	public final double param;
	
	public final double combo;
	public final boolean bound;
	
	public final GraphPosition gpos;
	
	public final double lengthToStartOfPath;
	public final double lengthToEndOfPath;
	
	private int hash;
	
	static Logger logger = Logger.getLogger(GraphPositionPathPosition.class);
	
	public GraphPositionPathPosition(GraphPositionPath path, int index, double param) {
		
		this.path = path;
		this.index = index;
		this.param = param;
		
		combo = index+param;
		
		this.bound = DMath.equals(param, 0.0);
		gpos = path.getGraphPosition(index, param);
		
		double acc = path.cumulativeDistancesFromStart[index];
		acc += Point.distance(path.get(index).p, gpos.p);
		
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
		return "GPPP[...] " + index + " " + param + " " + gpos;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public boolean isEndOfPath() {
		return (index == path.size-1) && DMath.equals(param, 0.0);
	}
	
	
	public double distanceTo(GraphPositionPathPosition p) {
		
		assert p.path == path;
//		assert !equals(p);
		assert DMath.greaterThanEquals(p.combo, combo);
		
		return p.lengthToStartOfPath - lengthToStartOfPath;
		
	}
	
	public GraphPositionPathPosition travel(double dist) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		
		assert dist > 0.0;
		
		double traveled = 0.0;
		
		GraphPositionPathPosition curPos = this;
		GraphPositionPathPosition nextVertexPosition;
		
		while (true) {
			
			nextVertexPosition = curPos.nextVertexPosition();
			double distanceToNextVertexPosition = curPos.distanceTo(nextVertexPosition);
			
			if (DMath.equals(traveled + distanceToNextVertexPosition, dist)) {
				
				return nextVertexPosition;
				
			} else if (traveled + distanceToNextVertexPosition < dist) {
				
				traveled += distanceToNextVertexPosition;
				curPos = nextVertexPosition;
				
			} else {
				/*
				 * traveled + distanceToNextVertexPosition > dist
				 */
				 
				GraphPositionPathPosition nextBound;
				
				while (true) {
					
					nextBound = curPos.nextBound();
					double distanceToNextBound = curPos.distanceTo(nextBound);
					
					if (DMath.equals(traveled + distanceToNextBound, dist)) {

						return nextBound;
						
					} else if (traveled + distanceToNextBound < dist) {
						
						traveled += distanceToNextBound;
						curPos = nextBound;
						
					} else {
						/*
						 * traveled + distanceToNextBound > dist, so
						 * 
						 * distanceToNextBound > dist - traveled
						 * 
						 * distanceToNextBound > toTravel
						 * 
						 * we are not going to reach the next bound, so we know it has to be an EdgePosition
						 */
						
						double toTravel = dist - traveled;
						
						EdgePosition g = (EdgePosition)curPos.gpos.travelToNeighbor(nextBound.gpos, toTravel);
						
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
							VertexPosition curPosV = (VertexPosition)curPos.gpos;
							
							assert !(g.entity instanceof Road && ((Road)g.entity).isLoop());
							
							if (g.entity.getReferenceVertex(g.axis) == curPosV.v) {
								// same direction as edge
								return new GraphPositionPathPosition(path, curPos.index, g.getParam());
							} else {
								return new GraphPositionPathPosition(path, curPos.index, 1-g.getParam());
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public GraphPositionPathPosition nextBound() {
		assert index < path.size-1;
		return new GraphPositionPathPosition(path, index+1, 0.0);
	}
	
	public GraphPositionPathPosition nextVertexPosition() {
		int i = index+1;
		while (true) {
			GraphPosition gpos = path.get(i);
			if (gpos instanceof VertexPosition) {
				return new GraphPositionPathPosition(path, i, 0.0);
			}
			i++;
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
