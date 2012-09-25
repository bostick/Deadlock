package com.gutabi.deadlock.core;

public class PathPosition extends Position {
	
	private final Path path;
	private final int index;
	private final double param;
	private final boolean bound;
	
	private final GraphPosition gpos;
	
	private final double distanceFromStartOfPath;
	
	private final int hash;
	
	public PathPosition(Path path, int index, double param) {
		super(DMath.equals(param, 0.0) ? path.getPoint(index) : Point.point(path.getPoint(index), path.getPoint(index+1), param));
		
		this.path = path;
		this.index = index;
		this.param = param;
		this.bound = DMath.equals(param, 0.0);
		
		gpos = path.getGraphPosition(index, param);
		
//		if (gpos instanceof EdgePosition) {
//			assert DMath.equals(((EdgePosition)gpos).getParam(), param);
//		} else {
//			assert DMath.equals(param, 0.0);
//		}
		
		double acc = path.cumulativeDistancesFromStart[index];
		acc += path.getGraphPosition(0, 0.0).distanceTo(gpos);
		distanceFromStartOfPath = acc;
		
		int h = 17;
		h = 37 * h + path.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public boolean isEndOfPath() {
		return (index == path.size()-1) && DMath.equals(param, 0.0);
	}
	
	public String toString() {
		return "Path " + path + " " + index + " " + param + "(" + distanceFromStartOfPath + "/" + path.getTotalLength() + ")" + gpos;
	}
	
	public GraphPosition getGraphPosition() {
		return gpos;
	}
	
	public Driveable getDriveable() {
		return gpos.getDriveable();
	}

//	@Override
//	public double distanceTo(Position b) {
//		return gpos.distanceTo(b);
//	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof PathPosition)) {
			return false;
		} else {
			PathPosition b = (PathPosition)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public int hashCode() {
		return hash;
	}
	
	public double distanceTo(PathPosition p) {
		
		int goalIndex = p.index;
		double goalParam = p.param;
		
		if (index == goalIndex || (goalIndex == index+1 && DMath.equals(goalParam, 0.0))) {
			
			if (gpos.equals(p.gpos)) {
				String.class.getName();
			}
			
			return gpos.distanceTo(p.gpos);
			
		} else {
			
			double acc = 0.0;
			PathPosition curPos = this;
			PathPosition nextPos = curPos.nextBound();
			
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
	
	public PathPosition travel(double dist) {
		
		double traveled = 0.0;
		
		PathPosition curPos = this;
		PathPosition nextPos;
		
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
				
				EdgePosition g = (EdgePosition)curPos.gpos.travel(nextPos.gpos, toTravel);
				
				if (curPos.getGraphPosition() instanceof EdgePosition) {
					EdgePosition curPosE = (EdgePosition)curPos.getGraphPosition();
					
					if (curPosE.getIndex() < g.getIndex() || (curPosE.getIndex() == g.getIndex() && DMath.lessThan(curPosE.getParam(), g.getParam()))) {
						// same direction as edge
						//assert curPosE.getIndex() == g.getIndex();
						return new PathPosition(path, curPos.index, g.getParam());
					} else {
						return new PathPosition(path, curPos.index, 1-g.getParam());
					}
					
				} else {
					Vertex curPosE = (Vertex)curPos.getGraphPosition();
					
					if (g.getEdge().getStart() == curPosE) {
						// same direction as edge
						return new PathPosition(path, curPos.index, g.getParam());
					} else {
						return new PathPosition(path, curPos.index, 1-g.getParam());
					}
					
				}
				
			}
			
		}
		
	}
	
	public PathPosition nextBound() {
		return new PathPosition(path, index+1, 0.0);
	}
}
