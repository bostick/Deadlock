package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Position;
import com.gutabi.deadlock.core.VertexPosition;

public class GraphPositionPathPosition extends Position {
	
	private final GraphPositionPath path;
	public final int index;
	public final double param;
	private final boolean bound;
	
	private final GraphPosition gpos;
	
	private final double distanceFromStartOfPath;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(GraphPositionPathPosition.class);
	
	public GraphPositionPathPosition(GraphPositionPath path, int index, double param) {
		super(DMath.equals(param, 0.0) ?
				path.getGraphPosition(index).getPoint() :
					Point.point(path.getGraphPosition(index).getPoint(), path.getGraphPosition(index+1).getPoint(), param));
		
		this.path = path;
		this.index = index;
		this.param = param;
		this.bound = DMath.equals(param, 0.0);
		
		gpos = path.getGraphPosition(index, param);
		
		double acc = path.cumulativeDistancesFromStart[index];
		if (path.getGraphPosition(0).isBound()) {
			acc += path.getGraphPosition(0, 0.0).distanceTo(gpos);
		} else {
			acc += path.getGraphPosition(0, ((EdgePosition)path.getGraphPosition(0)).getParam()).distanceTo(gpos);
		}
		distanceFromStartOfPath = acc;
		
		int h = 17;
		h = 37 * h + path.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
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
		return index + " " + param + " (" + distanceFromStartOfPath + "/" + path.getTotalLength() + ")";
	}
	
	public double getCombo() {
		return index + param;
	}
	
	
	
	public boolean isBound() {
		return bound;
	}
	
	public boolean isEndOfPath() {
		return (index == path.size()-1) && DMath.equals(param, 0.0);
	}
	
	public GraphPositionPath getRestOfPath() {
		
		List<GraphPosition> acc = new ArrayList<GraphPosition>();
		GraphPositionPathPosition cur = this;
		while (!cur.isEndOfPath()) {
			acc.add(cur.gpos);
			cur = cur.nextBound();
		}
		
		if (!acc.isEmpty()) {
			return new GraphPositionPath(acc, path.graph);
		} else {
			return null;
		}
	}
	
	public GraphPosition getGraphPosition() {
		return gpos;
	}
	
	public double distanceTo(GraphPositionPathPosition p) {
		
		logger.debug("distanceTo");
		
		int goalIndex = p.index;
		double goalParam = p.param;
		
		if (index == goalIndex || (goalIndex == index+1 && DMath.equals(goalParam, 0.0))) {
			
			if (gpos.equals(p.gpos)) {
				String.class.getName();
			}
			
			logger.debug("done distanceTo");
			
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
			
			logger.debug("done distanceTo");
			
			if (DMath.equals(nextPos.param, goalParam)) {
				return acc;
			} else {
				
				return acc + nextPos.gpos.distanceTo(p.gpos);
				
			}
			
		}
	}
	
	public GraphPositionPathPosition travel(double dist) {
		
		logger.debug("travel");
		
		if (DMath.equals(dist, 0)) {
			return this;
		}
		
		double traveled = 0.0;
		
		GraphPositionPathPosition curPos = this;
		GraphPositionPathPosition nextPos;
		
		int iterations = 0;
		while (true) {
			
			if (iterations == 100) {
				String.class.getName();
			}
			
			nextPos = curPos.nextBound();
			double distanceToNextPos = curPos.distanceTo(nextPos);
			
			if (DMath.equals(traveled + distanceToNextPos, dist)) {
				logger.debug("1");
				
				logger.debug("done travel");
				return nextPos;
				
			} else if (traveled + distanceToNextPos < dist) {
				logger.debug("2");
				
				traveled += distanceToNextPos;
				curPos = nextPos;
				
			} else {
				logger.debug("3");
				
				double toTravel = dist - traveled;
				
				EdgePosition g = (EdgePosition)(curPos.gpos.travel(nextPos.gpos, toTravel));
				
				if (curPos.gpos instanceof EdgePosition) {
					EdgePosition curPosE = (EdgePosition)curPos.gpos;
					
					logger.debug("done travel");
					if (curPosE.getIndex() < g.getIndex() || (curPosE.getIndex() == g.getIndex() && DMath.lessThan(curPosE.getParam(), g.getParam()))) {
						// same direction as edge
						//assert curPosE.getIndex() == g.getIndex();
						return new GraphPositionPathPosition(path, curPos.index, g.getParam());
					} else {
						return new GraphPositionPathPosition(path, curPos.index, 1-g.getParam());
					}
					
				} else {
					VertexPosition curPosE = (VertexPosition)curPos.gpos;
					
					logger.debug("done travel");
					if (g.getEdge().getStart() == curPosE.getVertex()) {
						// same direction as edge
						return new GraphPositionPathPosition(path, curPos.index, g.getParam());
					} else {
						return new GraphPositionPathPosition(path, curPos.index, 1-g.getParam());
					}
					
				}
				
			}
			
			iterations++;
		}
		
	}
	
	public GraphPositionPathPosition nextBound() {
		return new GraphPositionPathPosition(path, index+1, 0.0);
	}
}
