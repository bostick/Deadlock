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
	
	private List<STPosition> origPoss;
	private List<STPosition> poss;
	
	private double totalDistance;
	
	private List<Double> times;
	
	private double startTime;
	private double endTime;
	
	
	
	public Path(List<STPosition> origPoss) {
		
		this.origPoss = origPoss;
//		this.poss = poss;
		
		assert origPoss.size() >= 2;
		
		calculate();
		
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
			
			if (a.getSpace().equals(b.getSpace())) {
				return a.getSpace();
			} else {
				double p = (time - a.getTime()) / (b.getTime() - a.getTime());
				double d = a.getSpace().distanceTo(b.getSpace());
				return a.getSpace().travel(b.getSpace(), p * d);
			}
		}
	}
	
	/**
	 * return the subpath from start time s to end time e, inclusive
	 */
	private SubPath subPath(double s, double e) {
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
			
			if (a.getSpace().equals(b.getSpace())) {
				sub.add(new STPosition(a.getSpace(), s));
			} else {
				double p = (s - a.getTime()) / (b.getTime() - a.getTime());
				double d = a.getSpace().distanceTo(b.getSpace());
				sub.add(new STPosition(a.getSpace().travel(b.getSpace(), p * d), s));
			}
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
			
			if (a.getSpace().equals(b.getSpace())) {
				sub.add(new STPosition(a.getSpace(), e));
			} else {
				double p = (e - a.getTime()) / (b.getTime() - a.getTime());
				double d = a.getSpace().distanceTo(b.getSpace());
				sub.add(new STPosition(a.getSpace().travel(b.getSpace(), p * d), e));
			}
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
	 * or sinked position
	 */
	public static class SubPath {
		
		double startTime;
		double endTime;
		
		STPosition start;
		STPosition end;
		
		Edge e;
		
		SubPath(STPosition start, STPosition end) {
			this.start = start;
			this.end = end;
			
			startTime = start.getTime();
			endTime = end.getTime();
			
			if (start.getSpace() instanceof Vertex) {
				if (end.getSpace() instanceof Vertex) {
					e = (Edge)Vertex.commonConnector((Vertex)start.getSpace(), (Vertex)end.getSpace()); 
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
					e = ((EdgePosition)start.getSpace()).getEdge();
					assert ((SinkedPosition)end.getSpace()).getSink().getEdges().contains(e);
				}
			} else {
				assert ((SinkedPosition)start.getSpace()).getSink() == ((SinkedPosition)end.getSpace()).getSink();
				e = null;
			}
			
		}
		
		public Position getPosition(double time) {
			if (time < startTime) {
				throw new IllegalArgumentException();
			}
			if (time > endTime) {
				throw new IllegalArgumentException();
			}
			
			if (DMath.doubleEquals(time, startTime)) {
				return start.getSpace();
			} else if (DMath.doubleEquals(time, endTime)) {
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
		
		assert (DMath.doubleEquals(startTime, time) || startTime < time) && (DMath.doubleEquals(time, endTime) || time < endTime);
		
		List<STPosition> newPath = new ArrayList<STPosition>();
		
		Position crashPos = this.getPosition(time);
		newPath.add(new STPosition(crashPos, time));
		newPath.add(new STPosition(crashPos, endTime));
		
		return new Path(newPath);
	}
	
	/**
	 * Adds pos to path and removed all positions after it
	 */
	public Path synchronize(double time) {
		
		assert (DMath.doubleEquals(startTime, time) || startTime < time) && (DMath.doubleEquals(time, endTime) || time < endTime);
		
		List<STPosition> newPath = new ArrayList<STPosition>();
		STPosition last = null;
		for (int i = 0; i < poss.size(); i++) {
			STPosition pos = poss.get(i);
			if (DMath.doubleEquals(pos.getTime(), time)) {
				newPath.add(pos);
			} else if (pos.getTime() < time) {
				;
			} else if (last.getTime() < time && time < pos.getTime()) {
				Position synchPos = this.getPosition(time);
				newPath.add(new STPosition(synchPos, time));
				newPath.add(pos);
			} else {
				assert time < pos.getTime();
				newPath.add(pos);
			}
			last = pos;
		}
		
		assert newPath.size() >= 2;
		assert newPath.get(0).getTime() == time;
		assert newPath.get(newPath.size()-1).getTime() == poss.get(poss.size()-1).getTime();
		
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
	
	public double getStartTime() {
		return startTime;
	}
	
	public double getEndTime() {
		return endTime;
	}
	
	public double totalTime() {
		return endTime - startTime;
	}
	
	/**
	 * 
	 * returns the first time that the paths intersect, or -1 if they never intersect
	 */
	public static double intersection(Path a, Path b, double radius) {
		
		assert DMath.doubleEquals(a.startTime, b.startTime);
		assert DMath.doubleEquals(a.endTime, b.endTime);
		
		SortedSet<Double> times = new TreeSet<Double>();
		times.addAll(a.getTimes());
		times.addAll(b.getTimes());
		
		Double[] da = times.toArray(new Double[0]);
		for (int i = 0; i < da.length-1; i++) {
			double ta = da[i];
			double tb = da[i+1];
			
			SubPath ap = a.subPath(ta, tb);
			SubPath bp = b.subPath(ta, tb);
			
			if ((ap.start.getSpace() instanceof SinkedPosition && ap.end.getSpace() instanceof SinkedPosition) ||
					(bp.start.getSpace() instanceof SinkedPosition && bp.end.getSpace() instanceof SinkedPosition)) {
				continue;
			}
			
			if (!MODEL.areNeighbors(ap.e, bp.e)) {
				continue;
			}
			
			double crashTime = intersection(ap, bp, radius);
			if (crashTime != -1) {
				return crashTime;
			}
		}
		
		return -1;
	}
	
	/**
	 * given 2 distances at two times, solve when the distances are exactly radius apart, or -1 if never
	 */
	private static double intersection(SubPath ap, SubPath bp, double radius) {
		
		assert DMath.doubleEquals(ap.startTime, bp.startTime);
		assert DMath.doubleEquals(ap.endTime, bp.endTime);
		
		double crashTime = solve(ap, bp, radius);
		if (crashTime == -1) {
			Point ae = ap.end.getSpace().getPoint();
			Point be = bp.end.getSpace().getPoint();
			double realDistance = Point.distance(ae, be);
			assert !DMath.doubleEquals(realDistance, radius);
			assert realDistance > radius;
		} else {
			Position crash1 = ap.getPosition(crashTime);
			Position crash2 = bp.getPosition(crashTime);
			double crashDistance = crash1.distanceTo(crash2);
			double realDistance = Point.distance(crash1.getPoint(), crash2.getPoint());
			assert DMath.doubleEquals(crashDistance, radius);
			assert DMath.doubleEquals(realDistance, radius);
		}
		
		return crashTime;
	}
	
	/**
	 * given 2 distances at two times, solve when the distances are exactly radius apart, or -1 if never
	 */
	private static double solve(SubPath ap, SubPath bp, double radius) {
		
		double d1;
		double d2;
		
		double t1 = ap.startTime;
		double t2 = ap.endTime;
		
		if (ap.e == bp.e) {
			// both on same edge
			
			// use distance on edge as an oriented distance
			d1 = bp.start.getSpace().distanceTo(ap.e.getStart()) - ap.start.getSpace().distanceTo(ap.e.getStart());
			d2 = bp.end.getSpace().distanceTo(ap.e.getStart()) - ap.end.getSpace().distanceTo(ap.e.getStart());

			assert DMath.doubleEquals(Math.abs(d1), radius) || Math.abs(d1) > radius;

			if (d1 < 0) {
				//flip
				d1 = -d1;
				d2 = -d2;
			}
			
		} else {
			// neighbors
			
			d1 = ap.start.getSpace().distanceTo(bp.start.getSpace());
			d2 = ap.end.getSpace().distanceTo(bp.end.getSpace());

			assert DMath.doubleEquals(Math.abs(d1), radius) || Math.abs(d1) > radius;
			
		}
		
		double crashTime;
		
		if (DMath.doubleEquals(d1, radius)) {
			crashTime = t1;
		} else {
			assert !(d1 < radius);
			if (DMath.doubleEquals(d2, radius)) {
				crashTime = t2;
			} else if (d2 > radius) {
				crashTime = -1;
			} else {
				double bb = (d2 * t1 - d1 * t2)/(t1 - t2);
				double mm;
				if (!DMath.doubleEquals(t1, 0.0)) {
					mm = (d1 - bb) / t1;
				} else {
					mm = (d2 - bb) / t2;
				}
				crashTime = (radius - bb) / mm;
				assert t1 < crashTime && crashTime < t2;
			}
		}

		return crashTime;
	}
	
	
	
	
	/*
	 * calculate each segment of the path
	 */
	private void interpolate() {
		
		times = new ArrayList<Double>();
		poss = new ArrayList<STPosition>();
		
		times.add(origPoss.get(0).getTime());
		poss.add(origPoss.get(0));
		
		double accDist = 0.0;
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			STPosition a = origPoss.get(i);
			STPosition b = origPoss.get(i+1);
			
			double dist = a.s.distanceTo(b.s);
			double time = b.t - a.t;
			double speed = dist / time;
			
			Position cur = a.s;
			Position prev;
			double accSegDistance = 0.0;
			double accSegTime = a.t;
			while (!cur.equals(b)) {
				prev = cur;
				cur = cur.nextToward(b);
				double d = Point.distance(prev.getPoint(), cur.getPoint());
				double t = d / speed;
				accSegDistance += d;
				accSegTime += t;
				times.add(accSegTime);
				poss.add(new STPosition(cur, accSegTime));
			}
			
			accDist += accSegDistance;
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
