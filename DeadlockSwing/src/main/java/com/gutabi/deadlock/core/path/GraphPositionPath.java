package com.gutabi.deadlock.core.path;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.VertexPosition;

/**
 * Purely spatial 
 */
@SuppressWarnings("static-access")
public class GraphPositionPath {
	
	List<GraphPosition> poss;
	
	public final int size;
	public final GraphPosition start;
	public final GraphPosition end;
	
	public final double[] cumulativeDistancesFromStart;
	public final double totalLength;
	
	private final int hash;
	
	static Logger logger = Logger.getLogger(GraphPositionPath.class);
	
	public GraphPositionPath(List<GraphPosition> poss) {
		
		this.poss = poss;
		
		int h = 17;
		h = 37 * h + poss.hashCode();
		hash = h;
		
		size = poss.size();
		
		this.start = poss.get(0);
		this.end = poss.get(poss.size()-1);
		
		cumulativeDistancesFromStart = new double[poss.size()];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < poss.size(); i++) {
			if (i == 0) {
				cumulativeDistancesFromStart[i] = 0;
			} else {
				Point a  = poss.get(i-1).p;
				Point b = poss.get(i).p;
				length = Point.distance(a, b);
				l += length;
				cumulativeDistancesFromStart[i] = cumulativeDistancesFromStart[i-1] + length;
			}
		}
		
		totalLength = l;
		
