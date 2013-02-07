package com.gutabi.deadlock.world.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.CapsuleSequence;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.cars.AutonomousDriver;
import com.gutabi.deadlock.world.cars.Driver;
import com.gutabi.deadlock.world.cars.VertexArrivalEvent;

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
	
	public final List<AutonomousDriver> currentDrivers = new ArrayList<AutonomousDriver>();
	
	/**
	 * given another path, what is the set of shared edges?
	 * 
	 * empty sets are returned as null
	 */
	public final Map<GraphPositionPath, Set<Edge>> sharedEdgesMap = new HashMap<GraphPositionPath, Set<Edge>>();
	
	private int hash;
	
//	static Logger logger = Logger.getLogger(GraphPositionPath.class);
	
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
			} else if (pos instanceof EdgePosition) {
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
			} else if (g instanceof RoadPosition) {
				
			} else if (g instanceof MergerPosition) {
				
			} else if (g instanceof RushHourBoardPosition) {
				s = s + g + " ";
			}
		}
		return s;
	}
	
	public GraphPosition get(int index) {
		return poss.get(index);
	}
	
	
	public VertexArrivalEvent vertexArrivalTest(AutonomousDriver d, double dist) {
		
		for (GraphPositionPathPosition p : borderPositions) {
			GraphPositionPathPosition front = d.overallPos.travel(Math.min(d.c.CAR_LENGTH/2, d.overallPos.lengthToEndOfPath));
			if (p.combo >= front.combo && DMath.lessThanEquals(front.distanceTo(p), dist)) {
				return new VertexArrivalEvent(d, p);
			}
		}
		
		return null;
	}
	
	/**
	 * searches forward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 * 
	 * returns once a local minimum is found
	 */
	public GraphPositionPathPosition forwardSearch(Point p, GraphPositionPathPosition start, boolean returnOnLocalMinimum) {
		
		int closestIndex = start.index;
		double closestParam = start.param;
		
		double closestDistance = Point.distance(p, start.p);
		
		GraphPositionPathPosition a = start;
		GraphPositionPathPosition minCeiling = start.ceiling();
		GraphPositionPathPosition b;
		/*
		 * if start is not a bound, then search from start to the next bound
		 */
		if (!minCeiling.equals(start)) {
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
						
					} else if (returnOnLocalMinimum) {
						return new GraphPositionPathPosition(this, b.index, b.param);
					}
					
				}
			} else {
				
				Point pOnPath = Point.point(a.floor().p, b.p, u);
				double dist = Point.distance(p, pOnPath);
				
				if (dist < closestDistance) {
					closestIndex = a.index;
					closestParam = u;
					
					closestDistance = dist;
					
				} else if (returnOnLocalMinimum) {
					return new GraphPositionPathPosition(this, a.index, u);
				}
			}
			
			a = b;
		}
		assert a.isBound();
		/*
		 * search between bounds
		 */
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
						
					} else if (returnOnLocalMinimum) {
						return new GraphPositionPathPosition(this, b.index, b.param);
					}
				}
			} else {
				Point pOnPath = Point.point(a.p, b.p, u);
				double dist = Point.distance(p, pOnPath);
				if (dist < closestDistance) {
					closestIndex = a.index;
					closestParam = u;
					
					closestDistance = dist;
					
				} else if (returnOnLocalMinimum) {
					return new GraphPositionPathPosition(this, a.index, u);
				}
			}
			
			a = b;
		}
		
		return new GraphPositionPathPosition(this, closestIndex, closestParam);
	}
	
	/**
	 * searches backward from start position
	 * 
	 * finds closest position in a graphpositionpath to p
	 * 
	 * returns once a local minimum is found
	 */
	public GraphPositionPathPosition backwardSearch(Point p, GraphPositionPathPosition start, boolean returnOnLocalMinimum) {
		
		int closestIndex = start.index;
		double closestParam = start.param;
		
		double closestDistance = Point.distance(p, start.p);
		
		GraphPositionPathPosition b = start;
		GraphPositionPathPosition minFloor = start.floor();
		GraphPositionPathPosition a;
		if (!minFloor.equals(start)) {
			a = minFloor;
			
			double u = Point.u(a.p, p, b.ceiling().p);
			if (u > b.param) {
				u = b.param;
			}
			if (u < 0.0) {
				u = 0.0;
			}
			
			if (DMath.equals(u, 0.0)) {
				if (!a.isStartOfPath()) {
					/*
					 * the next iteration will pick this up
					 */
				} else {
					/*
					 * last iteration, deal with now
					 */
					double dist = Point.distance(p, a.p);
					if (dist < closestDistance) {
						closestIndex = a.index;
						closestParam = a.param;
						
						closestDistance = dist;
						
					} else if (returnOnLocalMinimum) {
						return new GraphPositionPathPosition(this, a.index, a.param);
					}
					
				}
			} else {
				
				Point pOnPath = Point.point(a.p, b.ceiling().p, u);
				double dist = Point.distance(p, pOnPath);
				
				if (dist < closestDistance) {
					closestIndex = a.index;
					closestParam = u;
					
					closestDistance = dist;
					
				} else if (returnOnLocalMinimum) {
					return new GraphPositionPathPosition(this, a.index, u);
				}
			}
			
			b = a;
		}
		assert b.isBound();
		while (true) {
			
			if (b.isStartOfPath()) {
				break;
			}
			
			a = b.prevBound();
			
			double u = Point.u(a.p, p, b.p);
			if (u < 0.0) {
				u = 0.0;
			}
			if (u > 1.0) {
				u = 1.0;
			}
			
			if (DMath.equals(u, 0.0)) {
				if (!a.isStartOfPath()) {
					/*
					 * the next iteration will pick this up
					 */
				} else {
					/*
					 * last iteration, deal with now
					 */
					double dist = Point.distance(p, a.p);
					if (dist < closestDistance) {
						closestIndex = a.index;
						closestParam = a.param;
						
						closestDistance = dist;
						
					} else if (returnOnLocalMinimum) {
						return new GraphPositionPathPosition(this, a.index, a.param);
					}
				}
			} else {
				Point pOnPath = Point.point(a.p, b.p, u);
				double dist = Point.distance(p, pOnPath);
				if (dist < closestDistance) {
					closestIndex = a.index;
					closestParam = u;
					
					closestDistance = dist;
					
				} else if (returnOnLocalMinimum) {
					return new GraphPositionPathPosition(this, a.index, u);
				}
			}
			
			b = a;
		}
		
		return new GraphPositionPathPosition(this, closestIndex, closestParam);
	}
	
	/**
	 * searches both forward and backward from start position
	 */
	public GraphPositionPathPosition generalSearch(Point p, GraphPositionPathPosition start) {
		GraphPositionPathPosition forwardPos = forwardSearch(p, start, false);
		GraphPositionPathPosition backwardPos = backwardSearch(p, start, false);
		
		double forwardDist = Point.distance(p, forwardPos.p);
		double backwardDist = Point.distance(p, backwardPos.p);
		
//		System.out.println("general search: forwardPos: " + forwardPos + " backwardPos: " + backwardPos + " forwardDist: " + forwardDist + " backwardDist: " + backwardDist);
		
		if (DMath.lessThan(backwardDist, forwardDist)) {
			return backwardPos;
		} else {
			return forwardPos;
		}
		
	}
	
	public GraphPositionPathPosition findClosestGraphPositionPathPosition(GraphPosition gp) {
		
		GraphPositionPathPosition a = startingPos;
		GraphPositionPathPosition minCeiling = startingPos.ceiling();
		GraphPositionPathPosition b;
		if (!minCeiling.equals(startingPos)) {
			b = minCeiling;
			
			double u = Point.u(a.floor().p, gp.p, b.p);
			if (DMath.lessThan(u, a.param) || DMath.greaterThan(u, 1.0)) {
				
			} else {
				
				if (DMath.equals(u, 1.0)) {
					if (!b.isEndOfPath()) {
						/*
						 * the next iteration will pick this up
						 */
					} else {
						/*
						 * last iteration, deal with now
						 */
						if (gp.p.equals(b.p)) {
							return new GraphPositionPathPosition(this, b.index, b.param);
						}
						
					}
				} else {
					
					Point pOnPath = Point.point(a.p, b.p, u);
					if (gp.p.equals(pOnPath)) {
						return new GraphPositionPathPosition(this, a.index, u);
						
					}
					
				}
				
			}
			
			a = b;
		}
		while (true) {
			
			if (a.isEndOfPath()) {
				break;
			}
			
			b = a.nextBound();
			
			double u = Point.u(a.p, gp.p, b.p);
			if (DMath.lessThan(u, a.param) || DMath.greaterThan(u, 1.0)) {
				
			} else {
				
				if (DMath.equals(u, 1.0)) {
					if (!b.isEndOfPath()) {
						/*
						 * the next iteration will pick this up
						 */
					} else {
						/*
						 * last iteration, deal with now
						 */
						if (gp.p.equals(b.p)) {
							return new GraphPositionPathPosition(this, b.index, b.param);
							
						}
					}
				} else {
					Point pOnPath = Point.point(a.p, b.p, u);
					if (gp.p.equals(pOnPath)) {
						return new GraphPositionPathPosition(this, a.index, u);
						
					}
				}
				
			}
			
			a = b;
		}
		
		return null;
	}


	
	private Map<AutonomousDriver, GraphPositionPathPosition> hitMap = new HashMap<AutonomousDriver, GraphPositionPathPosition>();
	
	public void precomputeHitTestData() {
		
		Map<AutonomousDriver, GraphPositionPathPosition> n = precomputeHitTestDataNew();
		
		hitMap = n;
		
	}
	
	private Map<AutonomousDriver, GraphPositionPathPosition> precomputeHitTestDataNew() {
		
		Map<AutonomousDriver, GraphPositionPathPosition> map = new HashMap<AutonomousDriver, GraphPositionPathPosition>();
		
		driverLoop:
		for (AutonomousDriver d : currentDrivers) {
			
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
	
	public AutonomousDriver driverProximityTest(GraphPositionPathPosition center, double dist) {
		
		for (Entry<AutonomousDriver, GraphPositionPathPosition> entry : hitMap.entrySet()) {
			AutonomousDriver d = entry.getKey();
			GraphPositionPathPosition otherCarCenter = entry.getValue();
			if (otherCarCenter.equals(center)) {
				continue;
			}
			if (DMath.lessThan(otherCarCenter.combo, center.combo)) {
				continue;
			}
			double centerCenterDist = center.distanceTo(otherCarCenter);
			double centerBackDist = centerCenterDist - d.c.CAR_LENGTH/2;
			if (DMath.lessThanEquals(centerBackDist, dist)) {
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
	
	public Entity pureGraphIntersectOBB(OBB q, GraphPositionPathPosition min) {
		
		for (Entry<Vertex, Integer> ent : verticesMap.entrySet()) {
			int i = ent.getValue();
			Vertex v = ent.getKey();
			if (i >= min.combo && ShapeUtils.intersectCO(v.getShape(), q)) {
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
					if (ShapeUtils.intersectOO((OBB)e.getShape(), q)) {
						return e;
					}
				}
			}
		}
		return null;
	}
	
	private boolean check() {
		for (int i = 0; i < poss.size(); i++) {
//			GraphPosition a = poss.get(i);
//			assert a.isBound();
		}
		return true;
	}
	
}
