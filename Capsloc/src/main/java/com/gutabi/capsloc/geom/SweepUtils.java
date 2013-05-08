package com.gutabi.capsloc.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;

public class SweepUtils {
	
	public static List<SweepEvent> sweepStartCSoverA(Object stillParent, AABB still, CapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersectAC(still, moving.getStart())) {
			events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, 0, 0.0, offset));
		}
		
		return events;
		
	}
	
	public static List<SweepEvent> sweepCSoverA(Object stillParent, AABB still, CapsuleSequence moving, int index, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Capsule cap = moving.getCapsule(index);
		
		boolean outside;
		if (ShapeUtils.intersectAC(still, cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleOverLine(still.getP0P1Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP1P2Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP2P3Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP3P0Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			
			if (DMath.greaterThan(param, 0.0)) {
				
				assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
				if (outside) {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, index, param, offset));
				} else {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.exit(stillParent), stillParent, still, moving, index, param, offset));
				}
				outside = !outside;
				
			}
			
		}
		
		return events;
		
	}

	public static List<SweepEvent> sweepCSoverA(Object stillParent, AABB still, MutableCapsuleSequence moving, int index, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Capsule cap = moving.getCapsule(index);
		
		boolean outside;
		if (ShapeUtils.intersectAC(still, cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleOverLine(still.getP0P1Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP1P2Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP2P3Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP3P0Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			
			if (DMath.greaterThan(param, 0.0)) {
				
				assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
				if (outside) {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, index, param, offset));
				} else {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.exit(stillParent), stillParent, still, moving, index, param, offset));
				}
				outside = !outside;
				
			}
			
		}
		
		return events;
		
	}
	
	public static List<SweepEvent> sweepStartCSoverCap(Object stillParent, Capsule still, CapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersect(still, moving.getStart())) {
			events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, 0, 0.0, offset));
		}
		
		return events;
	}

	public static List<SweepEvent> sweepStartCSoverCap(Object stillParent, Capsule still, MutableCapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersect(still, moving.getStart())) {
			events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, 0, 0.0, offset));
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepCSoverCap(Object stillParent, Capsule still, CapsuleSequence moving, int index, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Capsule cap = moving.getCapsule(index);
		
		Point c = cap.a;
		Point d = cap.b;
		
		boolean outside;
		if (ShapeUtils.intersect(still, cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam;
		
		/*
		 * a cap
		 */
		
		double[] capParams = new double[2];
		int n = SweepUtils.sweepCircleOverCircle(still.ac, cap, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it is beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.a, p, still.b);
				
				if (DMath.lessThanEquals(abParam, 0.0)) {
					
					assert ShapeUtils.intersectCC(still.ac, new Circle(p, cap.r));
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		/*
		 * top side, left hand side of <a, b>
		 */
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getABUp(), cap);
		
		if (cdParam != -1) {
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it isn't beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.aUp, p, still.bUp);
				
				if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
					
				}
				
			}
			
		}
		
		/*
		 * b cap
		 */
		
		n = SweepUtils.sweepCircleOverCircle(still.bc, cap, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it is beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.a, p, still.b);
				
				if (DMath.greaterThanEquals(abParam, 1.0)) {
					
					assert ShapeUtils.intersectCC(still.bc, new Circle(p, cap.r));
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		/*
		 * bottom side
		 */
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getBADown(), cap);
		
		if (cdParam != -1) {
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it isn't beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.bDown, p, still.aDown);
				
				if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			
			if (outside) {
				events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, index, param, offset));
			} else {
				events.add(new CapsuleSequenceSweepEvent(SweepEventType.exit(stillParent), stillParent, still, moving, index, param, offset));
			}
			outside = !outside;
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepCSoverCap(Object stillParent, Capsule still, MutableCapsuleSequence moving, int index, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Capsule cap = moving.getCapsule(index);
		
		Point c = cap.a;
		Point d = cap.b;
		
		boolean outside;
		if (ShapeUtils.intersect(still, cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam;
		
		/*
		 * a cap
		 */
		
		double[] capParams = new double[2];
		int n = SweepUtils.sweepCircleOverCircle(still.ac, cap, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it is beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.a, p, still.b);
				
				if (DMath.lessThanEquals(abParam, 0.0)) {
					
					assert ShapeUtils.intersectCC(still.ac, new Circle(p, cap.r));
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		/*
		 * top side, left hand side of <a, b>
		 */
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getABUp(), cap);
		
		if (cdParam != -1) {
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it isn't beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.aUp, p, still.bUp);
				
				if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
					
				}
				
			}
			
		}
		
		/*
		 * b cap
		 */
		
		n = SweepUtils.sweepCircleOverCircle(still.bc, cap, capParams);
		
		for (int i = 0; i < n; i++) {
			
			cdParam = capParams[i];
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it is beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.a, p, still.b);
				
				if (DMath.greaterThanEquals(abParam, 1.0)) {
					
					assert ShapeUtils.intersectCC(still.bc, new Circle(p, cap.r));
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		/*
		 * bottom side
		 */
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getBADown(), cap);
		
		if (cdParam != -1) {
			
			if (DMath.greaterThan(cdParam, 0.0)) {
				
				/*
				 * still have to test that it isn't beyond the end points
				 */
				Point p = Point.point(c, d, cdParam);
				double abParam = Point.u(still.bDown, p, still.aDown);
				
				if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
					
					boolean present = false;
					for (int j = 0; j < paramCount; j++) {
						if (DMath.equals(params[j], cdParam)) {
							present = true;
							break;
						}
					}
					if (!present) {
						params[paramCount] = cdParam;
						paramCount++;
					}
				}
				
			}
			
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
			
			if (outside) {
				events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, index, param, offset));
			} else {
				events.add(new CapsuleSequenceSweepEvent(SweepEventType.exit(stillParent), stillParent, still, moving, index, param, offset));
			}
			outside = !outside;
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepStartCSoverCS(Object stillParent, CapsuleSequence still, CapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (!ShapeUtils.intersectAA(still.aabb, moving.aabb)) {
			return events;
		}
		
		for (Capsule c : still.caps) {
			events.addAll(SweepUtils.sweepStartCSoverCap(stillParent, c, moving, offset));
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepStartCSoverCS(Object stillParent, CapsuleSequence still, MutableCapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (!ShapeUtils.intersectAA(still.aabb, moving.aabb)) {
			return events;
		}
		
		for (Capsule c : still.caps) {
			events.addAll(SweepUtils.sweepStartCSoverCap(stillParent, c, moving, offset));
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepCSoverCS(Object stillParent, CapsuleSequence still, MutableCapsuleSequence moving, int index, int offset) {
		
		if (!ShapeUtils.intersectAA(still.aabb, moving.aabb)) {
			return Collections.emptyList();
		}
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		for (int i = 0; i < still.caps.size(); i++) {
			Capsule c = still.caps.get(i);
			
			List<SweepEvent> capsuleEvents = SweepUtils.sweepCSoverCap(stillParent, c, moving, index, offset);
			
			for (SweepEvent e : capsuleEvents) {
				if (DMath.lessThan(e.param, 1.0)) {
					
					events.add(e);
					
				} else {
					assert DMath.equals(e.param, 1.0);
					if (i < still.caps.size()-1) {
						
						events.add(e);
						
					} else {
						events.add(e);
					}
				}
				
			}
			
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepStartCSoverC(Object stillParent, Circle still, CapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersectCC(still, moving.getStart())) {
			events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, 0, 0.0, offset));
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepCSoverC(Object stillParent, Circle still, MutableCapsuleSequence moving, int index, int offset) {
		
		if (!ShapeUtils.intersectAA(still.aabb, moving.aabb)) {
			return Collections.emptyList();
		}
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Capsule cap = moving.getCapsule(index);
		
		boolean outside;
		if (ShapeUtils.intersectCC(still, cap.ac)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = SweepUtils.sweepCircleOverCircle(still, cap, params);
		
		Arrays.sort(params);
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			
			if (DMath.greaterThan(param, 0.0)) {
				
				assert DMath.greaterThan(param, 0.0) && DMath.lessThanEquals(param, 1.0);
				if (outside) {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, index, param, offset));
				} else {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.exit(stillParent), stillParent, still, moving, index, param, offset));
				}
				outside = !outside;
				
			}
			
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepStartCSoverO(Object stillParent, OBB still, CapsuleSequence moving, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		if (ShapeUtils.intersectCO(moving.getStart(), still)) {
			events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, 0, 0.0, offset));
		}
		
		return events;
	}
	
	public static List<SweepEvent> sweepCSoverO(Object stillParent, OBB still, CapsuleSequence moving, int index, int offset) {
		
		List<SweepEvent> events = new ArrayList<SweepEvent>();
		
		Capsule cap = moving.getCapsule(index);
		
		boolean outside;
		if (ShapeUtils.intersectCO(cap.ac, still)) {
			outside = false;
		} else {
			outside = true;
		}
		
		double[] params = new double[2];
		Arrays.fill(params, Double.POSITIVE_INFINITY);
		int paramCount = 0;
		
		double cdParam = SweepUtils.sweepCircleOverLine(still.getP0P1Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP1P2Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP2P3Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		cdParam = SweepUtils.sweepCircleOverLine(still.getP3P0Line(), cap);
		if (cdParam != -1) {
			params[paramCount] = cdParam;
			paramCount++;
		}
		
		Arrays.sort(params);
		if (paramCount == 2 && DMath.equals(params[0], params[1])) {
			/*
			 * hit a seam
			 */
			paramCount = 1;
		}
		
		for (int i = 0; i < paramCount; i++) {
			double param = params[i];
			
			if (DMath.greaterThan(param, 0.0)) {
				
				assert DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0);
				if (outside) {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.enter(stillParent), stillParent, still, moving, index, param, offset));
				} else {
					events.add(new CapsuleSequenceSweepEvent(SweepEventType.exit(stillParent), stillParent, still, moving, index, param, offset));
				}
				outside = !outside;
				
			}
			
		}
		
		return events;
	}
	
	/**
	 * move circle of radius r from c to d
	 * 
	 * return Point between c and d where the circle touches <a, b>
	 * 
	 * the orientation of <a, b> matters. Going from a to b, the "outside" is considered
	 * to be on the left.
	 * 
	 */
	private static double sweepCircleOverLine(Line still, Capsule moving) {
		
		Point a = still.p0;
		Point b = still.p1;
		
		Point c = moving.a;
		Point d = moving.b;
		double r = moving.r;
		
		Point diff = new Point(b.x - a.x, b.y - a.y);
		Point norm = Point.ccw90AndNormalize(diff);
		
		/*
		 * test <a, b>
		 */
		
		double cDistance = Point.dot(c.minus(a), norm);
		double dDistance = Point.dot(d.minus(a), norm);
		double cdParam = (r - cDistance) / (dDistance - cDistance);
		if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
			Point p = Point.point(c, d, cdParam);
			
			double abParam = Point.u(a, p, b);
			if (DMath.greaterThanEquals(abParam, 0.0) && DMath.lessThanEquals(abParam, 1.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				
				return cdParam;
			}	
		}
		
		/*
		 * test a
		 */
		
		double[] params = new double[2];
		int n = sweepCircleOverCircle(new Circle(a, 0.0), moving, params);
		
		double adjustedCDParam;
//		if (n == 2) {
//			/*
//			 * figure out which to choose
//			 */
//			assert false;
//		} else if (n == 1) {
//			adjustedCDParam = params[0];
//			Point p = Point.point(c, d, adjustedCDParam);
//			double abParam = Point.u(a, p, b);
//			double pDistance = Point.dot(p.minus(a), norm);
//			if (DMath.lessThan(abParam, 0.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
//				assert DMath.equals(Point.distance(p, a, b), r);
//				return adjustedCDParam;
//			}
//		}
		for (int i = 0; i < n; i++) {
			adjustedCDParam = params[i];
			Point p = Point.point(c, d, adjustedCDParam);
			double abParam = Point.u(a, p, b);
			double pDistance = Point.dot(p.minus(a), norm);
			if (DMath.lessThan(abParam, 0.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				
				return adjustedCDParam;
			}
		}
		
		
		
		/*
		 * test b
		 */
		
		n = sweepCircleOverCircle(new Circle(b, 0.0), moving, params);
		
//		if (n == 2) {
//			/*
//			 * figure out which to choose
//			 */
//			assert false;
//		} else if (n == 1) {
//			adjustedCDParam = params[0];
//			Point p = Point.point(c, d, adjustedCDParam);
//			double abParam = Point.u(a, p, b);
//			double pDistance = Point.dot(p.minus(a), norm);
//			if (DMath.greaterThan(abParam, 1.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
//				assert DMath.equals(Point.distance(p, a, b), r);
//				return adjustedCDParam;
//			}
//		}
		
		for (int i = 0; i < n; i++) {
			adjustedCDParam = params[i];
			Point p = Point.point(c, d, adjustedCDParam);
			double abParam = Point.u(a, p, b);
			double pDistance = Point.dot(p.minus(a), norm);
			if (DMath.greaterThan(abParam, 1.0) && DMath.greaterThanEquals(pDistance, 0.0)) {
				assert DMath.equals(Point.distance(p, a, b), r);
				
				return adjustedCDParam;
			}
		}
		
		return -1;
	}
	
	/**
	 * return number of params set
	 */
	private static int sweepCircleOverCircle(Circle still, Capsule moving, double[] params) {
		
		Point p = still.center;
		double pRadius = still.radius;
		
		Point c = moving.a;
		Point d = moving.b;
		double cdRadius = moving.r;
		
		double aCoeff = ((d.x - c.x)*(d.x - c.x) + (d.y - c.y)*(d.y - c.y));
		double bCoeff = -2 * ((d.x - c.x)*(p.x - c.x) + (d.y - c.y)*(p.y - c.y));
		double cCoeff = ((p.x - c.x)*(p.x - c.x) + (p.y - c.y)*(p.y - c.y) - (cdRadius + pRadius)*(cdRadius + pRadius));
		double[] roots = new double[2];
		double discriminant = DMath.quadraticSolve(aCoeff, bCoeff, cCoeff, roots);
		if (DMath.equals(discriminant, 0.0)) {
			/*
			 * 1 event
			 */
			double cdParam = roots[0];
			if (DMath.greaterThanEquals(cdParam, 0.0) && DMath.lessThanEquals(cdParam, 1.0)) {
				Point cdPoint = Point.point(c, d, cdParam);
				assert DMath.equals(Point.distance(p, cdPoint), pRadius + cdRadius);
				
				params[0] = cdParam;
				return 1;
			}
			
		} else if (discriminant > 0) {
			/*
			 * 2 events
			 */
			double cdParam0 = roots[0];
			boolean cdParam0Set = false;
			if (DMath.greaterThanEquals(cdParam0, 0.0) && DMath.lessThanEquals(cdParam0, 1.0)) {
				Point cdPoint0 = Point.point(c, d, cdParam0);
				assert DMath.equals(Point.distance(p, cdPoint0), pRadius + cdRadius);
				
				params[0] = cdParam0;
				cdParam0Set = true;
			}
			double cdParam1 = roots[1];
			boolean cdParam1Set = false;
			if (DMath.greaterThanEquals(cdParam1, 0.0) && DMath.lessThanEquals(cdParam1, 1.0)) {
				Point cdPoint1 = Point.point(c, d, cdParam1);
				assert DMath.equals(Point.distance(p, cdPoint1), pRadius + cdRadius);
				
				if (cdParam0Set) {
					params[1] = cdParam1;
				} else {
					params[0] = cdParam1;
				}
				cdParam1Set = true;
			}
			
			if (cdParam0Set) {
				if (cdParam1Set) {
					return 2;
				} else {
					return 1;
				}
			} else {
				if (cdParam1Set) {
					return 1;
				} else {
					return 0;
				}
			}
			
		} else {
			/*
			 * 0 events
			 */
			return 0;
		}
		
		/*
		 * quadraticSolve returned events, but they were too far away to count
		 */
		return 0;
		
	}
	
	/**
	 * param when swept collides with l, or -1 if never collides
	 */
	public static double firstCollisionParam(Line l, SweptOBB swept) {
		assert swept.isAABB;
		assert l.rightAngle;
		
		assert !ShapeUtils.intersectAreaAL(swept.start.aabb, l);
		
		if (swept.dir == Point.UP) {
			if (DMath.rangesOverlapArea(l.p0.x, l.p1.x, swept.start.aabb.x, swept.start.aabb.brX)) {
				double param = (-(l.p1.y - swept.start.aabb.y)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir == Point.DOWN) {
			if (DMath.rangesOverlapArea(l.p0.x, l.p1.x, swept.start.aabb.x, swept.start.aabb.brX)) {
				double param = (l.p0.y-swept.start.aabb.brY) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir == Point.LEFT) {
			if (DMath.rangesOverlapArea(l.p0.y, l.p1.y, swept.start.aabb.y, swept.start.aabb.brY)) {
				double param = (-(l.p1.x-swept.start.aabb.x)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else {
			assert swept.dir == Point.RIGHT;
			if (DMath.rangesOverlapArea(l.p0.y, l.p1.y, swept.start.aabb.y, swept.start.aabb.brY)) {
				double param = (l.p0.x-swept.start.aabb.brX) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		}
		
	}
	
	public static double firstCollisionParam(Line l, MutableSweptOBB swept) {
		assert swept.isAABB;
		assert l.rightAngle;
		
		assert !ShapeUtils.intersectAreaAL(swept.start.aabb, l);
		
		if (swept.dir == Point.UP) {
			if (DMath.rangesOverlapArea(l.p0.x, l.p1.x, swept.start.aabb.x, (swept.start.aabb.x+swept.start.aabb.width))) {
				double param = (-(l.p1.y - swept.start.aabb.y)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir == Point.DOWN) {
			if (DMath.rangesOverlapArea(l.p0.x, l.p1.x, swept.start.aabb.x, (swept.start.aabb.x+swept.start.aabb.width))) {
				double param = (l.p0.y-(swept.start.aabb.y+swept.start.aabb.height)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir == Point.LEFT) {
			if (DMath.rangesOverlapArea(l.p0.y, l.p1.y, swept.start.aabb.y, (swept.start.aabb.y+swept.start.aabb.height))) {
				double param = (-(l.p1.x-swept.start.aabb.x)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else {
			assert swept.dir == Point.RIGHT;
			if (DMath.rangesOverlapArea(l.p0.y, l.p1.y, swept.start.aabb.y, (swept.start.aabb.y+swept.start.aabb.height))) {
				double param = (l.p0.x-(swept.start.aabb.x+swept.start.aabb.width)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		}
		
	}
	
	/**
	 * param when swept collides with o, or -1 if never collides
	 */
	public static double firstCollisionParam(MutableOBB o, SweptOBB swept) {
		assert swept.isAABB;
		assert o.rightAngle;
		assert !ShapeUtils.intersectAreaAA(swept.start.aabb, o.aabb);
		
		if (swept.dir.equals(Point.UP)) {
			if (DMath.rangesOverlapArea(o.p0.x, o.p2.x, swept.start.aabb.x, swept.start.aabb.brX)) {
				double param = (-(o.p2.y - swept.start.aabb.y)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir.equals(Point.DOWN)) {
			if (DMath.rangesOverlapArea(o.p0.x, o.p2.x, swept.start.aabb.x, swept.start.aabb.brX)) {
				double param = (o.p0.y-swept.start.aabb.brY) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir.equals(Point.LEFT)) {
			if (DMath.rangesOverlapArea(o.p0.y, o.p2.y, swept.start.aabb.y, swept.start.aabb.brY)) {
				double param = (-(o.p2.x-swept.start.aabb.x)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else {
			assert swept.dir.equals(Point.RIGHT);
			if (DMath.rangesOverlapArea(o.p0.y, o.p2.y, swept.start.aabb.y, swept.start.aabb.brY)) {
				double param = (o.p0.x-swept.start.aabb.brX) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		}

	}

	public static double firstCollisionParam(MutableOBB o, MutableSweptOBB swept) {
		assert swept.isAABB;
		assert o.rightAngle;
		assert !ShapeUtils.intersectAreaAA(swept.start.aabb, o.aabb);
		
		if (swept.dir.equals(Point.UP)) {
			if (DMath.rangesOverlapArea(o.p0.x, o.p2.x, swept.start.aabb.x, (swept.start.aabb.x+swept.start.aabb.width))) {
				double param = (-(o.p2.y - swept.start.aabb.y)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir.equals(Point.DOWN)) {
			if (DMath.rangesOverlapArea(o.p0.x, o.p2.x, swept.start.aabb.x, (swept.start.aabb.x+swept.start.aabb.width))) {
				double param = (o.p0.y-(swept.start.aabb.y+swept.start.aabb.height)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else if (swept.dir.equals(Point.LEFT)) {
			if (DMath.rangesOverlapArea(o.p0.y, o.p2.y, swept.start.aabb.y, (swept.start.aabb.y+swept.start.aabb.height))) {
				double param = (-(o.p2.x-swept.start.aabb.x)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		} else {
			assert swept.dir.equals(Point.RIGHT);
			if (DMath.rangesOverlapArea(o.p0.y, o.p2.y, swept.start.aabb.y, (swept.start.aabb.y+swept.start.aabb.height))) {
				double param = (o.p0.x-(swept.start.aabb.x+swept.start.aabb.width)) / swept.dist;
				return (DMath.greaterThanEquals(param, 0.0) && DMath.lessThanEquals(param, 1.0)) ? param : -1;
			} else {
				return -1;
			}
		}

	}
	
}
