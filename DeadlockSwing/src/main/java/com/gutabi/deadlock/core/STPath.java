package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;


/**
 * a path is a unique sequence of steps through space and time
 *
 */
public class STPath {
	
	private List<STPosition> origPoss;
	private List<STPosition> poss;
	
	private double totalDistance;
	
	private List<Double> times;
	
	private double startTime;
	private double endTime;
	
	private int hash;
	
	static Logger logger = Logger.getLogger(STPath.class);
	
	
	public STPath(List<STPosition> origPoss) {
		
		this.origPoss = origPoss;
//		this.poss = poss;
		
		assert origPoss.size() >= 2;
		
		interpolate();
		
		int h = 17;
		h = 37 * h + origPoss.hashCode();
		hash = h;
		
		List<Position> regularPoss = new ArrayList<Position>();
		for (STPosition stpos : origPoss) {
			regularPoss.add(stpos.getSpace());
		}
		Path regularPath = new Path(regularPoss);
		
		assert poss.size() == regularPath.poss.size();
		for (int i = 0; i < poss.size(); i++) {
			assert poss.get(i).s.equals(regularPath.poss.get(i));
		}
		
		startTime = times.get(0);
		endTime = times.get(times.size()-1);
		
		assert DMath.greaterThanEquals(startTime, 0.0);
		assert DMath.lessThanEquals(endTime, 1.0);
		
		assert check();
	}
	
