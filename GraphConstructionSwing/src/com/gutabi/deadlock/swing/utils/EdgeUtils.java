package com.gutabi.deadlock.swing.utils;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;

import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.DeadlockModel.EdgeInfo;
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
	public static Vertex split(EdgeInfo info) {
		Edge e = info.edge;
		int index = info.index;
		double param = info.param;
		logger.debug("split e: " + e + " index: " + index + " param: " + param);
		assert param >= 0.0;
		assert param < 1.0;
		
		/*
		 * we assert thar param < 1.0, but after adjusting, we may be at d
		 */
		
		Point c = e.getPoint(index);
		Point d = e.getPoint(index+1);
		
		DPoint p = Point.point(c, d, param);
		Point pInt = new Point((int)Math.round(p.x), (int)Math.round(p.y));
		
		/*
		 * v may already exist if we are splitting at p and p is different than pInt and v already exists at pInt
		 */
		Vertex v = MODEL.tryFindVertex(pInt);
		if (v == null) {
			v = MODEL.createVertex(pInt);
		}
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
			for (int i = 1; i < index; i++) {
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
			
			/*
			 * a loop has just been split, so it is in an inconsistent state, do not check here
			 * hopefully, another edge will be added soon
			 */
			//v.check();
			return v;
			
		} else {
			
			/*
			 * f1
			 */
			
			Edge f1 = null;
			for (Edge ee : eStart.getEdges()) {
				if (((ee.getStart() == eStart && ee.getEnd() == v) || (ee.getStart() == v && ee.getEnd() == eStart)) && ee.getPointsSize() == 2) {
					/*
					 * both a and b are already both vertices and connected, so just use this
					 */
					f1 = ee;
					break;
				}
			}
			if (f1 == null) {
				f1 = MODEL.createEdge();
				for (int i = 0; i < index; i++) {
					f1.addPoint(e.getPoint(i));
				}
				try {
					if (!(index > 0) || !Point.colinear(e.getPoint(index-1), c, pInt)) {
						f1.addPoint(c);
					}
				} catch (ColinearException ex) {
					
					assert MODEL.tryFindVertex(c) == null;
					
					Vertex cV = MODEL.createVertex(c);
					Edge f3 = MODEL.createEdge();
					f3.addPoint(c);
					f3.addPoint(pInt);
					f3.setStart(cV);
					f3.setEnd(v);
					f3.check();
					
					cV.add(f3);
					v.add(f3);
					
				}
				f1.addPoint(pInt);
				
				f1.setStart(eStart);
				f1.setEnd(v);
				
				f1.check();
				
				eStart.add(f1);
				v.add(f1);
			}
			
			/*
			 * f2
			 */
			
			Edge f2 = null;
			for (Edge ee : eStart.getEdges()) {
				if (((ee.getStart() == v && ee.getEnd() == eEnd) || (ee.getStart() == eEnd && ee.getEnd() == v)) && ee.getPointsSize() == 2) {
					/*
					 * both a and b are already both vertices and connected, so just use this
					 */
					f2 = ee;
					break;
				}
			}
			if (f2 == null) {
				f2 = MODEL.createEdge();
				f2.addPoint(pInt);
				try {
					if (!(index+1 < e.getPointsSize()-1) || !Point.colinear(pInt, d, e.getPoint(index+2))) {
						f2.addPoint(d);
					}
				} catch (ColinearException ex) {
					
					assert MODEL.tryFindVertex(d) == null;
					
					Vertex dV = MODEL.createVertex(d);
					Edge f3 = MODEL.createEdge();
					f3.addPoint(d);
					f3.addPoint(pInt);
					f3.setStart(dV);
					f3.setEnd(v);
					f3.check();
					
					dV.add(f3);
					v.add(f3);
					
				}
				for (int i = index+2; i < e.getPointsSize(); i++) {
					f2.addPoint(e.getPoint(i));
				}
				
				f2.setStart(v);
				f2.setEnd(eEnd);
				
				f2.check();
				
				v.add(f2);
				eEnd.add(f2);
			}
			
			eStart.remove(e);
			List<Edge> eStartEdges = eStart.getEdges();
			if (eStartEdges.size() == 2) {
				merge(eStartEdges.get(0), eStartEdges.get(1));
			}
			
			eEnd.remove(e);
			List<Edge> eEndEdges = eEnd.getEdges();
			if (eEndEdges.size() == 2) {
				merge(eEndEdges.get(0), eEndEdges.get(1));
			}
			
			MODEL.removeEdge(e);
			
			/*
			 * splitting into 2 edges, so in an inconsistent state
			 * don't check here
			 */
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
		
		Vertex e1Start = e1.getStart();
		Vertex e1End = e1.getEnd();
		
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
			return e1;
		}
		
		Edge newEdge = MODEL.createEdge();
		
		if (e1Start == e2End && e1End == e2Start) {
			// forming a loop
			
			assert e1.getPoint(e1.getPointsSize()-1).equals(e2.getPoint(0));
			assert e2.getPoint(e2.getPointsSize()-1).equals(e1.getPoint(0));
			
			// only add if not colinear
			boolean firstAdded = false;
			if (!Point.colinear(e2.getPoint(e2.getPointsSize()-2), e1.getPoint(0), e1.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(0));
				firstAdded = true;
			}
			for (int i = 1; i < e1.getPointsSize()-1; i++) {
				newEdge.addPoint(e1.getPoint(i));
			}
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(e1.getPointsSize()-2), e1.getPoint(e1.getPointsSize()-1), e2.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(e1.getPointsSize()-1));
			}
			for (int i = 1; i < e2.getPointsSize()-1; i++) {
				newEdge.addPoint(e2.getPoint(i));
			}
			// only add if not colinear
			if (firstAdded) {
				newEdge.addPoint(e2.getPoint(e2.getPointsSize()-1));
			} else {
				// first point of e1 was skipped, but it is still a loop, so first and last points of newEdge need to be the same 
				newEdge.addPoint(newEdge.getPoint(0));
			}
						
			int e1StartEdgeCount = e1Start.getEdges().size();
			
			if (e1StartEdgeCount == 1) {
				// stand-alone loop	
				throw new AssertionError();
			}
			
			if (e1StartEdgeCount == 2) {
				// stand-alone loop
				assert e1End.getEdges().size() == 2;
				
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
			
		} else if (e1Start == e2Start && e1End == e2End) {
			// forming a loop
			
			assert e1.getPoint(e1.getPointsSize()-1).equals(e2.getPoint(e2.getPointsSize()-1));
			assert e2.getPoint(0).equals(e1.getPoint(0));
			
			// only add if not colinear
			boolean firstAdded = false;
			if (!Point.colinear(e2.getPoint(1), e1.getPoint(0), e1.getPoint(1))) {
				newEdge.addPoint(e1.getPoint(0));
				firstAdded = true;
			}
			for (int i = 1; i < e1.getPointsSize()-1; i++) {
				newEdge.addPoint(e1.getPoint(i));
			}
			// only add if not colinear
			if (!Point.colinear(e1.getPoint(e1.getPointsSize()-2), e1.getPoint(e1.getPointsSize()-1), e2.getPoint(e2.getPointsSize()-2))) {
				newEdge.addPoint(e1.getPoint(e1.getPointsSize()-1));
			}
			for (int i = e2.getPointsSize()-2; i >= 1; i--) {
				newEdge.addPoint(e2.getPoint(i));
			}
			// only add if not colinear
			if (firstAdded) {
				newEdge.addPoint(e2.getPoint(0));
			} else {
				// first point of e1 was skipped, but it is still a loop, so first and last points of newEdge need to be the same 
				newEdge.addPoint(newEdge.getPoint(0));
			}
						
			int e1StartEdgeCount = e1Start.getEdges().size();
			
			if (e1StartEdgeCount == 1) {
				// stand-alone loop	
				throw new AssertionError();
			}
			
			if (e1StartEdgeCount == 2) {
				// stand-alone loop
				assert e1End.getEdges().size() == 2;
				
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
	
	
	/**
	 * remove segment <i, i+1> from edge e
	 * 
	 */
	public static void removeSegment(Edge e, int i) {
		assert i >= 0;
		assert i < e.getPointsSize();
		
		if (i == 0) {
			create 1 new edge
			check connectivity of vertex
		} else if (i == e.getPointsSize()-1) {
			create 1 new edge
			check connectivity of vertex
		} else {
			create 2 new edges without worrying about vertices
		}
		
	}
	
}
