package com.gutabi.deadlock.core.graph;

import java.util.ArrayList;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public class MergerPosition extends EdgePosition {

	public final Merger m;
	public final int index;
	public final double param;
	
	public final double distanceToLeftOfMerger;
	public final double distanceToRightOfMerger;
	public final double distanceToTopOfMerger;
	public final double distanceToBottomOfMerger;
	
	public final boolean bound;
	
	private final int hash;
	
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
		
		int h = 17;
		h = 37 * h + m.hashCode();
		h = 37 * h + a.hashCode();
		h = 37 * h + index;
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		if (a==Axis.LEFTRIGHT) {
			vs = new ArrayList<Vertex>();
			vs.add(m.left);
			vs.add(m.right);
		} else {
			vs = new ArrayList<Vertex>();
			vs.add(m.top);
			vs.add(m.bottom);
		}
		
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
		return hash;
	}
	
	public String toString() {
		return m + " " + index + " " + param + " (" + (a==Axis.LEFTRIGHT?distanceToLeftOfMerger:distanceToTopOfMerger) + "/" + (a==Axis.LEFTRIGHT?Merger.MERGER_WIDTH:Merger.MERGER_HEIGHT) + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPosition)) {
			throw new IllegalArgumentException();
		} else if (!(o instanceof MergerPosition)) {
			return false;
		} else {
			MergerPosition b = (MergerPosition)o;
			return (m == b.m) && (a == b.a) && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public GraphPosition floor() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != 0) {
			return new MergerPosition(m, a, index, 0.0);
		} else {
			return (a==Axis.LEFTRIGHT) ? new VertexPosition(m.left) : new VertexPosition(m.top);
		}
	}
	
	public GraphPosition ceiling() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else if (index != 1) {
			return new MergerPosition(m, a, index+1, 0.0);
		} else {
			return (a==Axis.LEFTRIGHT) ? new VertexPosition(m.right) : new VertexPosition(m.bottom);
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
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof VertexPosition) {
			
			VertexPosition vg = (VertexPosition)goal;
			
			if (a==Axis.LEFTRIGHT) {
				assert vg.v == m.left || vg.v == m.right;
				
				if (vg.v == m.left) {
					return nextBoundToLeft(m, index, param);
				} else {
					return nextBoundToRight(m, index, param);
				}
				
			} else {
				assert vg.v == m.top || vg.v == m.bottom;
				
				if (vg.v == m.top) {
					return nextBoundToTop(m, index, param);
				} else {
					return nextBoundToBottom(m, index, param);
				}
				
			}
			
		} else {
			MergerPosition mg = (MergerPosition)goal;
			assert mg.a == a;
			assert !equals(mg);
			
			if (a==Axis.LEFTRIGHT) {
				
				if (distanceToLeftOfMerger < mg.distanceToLeftOfMerger) {
					return nextBoundToRight(m, index, param);
				} else {
					return nextBoundToLeft(m, index, param);
				}
				
			} else {
				
				if (distanceToTopOfMerger < mg.distanceToTopOfMerger) {
					return nextBoundToBottom(m, index, param);
				} else {
					return nextBoundToTop(m, index, param);
				}
			}
			
		}
		
	}
	
	public Entity getEntity() {
		return m;
	}
	
	public double distanceToConnectedVertex(Vertex v) {
		assert vs.contains(v);
		if (a == Axis.LEFTRIGHT) {
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
	
	public GraphPosition travelToConnectedVertex(Vertex v, double dist) {
		
		if (v == m.top) {
			
			return travelToTop(m, index, param, dist);
			
		} else if (v == m.left) {
			
			return travelToLeft(m, index, param, dist);
			
		} else if (v == m.right) {
			
			return travelToRight(m, index, param, dist);
			
		} else {
			assert v == m.bottom;
			
			return travelToBottom(m, index, param, dist);
			
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
