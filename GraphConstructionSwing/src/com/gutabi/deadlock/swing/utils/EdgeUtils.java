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
	public static Vertex split(Edge e, int index, Rat param) {
		assert param.isGreaterThanOrEquals(Rat.ZERO);
		assert param.isLessThanOrEquals(Rat.ONE);
		
		Point c = e.getPoint(index);
		Point d = e.getPoint(index+1);
		
		Point ppp = Point.point(c, d, param);
		Point pInt = new Point((int)Math.round(ppp.x.getVal()), (int)Math.round(ppp.y.getVal()));
		
		Vertex v = MODEL.createVertex(pInt);
		
		Vertex eStart = e.getStart();
		Vertex eEnd = e.getEnd();
		
		if (eStart == null && eEnd == null) {
			// stand-alone loop
			
			Edge f = MODEL.createEdge();
			
			f.addPoint(pInt);
			for (int i = index+1; i < e.getPointsSize(); i++) {
				f.addPoint(e.getPoint(i));
			}
			for (int i = 0; i <= index; i++) {
				if (i < index) {
					f.addPoint(e.getPoint(i));
				} else if (!pInt.equals(c)) {
					f.addPoint(e.getPoint(i));
				}
			}
			
			f.checkColinearity();
			
			f.setStart(v);
			f.setEnd(v);
			
			v.add(f);
			v.add(f);
			
			MODEL.removeEdge(e);
			
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
			
			f1.checkColinearity();
			f2.checkColinearity();
			
			f1.setStart(eStart);
			f1.setEnd(v);
			
			f2.setStart(v);
			f2.setEnd(eEnd);
			
			eStart.remove(e);
			eStart.add(f1);
			
			eEnd.remove(e);
			eEnd.add(f2);
			
			v.add(f1);
			v.add(f2);
			
			MODEL.removeEdge(e);
			
		}
		
		return v;
	}
	
	/**
	 * merge two edges with common vertex (possibly the same edge)
	 * no colinear points in returned edge
	 */
	public static Edge merge(Edge e1, Edge e2) {
		//logger.debug("merge " + e1 + " " + e2);
		
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
				if (Point.colinear(e1.getPoint(n-1), e1.getPoint(0), e1.getPoint(1))) {
					e1.removePoint(0);
				} else {
					assert false;
				}
				
				MODEL.removeVertex(e1Start);
				
			} else {
				// start/end vertex has other edges
				// nothing to do
				assert false;
			}
			
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
			} else {
				// start/end vertex has other edges
				
				newEdge.setStart(e1Start);
				newEdge.setEnd(e1Start);
				
				newEdge.check();
				
				e1Start.remove(e1);
				e1Start.remove(e2);
				e1Start.add(newEdge);
				e1Start.add(newEdge);
				
				MODEL.removeVertex(e1End);
				MODEL.removeEdge(e1);
				MODEL.removeEdge(e2);
				
			}
			
		} else if (e1Start == e2Start) {
			
			for (int i = e1.getPointsSize()-1; i > 0; i--) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(0) == e2.getPoint(0);
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(1), e1.getPoint(0), e2.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(0));
			}
			for (int i = 1; i < e2.getPointsSize(); i++) {
				newEdge.addPoint(e2.getPoint(i));
			}
			
			newEdge.setStart(e1End);
			newEdge.setEnd(e2End);
			
			newEdge.check();
			
			e1End.remove(e1);
			e1End.add(newEdge);
			
			e2End.remove(e2);
			e2End.add(newEdge);
			
			MODEL.removeVertex(e1Start);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
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
			
			newEdge.check();
			
			e1End.remove(e1);
			e1End.add(newEdge);
			
			e2Start.remove(e2);
			e2Start.add(newEdge);
			
			MODEL.removeVertex(e1Start);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
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
			
			newEdge.check();
			
			e1Start.remove(e1);
			e1Start.add(newEdge);
			
			e2End.remove(e2);
			e2End.add(newEdge);
			
			MODEL.removeVertex(e1End);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
		} else if (e1End == e2End) {
			
			for (int i = 0; i < e1.getPointsSize()-1; i++) {
				newEdge.addPoint(e1.getPoint(i));
			}
			assert e1.getPoint(e1.getPointsSize()-1) == e2.getPoint(e2.getPointsSize()-1);
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(e1.getPointsSize()-2), e1.getPoint(e1.getPointsSize()-1), e2.getPoint(e2.getPointsSize()-2))) {
				newEdge.addPoint(e1.getPoint(e1.getPointsSize()-1));
			}
			for (int i = e2.getPointsSize()-2; i >= 0; i--) {
				newEdge.addPoint(e2.getPoint(i));
			}
			
			newEdge.setStart(e1Start);
			newEdge.setEnd(e2Start);
			
			newEdge.check();
			
			e1Start.remove(e1);
			e1Start.add(newEdge);
			
			e2Start.remove(e2);
			e2Start.add(newEdge);
			
			MODEL.removeVertex(e1End);
			MODEL.removeEdge(e1);
			MODEL.removeEdge(e2);
			
		} else {
			assert false;
		}
		
		return newEdge;
	}
	
}
