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
		
		int curIndex = index;
		double curParam = param;
		GraphPosition curPosition = getGraphPosition();
		double curLengthToStartOfPath = lengthToStartOfPath;
		
		while (true) {
			
			/*
			 * try to go to the next vertex first
			 */
			
			int nextVertexIndex = forward ? nextVertexIndex() : prevVertexIndex();
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
				
				curIndex = nextVertexIndex;
				curParam = 0.0;
				curPosition = nextVertexGP;
				curLengthToStartOfPath = nextVertexLengthToStartOfPath;
				
			} else {
				/* 
				 * distanceToNextVertexPosition > toTravel == dist - traveled
				 */
				
				while (true) {
					
					/*
					 * try to go to the next bound
					 */
					int nextBoundIndex = forward ? (curIndex < path.size-1 ? curIndex+1 : -1) : (DMath.equals(curParam, 0.0) ? (curIndex > 0 ? curIndex-1 : -1) : curIndex);
					GraphPosition nextBoundGP;
					double nextBoundLengthToStartOfPath;
					double distanceToNextBound;
					if (nextBoundIndex == -1) {
						/*
						 * there is no next bound
						 */
						assert false;
						nextBoundGP = null;
						nextBoundLengthToStartOfPath = Double.POSITIVE_INFINITY;
						distanceToNextBound = Double.POSITIVE_INFINITY;
					} else {
						nextBoundGP = path.get(nextBoundIndex);
						nextBoundLengthToStartOfPath = path.cumulativeDistancesFromStart[nextBoundIndex];
						distanceToNextBound = forward ? nextBoundLengthToStartOfPath - curLengthToStartOfPath : curLengthToStartOfPath - nextBoundLengthToStartOfPath;
					}
					
					if (DMath.equals(traveled + distanceToNextBound, dist)) {

						return new GraphPositionPathPosition(path, nextBoundIndex, 0.0);
						
					} else if (traveled + distanceToNextBound < dist) {
						
						traveled += distanceToNextBound;
						
						curIndex = nextBoundIndex;
						curParam = 0.0;
						curPosition = nextBoundGP;
						curLengthToStartOfPath = nextBoundLengthToStartOfPath;
						
					} else {
						/* 
						 * distanceToNextBound > toTravel == dist - traveled
						 * 
						 * we are not going to reach the next bound, so we know it has to be an EdgePosition
						 */
						
						double toTravel = dist - traveled;
						
						EdgePosition g = (EdgePosition)curPosition.approachNeighbor(nextBoundGP, toTravel);
						
						if (curPosition instanceof EdgePosition) {
							EdgePosition curPosE = (EdgePosition)curPosition;
							
							if (forward ? curPosE.getCombo() < g.getCombo() : curPosE.getCombo() > g.getCombo()) {
								// same direction as edge
								int retIndex = forward ? (DMath.equals(curParam, 0.0) ? curIndex : curIndex) : DMath.equals(curParam, 0.0) ? curIndex-1 : curIndex;
								double retParam = forward ? (DMath.equals(curParam, 0.0) ? g.getParam() : g.getParam()) : DMath.equals(curParam, 0.0) ? g.getParam() : g.getParam();
								
								GraphPositionPathPosition ret = new GraphPositionPathPosition(path, retIndex, retParam);
								double distToRet = this.distanceTo(ret);
								assert DMath.equals(distToRet, dist);
								
								return ret;
							} else {
								int retIndex = forward ? (DMath.equals(curParam, 0.0) ? curIndex : curIndex) : DMath.equals(curParam, 0.0) ? curIndex-1 : curIndex;
								double retParam = forward ? (DMath.equals(curParam, 0.0) ? 1-g.getParam() : 1-g.getParam()) : DMath.equals(curParam, 0.0) ? 1-g.getParam() : 1-g.getParam();
								
								GraphPositionPathPosition ret = new GraphPositionPathPosition(path, retIndex, retParam);
								double distToRet = this.distanceTo(ret);
								assert DMath.equals(distToRet, dist);
								
								return ret;
							}
							
						} else {
							assert false;
							
							EdgePosition nextBoundEdgePos = ((EdgePosition)nextBoundGP);
							Edge nextBoundEdge = (Edge)nextBoundEdgePos.entity;
							
							Vertex curVertex = ((VertexPosition)curPosition).v;
							Vertex nextBoundRefVertex = nextBoundEdge.getReferenceVertex(nextBoundEdgePos.axis);
							Vertex nextBoundOtherVertex = nextBoundEdge.getOtherVertex(nextBoundEdgePos.axis);
							
							if (forward ? curVertex == nextBoundRefVertex : curVertex == nextBoundOtherVertex) {
								// same direction as edge
								int retIndex = forward ? (DMath.equals(curParam, 0.0) ? curIndex : curIndex) : DMath.equals(curParam, 0.0) ? curIndex-1 : curIndex;
								double retParam = forward ? (DMath.equals(curParam, 0.0) ? g.getParam() : g.getParam()) : DMath.equals(curParam, 0.0) ? g.getParam() : g.getParam();
								
								GraphPositionPathPosition ret = new GraphPositionPathPosition(path, retIndex, retParam);
								double distToRet = this.distanceTo(ret);
								assert DMath.equals(distToRet, dist);
								
								return ret;
							} else {
								assert forward ? curVertex == nextBoundOtherVertex : curVertex == nextBoundRefVertex;
								int retIndex = forward ? (DMath.equals(curParam, 0.0) ? curIndex : curIndex) : DMath.equals(curParam, 0.0) ? curIndex-1 : curIndex;
								double retParam = forward ? (DMath.equals(curParam, 0.0) ? 1-g.getParam() : 1-g.getParam()) : DMath.equals(curParam, 0.0) ? 1-g.getParam() : 1-g.getParam();
								
								GraphPositionPathPosition ret = new GraphPositionPathPosition(path, retIndex, retParam);
								double distToRet = this.distanceTo(ret);
								assert DMath.equals(distToRet, dist);
								
								return ret;
							}
						}
						
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
	
	public int nextVertexIndex() {
		if (!(index < path.size-1)) {
			return -1;
		}
		int i = index+1;
		while (true) {
			GraphPosition gpos = path.get(i);
			if (gpos instanceof VertexPosition) {
				return i;
			}
			if (!(i < path.size)) {
				return -1;
			}
			i++;
		}
	}
	
	public int prevVertexIndex() {
		if (!(DMath.equals(param, 0.0) ? index > 0 : true)) {
			return -1;
		}
		int i = DMath.equals(param, 0.0) ? index - 1 : index;
		while (true) {
			GraphPosition gpos = path.get(i);
			if (gpos instanceof VertexPosition) {
				return i;
			}
			if (!(i > 0)) {
				return -1;
			}
			i--;
		}
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
