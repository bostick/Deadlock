package com.gutabi.deadlock.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

/**
 * a path is a unique sequence of steps through space and time
 *
 */
public class Path {
	
	private final List<STPosition> poss;
	
	private double totalDistance;
	
	private List<Double> times;
	
	private double startTime;
	private double endTime;
	
	
	
	public Path(List<STPosition> poss) {
		
		this.poss = poss;
		
		calculateTotalDistanceAndTime(poss);
		
		startTime = times.get(0);
		endTime = times.get(times.size()-1);
		
		assert check();
	}
	
	public String toString() {
		return poss.toString();
	}
	
	public STPosition get(int i) {
		return poss.get(i);
	}
	
	public Position getPosition(double time) {
		if (time < startTime) {
			throw new IllegalArgumentException();
		}
		if (time > endTime) {
			throw new IllegalArgumentException();
		}
		
		int key = Collections.binarySearch(times, time, DMath.COMPARATOR);
		if (key >= 0) {
			// found
			return poss.get(key).getSpace();
		} else {
			int insertionPoint = -(key+1);
			//start is between insertionPoint-1 and insertionPoint
			STPosition a = poss.get(insertionPoint-1);
			STPosition b = poss.get(insertionPoint);
			assert a.getTime() < time && time < b.getTime();
			double p = (time - a.getTime()) / (b.getTime() - a.getTime());
			double d = a.getSpace().distanceTo(b.getSpace());
			return a.getSpace().travel(b.getSpace(), p * d);
		}
	}
	
	/**
	 * return the subpath from start time s to end time e, inclusive
	 */
	public SubPath subPath(double s, double e) {
		if (s < startTime) {
			throw new IllegalArgumentException();
		}
		if (e > endTime) {
			throw new IllegalArgumentException();
		}
		
		List<STPosition> sub = new ArrayList<STPosition>();
		
		int sKey = Collections.binarySearch(times, s, DMath.COMPARATOR);
		int ssKey;
		if (sKey >= 0) {
			// found
			//start is exactly at index sKey
			ssKey = sKey;
		} else {
			int insertionPoint = -(sKey+1);
			//start is between insertionPoint-1 and insertionPoint
			ssKey = insertionPoint;
		}
		
		int eKey = Collections.binarySearch(times, e, DMath.COMPARATOR);
		int eeKey;
		if (eKey >= 0) {
			// found
			//end is exactly at index eKey
			eeKey = eKey;
		} else {
			int insertionPoint = -(eKey+1);
			//end is between insertionPoint-1 and insertionPoint
			eeKey = insertionPoint-1;
		}
		
		if (sKey < 0) {
			int insertionPoint = -(sKey+1);
			//interpolate between s and poss.get(sKey);
			STPosition a = poss.get(insertionPoint-1);
			STPosition b = poss.get(insertionPoint);
			assert a.getTime() < s && s < b.getTime();
			double p = (s - a.getTime()) / (b.getTime() - a.getTime());
			double d = a.getSpace().distanceTo(b.getSpace());
			sub.add(new STPosition(a.getSpace().travel(b.getSpace(), p * d), s));
		}
		for (int i = ssKey; i <= eeKey; i++) {
			STPosition p = poss.get(i);
			sub.add(p);
		}
		if (eKey < 0) {
			int insertionPoint = -(eKey+1);
			//interpolate between poss.get(eKey-1) and e
			STPosition a = poss.get(insertionPoint-1);
			STPosition b = poss.get(insertionPoint);
			assert a.getTime() < e && e < b.getTime();
			double p = (e - a.getTime()) / (b.getTime() - a.getTime());
			double d = a.getSpace().distanceTo(b.getSpace());
			sub.add(new STPosition(a.getSpace().travel(b.getSpace(), p * d), e));
		}
		
		assert DMath.doubleEquals(sub.get(0).getTime(), s);
		assert DMath.doubleEquals(sub.get(sub.size()-1).getTime(), e);
		assert sub.size() == 2;
		
		return new SubPath(sub.get(0), sub.get(1));
	}
	
	/**
	 * the smallest unit of a path
	 * either goes from start to end of an edge, or start to middle, or end to middle, or middle to middle
	 * 
	 * so a subpath always has exactly one edge
	 */
	public class SubPath {
		
