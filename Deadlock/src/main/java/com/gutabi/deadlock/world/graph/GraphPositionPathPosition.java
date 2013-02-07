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
	
//	static Logger logger = Logger.getLogger(GraphPositionPathPosition.class);
	
	public GraphPositionPathPosition(GraphPositionPath path, int index, double param) {
		
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
		
		double dist = Point.distance(p1.p, p2.p);
		
		gpos = p1.travelToNeighbor(p2, dist * param);
		
	}
	
	public GraphPosition getGraphPosition() {
		if (gpos == null) {
			computeGraphPosition();
		}
		return gpos;
	}
	
	public double distanceTo(GraphPositionPathPosition p) {
		
		assert p.path.equals(path);
		assert DMath.greaterThanEquals(p.combo, combo);
		
		return p.lengthToStartOfPath - lengthToStartOfPath;
		
	}
	
	public GraphPositionPathPosition travel(double dist) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		
		assert dist > 0.0;
		
		double traveled = 0.0;
		
		int curIndex = index;
		GraphPosition curPosition = getGraphPosition();
		double curLengthToStartOfPath = lengthToStartOfPath;
		
		while (true) {
			
			int nextVertexIndex = nextVertexIndex(curIndex);
			GraphPosition nextVertexGP = path.get(nextVertexIndex);
			double nextVertexLengthToStartOfPath = path.cumulativeDistancesFromStart[nextVertexIndex];
			
			double distanceToNextVertexPosition = nextVertexLengthToStartOfPath - curLengthToStartOfPath;
			
			if (DMath.equals(traveled + distanceToNextVertexPosition, dist)) {
				
				return new GraphPositionPathPosition(path, nextVertexIndex, 0.0);
				
			} else if (traveled + distanceToNextVertexPosition < dist) {
				
				traveled += distanceToNextVertexPosition;
				
				curIndex = nextVertexIndex;
				curPosition = nextVertexGP;
				curLengthToStartOfPath = nextVertexLengthToStartOfPath;
				
			} else {
				/*
				 * traveled + distanceToNextVertexPosition > dist
				 */
				
				while (true) {
					
					int nextBoundIndex = curIndex+1;
					GraphPosition nextBoundGP = path.get(nextBoundIndex);
					double nextBoundLengthToStartOfPath = path.cumulativeDistancesFromStart[nextBoundIndex];
					
					double distanceToNextBound = nextBoundLengthToStartOfPath - curLengthToStartOfPath;
					
					if (DMath.equals(traveled + distanceToNextBound, dist)) {

						return new GraphPositionPathPosition(path, nextBoundIndex, 0.0);
						
					} else if (traveled + distanceToNextBound < dist) {
						
						traveled += distanceToNextBound;
						
						curIndex = nextBoundIndex;
						curPosition = nextBoundGP;
						curLengthToStartOfPath = nextBoundLengthToStartOfPath;
						
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
						
						EdgePosition g = (EdgePosition)curPosition.travelToNeighbor(nextBoundGP, toTravel);
						
						if (curPosition instanceof EdgePosition) {
							EdgePosition curPosE = (EdgePosition)curPosition;
							
							if (curPosE.getCombo() < g.getCombo()) {
								// same direction as edge
								return new GraphPositionPathPosition(path, curIndex, g.getParam());
							} else {
								return new GraphPositionPathPosition(path, curIndex, 1-g.getParam());
							}
							
						} else {
							
							if (((VertexPosition)curPosition).v == ((Edge)((EdgePosition)nextBoundGP).entity).getReferenceVertex(((EdgePosition)nextBoundGP).axis)) {
								// same direction as edge
								return new GraphPositionPathPosition(path, curIndex, g.getParam());
							} else {
								assert ((VertexPosition)curPosition).v == ((Edge)((EdgePosition)nextBoundGP).entity).getOtherVertex(((EdgePosition)nextBoundGP).axis);
								return new GraphPositionPathPosition(path, curIndex, 1-g.getParam());
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
	
	public GraphPositionPathPosition prevBound() {
		if (DMath.equals(param, 0.0)) {
			return new GraphPositionPathPosition(path, index-1, 0.0);
		} else {
			return new GraphPositionPathPosition(path, index, 0.0);
		}
	}
	
	public int nextVertexIndex(int index) {
		int i = index+1;
		while (true) {
			GraphPosition gpos = path.get(i);
			if (gpos instanceof VertexPosition) {
				return i;
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
