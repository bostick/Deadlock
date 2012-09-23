package com.gutabi.deadlock.core;

import java.util.Comparator;

public abstract class Event {
	
//	Event(Point sourceStart, Point sourceEnd, double borderStartParam, double borderEndParam) {
//		
//	}
	
	public abstract Point getSourceStart();
	
	public abstract Point getSourceEnd();
	
	public abstract double getBorderStartParam();
	
	public abstract double getBorderEndParam();
	
	
	
	
	
	public static class IntersectionEvent extends Event {
		
		private Point sourceStart;
		private Point sourceEnd;
		
		private double borderStartParam;
		private double borderEndParam;
		double sourceParam;
		
		Segment seg;
		
		public IntersectionEvent(Point source, double sourceParam, double borderStartParam, double borderEndParam, Segment seg) {
			this.sourceStart = source;
			this.sourceEnd = source;
			this.borderStartParam = borderStartParam;
			this.borderEndParam = borderEndParam;
			this.sourceParam = sourceParam;
			this.seg = seg;
		}
		
		public String toString() {
			return "INTERSECTION";
		}
		
		public Point getSourceStart() {
			return sourceStart;
		}
		
		public Point getSourceEnd() {
			return sourceEnd;
		}
		
		public double getBorderStartParam() {
			return borderStartParam;
		}
		
		public double getBorderEndParam() {
			return borderEndParam;
		}
	}
	
	public static Comparator<IntersectionEvent> COMPARATOR = new IntersectionEventComparator();
	
	static public class IntersectionEventComparator implements Comparator<IntersectionEvent> {
		@Override
		public int compare(IntersectionEvent a, IntersectionEvent b) {
			
			if (DMath.equals(a.sourceParam, b.sourceParam)) {
				return 0;
			} else if (a.sourceParam < b.sourceParam) {
				return -1;
			} else {
				return 1;
			}
			
		}
		
	}
	
	public static class CloseEvent extends Event {
		
//		private Point sourceStart;
//		private Point sourceEnd;
		
		private double borderStartParam;
		private double borderEndParam;
		
		public CloseEvent(double borderStartParam, double borderEndParam) {
//			this.sourceStart = sourceStart;
//			this.sourceEnd = sourceEnd;
			this.borderStartParam = borderStartParam;
			this.borderEndParam = borderEndParam;
		}
		
		public String toString() {
			return "CLOSE";
		}
		
		public Point getSourceStart() {
			//return sourceStart;
			throw new IllegalStateException();
		}
		
		public Point getSourceEnd() {
			//return sourceEnd;
			throw new IllegalStateException();
		}
		
		public double getBorderStartParam() {
			return borderStartParam;
		}
		
		public double getBorderEndParam() {
			return borderEndParam;
		}
	}
	
}
