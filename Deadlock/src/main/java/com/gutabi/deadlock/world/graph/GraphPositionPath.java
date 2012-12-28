package com.gutabi.deadlock.world.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.CapsuleSequence;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.world.cars.Driver;
import com.gutabi.deadlock.world.cars.VertexArrivalEvent;

//@SuppressWarnings("static-access")
public class GraphPositionPath {
	
	private final List<GraphPosition> poss;
	
	public final int size;
	public final GraphPosition start;
	public final GraphPosition end;
	
	public final double[] cumulativeDistancesFromStart;
	public final double totalLength;
	
	public final boolean hasLoop;
	
	public final GraphPositionPathPosition startingPos;
	
	public final Map<Vertex, Integer> verticesMap = new HashMap<Vertex, Integer>();
	public final Map<Edge, Integer> edgesMap = new HashMap<Edge, Integer>();
	public final Map<Edge, Axis> axesMap = new HashMap<Edge, Axis>();
	
	private List<GraphPositionPathPosition> borderPositions;
	
	public final List<Driver> currentDrivers = new ArrayList<Driver>();
	
	/**
	 * given another path, what is the set of shared edges?
	 * 
	 * empty sets are returned as null
	 */
	public final Map<GraphPositionPath, Set<Edge>> sharedEdgesMap = new HashMap<GraphPositionPath, Set<Edge>>();
	
	private int hash;
	
	static Logger logger = Logger.getLogger(GraphPositionPath.class);
	