		STPosition start;
		STPosition end;
		
		Edge e;
		
		SubPath(STPosition start, STPosition end) {
			this.start = start;
			this.end = end;
			
			if (start.getSpace() instanceof Vertex) {
				if (end.getSpace() instanceof Vertex) {
					e = (Edge)Vertex.commonConnector((Vertex)start.getSpace(), (Vertex)end.getSpace()); 
				} else {
					e = ((EdgePosition)end.getSpace()).getEdge();
					assert ((Vertex)start.getSpace()).getEdges().contains(e);
				}
			} else {
				if (end.getSpace() instanceof Vertex) {
					e = ((EdgePosition)start.getSpace()).getEdge();
					assert ((Vertex)end.getSpace()).getEdges().contains(e);
				} else {
					e = ((EdgePosition)start.getSpace()).getEdge();
					assert ((EdgePosition)end.getSpace()).getEdge() == e;
				}
			}
		}
		
	}
	
	public int size() {
		return poss.size();
	}
	
	public List<Double> getTimes() {
		return times;
	}
	
	/**
	 * Adds pos to path and removed all positions after it
	 */
	public Path crash(double time) {
		
		List<STPosition> newPath = new ArrayList<STPosition>();
		STPosition last = null;
		for (int i = 0; i < poss.size(); i++) {
			STPosition pos = poss.get(i);
			if (DMath.doubleEquals(pos.getTime(), time)) {
				newPath.add(pos);
				break;
			} else if (pos.getTime() < time) {
				newPath.add(pos);
			} else {
				assert last.getTime() < time && time < pos.getTime();
				Position crashPos = this.getPosition(time);
				newPath.add(new STPosition(crashPos, time));
				break;
			}
			last = pos;
		}
		
		return new Path(newPath);
	}
	
	public Path append(STPosition q) {
		List<STPosition> newPoss = new ArrayList<STPosition>(poss);
		newPoss.add(q);
		return new Path(newPoss);
	}
	
	public STPosition getLastPosition() {
		return poss.get(poss.size()-1);
	}
	
	public double totalDistance() {
		return totalDistance;
	}
	
	public double totalTime() {
		return endTime - startTime;
	}
	
	/**
	 * 
	 * returns the first time that the paths intersect, or -1 if they never intersect
	 */
	public static double intersection(Path a, Path b, double radius) {
		
		SortedSet<Double> times = new TreeSet<Double>();
		times.addAll(a.getTimes());
		times.addAll(b.getTimes());
		
		Double[] da = times.toArray(new Double[0]);
		for (int i = 0; i < da.length-1; i++) {
			double ta = da[i];
			double tb = da[i+1];
			
			/*
			 * make sure that tb is in both paths (sinking a car shortens it's path, so path a and b don't always have the same time span) 
			 */
			if (tb > a.endTime || tb > b.endTime) {
				break;
			}
			
			SubPath ap = a.subPath(ta, tb);
			SubPath bp = b.subPath(ta, tb);
			
			if (!MODEL.areNeighbors(ap.e, bp.e)) {
				continue;
			}
			
			double d1;
			double d2;
			if (ap.e == bp.e) {
				// both on same edge
				
				/*
				 * could be moving in opposite directions, one may not be moving, maybe in same direction but different speeds
				 * all of these are non-linear, because one car could pass the other
				 */
				
				// use distance on edge as an oriented distance
				d1 = bp.start.getSpace().distanceTo(ap.e.getStart()) - ap.start.getSpace().distanceTo(ap.e.getStart());
				d2 = bp.end.getSpace().distanceTo(ap.e.getStart()) - ap.end.getSpace().distanceTo(ap.e.getStart());
				
				assert DMath.doubleEquals(Math.abs(d1), radius) || Math.abs(d1) > radius;
				
				if (d1 < -radius) {
					//flip
					d1 = -d1;
					d2 = -d2;
				}
				
			} else {
				// neighbors
				
				// construct linear function of distance between ap and bp through time
				// if reaches radius, return time
				// else return -1
				
				d1 = ap.start.getSpace().distanceTo(bp.start.getSpace());
				d2 = ap.end.getSpace().distanceTo(bp.end.getSpace());
				
				assert DMath.doubleEquals(Math.abs(d1), radius) || Math.abs(d1) > radius;
			}
			
			double crashTime = solve(ta, tb, d1, d2, radius);
			if (crashTime == -1) {
				continue;
			}
			Position crash1 = a.getPosition(crashTime);
			Position crash2 = b.getPosition(crashTime);
			double crashDistance = crash1.distanceTo(crash2);
			assert DMath.doubleEquals(crashDistance, radius);
			
			return crashTime;
			
		}
		
		return -1;
	}
	
