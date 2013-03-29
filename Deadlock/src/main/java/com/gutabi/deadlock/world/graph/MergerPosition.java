package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;

public class MergerPosition extends EdgePosition {

	public final Merger m;
	public final int index;
	public final double param;
	public final double combo;
	
	public final double distanceToLeftOfMerger;
	public final double distanceToRightOfMerger;
	public final double distanceToTopOfMerger;
	public final double distanceToBottomOfMerger;
	
	public final boolean bound;
	
	private int hash;
	
	public MergerPosition(Merger m, Axis a, int index, double param) {
		super(Point.point(m.get(index, a), m.get(index+1, a), param), m, a);
		
		this.m = m;
		
		if (index < 0 || index >= 2) {
			throw new IllegalArgumentException();
		}
		if (DMath.lessThan(param, 0.0) || DMath.greaterThanEquals(param, 1.0)) {
			throw new IllegalArgumentException();
		}
		if (index == 0 && DMath.equals(param, 0.0)) {
			throw new IllegalArgumentException();
		}
		
		this.index = index;
		this.param = param;
		
		combo = index+param;
		
		if (DMath.equals(param, 0.0)) {
			bound = true;
		} else {
			bound = false;
		}
		
		Point b = m.get(index, a);
		
		if (a==Axis.LEFTRIGHT) {
			distanceToLeftOfMerger = m.getLengthFromLeft(index) + Point.distance(p, b);
			distanceToRightOfMerger = Merger.MERGER_WIDTH-distanceToLeftOfMerger;
			distanceToTopOfMerger = -1;
			distanceToBottomOfMerger = -1;
		} else {
			distanceToTopOfMerger = m.getLengthFromTop(index) + Point.distance(p, b);
			distanceToBottomOfMerger = Merger.MERGER_HEIGHT-distanceToTopOfMerger;
			distanceToLeftOfMerger = -1;
			distanceToRightOfMerger = -1;
		}
		
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + m.hashCode();
			h = 37 * h + axis.hashCode();
			h = 37 * h + index;
			long l = Double.doubleToLongBits(param);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
	}
	
