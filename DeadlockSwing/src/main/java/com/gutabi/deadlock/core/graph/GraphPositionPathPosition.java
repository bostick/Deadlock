package com.gutabi.deadlock.core.graph;

import java.util.Comparator;

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
		acc += path.get(index).distanceTo(gpos);
		
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
		} else if (!hash) {
			
		} else if (!(o instanceof GraphPositionPathPosition)) {
			return false;
		} else {
			GraphPositionPathPosition b = (GraphPositionPathPosition)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public static Comparator<GraphPositionPathPosition> COMPARATOR = new GraphPositionPathPositionComparator();
	
	static class GraphPositionPathPositionComparator implements Comparator<GraphPositionPathPosition> {

		public int compare(GraphPositionPathPosition a, GraphPositionPathPosition b) {
			if (a.equals(b)) {
				return 0;
			}
			assert a.path == b.path;
			if (a.combo < b.combo) {
				return -1;
			} else {
				return 1;
			}
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
		
		assert DMath.greaterThanEquals(p.combo, combo);
		
		return p.lengthToStartOfPath - lengthToStartOfPath;
		
	}
	
//	private Map<GraphPositionPathPosition, Double> distMap = new HashMap<GraphPositionPathPosition, Double>();
//	int hashedCount;
//	int unhashedCount;
//	
//	public double distanceTo(GraphPositionPathPosition p) {
//		
//		assert p.path == path;
//		
//		assert DMath.greaterThanEquals(p.combo, combo);
//		
//		Double hashedDist = distMap.get(p);
//		if (hashedDist != null) {
//			hashedCount++;
//			return hashedDist;
//		} else {
//			unhashedCount++;
//		}
//		
//		int goalIndex = p.index;
//		double goalParam = p.param;
//		
//		if (index == goalIndex || (goalIndex == index+1 && DMath.equals(goalParam, 0.0))) {
//			
////			if (gpos.equals(p.gpos)) {
////				String.class.getName();
////			}
//			
//			double toHash = gpos.distanceTo(p.gpos);
//			
//			distMap.put(p, toHash);
//			
//			return toHash;
//			
//		} else {
//			
//			double acc = 0.0;
//			GraphPositionPathPosition curPos = this;
//			GraphPositionPathPosition nextPos = curPos.nextBound();
//			
//			acc += curPos.distanceTo(nextPos);
//			
//			while (true) {
//				
//				if (nextPos.index == goalIndex) {
//					break;
//				}
//				
//				curPos = nextPos;
//				nextPos = curPos.nextBound();
//				
//				acc += curPos.distanceTo(nextPos);
//				
//			}
//			
//			if (DMath.equals(nextPos.param, goalParam)) {
//				
//				distMap.put(p, acc);
//				
//				return acc;
//				
//			} else {
//				
//				acc = acc + nextPos.gpos.distanceTo(p.gpos);
//				
//				distMap.put(p, acc);
//				
//				return acc;
//				
//			}
//			
//		}
//	}
	
	public GraphPositionPathPosition travel(double dist) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		
		assert dist > 0.0;
		
		/*
		 * find combinations that would make computing easier
		 */
		d;
		
		double traveled = 0.0;
		
		GraphPositionPathPosition curPos = this;
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
				
				EdgePosition g = (EdgePosition)curPos.gpos.travelTo(nextBound.gpos, toTravel);
				
				double fromCurToG = curPos.gpos.distanceTo(g);
				assert DMath.equals(fromCurToG, toTravel);
				
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
					
					if (g.entity.getVertices(g.axis).get(0) == curPosE.entity.getVertices(curPosE.axis).get(0)) {
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