	/**
	 * given 2 distances at two times, solve when the distances are exactly radius apart, or -1 if never
	 */
	private static double solve(double t1, double t2, double d1, double d2, double radius) {
		
		if (DMath.doubleEquals(d1, radius)) {
			return t1;
		} else {
			assert !(d1 < radius);
			if (DMath.doubleEquals(d2, radius)) {
				return t2;
			} else if (d2 > radius) {
				return -1;
			} else {
				double bb = (d2 * t1 - d1 * t2)/(t1 - t2);
				double mm;
				if (!DMath.doubleEquals(t1, 0.0)) {
					mm = (d1 - bb) / t1;
				} else {
					mm = (d2 - bb) / t2;
				}
				double crashTime = (radius - bb) / mm;
				assert t1 < crashTime && crashTime < t2;
				return crashTime;
			}
		}
		
	}
	
	
	
	private void calculateTotalDistanceAndTime(List<STPosition> poss) {
		
		double accDist = 0.0;
		
		times = new ArrayList<Double>();
		
		times.add(poss.get(0).getTime());
		
		for (int i = 0; i < poss.size()-1; i++) {
			STPosition a = poss.get(i);
			STPosition b = poss.get(i+1);
			
			times.add(b.getTime());
			
			double dist;
			
			if (a.s instanceof Vertex) {
				if (b.s instanceof Vertex) {
					
					Vertex aa = (Vertex)a.s;
					Vertex bb = (Vertex)b.s;
					
					List<Connector> cons = Vertex.commonConnectors(aa, bb);
					assert cons.size() == 1;
					
					Edge e = (Edge)cons.get(0);
					assert !e.isLoop();
					
					dist = e.getTotalLength();
					
				} else {
					Vertex aa = (Vertex)a.s;
					EdgePosition bb = (EdgePosition)b.s;
					
					Edge e = bb.getEdge();
					if (aa == e.getStart()) {
						dist = bb.distanceToStartOfEdge();
					} else {
						assert aa == e.getEnd();
						dist = bb.distanceToEndOfEdge();
					}
				}
			} else {
				if (b.s instanceof Vertex) {
					EdgePosition aa = (EdgePosition)a.s;
					Vertex bb = (Vertex)b.s;
					
					Edge e = aa.getEdge();
					if (bb == e.getStart()) {
						dist = aa.distanceToStartOfEdge();
					} else {
						assert bb == e.getEnd();
						dist = aa.distanceToEndOfEdge();
					}
					
				} else {
					EdgePosition aa = (EdgePosition)a.s;
					EdgePosition bb = (EdgePosition)b.s;
					
					Edge ae = aa.getEdge();
					Edge be = bb.getEdge();
					assert ae == be;
					
					dist = Math.abs(aa.distanceToStartOfEdge() - bb.distanceToStartOfEdge());
				}
			}
			
			accDist += dist;
		}
		
		totalDistance = accDist;
	}
	
	private boolean check() {
		for (int i = 1; i < poss.size(); i++) {
			STPosition cur = poss.get(i);
			STPosition prev = poss.get(i-1);
			assert cur.getTime() > prev.getTime();
			
			/*
			 * if between 2 vertices, assert 1 unique path from one to the other
			 */
			if (cur.getSpace() instanceof Vertex && prev.getSpace() instanceof Vertex) {
				Vertex p1 = (Vertex)cur.getSpace();
				Vertex p2 = (Vertex)prev.getSpace();
				assert Vertex.commonConnectors(p1, p2).size() == 1;
			}
		}
		return true;
	}
}