	public String toString() {
		return m + " " + index + " " + param + " (" + (axis==Axis.LEFTRIGHT?distanceToLeftOfMerger:distanceToTopOfMerger) + "/" + (axis==Axis.LEFTRIGHT?Merger.MERGER_WIDTH:Merger.MERGER_HEIGHT) + ")";
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof MergerPosition)) {
			return false;
		} else {
			MergerPosition b = (MergerPosition)o;
			return (m == b.m) && (axis == b.axis) && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getParam() {
		return param;
	}
	
	public double getCombo() {
		return combo;
	}
	
	public GraphPosition nextBoundTowardOtherVertex() {
		if (axis == Axis.LEFTRIGHT) {
			return nextBoundToRight(m, index, param);
		} else {
			return nextBoundToBottom(m, index, param);
		}
	}
	
	public GraphPosition nextBoundTowardReferenceVertex() {
		if (axis == Axis.LEFTRIGHT) {
			return nextBoundToLeft(m, index, param);
		} else {
			return nextBoundToTop(m, index, param);
		}
	}
	
	public Entity getEntity() {
		return m;
	}
	
	public double distanceToConnectedVertex(Vertex v) {
		if (axis == Axis.LEFTRIGHT) {
			if (v == m.left) {
				return distanceToLeftOfMerger;
			} else {
				return distanceToRightOfMerger;
			}
		} else {
			if (v == m.top) {
				return distanceToTopOfMerger;
			} else {
				return distanceToBottomOfMerger;
			}
		}
	}
	
	public GraphPosition travelToReferenceVertex(Axis a, double dist) {
		if (a == Axis.TOPBOTTOM) {
			return travelToTop(m, index, param, dist);
		} else {
			assert a == Axis.LEFTRIGHT;
			return travelToLeft(m, index, param, dist);	
		}
	}
	
	public GraphPosition travelToOtherVertex(Axis a, double dist) {
		if (a == Axis.TOPBOTTOM) {
			return travelToBottom(m, index, param, dist);
		} else {
			assert a == Axis.LEFTRIGHT;
			return travelToRight(m, index, param, dist);	
		}
	}
	
	public static GraphPosition travelFromTop(Merger m, double dist) {
		return travelToBottom(m, 0, 0.0, dist);
	}
	
	public static GraphPosition travelFromBottom(Merger m, double dist) {
		return travelToTop(m, 1, 1.0, dist);
	}
	
	public static GraphPosition travelFromLeft(Merger m, double dist) {
		return travelToRight(m, 0, 0.0, dist);
	}
	
	public static GraphPosition travelFromRight(Merger m, double dist) {
		return travelToLeft(m, 1, 1.0, dist);
	}
	
	public static GraphPosition nextBoundFromTop(Merger m) {
		return nextBoundToBottom(m, 0, 0.0);
	}
	
	public static GraphPosition nextBoundFromBottom(Merger m) {
		return nextBoundToTop(m, 1, 1.0);
	}
	
	public static GraphPosition nextBoundFromLeft(Merger m) {
		return nextBoundToRight(m, 0, 0.0);
	}
	
	public static GraphPosition nextBoundFromRight(Merger m) {
		return nextBoundToLeft(m, 1, 1.0);
	}
	
	
	
	
	
	private static GraphPosition nextBoundToLeft(Merger m, int index, double param) {
		if (index == 0) {
			return new VertexPosition(m.left);
		} else {
			assert index == 1;
			if (DMath.equals(param, 0.0)) {
				return new VertexPosition(m.left);
			} else {
				return new MergerPosition(m, Axis.LEFTRIGHT, 1, 0.0);
			}
		}
	}
	
	private static GraphPosition nextBoundToRight(Merger m, int index, double param) {
		if (index == 0) {
			return new MergerPosition(m, Axis.LEFTRIGHT, 1, 0.0);
		} else {
			assert index == 1;
			return new VertexPosition(m.right);
		}
	}

	private static GraphPosition nextBoundToTop(Merger m, int index, double param) {
		if (index == 0) {
			return new VertexPosition(m.top);
		} else {
			assert index == 1;
			if (DMath.equals(param, 0.0)) {
				return new VertexPosition(m.top);
			} else {
				return new MergerPosition(m, Axis.TOPBOTTOM, 1, 0.0);
			}
		}
	}
	
	private static GraphPosition nextBoundToBottom(Merger m, int index, double param) {
		if (index == 0) {
			return new MergerPosition(m, Axis.TOPBOTTOM, 1, 0.0);
		} else {
			assert index == 1;
			return new VertexPosition(m.bottom);
		}
	}
	
	private static GraphPosition travelToLeft(Merger m, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = m.get(index, Axis.LEFTRIGHT);
			Point b = m.get(index+1, Axis.LEFTRIGHT);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.distance(c, a);
			
			if (DMath.equals(distanceToTravel, distanceToStartOfSegment)) {
				return new MergerPosition(m, Axis.LEFTRIGHT, index, 0.0);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new MergerPosition(m, Axis.LEFTRIGHT, index, newParam);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
	private static GraphPosition travelToRight(Merger m, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = m.get(index, Axis.LEFTRIGHT);
			Point b = m.get(index+1, Axis.LEFTRIGHT);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.distance(c, b);
			
			if (DMath.equals(distanceToTravel, distanceToEndOfSegment)) {
				return new MergerPosition(m, Axis.LEFTRIGHT, index+1, 0.0);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new MergerPosition(m, Axis.LEFTRIGHT, index, newParam);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
		
	}
	
	private static GraphPosition travelToTop(Merger m, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = m.get(index, Axis.TOPBOTTOM);
			Point b = m.get(index+1, Axis.TOPBOTTOM);
			
			Point c = Point.point(a, b, param);
			double distanceToStartOfSegment = Point.distance(c, a);
			
			if (DMath.equals(distanceToTravel, distanceToStartOfSegment)) {
				return new MergerPosition(m, Axis.TOPBOTTOM, index, 0.0);
			} else if (distanceToTravel < distanceToStartOfSegment) {
				double newParam = Point.travelBackward(a, b, param, distanceToTravel);
				return new MergerPosition(m, Axis.TOPBOTTOM, index, newParam);
			} else {
				index--;
				param = 1.0;
				distanceToTravel -= distanceToStartOfSegment;
			}
		}
		
	}
	
	private static GraphPosition travelToBottom(Merger m, int index, double param, double dist) {
		
		double distanceToTravel = dist;
		
		while (true) {
			Point a = m.get(index, Axis.TOPBOTTOM);
			Point b = m.get(index+1, Axis.TOPBOTTOM);
			
			Point c = Point.point(a, b, param);
			double distanceToEndOfSegment = Point.distance(c, b);
			
			if (DMath.equals(distanceToTravel, distanceToEndOfSegment)) {
				return new MergerPosition(m, Axis.TOPBOTTOM, index+1, 0.0);
			} else if (distanceToTravel < distanceToEndOfSegment) {
				double newParam = Point.travelForward(a, b, param, distanceToTravel);
				return new MergerPosition(m, Axis.TOPBOTTOM, index, newParam);
			} else {
				index++;
				param = 0.0;
				distanceToTravel -= distanceToEndOfSegment;
			}
		}
		
	}
	
}