	public int hashCode() {
		return hash;
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
			assert DMath.lessThan(a.getTime(), time) && DMath.lessThan(time, b.getTime());
			
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
		
		STPosition subStart;
		STPosition subEnd;
		
		int sKey = Collections.binarySearch(times, s, DMath.COMPARATOR);
		int ssKey;
		if (sKey >= 0) {
			// found
			//start is exactly at index sKey
			ssKey = sKey;
			subStart = poss.get(ssKey);
		} else {
			int insertionPoint = -(sKey+1);
			//start is between insertionPoint-1 and insertionPoint
			ssKey = insertionPoint;
			//interpolate between s and poss.get(sKey);
			STPosition a = poss.get(insertionPoint-1);
			STPosition b = poss.get(insertionPoint);
			assert DMath.lessThan(a.getTime(), s) && DMath.lessThan(s, b.getTime());
			
			if (a.getSpace().equals(b.getSpace())) {
				subStart = new STPosition(a.getSpace(), s);
			} else {
				double p = (s - a.getTime()) / (b.getTime() - a.getTime());
				double d = a.getSpace().distanceTo(b.getSpace());
				subStart = new STPosition(a.getSpace().travel(b.getSpace(), p * d), s);
			}
		}
		
		int eKey = Collections.binarySearch(times, e, DMath.COMPARATOR);
		int eeKey;
		if (eKey >= 0) {
			// found
			//end is exactly at index eKey
			eeKey = eKey;
			subEnd = poss.get(eeKey);
		} else {
			int insertionPoint = -(eKey+1);
			//end is between insertionPoint-1 and insertionPoint
			eeKey = insertionPoint-1;
			//interpolate between poss.get(eKey-1) and e
			STPosition a = poss.get(insertionPoint-1);
			STPosition b = poss.get(insertionPoint);
			assert a.getTime() < e && e < b.getTime();
			
			if (a.getSpace().equals(b.getSpace())) {
				subEnd = new STPosition(a.getSpace(), e);
			} else {
				double p = (e - a.getTime()) / (b.getTime() - a.getTime());
				double d = a.getSpace().distanceTo(b.getSpace());
				subEnd = new STPosition(a.getSpace().travel(b.getSpace(), p * d), e);
			}
		}
		
		assert DMath.equals(subStart.getTime(), s);
		assert DMath.equals(subEnd.getTime(), e);
		
		return new SubPath(subStart, subEnd);
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
	public STPath crash(double time) {
		
		assert (DMath.lessThanEquals(startTime, time) && DMath.lessThanEquals(time, endTime));
		
		List<STPosition> newPath = new ArrayList<STPosition>();
		
		Position crashPos = this.getPosition(time);
		newPath.add(new STPosition(crashPos, time));
		newPath.add(new STPosition(crashPos, endTime));
		
		return new STPath(newPath);
	}
	
	/**
	 * Adds pos to path and removed all positions after it
	 */
	public STPath synchronize(double time) {
		
		assert (DMath.lessThanEquals(startTime, time) && DMath.lessThanEquals(time, endTime));
		
		List<STPosition> newPath = new ArrayList<STPosition>();
		STPosition last = null;
		for (int i = 0; i < poss.size(); i++) {
			STPosition pos = poss.get(i);
			if (DMath.equals(pos.getTime(), time)) {
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
		
		return new STPath(newPath);
	}
	
	public STPath append(STPosition q) {
		List<STPosition> newPoss = new ArrayList<STPosition>(poss);
		newPoss.add(q);
		return new STPath(newPoss);
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
	
	/**
	 * 
	 * returns the first time that the paths intersect, or -1 if they never intersect
	 */
	public static double intersection(STPath a, STPath b, double radius) {
		
		assert DMath.equals(a.startTime, b.startTime);
		assert DMath.equals(a.endTime, b.endTime);
		
		SortedSet<Double> times = new TreeSet<Double>(DMath.COMPARATOR);
		times.addAll(a.getTimes());
		times.addAll(b.getTimes());
		
		Double[] da = times.toArray(new Double[0]);
		for (int i = 0; i < da.length-1; i++) {
			double ta = da[i];
			double tb = da[i+1];
			
			SubPath ap = a.subPath(ta, tb);
			SubPath bp = b.subPath(ta, tb);
			
//			if ((ap.start.getSpace() instanceof SinkedPosition && ap.end.getSpace() instanceof SinkedPosition) ||
//					(bp.start.getSpace() instanceof SinkedPosition && bp.end.getSpace() instanceof SinkedPosition)) {
//				continue;
//			}
			
			if (ap.getStartPosition() instanceof Sink || bp.getStartPosition() instanceof Sink) {
				continue;
			}
			
			/*
			 * e is only null if the path is on a vertex and not moving
			 */
			
			if (ap.getEdge() != null) {
				if (bp.getEdge() != null) {
					if (!MODEL.areNeighbors(ap.getEdge(), bp.getEdge())) {
						continue;
					}
				} else {
					if (!((Vertex)bp.getStartPosition()).getEdges().contains(ap.getEdge())) {
						continue;
					}
				}
			} else {
				if (bp.getEdge() != null) {
					if (!((Vertex)ap.getStartPosition()).getEdges().contains(bp.getEdge())) {
						continue;
					}
				} else {
					/*
					 * both are on vertices and not moving
					 */
					continue;
				}
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
		
		assert DMath.equals(ap.getStartTime(), bp.getStartTime());
		assert DMath.equals(ap.getEndTime(), bp.getEndTime());
		
		double realDistanceStart = Point.distance(ap.getStartPosition().getPoint(), bp.getStartPosition().getPoint());
		assert DMath.greaterThan(realDistanceStart, radius);
		
		double crashTime = solve(ap, bp, radius);
		
		return crashTime;
	}
	
	/**
	 * given 2 distances at two times, solve when the distances are exactly radius apart, or -1 if never
	 */
	private static double solve(SubPath ap, SubPath bp, double radius) {
		
		double d1;
		double d2;
		
		double t1 = ap.getStartTime();
		double t2 = ap.getEndTime();
		
		boolean sameEdge = ap.getEdge() == bp.getEdge();
		
		double crashTime;
		
		if (sameEdge) {
			// both on same edge
			
			d1 = Point.distance(ap.getStartPosition().getPoint(), bp.getStartPosition().getPoint());
			d2 = Point.distance(ap.getEndPosition().getPoint(), bp.getEndPosition().getPoint());
			
			/*
			 * remember original orientation
			 */
			boolean ABstart = ap.getStartPosition().distanceTo(ap.getEdge().getStart()) < bp.getStartPosition().distanceTo(ap.getEdge().getStart());
			boolean ABend = ap.getEndPosition().distanceTo(ap.getEdge().getStart()) < bp.getEndPosition().distanceTo(ap.getEdge().getStart());
			
			if (DMath.equals(d1, radius)) {
				crashTime = t1;
			} else {
				assert !(d1 < radius);
				/*
				 * only use d2 if a and b did not cross
				 */
				if ((ABend == ABstart) && DMath.equals(d2, radius)) {
					crashTime = t2;
				} else if ((ABend == ABstart) && d2 > radius) {
					crashTime = -1;
				} else {
					
					double goodT2;
					
					if (ABend != ABstart) {
						/*
						 * first find crossing time
						 */
						
						double goodT2Low = t1;
						double goodT2High = t2;
						
						while (true) {
							goodT2 = (goodT2Low + goodT2High) / 2;
							
							Position goodT21 = ap.getPosition(goodT2);
							Position goodT22 = bp.getPosition(goodT2);
							
							double goodT21ToStart = goodT21.distanceTo(ap.getEdge().getStart());
							double goodT22ToStart = goodT22.distanceTo(ap.getEdge().getStart());
							boolean newAB = goodT21ToStart < goodT22ToStart;
							
							if (DMath.equals(goodT21ToStart, goodT22ToStart)) {
								break;
							} else if (newAB != ABstart) {
								/*
								 * still crossed
								 */
								goodT2High = goodT2;
							} else {
								goodT2Low = goodT2;
							}
							
//							if (DMath.equals(goodT21ToStart, goodT22ToStart) || newAB != ABstart) {
//								/*
//								 * a and b are still crossed
//								 */
//								goodT2High = goodT2;
//							} else {
//								
//								//double crashDistance = crash1.distanceTo(crash2);
//								double realDistance = Point.distance(goodT21.getPoint(), goodT22.getPoint());
//								//assert DMath.equals(crashDistance, radius);
//								
////								logger.debug("crashTime: " + crashTime + " realDistance: " + realDistance);
//								
//								if (DMath.equals(realDistance, 0.0)) {
//									break;
//								} else if (realDistance > radius) {
//									crashTimeLow = crashTime;
//								} else {
//									crashTimeHigh = crashTime;
//								}
//							}
							
						}
						
					} else {
						goodT2 = t2;
					}
					
					double crashTimeLow = t1;
					double crashTimeHigh = goodT2;
					
					while (true) {
						crashTime = (crashTimeLow + crashTimeHigh) / 2;
						
						Position crash1 = ap.getPosition(crashTime);
						Position crash2 = bp.getPosition(crashTime);
						
						double crash1ToStart = crash1.distanceTo(ap.getEdge().getStart());
						double crash2ToStart = crash2.distanceTo(ap.getEdge().getStart());
						boolean newAB = crash1ToStart < crash2ToStart;
						
						if (DMath.equals(crash1ToStart, crash2ToStart) || newAB != ABstart) {
							/*
							 * a and b have crossed
							 */
							throw new AssertionError();
						} else {
							
							//double crashDistance = crash1.distanceTo(crash2);
							double realDistance = Point.distance(crash1.getPoint(), crash2.getPoint());
							//assert DMath.equals(crashDistance, radius);
							
//							logger.debug("crashTime: " + crashTime + " realDistance: " + realDistance);
							
							if (DMath.equals(realDistance, radius)) {
								break;
							} else if (realDistance > radius) {
								crashTimeLow = crashTime;
							} else {
								crashTimeHigh = crashTime;
							}
						}
						
					}
					
				}
			}
			
			if (crashTime == -1) {
				Point ae = ap.getEndPosition().getPoint();
				Point be = bp.getEndPosition().getPoint();
				double realDistanceEnd = Point.distance(ae, be);
				assert DMath.greaterThan(realDistanceEnd, radius);
			} else {
				Position crash1 = ap.getPosition(crashTime);
				Position crash2 = bp.getPosition(crashTime);
				//double crashDistance = crash1.distanceTo(crash2);
				double realDistance = Point.distance(crash1.getPoint(), crash2.getPoint());
				//assert DMath.equals(crashDistance, radius);
				assert DMath.equals(realDistance, radius);
			}
			
		} else {
			// neighbors
			
			d1 = Point.distance(ap.getStartPosition().getPoint(), bp.getStartPosition().getPoint());
			d2 = Point.distance(ap.getEndPosition().getPoint(), bp.getEndPosition().getPoint());

			assert DMath.greaterThanEquals(Math.abs(d1), radius);
			
			if (DMath.equals(d1, radius)) {
				crashTime = t1;
			} else {
				assert !(d1 < radius);
				if (DMath.equals(d2, radius)) {
					crashTime = t2;
				} else if (d2 > radius) {
					crashTime = -1;
				} else {
					
					double crashTimeLow = t1;
					double crashTimeHigh = t2;
					
					while (true) {
						crashTime = (crashTimeLow + crashTimeHigh) / 2;
						
						Position crash1 = ap.getPosition(crashTime);
						Position crash2 = bp.getPosition(crashTime);
						//double crashDistance = crash1.distanceTo(crash2);
						double realDistance = Point.distance(crash1.getPoint(), crash2.getPoint());
						//assert DMath.equals(crashDistance, radius);
						
						if (DMath.equals(realDistance, radius)) {
							break;
						} else if (realDistance > radius) {
							crashTimeLow = crashTime;
						} else {
							crashTimeHigh = crashTime;
						}
					}
					
				}
			}
			
			if (crashTime == -1) {
				Point ae = ap.getEndPosition().getPoint();
				Point be = bp.getEndPosition().getPoint();
				double realDistanceEnd = Point.distance(ae, be);
				assert DMath.greaterThan(realDistanceEnd, radius);
			} else {
				Position crash1 = ap.getPosition(crashTime);
				Position crash2 = bp.getPosition(crashTime);
				//double crashDistance = crash1.distanceTo(crash2);
				double realDistance = Point.distance(crash1.getPoint(), crash2.getPoint());
				//assert DMath.equals(crashDistance, radius);
				assert DMath.equals(realDistance, radius);
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
			
			if ((a.s.equals(b.s)) ||
					((a.s instanceof EdgePosition && b.s instanceof EdgePosition &&
							((EdgePosition)a.s).getEdge() == ((EdgePosition)b.s).getEdge() && ((EdgePosition)a.s).getIndex() == ((EdgePosition)b.s).getIndex())) ||
					(a.s.nextBoundToward(b.s).equals(b.s) ||
							b.s.nextBoundToward(a.s).equals(a.s))) {
				
				/*
				 * there are no bounds between a and b
				 * either:
				 * 1. a and b are equal
				 * 1. both a and b are not bounds and are between the same bounds
				 * 2. b is a's next bound
				 * 3. a is b's next bound
				 */
				
				times.add(b.t);
				poss.add(b);
				
				accDist = dist;
				
			} else {
				
				Position bEnd;
				if (!b.s.isBound()) {
					bEnd = b.s.nextBoundToward(a.s);
				} else {
					bEnd = b.s;
				}
				
				Position cur = a.s;
				Position prev;
				double accSegDistance = 0.0;
				double accSegTime = a.t;
				while (!cur.equals(bEnd)) {
					prev = cur;
					cur = cur.nextBoundToward(bEnd);
					double d = Point.distance(prev.getPoint(), cur.getPoint());
					double t = d / speed;
					accSegDistance += d;
					accSegTime += t;
					if (DMath.equals(accSegTime, 1.0)) {
						accSegTime = 1.0;
					}
					times.add(accSegTime);
					assert DMath.lessThanEquals(accSegTime, 1.0);
					poss.add(new STPosition(cur, accSegTime));
				}
				if (!bEnd.equals(b.s)) {
					double d = Point.distance(bEnd.getPoint(), b.s.getPoint());
					double t = d / speed;
					accSegDistance += d;
					accSegTime += t;
					if (DMath.equals(accSegTime, 1.0)) {
						accSegTime = 1.0;
					}
					times.add(accSegTime);
					assert DMath.lessThanEquals(accSegTime, 1.0);
					poss.add(new STPosition(b.s, accSegTime));
				}
				
				accDist += accSegDistance;
				
			}
			
		}
		
		totalDistance = accDist;
	}
	
	private boolean check() {
		for (int i = 1; i < poss.size(); i++) {
			STPosition cur = poss.get(i);
			STPosition prev = poss.get(i-1);
			assert cur.getTime() > prev.getTime();
			
			/*
			 * if between 2 different vertices, assert 1 unique path from one to the other
			 */
			if (cur.getSpace() instanceof Vertex && prev.getSpace() instanceof Vertex && cur.getSpace() != prev.getSpace()) {
				Vertex p1 = (Vertex)cur.getSpace();
				Vertex p2 = (Vertex)prev.getSpace();
				assert Vertex.commonConnectors(p1, p2).size() == 1;
			}
		}
		return true;
	}
}