	public GraphPositionPath(List<GraphPosition> poss) {
		
		this.poss = poss;
		
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
		
		
		List<Vertex> visited = new ArrayList<Vertex>();
		boolean tmp = false;
		for (int i = 0; i < poss.size(); i++) {
			GraphPosition pos = poss.get(i);
			if (pos instanceof VertexPosition) {
				Vertex v = ((VertexPosition)pos).v;
				/*
				 * work to determine hasLoop
				 */
				if (!tmp) {
					if (visited.contains(v)) {
						tmp = true;
					} else {
						visited.add(v);
					}
				}
				verticesMap.put(v, i);
			} else {
				/*
				 * work to determine list of edges
				 */
				Edge e = (Edge)((EdgePosition)pos).entity;
				if (!edgesMap.containsKey(e)) {
					edgesMap.put(e, i-1);
					if (e instanceof Merger) {
						Vertex v = ((VertexPosition)poss.get(i-1)).v;
						Merger m = (Merger)e;
						if (v == m.top || v == m.bottom) {
							axesMap.put(e, Axis.TOPBOTTOM);
						} else {
							assert v == m.left || v == m.right;
							axesMap.put(e, Axis.LEFTRIGHT);
						}
					} else {
						axesMap.put(e, null);
					}
				}
			}
		}
		hasLoop = tmp;
		assert !hasLoop;
		
		borderPositions = new ArrayList<GraphPositionPathPosition>();
		
		for (int i = 0; i < poss.size(); i++) {
			GraphPosition gp = poss.get(i);
			if (gp instanceof RoadPosition && poss.get(i+1) instanceof VertexPosition) {
				/*
				 * only count signs in the correct direction: road -> sign -> vertex
				 */
				if (poss.get(i+1) instanceof VertexPosition) {
					borderPositions.add(new GraphPositionPathPosition(this, i, 0.0));
				}
			}
		}
		
		startingPos = new GraphPositionPathPosition(this, 0, 0.0);
		
		assert check();
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (hashCode() != o.hashCode()) {
			/*
			 * should be ok to do here
			 */
			return false;
		} else if (!(o instanceof GraphPositionPath)) {
			return false;
		} else {
			GraphPositionPath b = (GraphPositionPath)o;
			return poss.equals(b.poss);
		}
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + poss.hashCode();
			hash = h;
		}
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
	
	
	/**
	 * return list of events starting from position pos, and going distance dist
	 */
	public VertexArrivalEvent vertexArrivalTest(Driver d, double dist) {
		
		for (GraphPositionPathPosition p : borderPositions) {
			if (p.combo >= d.overallPos.combo && DMath.lessThanEquals(d.overallPos.distanceTo(p), dist)) {
				return new VertexArrivalEvent(d, p);
			}
		}
		
		return null;
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
	public GraphPositionPathPosition findClosestGraphPositionPathPosition(Point p, GraphPositionPathPosition min) {
		
		int closestIndex = min.index;
		double closestParam = min.param;
		
		double closestDistance = Double.POSITIVE_INFINITY;
		
		GraphPositionPathPosition a = min;
		GraphPositionPathPosition minCeiling = min.ceiling();
		GraphPositionPathPosition b;
		if (!minCeiling.equals(min)) {
			b = minCeiling;
			
			double u = Point.u(a.floor().p, p, b.p);
			if (u < a.param) {
				u = a.param;
			}
			if (u > 1.0) {
				u = 1.0;
			}
			
			if (DMath.equals(u, 1.0)) {
				if (!b.isEndOfPath()) {
					/*
					 * the next iteration will pick this up
					 */
				} else {
					/*
					 * last iteration, deal with now
					 */
					double dist = Point.distance(p, b.p);
					if (dist < closestDistance) {
						closestIndex = b.index;
						closestParam = b.param;
						
						closestDistance = dist;
						
//						logger.debug("closestDistance: " + closestDistance);
						
					} else {
						return new GraphPositionPathPosition(this, closestIndex, closestParam);
					}
					
				}
			} else {
				
				Point pOnPath = Point.point(a.floor().p, b.p, u);
				double dist = Point.distance(p, pOnPath);
				
				if (dist < closestDistance) {
					closestIndex = a.index;
					closestParam = u;
					
					closestDistance = dist;
					
//					logger.debug("closestDistance: " + closestDistance);
					
				} else {
					return new GraphPositionPathPosition(this, closestIndex, closestParam);
				}
			}
			
			a = b;
		}
		while (true) {
			
			if (a.isEndOfPath()) {
				break;
			}
			
			b = a.nextBound();
			
			double u = Point.u(a.p, p, b.p);
			if (u < 0.0) {
				u = 0.0;
			}
			if (u > 1.0) {
				u = 1.0;
			}
			
			if (DMath.equals(u, 1.0)) {
				if (!b.isEndOfPath()) {
					/*
					 * the next iteration will pick this up
					 */
				} else {
					/*
					 * last iteration, deal with now
					 */
					double dist = Point.distance(p, b.p);
					if (dist < closestDistance) {
						closestIndex = b.index;
						closestParam = b.param;
						
						closestDistance = dist;
						
//						logger.debug("closestDistance: " + closestDistance);
						
					} else {
						return new GraphPositionPathPosition(this, closestIndex, closestParam);
					}
				}
			} else {
				Point pOnPath = Point.point(a.p, b.p, u);
				double dist = Point.distance(p, pOnPath);
				if (dist < closestDistance) {
					closestIndex = a.index;
					closestParam = u;
					
					closestDistance = dist;
					
//					logger.debug("closestDistance: " + closestDistance);
					
				} else {
					return new GraphPositionPathPosition(this, closestIndex, closestParam);
				}
			}
			
			a = b;
		}
		
//		assert closestIndex != -1;
//		assert closestParam != -1.0;
		
		return new GraphPositionPathPosition(this, closestIndex, closestParam);
	}
	
	private Map<Driver, GraphPositionPathPosition> hitMap = new HashMap<Driver, GraphPositionPathPosition>();
	
	public void precomputeHitTestData() {
		
		Map<Driver, GraphPositionPathPosition> n = precomputeHitTestDataNew();
		
		hitMap = n;
		
	}
	
	private Map<Driver, GraphPositionPathPosition> precomputeHitTestDataNew() {
		
		Map<Driver, GraphPositionPathPosition> map = new HashMap<Driver, GraphPositionPathPosition>();
		
		driverLoop:
		for (Driver d : currentDrivers) {
			
			if (d.overallPath.equals(this) && !hasLoop) {
				
				map.put(d, d.overallPos);
				continue;
				
			}
			
			Set<Edge> sharedEdges = sharedEdgesMap.get(d.overallPath);
			assert !sharedEdges.isEmpty();
			
			GraphPosition gp = d.overallPos.getGraphPosition();
			
			if (gp instanceof VertexPosition) {
				VertexPosition vp = (VertexPosition)gp;
				
				if (verticesMap.containsKey(vp.v)) {
					
					int vi = verticesMap.get(vp.v);
					
					assert !map.containsValue(d);
					map.put(d, new GraphPositionPathPosition(this, vi, 0.0));
					
				}
				
				continue driverLoop;
				
			} else {
				EdgePosition ep = (EdgePosition)gp;
				
				Edge e = (Edge)ep.entity;
				
				if (edgesMap.containsKey(e)) {
					
					int ei = edgesMap.get(e);
					
					Vertex v = ((VertexPosition)poss.get(ei)).v;
					
					if (e.getReferenceVertex(ep.axis) == v) {
						
						assert !map.containsValue(d);
						map.put(d, new GraphPositionPathPosition(this, ei + ep.getIndex(), ep.getParam()));
						
						
					} else {
						assert e.getOtherVertex(ep.axis) == v;
						
						double newCombo = e.pointCount()-1 - ep.getCombo();
						
						int newIndex = (int)Math.floor(newCombo);
						double newParam = newCombo - newIndex;
						
						assert !map.containsValue(d);
						map.put(d, new GraphPositionPathPosition(this, ei + newIndex, newParam));
						
					}
					
				}
				
				continue driverLoop;
				
			}
		}
		
		return map;
	}
	
	public void clearHitTestData() {
		hitMap.clear();
	}
	
	/**
	 * returns a sorted list of driver proximity events
	 */
	public Driver driverProximityTest(GraphPositionPathPosition center, double dist) {
		
		for (Entry<Driver, GraphPositionPathPosition> entry : hitMap.entrySet()) {
			GraphPositionPathPosition otherCarCenter = entry.getValue();
			Driver d = entry.getKey();
			if (otherCarCenter.equals(center)) {
				continue;
			}
			if (DMath.lessThan(otherCarCenter.combo, center.combo)) {
				continue;
			}
			double centerCenterDist = center.distanceTo(otherCarCenter);
			if (DMath.lessThanEquals(centerCenterDist, dist)) {
				assert !d.overallPos.equals(center);
				return d;
			}
		}
		
		return null;
	}
	
	/**
	 * resolve a gp into a gppp on this path
	 * 
	 * test gp for a position on this path, but only start looking after startingPosition
	 * this is to help handle loops in paths
	 */
	public GraphPositionPathPosition hitTest(Driver d, GraphPositionPathPosition startingPosition) {
		
		GraphPositionPathPosition gppp = hitMap.get(d);
		
		if (gppp == null) {
			/*
			 * a car may have driven and no longer be itnerecting with this path
			 */
			return null;
		}
		
		if (DMath.greaterThanEquals(gppp.combo, startingPosition.combo)) {
			return gppp;
		}
		
		return null;
	}
	
	public Entity pureGraphIntersectQuad(Quad q, GraphPositionPathPosition min) {
		
		for (Entry<Vertex, Integer> ent : verticesMap.entrySet()) {
			int i = ent.getValue();
			Vertex v = ent.getKey();
			if (i >= min.combo && ShapeUtils.intersectCQ(v.getShape(), q)) {
				return v;
			}
		}
		for (Entry<Edge, Integer> ent : edgesMap.entrySet()) {
			int i = ent.getValue();
			Edge e = ent.getKey();
			if (i + e.pointCount() >= min.combo) {
				if (e instanceof Road) {
					if (((CapsuleSequence)e.getShape()).intersect(q)) {
						return e;
					}
				} else {
					if (ShapeUtils.intersectQQ((Quad)e.getShape(), q)) {
						return e;
					}
				}
			}
		}
		return null;
	}
	
	private boolean check() {
		for (int i = 0; i < poss.size(); i++) {
			GraphPosition a = poss.get(i);
			assert a.isBound();
		}
		return true;
	}
	
}
