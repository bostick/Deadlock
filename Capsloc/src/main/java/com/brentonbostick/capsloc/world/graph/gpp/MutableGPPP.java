package com.brentonbostick.capsloc.world.graph.gpp;

import com.brentonbostick.capsloc.geom.Geom;
import com.brentonbostick.capsloc.geom.Line;
import com.brentonbostick.capsloc.geom.MutableOBB;
import com.brentonbostick.capsloc.geom.MutableSweptOBB;
import com.brentonbostick.capsloc.geom.SweepUtils;
import com.brentonbostick.capsloc.geom.SweptOBB;
import com.brentonbostick.capsloc.math.DMath;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.graph.BypassBoard;
import com.brentonbostick.capsloc.world.graph.BypassBoardPosition;
import com.brentonbostick.capsloc.world.graph.BypassStud;
import com.brentonbostick.capsloc.world.graph.GraphPosition;
import com.brentonbostick.capsloc.world.graph.VertexPosition;

public class MutableGPPP {
	
	public GraphPositionPath path;
	public int index;
	public double param;
	
	public double combo;
	
	public boolean bound;
	
	public Point p;
	public GraphPosition gp;
	
	public double lengthToStartOfPath;
	public double lengthToEndOfPath;
	
	public Point pathVector;
	public double angle;
	
	private int hash;
	
	public void set(GraphPositionPath path, int preIndex, double preParam) {
		
		if (preIndex < 0) {
			throw new IllegalArgumentException("preIndex < 0: " + preIndex);
		}
		if (preIndex >= path.size) {
			throw new IllegalArgumentException("preIndex >= path.size: " + preIndex + " " + path.size);
		}
		if (DMath.lessThan(preParam, 0.0) || DMath.greaterThan(preParam, 1.0)) {
			throw new IllegalArgumentException();
		}
		if (DMath.equals(preParam, 1.0) && preIndex == path.size-1) {
			throw new IllegalArgumentException();
		}
		
		this.path = path;
		/*
		 * allow 1.0 params to make life easier
		 */
		if (DMath.equals(preParam, 1.0)) {
			this.index = preIndex+1;
			this.param = 0.0;
		} else {
			this.index = preIndex;
			this.param = preParam;
		}
		
		this.combo = index+param;
		
		this.bound = DMath.equals(param, 0.0);
		
		if (bound) {
			gp = path.get(index);
			p = gp.p;
		} else {
			GraphPosition p1 = path.get(index);
			GraphPosition p2 = path.get(index+1);			
			double dist = Point.distance(p1.p, p2.p);
			gp = p1.approachNeighbor(p2, dist * param);
			p = gp.p;
		}
		
		double acc = path.cumulativeDistancesFromStart[index];
		acc += Point.distance(path.get(index).p, p);
		
		lengthToStartOfPath = acc;
		lengthToEndOfPath = path.totalLength - lengthToStartOfPath;
		
		assert lengthToStartOfPath <= path.totalLength;
		assert lengthToEndOfPath >= 0;
		
		Point a;
		Point b;
		if (DMath.equals(param, 0.0)) {
			
			if (index == 0) {
				a = path.get(index).p;
				b = path.get(index+1).p;
			} else if (index == path.size-1) {
				a = path.get(index-1).p;
				b = path.get(index).p;
			} else {
				a = path.get(index-1).p;
				b = path.get(index+1).p;
			}
			
		} else {
			a = path.get(index).p;
			b = path.get(index+1).p;
		}
		
		pathVector = new Point(b.y - a.y, b.x - a.x);
		double ang = Math.atan2(b.y - a.y, b.x - a.x);
		ang = DMath.tryAdjustToRightAngle(ang);
		angle = ang;
		
	}
	
