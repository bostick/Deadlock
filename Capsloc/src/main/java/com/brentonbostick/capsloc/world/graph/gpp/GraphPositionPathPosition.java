package com.brentonbostick.capsloc.world.graph.gpp;

import java.io.Serializable;

import com.brentonbostick.capsloc.geom.Line;
import com.brentonbostick.capsloc.geom.MutableSweptOBB;
import com.brentonbostick.capsloc.geom.SweepUtils;
import com.brentonbostick.capsloc.geom.SweptOBB;
import com.brentonbostick.capsloc.math.DMath;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.graph.BypassBoard;
import com.brentonbostick.capsloc.world.graph.GraphPosition;

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
	
	public final double pathVectorX;
	public final double pathVectorY;
	public final double angle;
	
	private int hash;
	
	public transient MutableGPPP gppp = new MutableGPPP();
	
	public GraphPositionPathPosition(GraphPositionPath path, int preIndex, double preParam) {
		
		gppp.set(path, preIndex, preParam);
		
		this.path = gppp.path;
		this.index = gppp.index;
		this.param = gppp.param;
		
		this.combo = gppp.combo;
		
		this.bound = gppp.bound;
		
		this.gp = gppp.gp;
		this.p = gppp.p;
		
		this.lengthToStartOfPath = gppp.lengthToStartOfPath;
		this.lengthToEndOfPath = gppp.lengthToEndOfPath;
		
		this.pathVectorX = gppp.pathVectorX;
		this.pathVectorY = gppp.pathVectorY;
		this.angle = gppp.angle;
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
			MutableGPPP b = (MutableGPPP)o;
			return path == b.path && index == b.index && DMath.equals(param, b.param);
		}
	}
	
	public String toString() {
		return "GPPP[...] " + index + " " + param;
	}
	public boolean isEndOfPath() {
		return gppp.isEndOfPath();
	}
	
	public double lengthTo(GraphPositionPathPosition p) {
		return gppp.lengthTo(p.gppp);
	}
	
	public GraphPositionPathPosition travelForward(double dist) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.travelForward(dist);
		return tmp.copy();
	}
	
	public GraphPositionPathPosition travelBackward(double dist) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.travelBackward(dist);
		return tmp.copy();
	}
	
	public GraphPositionPathPosition nextBound() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.nextBound();
		return tmp.copy();
	}
	
	public GraphPositionPathPosition prevBound() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.prevBound();
		return tmp.copy();
	}
	
	public GraphPositionPathPosition floor() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.floor();
		return tmp.copy();
	}
	
	public GraphPositionPathPosition ceil() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.ceil();
		return tmp.copy();
	}
	
	public GraphPositionPathPosition round() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.round();
		return tmp.copy();
	}
	
	public GraphPositionPathPosition floor(double length) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.floor(length);
		return tmp.copy();
	}
	
	public GraphPositionPathPosition ceil(double length) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.ceil(length);
		return tmp.copy();
	}
	
	public GraphPositionPathPosition round(double length) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.round(length);
		return tmp.copy();
	}
	
	
	public int prevVertexIndex() {
		return path.prevVertexIndex(index, param);
	}
	
	public int nextVertexIndex() {
		return path.nextVertexIndex(index, param);
	}
	
	public GraphPositionPathPosition prevVertexPosition() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.prevVertexPosition();
		return tmp.copy();
	}
	
	public GraphPositionPathPosition nextVertexPosition() {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.nextVertexPosition();
		return tmp.copy();
	}
	
	/**
	 * searches forward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 */
	public GraphPositionPathPosition forwardSearch(final Point p, double lengthFromStart, MutableGPPAccumulator acc) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.forwardSearch(p, lengthFromStart, acc);
		return tmp.copy();
	}
	
	/**
	 * searches backward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 */
	public GraphPositionPathPosition backwardSearch(Point p, double lengthFromStart, MutableGPPAccumulator acc) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.backwardSearch(p, lengthFromStart, acc);
		return tmp.copy();
	}
	
	/**
	 * searches both forward and backward from start position
	 */
	public GraphPositionPathPosition generalSearch(Point p, double radius, MutableGPPAccumulator acc) {
		MutableGPPP tmp = new MutableGPPP();
		tmp.set(gppp);
		tmp.generalSearch(p, radius, acc);
		return tmp.copy();
	}
	
	/**
	 * from start, move along path until either a collision or reach end. return the position
	 */
//	public GraphPositionPathPosition furthestAllowablePosition(Car car, GraphPositionPathPosition end) {
//		MutableGPPP tmp = new MutableGPPP();
//		tmp.set(gppp);
//		tmp.furthestAllowablePosition(car, end);
//		return tmp.copy();		
//	}
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
//	public GraphPositionPathPosition furthestAllowablePositionForward(Car car, GraphPositionPathPosition end) {
//		MutableGPPP tmp = new MutableGPPP();
//		tmp.set(gppp);
//		tmp.furthestAllowablePositionForward(car, end);
//		return tmp.copy();
//	}
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
//	public GraphPositionPathPosition furthestAllowablePositionBackward(Car car, GraphPositionPathPosition end) {
//		MutableGPPP tmp = new MutableGPPP();
//		tmp.set(gppp);
//		tmp.furthestAllowablePositionBackward(car, end);
//		return tmp.copy();
//	}
	
//	public GraphPositionPathPosition findFirstRightAngleForwardOrEnd(GraphPositionPathPosition end) {
//		MutableGPPP tmp = new MutableGPPP();
//		tmp.set(gppp);
//		tmp.findFirstRightAngleForwardOrEnd(end);
//		return tmp.copy();
//	}
	
//	public GraphPositionPathPosition findFirstRightAngleBackwardOrEnd(GraphPositionPathPosition end) {
//		MutableGPPP tmp = new MutableGPPP();
//		tmp.set(gppp);
//		tmp.findFirstRightAngleBackwardOrEnd(end);
//		return tmp.copy();
//	}
	
	/**
	 * on the segment from start.center to end.center, what is the param of the first collision?
	 */
	public static double firstCollisionParam(Car car, SweptOBB swept) {
		
		double bestParam = -1.0;
		
		for (int i = 0; i < car.world.graph.boards.size(); i++) {
			BypassBoard b = car.world.graph.boards.get(i);
			for (int j = 0; j < b.perimeterSegments.size(); j++) {
				Line l = b.perimeterSegments.get(j);
				double param = SweepUtils.firstCollisionParam(l, swept);
				if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
					bestParam = param;
				}
			}
		}
		
		for (int i = 0; i < car.world.carMap.cars.size(); i++) {
			Car c = car.world.carMap.cars.get(i);
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
		
		for (int i = 0; i < car.world.graph.boards.size(); i++) {
			BypassBoard b = car.world.graph.boards.get(i);
			for (int j = 0; j < b.perimeterSegments.size(); j++) {
				Line l = b.perimeterSegments.get(j);
				double param = SweepUtils.firstCollisionParam(l, swept);
				if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
					bestParam = param;
				}
			}
		}
		
		for (int i = 0; i < car.world.carMap.cars.size(); i++) {
			Car c = car.world.carMap.cars.get(i);
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
	
//	public int movesDistance(GraphPositionPathPosition end, Car c) {
//		MutableGPPP tmp = new MutableGPPP();
//		tmp.set(gppp);
//		return tmp.movesDistance(end, c);
//	}
	
}
