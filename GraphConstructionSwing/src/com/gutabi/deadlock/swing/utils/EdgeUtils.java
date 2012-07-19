package com.gutabi.deadlock.swing.utils;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.model.Vertex;

public final class EdgeUtils {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	private EdgeUtils() {
		
	}
	
	/**
	 * split an edge at point with index i and param p between indices i and i+1
	 * takes care of adjusting to integer coordinates
	 * Edge e will have been removed
	 * return Vertex at split point
	 */
	public static Vertex split(Edge e, int index, double param) {
		logger.debug("split e: " + e + " index: " + index + " param: " + param);
		assert param >= 0.0;
		assert param < 1.0;
		
		/*
		 * we assert thar param < 1.0, but after adjusting, we may be at d
		 */
		
		Point c = e.getPoint(index);
		Point d = e.getPoint(index+1);
		
		DPoint ppp = Point.point(c, d, param);
		Point pInt = null;
//		if (param < 0.5) {
//			pInt = new Point((c.x > ppp.x) ? (int)Math.ceil(ppp.x) : (int)Math.floor(ppp.x), (c.y > ppp.y) ? (int)Math.ceil(ppp.y) : (int)Math.floor(ppp.y));
//		} else if (param > 0.5) {
//			pInt = new Point((d.x > ppp.x) ? (int)Math.ceil(ppp.x) : (int)Math.floor(ppp.x), (d.y > ppp.y) ? (int)Math.ceil(ppp.y) : (int)Math.floor(ppp.y));
//		} else {
			// make a choice
		pInt = new Point((int)Math.round(ppp.x), (int)Math.round(ppp.y));
		assert MODEL.tryFindVertex(pInt) == null;
		//}
		
		//assert !pInt.equals(c);
		//assert !pInt.equals(d);
		
		Vertex v = MODEL.createVertex(pInt);
		assert v.getPoint().equals(pInt);
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			Edge f = MODEL.createEdge();
			
			f.addPoint(pInt);
			if (!(index+1 < e.getPointsSize()-1) || !Point.colinear(pInt, d, e.getPoint(index+2))) {
				f.addPoint(d);
			}
			for (int i = index+2; i < e.getPointsSize(); i++) {
				f.addPoint(e.getPoint(i));
			}
			for (int i = 0; i < index; i++) {
				f.addPoint(e.getPoint(i)); 
			}
			if (!(index > 0) || !Point.colinear(e.getPoint(index-1), c, pInt)) {
				f.addPoint(c);
			}
			f.addPoint(pInt);
			
			f.setStart(v);
			f.setEnd(v);
			
			f.check();
			
			v.add(f);
			v.add(f);
			
			MODEL.removeEdge(e);
			
			v.check();
			return v;
			
		} else {
			
			Edge f1 = MODEL.createEdge();
			Edge f2 = MODEL.createEdge();
			
			for (int i = 0; i < index; i++) {
				f1.addPoint(e.getPoint(i));
			}
			if (!(index > 0) || !Point.colinear(e.getPoint(index-1), c, pInt)) {
				f1.addPoint(c);
			}
			f1.addPoint(pInt);
			
			f2.addPoint(pInt);
			if (!(index+1 < e.getPointsSize()-1) || !Point.colinear(pInt, d, e.getPoint(index+2))) {
				f2.addPoint(d);
			}
			for (int i = index+2; i < e.getPointsSize(); i++) {
				f2.addPoint(e.getPoint(i));
			}
			
			f1.setStart(eStart);
			f1.setEnd(v);
			
			f2.setStart(v);
			f2.setEnd(eEnd);
			
			f1.check();
			f2.check();
			
			eStart.remove(e);
			eStart.add(f1);
			
			eEnd.remove(e);
			eEnd.add(f2);
			
			v.add(f1);
			v.add(f2);
			
			MODEL.removeEdge(e);
			
			//v.check();
			return v;
		}
	}
	
	/**
	 * merge two edges with common vertex (possibly the same edge)
	 * no colinear points in returned edge
	 */
	public static Edge merge(Edge e1, Edge e2) {
		logger.debug("merge " + e1 + " " + e2);
		
		//List<Point> e1Points = e1.getPoints();
		Vertex e1Start = e1.getStart();
		Vertex e1End = e1.getEnd();
		
		//List<Point> e2Points = e2.getPoints();
		Vertex e2Start = e2.getStart();
		Vertex e2End = e2.getEnd();
		
		if (e1 == e2) {
			assert e1Start == e1End;
			// in the middle of merging a loop
			
			int e1StartEdgeCount = e1Start.getEdges().size();
			
			if (e1StartEdgeCount == 1) {
				assert false;
			} else if (e1StartEdgeCount == 2) {
				// stand-alone loop
				
				e1.setStart(null);
				e1.setEnd(null);
				
				int n = e1.getPointsSize();
				assert e1.getPoint(0) == e1.getPoint(n-1);
				
				// remove colinear
				if (Point.colinear(e1.getPoint(n-2), /*same as n-1*/e1.getPoint(0), e1.getPoint(1))) {
					// remove old end points and add new ones, making sure that first and last are the same 
					e1.removePoint(0);
					e1.removePoint(e1.getPointsSize()-1);
					e1.addPoint(e1.getPoint(0));
				}
				
				MODEL.removeVertex(e1Start);
				
			} else {
				// start/end vertex has other edges
				// nothing to do
				assert false;
			}
			
			e1.check();
			//logger.debug("return " + e1);
			return e1;
		}
		
		Edge newEdge = MODEL.createEdge();
		//List<Point> newEdgePoints = newEdge.getPoints();
		
		if (e1Start == e2End && e1End == e2Start) {
			// forming a loop
			
			for (int i = 0; i < e1.getPointsSize()-1; i++) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(e1.getPointsSize()-1).equals(e2.getPoint(0));
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(e1.getPointsSize()-2), e1.getPoint(e1.getPointsSize()-1), e2.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(e1.getPointsSize()-1));
			}
			for (int i = 1; i < e2.getPointsSize(); i++) {
				newEdge.addPoint(e2.getPoint(i));
			}
			assert e2.getPoint(e2.getPointsSize()-1).equals(e1.getPoint(0));
			
			int e1StartEdgeCount = e1Start.getEdges().size();
			
			if (e1StartEdgeCount == 1) {
				// stand-alone loop
				
				newEdge.setStart(null);
				newEdge.setEnd(null);
				
				newEdge.check();
				
				MODEL.removeVertex(e1Start);
				MODEL.removeVertex(e1End);
				MODEL.removeEdge(e1);
				MODEL.removeEdge(e2);
				
				newEdge.check();
				return newEdge;
			} else {
				// start/end vertex has other edges
				
				newEdge.setStart(e1Start);
				newEdge.setEnd(e1Start);
				
				e1Start.remove(e1);
				e1Start.remove(e2);
				e1Start.add(newEdge);
				e1Start.add(newEdge);
				
				MODEL.removeVertex(e1End);
				MODEL.removeEdge(e1);
				MODEL.removeEdge(e2);
				
				newEdge.check();
				return newEdge;
			}
			
		} else if (e1Start == e2Start) {
			
			for (int i = e1.getPointsSize()-1; i > 0; i--) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(0));
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(0));
			}
			for (int i = 1; i < e2.getPointsSize(); i++) {
				newEdge.addPoint(e2.getPoint(i));
			}
			
			newEdge.setStart(e1End);
			newEdge.setEnd(e2End);
			
			//newEdge.check();
			
			e1End.remove(e1);
			e1End.add(newEdge);
			
			e2End.remove(e2);
			e2End.add(newEdge);
			
			MODEL.removeVertex(e1Start);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
			newEdge.check();
			return newEdge;
		} else if (e1Start == e2End) {
				
			for (int i = e1.getPointsSize()-1; i > 0; i--) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(0).equals(e2.getPoint(e2.getPointsSize()-1));
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(e2.getPointsSize()-2))) {
				newEdge.addPoint(e1.getPoint(0));
			}
			for (int i = e2.getPointsSize()-2; i >= 0; i--) {
				newEdge.addPoint(e2.getPoint(i));
			}
			
			newEdge.setStart(e1End);
			newEdge.setEnd(e2Start);
			
			//newEdge.check();
			
			e1End.remove(e1);
			e1End.add(newEdge);
			
			e2Start.remove(e2);
			e2Start.add(newEdge);
			
			MODEL.removeVertex(e1Start);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
			newEdge.check();
			return newEdge;
		} else if (e1End == e2Start) {
			
			for (int i = 0; i < e1.getPointsSize()-1; i++) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(e1.getPointsSize()-1).equals(e2.getPoint(0));
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(e1.getPointsSize()-2), e1.getPoint(e1.getPointsSize()-1), e2.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(e1.getPointsSize()-1));
			}
			for (int i = 1; i < e2.getPointsSize(); i++) {
				newEdge.addPoint(e2.getPoint(i));
			}
			
			newEdge.setStart(e1Start);
			newEdge.setEnd(e2End);
			
			//newEdge.check();
			
			e1Start.remove(e1);
			e1Start.add(newEdge);
			
			e2End.remove(e2);
			e2End.add(newEdge);
			
			MODEL.removeVertex(e1End);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
			newEdge.check();
			return newEdge;
		} else if (e1End == e2End) {
			
			for (int i = 0; i < e1.getPointsSize()-1; i++) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(e1.getPointsSize()-1).equals(e2.getPoint(e2.getPointsSize()-1));
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(e1.getPointsSize()-2), e1.getPoint(e1.getPointsSize()-1), e2.getPoint(e2.getPointsSize()-2))) {
				newEdge.addPoint(e1.getPoint(e1.getPointsSize()-1));
			}
			for (int i = e2.getPointsSize()-2; i >= 0; i--) {
				newEdge.addPoint(e2.getPoint(i));
			}
			
			newEdge.setStart(e1Start);
			newEdge.setEnd(e2Start);
			
			//newEdge.check();
			
			e1Start.remove(e1);
			e1Start.add(newEdge);
			
			e2Start.remove(e2);
			e2Start.add(newEdge);
			
			MODEL.removeVertex(e1End);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
			newEdge.check();
			return newEdge;
		} else {
			throw new AssertionError();
		}
	}
	
}