	public void set(MutableGPPP v) {
		set(v.path, v.index, v.param);
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + path.hashCode();
			h = 37 * h + index;
			long l = Double.doubleToLongBits(param);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPositionPathPosition || o instanceof MutableGPPP)) {
			return false;
		} else if (o instanceof GraphPositionPathPosition) {
			GraphPositionPathPosition b = (GraphPositionPathPosition)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		} else {
			final MutableGPPP b = (MutableGPPP)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public String toString() {
		return "MutableGPPP[...] " + index + " " + param;
	}
	
	public boolean isEndOfPath() {
		return (index == path.size-1) && DMath.equals(param, 0.0);
	}
	
	public double lengthTo(MutableGPPP p) {
		
		assert p.path.equals(path);
		
		if (DMath.equals(p.combo, combo)) {
			return 0.0;
		} else if (DMath.greaterThanEquals(p.combo, combo)) {
			return p.lengthToStartOfPath - lengthToStartOfPath;
		} else {
			return lengthToStartOfPath - p.lengthToStartOfPath;
		}
		
	}
	
	public void travelForward(double dist) {
		travel(dist, true);
	}
	
	public void travelBackward(double dist) {
		travel(dist, false);
	}
	
	private void travel(double dist, boolean forward) {
		travelByVertex(dist, forward);
	}
	
	private void travelByVertex(double dist, boolean forward) {
		
		if (DMath.equals(dist, 0.0)) {
			return;
		}
		
		assert dist > 0.0;
		
		double traveled = 0.0;
		
		int curPathIndex = index;
		double curPathParam = param;
//		GraphPosition curGraphPosition = gp;
		double curLengthToStartOfPath = lengthToStartOfPath;
		
		while (true) {
			
			/*
			 * try to go to the next vertex first
			 */
			
			int nextVertexIndex = forward ? path.nextVertexIndex(curPathIndex, curPathParam) : path.prevVertexIndex(curPathIndex, curPathParam);
//			GraphPosition nextVertexGP;
			double nextVertexLengthToStartOfPath;
			double distanceToNextVertexPosition;
			if (nextVertexIndex == -1) {
				/*
				 * there is no next vertex
				 */
//				nextVertexGP = null;
				nextVertexLengthToStartOfPath = Double.POSITIVE_INFINITY;
				distanceToNextVertexPosition = Double.POSITIVE_INFINITY;
			} else {
//				nextVertexGP = path.get(nextVertexIndex);
				nextVertexLengthToStartOfPath = path.cumulativeDistancesFromStart[nextVertexIndex];
				distanceToNextVertexPosition = forward ? nextVertexLengthToStartOfPath - curLengthToStartOfPath : curLengthToStartOfPath - nextVertexLengthToStartOfPath;
			}
			
			if (DMath.equals(traveled + distanceToNextVertexPosition, dist)) {
				
				set(path, nextVertexIndex, 0.0);
				
			} else if (traveled + distanceToNextVertexPosition < dist) {
				
				traveled += distanceToNextVertexPosition;
				
				curPathIndex = nextVertexIndex;
				curPathParam = 0.0;
//				curGraphPosition = nextVertexGP;
				curLengthToStartOfPath = nextVertexLengthToStartOfPath;
				
			} else {
				/* 
				 * distanceToNextVertexPosition > toTravel == dist - traveled
				 */
				set(path, curPathIndex, curPathParam);
				travelByBound(dist-traveled, forward, null);
				
			}
			
		}
		
	}
	
	private void travelByBound(double dist, boolean forward, MutableGPPAccumulator acc) {
		
		if (DMath.equals(dist, 0.0)) {
			return;
		}
		
		assert dist > 0.0;
		
		double traveled = 0.0;
		
		final MutableGPPP cur = new MutableGPPP();
		cur.set(path, index, param);
		
		final MutableGPPP next = new MutableGPPP();
		
		while (true) {
			
			/*
			 * try to go to the next bound
			 */
			int nextBoundIndex = forward ? (cur.index < path.size-1 ? cur.index+1 : -1) : (DMath.equals(cur.param, 0.0) ? (cur.index > 0 ? cur.index-1 : -1) : cur.index);
			GraphPosition nextBoundGP;
			double nextBoundLengthToStartOfPath;
			double distanceToNextBound;
			if (nextBoundIndex == -1) {
				/*
				 * there is no next bound
				 */
				throw new IllegalArgumentException(forward ? "There is no next bound" : "There is no previous bound");
			} else {
				nextBoundGP = path.get(nextBoundIndex);
				nextBoundLengthToStartOfPath = path.cumulativeDistancesFromStart[nextBoundIndex];
				distanceToNextBound = forward ? nextBoundLengthToStartOfPath - cur.lengthToStartOfPath : cur.lengthToStartOfPath - nextBoundLengthToStartOfPath;
			}
			
			next.set(path, nextBoundIndex, 0.0);
			
			if (DMath.equals(traveled + distanceToNextBound, dist)) {	
				
				if (acc != null) {
					if (forward) {
						acc.apply(cur, next);
					} else {
						acc.apply(next, cur);
					}
				}
				cur.set(next.path, next.index, next.param);
				
				set(cur.path, cur.index, cur.param);
				return;
				
			} else if (traveled + distanceToNextBound < dist) {
				
				if (acc != null) {
					if (forward) {
						acc.apply(cur, next);
					} else {
						acc.apply(next, cur);
					}
				}
				cur.set(next.path, next.index, next.param);
				
				traveled += distanceToNextBound;
				
			} else {
				/* 
				 * distanceToNextBound > toTravel == dist - traveled
				 */
				
				set(cur.path, cur.index, cur.param);
				travelWithinBound(nextBoundGP, dist - traveled, forward, acc);
				return;
			}
			
		}
		
	}
	
	private void travelWithinBound(GraphPosition nextBoundGP, double dist, boolean forward, MutableGPPAccumulator acc) {
		
		if (DMath.equals(dist, 0.0)) {
			return;
		}
		
		assert dist > 0.0;
		
		assert !gp.equals(nextBoundGP);
		
		GraphPosition ggoal = gp.approachNeighbor(nextBoundGP, dist);
		
		double retCombo = gp.goalGPPPCombo(index, param, forward, ggoal);
		
		int retIndex = (int)Math.floor(retCombo);
		double retParam = retCombo - retIndex;
		
		final MutableGPPP ret = new MutableGPPP();
		ret.set(path, retIndex, retParam);
		
		if (acc != null) {
			if (forward) {
				acc.apply(this, ret);
			} else {
				acc.apply(ret, this);
			}
		}
		
		assert DMath.equals(this.lengthTo(ret), dist);
		
		set(ret.path, ret.index, ret.param);
	}
	
	public void nextBound() {
		assert index < path.size-1;
		set(path, index+1, 0.0);
	}
	
	public void prevBound() {
		if (DMath.equals(param, 0.0)) {
			assert index > 0;
			set(path, index-1, 0.0);
		} else {
			set(path, index, 0.0);
		}
	}
	
	public void floor() {
		if (DMath.equals(param, 0.0)) {
			return;
		} else {
			set(path, index, 0.0);
		}
	}
	
	public void ceil() {
		if (DMath.equals(param, 0.0)) {
			return;
		} else {
			set(path, index+1, 0.0);
		}
	}
	
	public void round() {
		if (DMath.equals(param, 0.0)) {
			return;
		} else {
			if (Math.round(param) == 0) {
				set(path, index, 0.0);
			} else {
				set(path, index+1, 0.0);
			}
		}
	}
	
	public void floor(double length) {
		
		if (DMath.lessThanEquals(length, lengthToStartOfPath)) {
			
			travelBackward(length);
			double tmpCombo = combo;
			
			int tmpFloorIndex = (int)Math.floor(tmpCombo);
			
			set(path, tmpFloorIndex, 0.0);
			travelForward(length);
		} else {
			
			travelForward(length);
			double tmpCombo = combo;
			
			int tmpFloorIndex = (int)Math.floor(tmpCombo);
			
			set(path, tmpFloorIndex, 0.0);
			travelBackward(length);
		}
		
	}
	
	public void ceil(double length) {
		
		if (DMath.lessThanEquals(length, lengthToStartOfPath)) {
			
			travelBackward(length);
			double tmpCombo = combo;
			
			int tmpFloorIndex = (int)Math.ceil(tmpCombo);
			
			set(path, tmpFloorIndex, 0.0);
			travelForward(length);
		} else {
			
			travelForward(length);
			double tmpCombo = combo;
			
			int tmpFloorIndex = (int)Math.ceil(tmpCombo);
			
			set(path, tmpFloorIndex, 0.0);
			travelBackward(length);
		}
	}
	
	public void round(double length) {
		
		if (DMath.lessThanEquals(length, lengthToStartOfPath)) {
			
			travelBackward(length);
			double tmpCombo = combo;
			
			int tmpFloorIndex = (int)Math.round(tmpCombo);
			
			set(path, tmpFloorIndex, 0.0);
			travelForward(length);
		} else {
			
			travelForward(length);
			double tmpCombo = combo;
			
			int tmpFloorIndex = (int)Math.round(tmpCombo);
			
			set(path, tmpFloorIndex, 0.0);
			travelBackward(length);
		}
	}
	
	
	public int prevVertexIndex() {
		return path.prevVertexIndex(index, param);
	}
	
	public int nextVertexIndex() {
		return path.nextVertexIndex(index, param);
	}
	
	public void prevVertexPosition() {
		set(path, path.prevVertexIndex(index, param), 0.0);
	}
	
	public void nextVertexPosition() {
		set(path, path.nextVertexIndex(index, param), 0.0);
	}
	
	/**
	 * searches forward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 */
	public void forwardSearch(final Point p, double lengthFromStart) {
		
		MutableGPPAccumulator forwardSearchAcc = new MutableGPPAccumulator(this);
		forwardSearchAcc.reset(p);
		
		travelByBound(lengthFromStart, true, forwardSearchAcc);
		
		set(forwardSearchAcc.closest.path, forwardSearchAcc.closest.index, forwardSearchAcc.closest.param);
	}
	
	/**
	 * searches backward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 */
	public void backwardSearch(Point p, double lengthFromStart) {
		
		MutableGPPAccumulator forwardSearchAcc = new MutableGPPAccumulator(this);
		forwardSearchAcc.reset(p);
		
		travelByBound(lengthFromStart, false, forwardSearchAcc);
		
		set(forwardSearchAcc.closest.path, forwardSearchAcc.closest.index, forwardSearchAcc.closest.param);
	}
	
	/**
	 * searches both forward and backward from start position
	 */
	public void generalSearch(Point p, double radius) {
		
		final MutableGPPP forwardPos = new MutableGPPP();
		forwardPos.set(this);
		forwardPos.forwardSearch(p, Math.min(radius, this.lengthToEndOfPath));
		
		final MutableGPPP backwardPos = new MutableGPPP();
		backwardPos.set(this);
		backwardPos.backwardSearch(p, Math.min(radius, this.lengthToStartOfPath));
		
		if (this.equals(backwardPos)) {
			
			set(forwardPos);
			
		} else if (this.equals(forwardPos)) {
			
			set(backwardPos);
			
		} else {
			
			double forwardDist = Point.distance(p, forwardPos.p);
			double backwardDist = Point.distance(p, backwardPos.p);
			
			if (DMath.lessThan(backwardDist, forwardDist)) {
				
				set(backwardPos);
			} else {
				
				set(forwardPos);
			}
			
		}
		
	}
	
	/**
	 * from start, move along path until either a collision or reach end. return the position
	 */
	public void furthestAllowablePosition(Car car, MutableGPPP end) {
		
		if (this.equals(end)) {
			return;
		}
		
		if (combo < end.combo) {
			furthestAllowablePositionForward(car, end);		
		} else {
			furthestAllowablePositionBackward(car, end);
		}
	}
	
	
	public transient MutableOBB mao = new MutableOBB();
	public transient MutableOBB mbo = new MutableOBB();
	public transient MutableSweptOBB swept = new MutableSweptOBB();
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
	public void furthestAllowablePositionForward(Car car, MutableGPPP end) {
		
		final MutableGPPP start = new MutableGPPP();
		start.set(this);
		start.findFirstRightAngleForwardOrEnd(end);
		
		if (start.equals(end)) {
			
			set(end);
			return;
			
		} else if (start.index == end.index) {
			
			if (!DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, end.param, param);
				set(car.driver.overallPath, start.index, param);
				return;
			}
			
			set(end);
			return;
			
		} else if (end.index == start.index+1 && DMath.equals(end.param, 0.0)) {
			
			if (!DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					set(car.driver.overallPath, end.index, 0.0);
					return;
				} else {
					set(car.driver.overallPath, start.index, param);
					return;
				}
			}
			
			set(end);
			return;
		}
		
		final MutableGPPP a = new MutableGPPP();
		a.set(start);
		final MutableGPPP b = new MutableGPPP();
		final MutableGPPP startCeiling = new MutableGPPP();
		startCeiling.set(start);
		startCeiling.ceil();
		final MutableGPPP endFloor = new MutableGPPP();
		endFloor.set(end);
		endFloor.floor();
		
		if (!startCeiling.equals(start)) {
			b.set(startCeiling);
			
			if (!DMath.isRightAngle(b.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(a.param, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					set(car.driver.overallPath, a.index+1, 0.0);
					return;
				} else {
					set(car.driver.overallPath, a.index, param);
					return;
				}
			}
			
			a.set(b);
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b.set(a);
			b.nextBound();
			
			if (!DMath.isRightAngle(b.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				// identity
				param = DMath.lerp(0.0, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					set(car.driver.overallPath, a.index+1, 0.0);
					return;
				} else {
					set(car.driver.overallPath, a.index, param);
					return;
				}
			}
			
			a.set(b);
		}
		if (!endFloor.equals(end)) {
			b.set(end);
			
			if (!DMath.isRightAngle(b.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(0.0, b.param, param);
				set(car.driver.overallPath, a.index, param);
				return;
			}
		}
		
		set(end);
		return;
	}
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
	public void furthestAllowablePositionBackward(Car car, MutableGPPP end) {
		
		final MutableGPPP start = new MutableGPPP();
		start.set(this);
		start.findFirstRightAngleBackwardOrEnd(end);
		
		if (start.equals(end)) {
			
			set(end);
			return;
			
		} else if (start.index == end.index) {
			
			if (!DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, end.param, param);
				set(car.driver.overallPath, start.index, param);
				return;
			}
			
			set(end);
			return;
			
		} else if (end.index == start.index-1 && DMath.equals(start.param, 0.0)) {
			
			if (!DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, end.param, param);
				if (DMath.equals(param, 1.0)) {
					set(car.driver.overallPath, start.index, 0.0);
					return;
				} else {
					set(car.driver.overallPath, end.index, param);
					return;
				}
			}
			
			set(end);
			return;
		}
		
		final MutableGPPP b = new MutableGPPP();
		b.set(start);
		final MutableGPPP a = new MutableGPPP();
		final MutableGPPP startFloor = new MutableGPPP();
		startFloor.set(start);
		startFloor.floor();
		final MutableGPPP endCeil = new MutableGPPP();
		endCeil.set(end);
		endCeil.ceil();
		
		if (!startFloor.equals(start)) {
			a.set(startFloor);
			
			if (!DMath.isRightAngle(a.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mbo, mao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(b.param, 0.0, param);
				if (DMath.equals(param, 1.0)) {
					set(car.driver.overallPath, a.index+1, 0.0);
					return;
				} else {
					set(car.driver.overallPath, a.index, param);
					return;
				}
			}
			
			b.set(a);
		}
		while (true) {
			
			if (b.equals(endCeil)) {
				break;
			}
			
			a.set(b);
			a.prevBound();
			
			if (!DMath.isRightAngle(a.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mbo, mao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, 0.0, param);
				if (DMath.equals(param, 1.0)) {
					set(car.driver.overallPath, a.index+1, 0.0);
					return;
				} else {
					set(car.driver.overallPath, a.index, param);
					return;
				}
			}
			
			b.set(a);
		}
		if (!endCeil.equals(end)) {
			a.set(end);
			
			if (!DMath.isRightAngle(a.angle)) {
				set(end);
				return;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mbo, mao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, a.param, param);
				set(car.driver.overallPath, a.index, param);
				return;
			}
		}
		
		set(end);
		return;
	}
	
	public void findFirstRightAngleForwardOrEnd(MutableGPPP end) {
		
		if (DMath.isRightAngle(this.angle)) {
			
			return;
			
		} else if (this.index == end.index) {
			
			if (DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			set(end);
			return;
			
		} else if (end.index == this.index+1 && DMath.equals(end.param, 0.0)) {
			
			if (DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			set(end);
			return;
			
		}
		
		final MutableGPPP a = new MutableGPPP();
		a.set(this);
		final MutableGPPP b = new MutableGPPP();
		final MutableGPPP startCeiling = new MutableGPPP();
		startCeiling.set(this);
		startCeiling.ceil();
		final MutableGPPP endFloor = new MutableGPPP();
		endFloor.set(end);
		endFloor.floor();
		
		if (!startCeiling.equals(this)) {
			b.set(startCeiling);
			
			if (DMath.isRightAngle(b.angle)) {
				set(b);
				return;
			}
			
			a.set(b);
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b.set(a);
			b.nextBound();
			
			if (DMath.isRightAngle(b.angle)) {
				set(b);
				return;
			}
			
			a.set(b);
		}
		if (!endFloor.equals(end)) {
			b.set(end);
			
			if (DMath.isRightAngle(b.angle)) {
				set(b);
				return;
			}
			
		}
		
		set(end);
		return;
	}
	
	public void findFirstRightAngleBackwardOrEnd(MutableGPPP end) {
		
		if (DMath.isRightAngle(this.angle)) {
			
			return;
			
		} else if (this.index == end.index) {
			
			if (DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			set(end);
			return;
			
		} else if (end.index == this.index-1 && DMath.equals(this.param, 0.0)) {
			
			if (DMath.isRightAngle(end.angle)) {
				set(end);
				return;
			}
			
			set(end);
			return;
		}
		
		final MutableGPPP b = new MutableGPPP();
		b.set(this);
		final MutableGPPP a = new MutableGPPP();
		final MutableGPPP startFloor = new MutableGPPP();
		startFloor.set(this);
		startFloor.floor();
		final MutableGPPP endCeil = new MutableGPPP();
		endCeil.set(end);
		endCeil.ceil();
		
		if (!startFloor.equals(this)) {
			a.set(startFloor);
			
			if (DMath.isRightAngle(a.angle)) {
				set(a);
				return;
			}
			
			b.set(a);
		}
		while (true) {
			
			if (b.equals(endCeil)) {
				break;
			}
			
			a.set(b);
			a.prevBound();
			
			if (DMath.isRightAngle(a.angle)) {
				set(a);
				return;
			}
			
			b.set(a);
		}
		if (!endCeil.equals(end)) {
			a.set(end);
			
			if (DMath.isRightAngle(a.angle)) {
				set(a);
				return;
			}
			
		}
		
		set(end);
		return;
	}
	
	/**
	 * on the segment from start.center to end.center, what is the param of the first collision?
	 */
	public static double firstCollisionParam(Car car, SweptOBB swept) {
		
		double bestParam = -1.0;
		
		for (BypassBoard b : car.world.graph.boards) {
			for (Line l : b.perimeterSegments) {
				double param = SweepUtils.firstCollisionParam(l, swept);
				if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
					bestParam = param;
				}
			}
		}
		
		for (Car c : car.world.carMap.cars) {
			if (c == car) {
				continue;
			}
			double param = SweepUtils.firstCollisionParam(c.shape, swept);
			if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
				bestParam = param;
			}
		}
		
		return bestParam;
	}
	
	public static double firstCollisionParam(Car car, MutableSweptOBB swept) {
		
		double bestParam = -1.0;
		
		for (BypassBoard b : car.world.graph.boards) {
			for (Line l : b.perimeterSegments) {
				double param = SweepUtils.firstCollisionParam(l, swept);
				if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
					bestParam = param;
				}
			}
		}
		
		for (Car c : car.world.carMap.cars) {
			if (c == car) {
				continue;
			}
			double param = SweepUtils.firstCollisionParam(c.shape, swept);
			if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
				bestParam = param;
			}
		}
		
		return bestParam;
	}
	
	public int movesDistance(GraphPositionPathPosition end, Car c) {
		
		boolean forward;
		if (end.combo > this.combo) {
			forward = true;
		} else {
			forward = false;
		}
		
		final MutableGPPP start = this;
		
		assert start.gp instanceof BypassBoardPosition;
		BypassBoard board = (BypassBoard)((BypassBoardPosition)start.gp).entity;
		assert board.withinGrid(c, start.angle, start.p);
		
		if (start.equals(end)) {
			return 0;
		}
		
		int m = 0;
		
		final MutableGPPP a = new MutableGPPP();
		a.set(start);
		final MutableGPPP b = new MutableGPPP();
		
		while (true) {
			
			if (a.equals(end)) {
				break;
			}
			
			b.set(a);
			b.travel(BypassStud.SIZE, forward);
			
			assert b.gp instanceof BypassBoardPosition;
			if (board.withinGrid(c, b.angle, b.p)) {
				m++;
			} else {	
				
				// find first vertex
				while (true) {
					if (forward) {
						b.nextBound();
					} else {
						b.prevBound();
					}
					if (b.gp instanceof VertexPosition) {
						break;
					}
				}
				//go through road, find next vertex
				while (true) {
					if (forward) {
						b.nextBound();
					} else {
						b.prevBound();
					}
					if (b.gp instanceof VertexPosition) {
						break;
					}
				}
				
				if (b.isEndOfPath()) {
					
					break;
					
				} else {
					
					b.travel(BypassStud.SIZE + c.length/2, forward);
					
					assert b.gp instanceof BypassBoardPosition;
					assert board.withinGrid(c, b.angle, b.p);
					
				}
				
				m++;
				
			}
			
			a.set(b);
		}
		
		return m;
	}
	
}
