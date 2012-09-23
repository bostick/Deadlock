package com.gutabi.deadlock.core;

/**
 * the smallest unit of a path
 * either goes from start to end of an edge, or start to middle, or end to middle, or middle to middle
 * 
 * or sinked position
 */
public class SubPath {
	
	private double startTime;
	private double endTime;
	
	private STPosition start;
	private STPosition end;
	
	private Edge e;
	
	public SubPath(STPosition start, STPosition end) {
		this.start = start;
		this.end = end;
		
		startTime = start.getTime();
		endTime = end.getTime();
		
		if (start.getSpace() instanceof Vertex) {
			if (end.getSpace() instanceof Vertex) {
				if (start.getSpace().equals(end.getSpace())) {
					e = null;
				} else {
					e = (Edge)Vertex.commonConnector((Vertex)start.getSpace(), (Vertex)end.getSpace());
				} 
			} else {
				e = ((EdgePosition)end.getSpace()).getEdge();
				assert ((Vertex)start.getSpace()).getEdges().contains(e);
			}
		} else if (start.getSpace() instanceof EdgePosition) {
			if (end.getSpace() instanceof Vertex) {
				e = ((EdgePosition)start.getSpace()).getEdge();
				assert ((Vertex)end.getSpace()).getEdges().contains(e);
			} else if (end.getSpace() instanceof EdgePosition) {
				e = ((EdgePosition)start.getSpace()).getEdge();
				assert ((EdgePosition)end.getSpace()).getEdge() == e;
			} else {
//					e = ((EdgePosition)start.getSpace()).getEdge();
//					assert ((SinkedPosition)end.getSpace()).getSink().getEdges().contains(e);
				throw new AssertionError();
			}
		} else {
//				assert ((SinkedPosition)start.getSpace()).getSink() == ((SinkedPosition)end.getSpace()).getSink();
//				e = null;
			throw new AssertionError();
		}
		
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public double getStartTime() {
		return startTime;
	}
	
	public double getEndTime() {
		return endTime;
	}
	
	public Position getStartPosition() {
		return start.getSpace();
	}
	
	public Position getEndPosition() {
		return end.getSpace();
	}
	
	public Position getPosition(double time) {
		if (time < startTime) {
			throw new IllegalArgumentException();
		}
		if (time > endTime) {
			throw new IllegalArgumentException();
		}
		
		if (DMath.equals(time, startTime)) {
			return start.getSpace();
		} else if (DMath.equals(time, endTime)) {
			return end.getSpace();
		} else {
			if (start.getSpace().equals(end.getSpace())) {
				return start.getSpace();
			} else {
				double p = (time - start.getTime()) / (end.getTime() - start.getTime());
				double d = start.getSpace().distanceTo(end.getSpace());
				return start.getSpace().travel(end.getSpace(), p * d);
			}
		}
	}
	
}
