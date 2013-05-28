package com.brentonbostick.capsloc.world.graph.gpp;

import java.io.Serializable;

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

public class GraphPositionPathPosition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public transient GraphPositionPath path;
	public final int index;
	public final double param;
	
	public final double combo;
	public final boolean bound;
	
	public transient Point p;
	public transient GraphPosition gp;
	
	public final double lengthToStartOfPath;
	public final double lengthToEndOfPath;
	
	public final double angle;
	
	private int hash;
	
	public GraphPositionPathPosition(GraphPositionPath path, int preIndex, double preParam) {
		
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
		
		combo = index+param;
		
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
		
		double ang = Math.atan2(b.y - a.y, b.x - a.x);
		ang = DMath.tryAdjustToRightAngle(ang);
		angle = ang;
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
		} else if (!(o instanceof GraphPositionPathPosition)) {
			return false;
		} else {
			GraphPositionPathPosition b = (GraphPositionPathPosition)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public String toString() {
		return "GPPP[...] " + index + " " + param;
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public boolean isStartOfPath() {
		return (index == 0) && DMath.equals(param, 0.0);
	}
	
	public boolean isEndOfPath() {
		return (index == path.size-1) && DMath.equals(param, 0.0);
	}
	
	public Point pathVector() {
		
		if (DMath.equals(param, 0.0)) {
			if (index == 0) {
				return path.get(index+1).p.minus(path.get(index).p);
			} else if (index == path.size-1) {
				return path.get(index).p.minus(path.get(index-1).p);
			} else {
				return path.get(index+1).p.minus(path.get(index-1).p);
			}
		} else {
			return path.get(index+1).p.minus(path.get(index).p);
		}
		
	}
	
	public double lengthTo(GraphPositionPathPosition p) {
		
		assert p.path.equals(path);
		
		if (DMath.equals(p.combo, combo)) {
			return 0.0;
		} else if (DMath.greaterThanEquals(p.combo, combo)) {
			return p.lengthToStartOfPath - lengthToStartOfPath;
		} else {
			return lengthToStartOfPath - p.lengthToStartOfPath;
		}
		
	}
	
	private GraphPositionPathPosition travel(double dist, boolean forward) {
		return travelByVertex(dist, forward);
	}
	
	private GraphPositionPathPosition travelByVertex(double dist, boolean forward) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
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
				
				return new GraphPositionPathPosition(path, nextVertexIndex, 0.0);
				
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
				return new GraphPositionPathPosition(path, curPathIndex, curPathParam).travelByBound(dist-traveled, forward, null);
				
			}
			
		}
		
	}
	
	private GraphPositionPathPosition travelByBound(double dist, boolean forward, GPPAccumulator acc) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		
		assert dist > 0.0;
		
		double traveled = 0.0;
		
		GraphPositionPathPosition cur = this;
		
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
			
			GraphPositionPathPosition next = new GraphPositionPathPosition(path, nextBoundIndex, 0.0);
			
			if (DMath.equals(traveled + distanceToNextBound, dist)) {	
				
				if (acc != null) {
					if (forward) {
						acc.apply(cur, next);
					} else {
						acc.apply(next, cur);
					}
				}
				cur = next;
				
				return cur;
				
			} else if (traveled + distanceToNextBound < dist) {
				
				if (acc != null) {
					if (forward) {
						acc.apply(cur, next);
					} else {
						acc.apply(next, cur);
					}
				}
				
				cur = next;
				
				traveled += distanceToNextBound;
				
			} else {
				/* 
				 * distanceToNextBound > toTravel == dist - traveled
				 */
				
				return cur.travelWithinBound(nextBoundGP, dist - traveled, forward, acc);
				
			}
			
		}
		
	}
	
	private GraphPositionPathPosition travelWithinBound(GraphPosition nextBoundGP, double dist, boolean forward, GPPAccumulator acc) {
		
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		
		assert dist > 0.0;
		
		assert !gp.equals(nextBoundGP);
		
		GraphPosition ggoal = gp.approachNeighbor(nextBoundGP, dist);
		
		double retCombo = gp.goalGPPPCombo(index, param, forward, ggoal, path, this, dist);
		
		int retIndex = (int)Math.floor(retCombo);
		double retParam = retCombo - retIndex;
		
		GraphPositionPathPosition ret = new GraphPositionPathPosition(path, retIndex, retParam);
		
		if (acc != null) {
			if (forward) {
				acc.apply(this, ret);
			} else {
				acc.apply(ret, this);
			}
		}
		
		assert DMath.equals(this.lengthTo(ret), dist);
		
		return ret;
		
	}
	
	public GraphPositionPathPosition travelForward(double dist) {
		return travel(dist, true);
	}
	
	public GraphPositionPathPosition travelBackward(double dist) {
		return travel(dist, false);
	}
	
	public GraphPositionPathPosition nextBound() {
		assert index < path.size-1;
		return new GraphPositionPathPosition(path, index+1, 0.0);
	}
	
	public GraphPositionPathPosition prevBound() {
		if (DMath.equals(param, 0.0)) {
			assert index > 0;
			return new GraphPositionPathPosition(path, index-1, 0.0);
		} else {
			return new GraphPositionPathPosition(path, index, 0.0);
		}
	}
	
	public GraphPositionPathPosition floor() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else {
			return new GraphPositionPathPosition(path, index, 0.0);
		}
	}
	
	public GraphPositionPathPosition ceil() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else {
			return new GraphPositionPathPosition(path, index+1, 0.0);
		}
	}
	
	public GraphPositionPathPosition round() {
		if (DMath.equals(param, 0.0)) {
			return this;
		} else {
			if (Math.round(param) == 0) {
				return new GraphPositionPathPosition(path, index, 0.0);
			} else {
				return new GraphPositionPathPosition(path, index+1, 0.0);
			}
		}
	}
	
	
	
	
	
	
	public GraphPositionPathPosition floor(double length) {
		
		if (DMath.lessThanEquals(length, lengthToStartOfPath)) {
			
			double tmpCombo = travelBackward(length).combo;
			
			int tmpFloorIndex = (int)Math.floor(tmpCombo);
			
			return new GraphPositionPathPosition(path, tmpFloorIndex, 0.0).travelForward(length);
		} else {
			
			double tmpCombo = travelForward(length).combo;
			
			int tmpFloorIndex = (int)Math.floor(tmpCombo);
			
			return new GraphPositionPathPosition(path, tmpFloorIndex, 0.0).travelBackward(length);
		}
		
	}
	
	public GraphPositionPathPosition ceil(double length) {
		
		if (DMath.lessThanEquals(length, lengthToStartOfPath)) {
			
			double tmpCombo = travelBackward(length).combo;
			
			int tmpFloorIndex = (int)Math.ceil(tmpCombo);
			
			return new GraphPositionPathPosition(path, tmpFloorIndex, 0.0).travelForward(length);
		} else {
			
			double tmpCombo = travelForward(length).combo;
			
			int tmpFloorIndex = (int)Math.ceil(tmpCombo);
			
			return new GraphPositionPathPosition(path, tmpFloorIndex, 0.0).travelBackward(length);
		}
	}
	
	public GraphPositionPathPosition round(double length) {
		
		if (DMath.lessThanEquals(length, lengthToStartOfPath)) {
			
			double tmpCombo = travelBackward(length).combo;
			
			int tmpFloorIndex = (int)Math.round(tmpCombo);
			
			return new GraphPositionPathPosition(path, tmpFloorIndex, 0.0).travelForward(length);
		} else {
			
			double tmpCombo = travelForward(length).combo;
			
			int tmpFloorIndex = (int)Math.round(tmpCombo);
			
			return new GraphPositionPathPosition(path, tmpFloorIndex, 0.0).travelBackward(length);
		}
	}
	
	
	public int prevVertexIndex() {
		return path.prevVertexIndex(index, param);
	}
	
	public int nextVertexIndex() {
		return path.nextVertexIndex(index, param);
	}
	
	public GraphPositionPathPosition prevVertexPosition() {
		return new GraphPositionPathPosition(path, path.prevVertexIndex(index, param), 0.0);
	}
	
	public GraphPositionPathPosition nextVertexPosition() {
		return new GraphPositionPathPosition(path, path.nextVertexIndex(index, param), 0.0);
	}
	
	/**
	 * searches forward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 */
	public GraphPositionPathPosition forwardSearch(final Point p, double lengthFromStart) {
		
		GPPAccumulator forwardSearchAcc = new GPPAccumulator(this);
		forwardSearchAcc.reset(p);
		
		travelByBound(lengthFromStart, true, forwardSearchAcc);
		
		return forwardSearchAcc.result();
	}
	
	/**
	 * searches backward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 */
	public GraphPositionPathPosition backwardSearch(Point p, double lengthFromStart) {
		
		GPPAccumulator forwardSearchAcc = new GPPAccumulator(this);
		forwardSearchAcc.reset(p);
		
		travelByBound(lengthFromStart, false, forwardSearchAcc);
		
		return forwardSearchAcc.result();
	}
	
	/**
	 * searches both forward and backward from start position
	 */
	public GraphPositionPathPosition generalSearch(Point p, double radius) {
		
		GraphPositionPathPosition forwardPos = forwardSearch(p, Math.min(radius, this.lengthToEndOfPath));
		GraphPositionPathPosition backwardPos = backwardSearch(p, Math.min(radius, this.lengthToStartOfPath));
		
		if (this.equals(backwardPos)) {
			
			return forwardPos;
			
		} else if (this.equals(forwardPos)) {
			
			return backwardPos;
			
		} else {
			
			double forwardDist = Point.distance(p, forwardPos.p);
			double backwardDist = Point.distance(p, backwardPos.p);
			
			if (DMath.lessThan(backwardDist, forwardDist)) {
				
				return backwardPos;
			} else {
				
				return forwardPos;
			}
			
		}
		
	}
	
	/**
	 * from start, move along path until either a collision or reach end. return the position
	 */
	public GraphPositionPathPosition furthestAllowablePosition(Car car, GraphPositionPathPosition end) {
		
		if (this.equals(end)) {
			return this;
		}
		
		return (combo < end.combo) ? furthestAllowablePositionForward(car, end) : furthestAllowablePositionBackward(car, end);		
	}
	
	
	public transient MutableOBB mao = new MutableOBB();
	public transient MutableOBB mbo = new MutableOBB();
	public transient MutableSweptOBB swept = new MutableSweptOBB();
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
	public GraphPositionPathPosition furthestAllowablePositionForward(Car car, GraphPositionPathPosition end) {
		
		GraphPositionPathPosition start = findFirstRightAngleForwardOrEnd(end);
		
		if (start.equals(end)) {
			
			return end;
			
		} else if (start.index == end.index) {
			
			if (!DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, end.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, start.index, param);
			}
			
			return end;
		} else if (end.index == start.index+1 && DMath.equals(end.param, 0.0)) {
			
			if (!DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, end.index, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, start.index, param);
				}
			}
			
			return end;
			
		}
		
		GraphPositionPathPosition a = start;
		GraphPositionPathPosition b;
		GraphPositionPathPosition startCeiling = start.ceil();
		GraphPositionPathPosition endFloor = end.floor();
		
		if (!startCeiling.equals(start)) {
			b = startCeiling;
			
			if (!DMath.isRightAngle(b.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(a.param, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			a = b;
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b = a.nextBound();
			
			if (!DMath.isRightAngle(b.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				// identity
				param = DMath.lerp(0.0, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			a = b;
		}
		if (!endFloor.equals(end)) {
			b = end;
			
			if (!DMath.isRightAngle(b.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(0.0, b.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
			}
		}
		
		return end;
	}
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
	public GraphPositionPathPosition furthestAllowablePositionBackward(Car car, GraphPositionPathPosition end) {
		
		GraphPositionPathPosition start = findFirstRightAngleBackwardOrEnd(end);
		
		if (start.equals(end)) {
			
			return end;
			
		} else if (start.index == end.index) {
			
			if (!DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, end.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, start.index, param);
			}
			
			return end;
			
		} else if (end.index == start.index-1 && DMath.equals(start.param, 0.0)) {
			
			if (!DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, start.angle, start.p, mao);
			Geom.localToWorld(car.localAABB, end.angle, end.p, mbo);
			swept.setShape(mao, mbo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, end.param, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, start.index, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, end.index, param);
				}
			}
			
			return end;
		}
		
		GraphPositionPathPosition b = start;
		GraphPositionPathPosition a;
		GraphPositionPathPosition startFloor = start.floor();
		GraphPositionPathPosition endCeil = end.ceil();
		
		if (!startFloor.equals(start)) {
			a = startFloor;
			
			if (!DMath.isRightAngle(a.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mbo, mao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(b.param, 0.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			b = a;
		}
		while (true) {
			
			if (b.equals(endCeil)) {
				break;
			}
			
			a = b.prevBound();
			
			if (!DMath.isRightAngle(a.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mbo, mao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, 0.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			b = a;
		}
		if (!endCeil.equals(end)) {
			a = end;
			
			if (!DMath.isRightAngle(a.angle)) {
				return end;
			}
			
			Geom.localToWorld(car.localAABB, a.angle, a.p, mao);
			Geom.localToWorld(car.localAABB, b.angle, b.p, mbo);
			swept.setShape(mbo, mao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, a.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
			}
		}
		
		return end;
	}
	
	public GraphPositionPathPosition findFirstRightAngleForwardOrEnd(GraphPositionPathPosition end) {
		
		if (DMath.isRightAngle(this.angle)) {
			
			return this;
			
		} else if (this.index == end.index) {
			
			if (DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			return end;
			
		} else if (end.index == this.index+1 && DMath.equals(end.param, 0.0)) {
			
			if (DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			return end;
			
		}
		
		GraphPositionPathPosition a = this;
		GraphPositionPathPosition b;
		GraphPositionPathPosition startCeiling = this.ceil();
		GraphPositionPathPosition endFloor = end.floor();
		
		if (!startCeiling.equals(this)) {
			b = startCeiling;
			
			if (DMath.isRightAngle(b.angle)) {
				return b;
			}
			
			a = b;
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b = a.nextBound();
			
			if (DMath.isRightAngle(b.angle)) {
				return b;
			}
			
			a = b;
		}
		if (!endFloor.equals(end)) {
			b = end;
			
			if (DMath.isRightAngle(b.angle)) {
				return b;
			}
			
		}
		
		return end;
	}
	
	public GraphPositionPathPosition findFirstRightAngleBackwardOrEnd(GraphPositionPathPosition end) {
		
		if (DMath.isRightAngle(this.angle)) {
			
			return this;
			
		} else if (this.index == end.index) {
			
			if (DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			return end;
			
		} else if (end.index == this.index-1 && DMath.equals(this.param, 0.0)) {
			
			if (DMath.isRightAngle(end.angle)) {
				return end;
			}
			
			return end;
		}
		
		GraphPositionPathPosition b = this;
		GraphPositionPathPosition a;
		GraphPositionPathPosition startFloor = this.floor();
		GraphPositionPathPosition endCeil = end.ceil();
		
		if (!startFloor.equals(this)) {
			a = startFloor;
			
			if (DMath.isRightAngle(a.angle)) {
				return a;
			}
			
			b = a;
		}
		while (true) {
			
			if (b.equals(endCeil)) {
				break;
			}
			
			a = b.prevBound();
			
			if (DMath.isRightAngle(a.angle)) {
				return a;
			}
			
			b = a;
		}
		if (!endCeil.equals(end)) {
			a = end;
			
			if (DMath.isRightAngle(a.angle)) {
				return a;
			}
			
		}
		
		return end;
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
		
		GraphPositionPathPosition start = this;
		
		assert start.gp instanceof BypassBoardPosition;
		BypassBoard board = (BypassBoard)((BypassBoardPosition)start.gp).entity;
		assert board.withinGrid(c, start.angle, start.p);
		
		if (start.equals(end)) {
			return 0;
		}
		
		int m = 0;
		
		GraphPositionPathPosition a = start;
		GraphPositionPathPosition b;
		
		while (true) {
			
			if (a.equals(end)) {
				break;
			}
			
			b = a.travel(BypassStud.SIZE, forward);
			
			assert b.gp instanceof BypassBoardPosition;
			if (board.withinGrid(c, b.angle, b.p)) {
				m++;
			} else {	
				
				// find first vertex
				while (true) {
					b = forward ? b.nextBound() : b.prevBound();
					if (b.gp instanceof VertexPosition) {
						break;
					}
				}
				//go through road, find next vertex
				while (true) {
					b = forward ? b.nextBound() : b.prevBound();
					if (b.gp instanceof VertexPosition) {
						break;
					}
				}
				
				if (b.isEndOfPath()) {
					
					break;
					
				} else {
					
					b = b.travel(BypassStud.SIZE + c.length/2, forward);
					
					assert b.gp instanceof BypassBoardPosition;
					assert board.withinGrid(c, b.angle, b.p);
					
				}
				
				m++;
				
			}
			
			a = b;
		}
		
		return m;
	}
	
}
