package com.gutabi.deadlock.core.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;

public class STPointPath {
	
	private List<STPoint> poss;
	public STPoint start;
	public STPoint end;
	public List<Double> times;
	int hash;
	
	public STPointPath(List<STPoint> poss) {
		
		assert poss.size() >= 2;
		
		this.poss = poss;
		this.start = poss.get(0);
		this.end = poss.get(poss.size()-1);
		
		times = new ArrayList<Double>();
		for (STPoint pos : poss) {
			times.add(pos.getTime());
		}
		
		int h = 17;
		h = 37 * h + poss.hashCode();
		hash = h;
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	public int size() {
		return poss.size();
	}
	
	public STPoint get(int i) {
		return poss.get(i);
	}
	
	public Point getPoint(double time) {
		if (time < start.getTime()) {
			throw new IllegalArgumentException();
		}
		if (time > end.getTime()) {
			throw new IllegalArgumentException();
		}
		
		int key = Collections.binarySearch(times, time, DMath.COMPARATOR);
		if (key >= 0) {
			// found
			return poss.get(key).getSpace();
		} else {
			int insertionPoint = -(key+1);
			//start is between insertionPoint-1 and insertionPoint
			STPoint a = poss.get(insertionPoint-1);
			STPoint b = poss.get(insertionPoint);
			assert DMath.lessThan(a.getTime(), time) && DMath.lessThan(time, b.getTime());
			
			if (a.getSpace().equals(b.getSpace())) {
				return a.getSpace();
			} else {
				double p = (time - a.getTime()) / (b.getTime() - a.getTime());
//				double d = a.getSpace().distanceTo(b.getSpace());
				return Point.point(a.getSpace(), b.getSpace(), p);
			}
		}
	}
	
	public static STPointPath advanceOneTimeStep(Point pos, double heading, double distance) {
		List<STPoint> newPath = new ArrayList<STPoint>();
		
		newPath.add(new STPoint(pos, 0.0));
		newPath.add(new STPoint(new Point(pos.getX() + Math.cos(heading) * distance, pos.getY() + Math.sin(heading) * distance), 1.0));
		
		return new STPointPath(newPath);
	}
	
	public STPointPath crash(double time) {
		
		assert (DMath.lessThanEquals(start.getTime(), time) && DMath.lessThanEquals(time, end.getTime()));
		
		Point crash = this.getPoint(time);
		
		List<STPoint> newPath = new ArrayList<STPoint>();
		STPoint last = null;
		for (int i = 0; i < poss.size(); i++) {
			STPoint pos = poss.get(i);
			if (DMath.equals(pos.getTime(), time)) {
				assert pos.getSpace().equals(crash);
				if (!DMath.equals(time, end.getTime())) {
					newPath.add(new STPoint(crash, time));
				}
				newPath.add(new STPoint(crash, end.getTime()));
				break;
			} else if (pos.getTime() < time) {
				newPath.add(pos);
			} else if (last.getTime() < time && time < pos.getTime()) {
				newPath.add(new STPoint(crash, time));
				newPath.add(new STPoint(crash, end.getTime()));
				break;
			} else {
				assert time < pos.getTime();
				assert false;
			}
			last = pos;
		}
		
		return new STPointPath(newPath);
	}
	
//	public STPointSubPath subPathX(double s, double e) {
//		if (s < start.getTime()) {
//			throw new IllegalArgumentException();
//		}
//		if (e > end.getTime()) {
//			throw new IllegalArgumentException();
//		}
//		
//		STPoint subStart;
//		STPoint subEnd;
//		
//		int sKey = Collections.binarySearch(times, s, DMath.COMPARATOR);
//		int ssKey;
//		if (sKey >= 0) {
//			// found
//			//start is exactly at index sKey
//			ssKey = sKey;
//			subStart = poss.get(ssKey);
//		} else {
//			int insertionPoint = -(sKey+1);
//			//start is between insertionPoint-1 and insertionPoint
//			ssKey = insertionPoint;
//			//interpolate between s and poss.get(sKey);
//			STPoint a = poss.get(insertionPoint-1);
//			STPoint b = poss.get(insertionPoint);
//			assert DMath.lessThan(a.getTime(), s) && DMath.lessThan(s, b.getTime());
//			
//			if (a.getSpace().equals(b.getSpace())) {
//				subStart = new STPoint(a.getSpace(), s);
//			} else {
//				double p = (s - a.getTime()) / (b.getTime() - a.getTime());
////				double d = a.getSpace().distanceTo(b.getSpace());
//				subStart = new STPoint(Point.point(a.getSpace(), b.getSpace(), p), s);
//			}
//		}
//		
//		int eKey = Collections.binarySearch(times, e, DMath.COMPARATOR);
//		int eeKey;
//		if (eKey >= 0) {
//			// found
//			//end is exactly at index eKey
//			eeKey = eKey;
//			subEnd = poss.get(eeKey);
//		} else {
//			int insertionPoint = -(eKey+1);
//			//end is between insertionPoint-1 and insertionPoint
//			eeKey = insertionPoint-1;
//			//interpolate between poss.get(eKey-1) and e
//			STPoint a = poss.get(insertionPoint-1);
//			STPoint b = poss.get(insertionPoint);
//			assert a.getTime() < e && e < b.getTime();
//			
//			if (a.getSpace().equals(b.getSpace())) {
//				subEnd = new STPoint(a.getSpace(), e);
//			} else {
//				double p = (e - a.getTime()) / (b.getTime() - a.getTime());
////				double d = a.getSpace().distanceTo(b.getSpace());
//				subEnd = new STPoint(Point.point(a.getSpace(), b.getSpace(), p), e);
//			}
//		}
//		
//		assert DMath.equals(subStart.getTime(), s);
//		assert DMath.equals(subEnd.getTime(), e);
//		
//		return new STPointSubPath(subStart, subEnd);
//	}
	
	/**
	 * 
	 * returns the first time that the paths intersect, or -1 if they never intersect
	 */
//	public static double intersection(STPointPath a, STPointPath b, double radius, double cutoffTime) {
//		
//		SortedSet<Double> times = new TreeSet<Double>(DMath.COMPARATOR);
//		for (double t : a.times) {
//			if (DMath.lessThanEquals(t, cutoffTime)) {
//				times.add(t);
//			}
//		}
//		for (double t : b.times) {
//			if (DMath.lessThanEquals(t, cutoffTime)) {
//				times.add(t);
//			}
//		}
//		
//		Double[] da = times.toArray(new Double[0]);
//		for (int i = 0; i < da.length-1; i++) {
//			double ta = da[i];
//			double tb = da[i+1];
//			
//			STPointSubPath ap = a.subPath(ta, tb);
//			STPointSubPath bp = b.subPath(ta, tb);
//			
//			double crashTime = STPointPath.intersection(ap, bp, radius);
//			if (crashTime != -1) {
//				return crashTime;
//			}
//		}
//		
//		return -1;
//	}
	
	/**
	 * given 2 distances at two times, solve when the distances are exactly radius apart, or -1 if never
	 */
//	private static double intersection(STPointSubPath ap, STPointSubPath bp, double radius) {
//		
//		assert DMath.equals(ap.start.getTime(), bp.start.getTime());
//		assert DMath.equals(ap.end.getTime(), bp.end.getTime());
//		
//		double realDistanceStart = Point.distance(ap.start.getSpace(), bp.start.getSpace());
//		assert DMath.greaterThanEquals(realDistanceStart, radius);
//		
//		double crashTime = solve(ap, bp, radius);
//		
//		return crashTime;
//	}
	
	/**
	 * given 2 distances at two times, solve when the distances are exactly radius apart, or -1 if never
	 */
//	private static double solve(STPointSubPath ap, STPointSubPath bp, double radius) {
//		
//		double d1;
//		double d2;
//		
//		double t1 = ap.start.getTime();
//		double t2 = ap.end.getTime();
//		
//		double crashTime;
//		
//		d1 = Point.distance(ap.start.getSpace(), bp.start.getSpace());
//		
//		assert DMath.greaterThanEquals(d1, radius);
//		
//		try {
//			Point aps = ap.start.getSpace();
//			Point ape = ap.end.getSpace();
//			Point bps = bp.start.getSpace();
//			Point bpe = bp.end.getSpace();
//			if (aps.equals(ape) && bps.equals(bpe)) {
//				
//				return -1;
//				
//			} else if (aps.equals(ape)) {
//				//a is not moving
//				if (Point.intersect(aps, bps, bpe)) {
//					
//					double goodT2;
//					
//					double goodT2Low = t1;
//					double goodT2High = t2;
//					
//					while (true) {
//						goodT2 = (goodT2Low + goodT2High) / 2;
//						
//						Point goodT2B = bp.getPoint(goodT2);
//						
//						double auOnB = Point.u(bp.start.getSpace(), aps, goodT2B);
//						
//						if (DMath.equals(auOnB, 1.0)) {
//							break;
//						} else if (DMath.lessThan(auOnB, 1.0)) {
//							/*
//							 * still crossed
//							 */
//							goodT2High = goodT2;
//						} else {
//							assert DMath.greaterThan(auOnB, 1.0);
//							goodT2Low = goodT2;
//						}
//						
//					}
//					
//					t2 = goodT2;
//					
//				}
//			} else if (bps.equals(bpe)) {
//				//b is not moving
//				if (Point.intersect(bps, aps, ape)) {
//					
//					double goodT2;
//					
//					double goodT2Low = t1;
//					double goodT2High = t2;
//					
//					while (true) {
//						goodT2 = (goodT2Low + goodT2High) / 2;
//						
//						Point goodT2A = ap.getPoint(goodT2);
//						
//						double buOnA = Point.u(ap.start.getSpace(), bps, goodT2A);
//						
//						if (DMath.equals(buOnA, 1.0)) {
//							break;
//						} else if (DMath.lessThan(buOnA, 1.0)) {
//							/*
//							 * still crossed
//							 */
//							goodT2High = goodT2;
//						} else {
//							assert DMath.greaterThan(buOnA, 1.0);
//							goodT2Low = goodT2;
//						}
//						
//					}
//					
//					t2 = goodT2;
//					
//				}
//			} else {
//				Point.intersection(aps, ape, bps, bpe);
//			}
//		} catch (OverlappingException ex) {
//			
//			/*
//			 * first find crossing time
//			 */
//			
//			double goodT2;
//			
//			double goodT2Low = t1;
//			double goodT2High = t2;
//			
//			while (true) {
//				goodT2 = (goodT2Low + goodT2High) / 2;
//				
//				Point goodT2A = ap.getPoint(goodT2);
//				Point goodT2B = bp.getPoint(goodT2);
//				
//				double buOnA = Point.u(ap.start.getSpace(), goodT2B, goodT2A);
//				double auOnB = Point.u(bp.start.getSpace(), goodT2A, goodT2B);
//				
//				if (DMath.equals(buOnA, 1.0) && DMath.equals(auOnB, 1.0)) {
//					break;
//				} else if (DMath.lessThan(buOnA, 1.0) && DMath.lessThan(auOnB, 1.0)) {
//					/*
//					 * still crossed
//					 */
//					goodT2High = goodT2;
//				} else {
//					assert DMath.greaterThan(buOnA, 1.0) && DMath.greaterThan(auOnB, 1.0);
//					goodT2Low = goodT2;
//				}
//				
//			}
//			
//			t2 = goodT2;
//			
//		}
//		
//		if (DMath.equals(d1, radius)) {
//			crashTime = t1;
//		} else {
//			assert !(d1 < radius);
//			
//			d2 = Point.distance(ap.getPoint(t2), bp.getPoint(t2));
//			
//			if (DMath.equals(d2, radius)) {
//				crashTime = t2;
//			} else if (d2 > radius) {
//				crashTime = -1;
//			} else {
//				
//				double crashTimeLow = t1;
//				double crashTimeHigh = t2;
//				
//				while (true) {
//					crashTime = (crashTimeLow + crashTimeHigh) / 2;
//					
//					Point crashA = ap.getPoint(crashTime);
//					Point crashB = bp.getPoint(crashTime);
//					double realDistance = Point.distance(crashA, crashB);
//					
//					if (DMath.equals(realDistance, radius)) {
//						break;
//					} else if (realDistance > radius) {
//						crashTimeLow = crashTime;
//					} else {
//						crashTimeHigh = crashTime;
//					}
//				}
//				
//			}
//		}
//		
//		if (crashTime == -1) {
//			Point ae = ap.end.getSpace();
//			Point be = bp.end.getSpace();
//			double realDistanceEnd = Point.distance(ae, be);
//			assert DMath.greaterThan(realDistanceEnd, radius);
//		} else {
//			Point crashA = ap.getPoint(crashTime);
//			Point crashB = bp.getPoint(crashTime);
//			double realDistance = Point.distance(crashA, crashB);
//			assert DMath.equals(realDistance, radius);
//		}
//		
//		return crashTime;
//	}

}