		assert check();
	}
	
	public static GraphPositionPath createShortestPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (MODEL.world.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
				return null;
			}
		}
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
		
		poss.add(new VertexPosition(origPoss.get(0)));
		for (int i = 0; i < origPoss.size()-1; i++) {
			VertexPosition a = new VertexPosition(origPoss.get(i));
			VertexPosition b = new VertexPosition(origPoss.get(i+1));
			calculateShortestPath(poss, a, b);
		}
		
		return new GraphPositionPath(poss);
	}
	
	public static GraphPositionPath createRandomPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (MODEL.world.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
				return null;
			}
		}
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
		
		poss.add(new VertexPosition(origPoss.get(0)));
		for (int i = 0; i < origPoss.size()-1; i++) {
			VertexPosition a = new VertexPosition(origPoss.get(i));
			VertexPosition b = new VertexPosition(origPoss.get(i+1));
			calculateRandomPath(poss, a, b);
		}
		
		return new GraphPositionPath(poss);
	}
	
	
	
	
	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		String s = "";
		for (GraphPosition g : poss) {
			if (g instanceof VertexPosition) {
				s = s + g + " ";
			}
		}
		return s;
	}
	
	
	public GraphPosition get(int index) {
		return poss.get(index);
	}
	
	
	
	public GraphPositionPathPosition hitTest(GraphPosition pos) {
		
		assert pos != null;
		
		if (pos.isBound()) {
			for (int i = 0; i < poss.size(); i++) {
				GraphPosition test = poss.get(i);
				if (pos.equals(test)) {
					return new GraphPositionPathPosition(this, i, 0.0);
				}
			}
			return null;
		} else {
			EdgePosition ep = (EdgePosition)pos;
			GraphPosition ep1 = ep.nextBoundBackward();
			GraphPosition ep2 = ep.nextBoundForward();
			for (int i = 0; i < poss.size()-1; i++) {
				GraphPosition test1 = poss.get(i);
				GraphPosition test2 = poss.get(i+1);
				if (ep1.equals(test1) && ep2.equals(test2)) {
					return new GraphPositionPathPosition(this, i, ep.param);
				} else if (ep1.equals(test2) && ep2.equals(test1)) {
					return new GraphPositionPathPosition(this, i, 1-ep.param);
				}
			}
			return null;
		}
		
	}
	
	/**
	 * finds closest position in a graphpositionpath to p
	 * 
	 * takes loops in the gpp into account, uses pos as a reference
	 * where there would be ambiguities about which graphpositions to use (because of looping), use the one closest to pos in the gpp
	 * 
	 * also, make sure it is at least pos, no back tracking in allowed
	 * 
	 * and since we are only allowing positions further in the path from pos, then at the very least, we will always return the end of the path
	 * never need to return null or anything
	 * 
	 */
	public GraphPositionPathPosition findClosestGraphPositionPathPosition(Point p, GraphPositionPathPosition min, GraphPositionPathPosition max) {
		
		/*
		 * end of path
		 */
		GraphPositionPathPosition closest = max;
		double closestDistance = Point.distance(p, closest.gpos.p);
		
		for (int i = 0; i < poss.size()-1; i++) {
			GraphPosition a = poss.get(i);
			GraphPosition b = poss.get(i+1);
			boolean sameDirAsEdge = GraphPosition.direction(a, b) == 0;
			
			double u;
			if (sameDirAsEdge) {
				u = Point.u(a.floor().p, p, b.ceiling().p);
			} else {
				u = Point.u(a.ceiling().p, p, b.floor().p);
			}
			if (i == 0) {
				assert b.isBound();
				if (a instanceof EdgePosition && u < ((EdgePosition)a).param) {
					u = ((EdgePosition)a).param;
				} else if (u < 0.0){
					u = 0.0;
				}
				if (u > 1.0) {
					u = 1.0;
				}
				if (DMath.equals(u, 1.0)) {
					/*
					 * the next iteration will pick this up
					 */
					continue;
				}
				Point pOnPath;
				if (sameDirAsEdge) {
					pOnPath = Point.point(a.floor().p, b.ceiling().p, u);
				} else {
					pOnPath = Point.point(a.ceiling().p, b.floor().p, u);
				}
				double dist = Point.distance(p, pOnPath);
				if (dist < closestDistance && DMath.greaterThanEquals((i+u), min.combo) && DMath.lessThanEquals((i+u), max.combo)) {
					closest = new GraphPositionPathPosition(this, i, u);
					closestDistance = dist;
				}
				
				
			} else if (i == poss.size()-2) {
				assert a.isBound();
				if (b instanceof EdgePosition && u > ((EdgePosition)b).param) {
					u = ((EdgePosition)b).param;
				} else if (u > 1.0) {
					u = 1.0;
				}
				if (u < 0.0) {
					u = 0.0;
				}
				if (DMath.equals(u, 1.0)) {
					/*
					 * but if last iteration, then do test now
					 */
					Point pOnPath = b.p;
					double dist = Point.distance(p, pOnPath);
					if (dist < closestDistance && DMath.greaterThanEquals((i+u), min.combo) && DMath.lessThanEquals((i+u), max.combo)) {
						closest = new GraphPositionPathPosition(this, i+1, 0.0);
						closestDistance = dist;
					}
				} else {
					Point pOnPath;
					if (sameDirAsEdge) {
						pOnPath = Point.point(a.floor().p, b.ceiling().p, u);
					} else {
						pOnPath = Point.point(a.ceiling().p, b.floor().p, u);
					}
					double dist = Point.distance(p, pOnPath);
					if (dist < closestDistance && DMath.greaterThanEquals((i+u), min.combo) && DMath.lessThanEquals((i+u), max.combo)) {
						closest = new GraphPositionPathPosition(this, i, u);
						closestDistance = dist;
					}
				}
				
				
			} else {
				assert a.isBound();
				assert b.isBound();
				
				if (u < 0.0) {
					u = 0.0;
				}
				if (u > 1.0) {
					u = 1.0;
				}
				
				if (DMath.equals(u, 1.0)) {
					/*
					 * the next iteration will pick this up
					 */
					continue;
				}
				Point pOnPath;
				if (sameDirAsEdge) {
					pOnPath = Point.point(a.floor().p, b.ceiling().p, u);
				} else {
					pOnPath = Point.point(a.ceiling().p, b.floor().p, u);
				}
				double dist = Point.distance(p, pOnPath);
				if (dist < closestDistance && DMath.greaterThanEquals((i+u), min.combo) && DMath.lessThanEquals((i+u), max.combo)) {
					closest = new GraphPositionPathPosition(this, i, u);
					closestDistance = dist;
				}
				
			}
		}
		
		return closest;
	}
	
	/**
	 * 
	 */
	public GraphPosition getGraphPosition(int pathIndex, double pathParam) {
		
		GraphPosition p1 = poss.get(pathIndex);
		
		if (DMath.equals(pathParam, 0.0)) {
			if (p1 instanceof EdgePosition) {
				assert DMath.equals(((EdgePosition)p1).param, pathParam);
			}
			return p1;
		}
		
		GraphPosition p2 = poss.get(pathIndex+1);
		
		int graphIndex;
		double graphParam;
		
		/*
		 * p1 and p2 might not be bounds, so have to convert from pathParam to graphParam
		 */
		
		if (p1 instanceof EdgePosition) {
			EdgePosition pp1 = (EdgePosition)p1;
			
			if (p2 instanceof EdgePosition) {
				EdgePosition pp2 = (EdgePosition)p2;
				
				if (pp1.index < pp2.index || (pp1.index == pp2.index && DMath.lessThan(pp1.param, pp2.param))) {
					// same direction as edge
					
					graphIndex = pp1.index;
					graphParam = pathParam;
					
				} else {
					
					graphIndex = pp2.index;
					graphParam = 1-pathParam;
					
				}
				
			} else {
				VertexPosition pp2 = (VertexPosition)p2;
				
				if (pp2.v == pp1.e.end) {
					// same direction as edge
					
					graphIndex = pp1.index;
					graphParam = pathParam;
					
				} else {
					
					graphIndex = 0;
					graphParam = 1-pathParam;
					
				}
				
			}
			
			EdgePosition newE = new EdgePosition(((EdgePosition)p1).e, graphIndex, graphParam);
			
			return newE;
			
		} else {
			VertexPosition pp1 = (VertexPosition)p1;
			
			if (p2 instanceof EdgePosition) {
				EdgePosition pp2 = (EdgePosition)p2;
				
				EdgePosition p2e = (EdgePosition)p2;
				
				if (pp1.v == pp2.e.start) {
					// same direction as edge
					
					graphIndex = 0;
					graphParam = pathParam;
					
				} else {
					
					graphIndex = pp2.index;
					graphParam = 1-pathParam;
					
				}
				
				EdgePosition newE = new EdgePosition(p2e.e, graphIndex, graphParam);
				
				return newE;
				
			} else {
				
				VertexPosition pp2 = (VertexPosition)p2;
				
				List<Edge> eds = Vertex.commonEdges(pp1.v, pp2.v);
				
				Edge e = null;
				
				for (Edge ce : eds) {
					if (ce.size() == 2) {
						e = ce;
						break;
					}
				}
				assert e != null;
				
				graphParam = (pp1.v == e.start) ? pathParam : 1-pathParam;
				
				Vertex p1v = ((VertexPosition)p1).v;
				
				if (p1v == e.start) {
					// same direction as edge
					
					graphIndex = 0;
					graphParam = pathParam;
					
				} else {
					
					graphIndex = 0;
					graphParam = 1-pathParam;
					
				}
				
				EdgePosition newE = new EdgePosition(e, graphIndex, graphParam);
				
				return newE;
			}
		}
	}
	
	/**
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private static void calculateShortestPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
		if (a.equals(b)) {
			
			assert false;
			
		} else {
			List<Edge> eds = Vertex.commonEdges(a.v, b.v);
			
			if (eds.isEmpty()) {
				
				Vertex choice = MODEL.world.shortestPathChoice(a.v, b.v);
				
				if (choice == null) {
					
					throw new IllegalArgumentException();
					
				} else {
					
					calculateShortestPath(acc, a, new VertexPosition(choice));
					calculateShortestPath(acc, new VertexPosition(choice), b);
					
				}
				
			} else {
				
				Edge shortest = null;
				for (Edge e : eds) {
					if (shortest == null || DMath.lessThan(e.getTotalLength(), shortest.getTotalLength())) {
						shortest = e;
					}
				}
				
				fillin(acc, a, b, shortest);
				
			}
		}
		
	}
	
	/**
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private static void calculateRandomPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
		if (a.equals(b)) {
			return;
		}
		
		Edge prev = null;
		if (acc.size() >= 2) {
			prev = ((EdgePosition)acc.get(acc.size()-2)).e;
		}
		
		Vertex choice = MODEL.world.randomPathChoice(prev, a.v, b.v);
		VertexPosition choicePos = new VertexPosition(choice);
		
		List<Edge> eds = new ArrayList<Edge>(Vertex.commonEdges(a.v, choice));
		if (prev != null) {
			eds.remove(prev);
		}
		
		int n = MODEL.world.RANDOM.nextInt(eds.size());
		Edge r = eds.get(n);
		fillin(acc, a, choicePos, r);
		
		calculateRandomPath(acc, choicePos, b);
		
	}
	
	private static void fillin(List<GraphPosition> acc, VertexPosition aa, VertexPosition bb, Edge e) {
		
		if (aa.v == e.start) {
			assert bb.v == e.end;
			
			GraphPosition cur = EdgePosition.nextBoundfromStart(e);
				
			acc.add(cur);
			
			while (true) {
				if (cur.equals(bb)) {
					break;
				}
				cur = cur.nextBoundToward(bb);
				assert cur.isBound();
				acc.add(cur);
			}
			
		} else {
			assert bb.v == e.start;
			assert aa.v == e.end;
			
			GraphPosition cur = EdgePosition.nextBoundfromEnd(e);
			
			acc.add(cur);
			
			while (true) {
				
				if (cur.equals(bb)) {
					break;
				}
				
				cur = cur.nextBoundToward(bb);
				assert cur.isBound();
				acc.add(cur);
			}
			
		}
		
	}
	
	private boolean check() {
		for (int i = 1; i < poss.size(); i++) {
			GraphPosition cur = poss.get(i);
			GraphPosition prev = poss.get(i-1);
			
			assert prev.nextBoundToward(cur).equals(cur);
			
		}
		return true;
	}
	
}
