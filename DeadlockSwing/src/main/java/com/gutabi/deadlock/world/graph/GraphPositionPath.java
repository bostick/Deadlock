package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

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
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.VertexArrivalEvent;

@SuppressWarnings("static-access")
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
	
	public final List<Car> currentCars = new ArrayList<Car>();
	
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
	
	public static GraphPositionPath createShortestPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (APP.world.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
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
			if (APP.world.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
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
	
	
	/**
	 * return list of events starting from position pos, and going distance dist
	 */
	public VertexArrivalEvent vertexArrivalTest(Car c, double dist) {
		
		for (GraphPositionPathPosition p : borderPositions) {
			if (p.combo >= c.overallPos.combo && DMath.lessThanEquals(c.overallPos.distanceTo(p), dist)) {
				return new VertexArrivalEvent(c, p);
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
		
		int closestIndex = -1;
		double closestParam = -1;
		
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
		
		assert closestIndex != -1;
		assert closestParam != -1;
		
		return new GraphPositionPathPosition(this, closestIndex, closestParam);
	}
	
	private Map<Car, GraphPositionPathPosition> hitMap = new HashMap<Car, GraphPositionPathPosition>();
	
	public void precomputeHitTestData() {
		
		Map<Car, GraphPositionPathPosition> n = precomputeHitTestDataNew();
		
		hitMap = n;
		
	}
	
	private Map<Car, GraphPositionPathPosition> precomputeHitTestDataNew() {
		
		Map<Car, GraphPositionPathPosition> map = new HashMap<Car, GraphPositionPathPosition>();
		
		carLoop:
		for (Car c : currentCars) {
			
			if (c.overallPath.equals(this) && !hasLoop) {
				
				map.put(c, c.overallPos);
				continue;
				
			}
			
			Set<Edge> sharedEdges = sharedEdgesMap.get(c.overallPath);
			assert !sharedEdges.isEmpty();
			
			GraphPosition gp = c.overallPos.getGraphPosition();
			
			if (gp instanceof VertexPosition) {
				VertexPosition vp = (VertexPosition)gp;
				
				if (verticesMap.containsKey(vp.v)) {
					
					int vi = verticesMap.get(vp.v);
					
					assert !map.containsValue(c);
					map.put(c, new GraphPositionPathPosition(this, vi, 0.0));
					
				}
				
				continue carLoop;
				
			} else {
				EdgePosition ep = (EdgePosition)gp;
				
				Edge e = (Edge)ep.entity;
				
				if (edgesMap.containsKey(e)) {
					
					int ei = edgesMap.get(e);
					
					Vertex v = ((VertexPosition)poss.get(ei)).v;
					
					if (e.getReferenceVertex(ep.axis) == v) {
						
						assert !map.containsValue(c);
						map.put(c, new GraphPositionPathPosition(this, ei + ep.getIndex(), ep.getParam()));
						
						
					} else {
						assert e.getOtherVertex(ep.axis) == v;
						
						double newCombo = e.pointCount()-1 - ep.getCombo();
						
						int newIndex = (int)Math.floor(newCombo);
						double newParam = newCombo - newIndex;
						
						assert !map.containsValue(c);
						map.put(c, new GraphPositionPathPosition(this, ei + newIndex, newParam));
						
					}
					
				}
				
				continue carLoop;
				
			}
		}
		
		return map;
	}
	
	public void clearHitTestData() {
		hitMap.clear();
	}
	
	/**
	 * returns a sorted list of car proximity events
	 */
	public Car carProximityTest(GraphPositionPathPosition center, double dist) {
		
		for (Entry<Car, GraphPositionPathPosition> entry : hitMap.entrySet()) {
			GraphPositionPathPosition otherCarCenter = entry.getValue();
			Car c = entry.getKey();
			if (otherCarCenter.equals(center)) {
				continue;
			}
			if (DMath.lessThan(otherCarCenter.combo, center.combo)) {
				continue;
			}
			double centerCenterDist = center.distanceTo(otherCarCenter);
			if (DMath.lessThanEquals(centerCenterDist, dist)) {
				assert !c.overallPos.equals(center);
				return c;
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
	public GraphPositionPathPosition hitTest(Car c, GraphPositionPathPosition startingPosition) {
		
		GraphPositionPathPosition gppp = hitMap.get(c);
		
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
	
	public Entity pureGraphBestHitTestQuad(Quad q, GraphPositionPathPosition min) {
		
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
					if (ShapeUtils.intersectCapSeqQ((CapsuleSequence)e.getShape(), q)) {
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
	
	/**
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private static void calculateShortestPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
		if (a.equals(b)) {
			
			assert false;
			
		} else {
			List<Edge> edges = Vertex.commonEdges(a.v, b.v);
			
			if (edges.isEmpty()) {
				
				Vertex choice = APP.world.shortestPathChoice(a.v, b.v);
				
				if (choice == null) {
					
					throw new IllegalArgumentException();
					
				} else {
					
					calculateShortestPath(acc, a, new VertexPosition(choice));
					calculateShortestPath(acc, new VertexPosition(choice), b);
					
				}
				
			} else { 
						
				Edge shortest = null;
				for (Edge e : edges) {
					
					if (e instanceof Road) {
						Road r = (Road)e;
						boolean aIsStart = a.v == r.start;
						
						if ((aIsStart?(e.getDirection(null) != Direction.ENDTOSTART):(e.getDirection(null) != Direction.STARTTOEND))) {
							if (shortest == null || DMath.lessThan(e.getTotalLength(a.v, b.v), shortest.getTotalLength(a.v, b.v))) {
								shortest = e;
							}
						}
						
					} else {
						Merger m = (Merger)e;
						boolean aIsStart = a.v == m.top || a.v == m.left;
						
						if (a.v == m.top || a.v == m.bottom) {
							
							if ((aIsStart?(e.getDirection(Axis.TOPBOTTOM) != Direction.ENDTOSTART):(e.getDirection(Axis.TOPBOTTOM) != Direction.STARTTOEND))) {
								if (shortest == null || DMath.lessThan(e.getTotalLength(a.v, b.v), shortest.getTotalLength(a.v, b.v))) {
									shortest = e;
								}
							}
							
						} else {
							
							if ((aIsStart?(e.getDirection(Axis.LEFTRIGHT) != Direction.ENDTOSTART):(e.getDirection(Axis.LEFTRIGHT) != Direction.STARTTOEND))) {
								if (shortest == null || DMath.lessThan(e.getTotalLength(a.v, b.v), shortest.getTotalLength(a.v, b.v))) {
									shortest = e;
								}
							}
							
						}
						
					}
					
				}
				
				assert a.v != b.v;
				fillin(acc, a, b, shortest, 2);
				
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
			GraphPosition pen = acc.get(acc.size()-2);
			if (pen instanceof RoadPosition) {
				prev = ((RoadPosition)pen).r;
			} else {
				prev = ((MergerPosition)pen).m;
			}
		}
		
		Vertex choice = APP.world.randomPathChoice(prev, a.v, b.v);
		VertexPosition choicePos = new VertexPosition(choice);
		
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Vertex.commonEdges(a.v, choice));
		
		List<Edge> toRemove = new ArrayList<Edge>();
		
		if (prev != null) {
			toRemove.add(prev);
		}
		
		for (Edge e : edges) {
			
			if (e instanceof Road) {
				Road r = (Road)e;
				boolean aIsStart = a.v == r.start;
				
				if ((aIsStart?(e.getDirection(null) != Direction.ENDTOSTART):(e.getDirection(null) != Direction.STARTTOEND))) {
					
				} else {
					toRemove.add(e);
				}
				
			} else {
				Merger m = (Merger)e;
				boolean aIsStart = a.v == m.top || a.v == m.left;
				
				if (a.v == m.top || a.v == m.bottom) {
					
					if ((aIsStart?(e.getDirection(Axis.TOPBOTTOM) != Direction.ENDTOSTART):(e.getDirection(Axis.TOPBOTTOM) != Direction.STARTTOEND))) {
						
					} else {
						toRemove.add(e);
					}
					
				} else {
					
					if ((aIsStart?(e.getDirection(Axis.LEFTRIGHT) != Direction.ENDTOSTART):(e.getDirection(Axis.LEFTRIGHT) != Direction.STARTTOEND))) {
						
					} else {
						toRemove.add(e);
					}
					
				}
				
			}
			
		}
		
		edges.removeAll(toRemove);
		
		int n = APP.world.RANDOM.nextInt(edges.size());
		Edge e = edges.get(n);
		int dir;
		if (a.v == choicePos.v) {
			dir = APP.world.RANDOM.nextInt(2);
		} else {
			dir = 2;
		}
		fillin(acc, a, choicePos, e, dir);
		
		calculateRandomPath(acc, choicePos, b);
		
	}
	
	private static void fillin(List<GraphPosition> acc, VertexPosition aa, VertexPosition bb, Edge e, int dir) {
		
		if (dir == 0 || (dir == 2 && (
				(e instanceof Road && (aa.v == ((Road)e).getReferenceVertex(null))) ||
				(e instanceof Merger && (aa.v == ((Merger)e).getReferenceVertex(Axis.TOPBOTTOM) || aa.v == ((Merger)e).getReferenceVertex(Axis.LEFTRIGHT)))))) {
			
			GraphPosition cur;
			if (e instanceof Road) {
				assert aa.v == ((Road)e).start;
				assert bb.v == ((Road)e).end;
				cur = RoadPosition.nextBoundfromStart((Road)e);
			} else {
				if (aa.v == ((Merger)e).top) {
					cur = MergerPosition.nextBoundFromTop((Merger)e);
				} else {
					assert aa.v == ((Merger)e).left;
					cur = MergerPosition.nextBoundFromLeft((Merger)e);
				}
			}
			
			acc.add(cur);
			
			while (true) {
				if (cur.equals(bb)) {
					break;
				}
				cur = ((EdgePosition)cur).nextBoundTowardOtherVertex();
				assert cur.isBound();
				acc.add(cur);
			}
			
		} else {
			
			GraphPosition cur;
			if (e instanceof Road) {
				assert aa.v == ((Road)e).end;
				assert bb.v == ((Road)e).start;
				cur = RoadPosition.nextBoundfromEnd((Road)e);
			} else {
				if (aa.v == ((Merger)e).bottom) {
					cur = MergerPosition.nextBoundFromBottom((Merger)e);
				} else {
					assert aa.v == ((Merger)e).right;
					cur = MergerPosition.nextBoundFromRight((Merger)e);
				}
			}
			
			acc.add(cur);
			
			while (true) {
				if (cur.equals(bb)) {
					break;
				}
				cur = ((EdgePosition)cur).nextBoundTowardReferenceVertex();
				assert cur.isBound();
				acc.add(cur);
			}
			
		}
		
	}
	
	private boolean check() {
		for (int i = 0; i < poss.size(); i++) {
			GraphPosition a = poss.get(i);
			assert a.isBound();
		}
		return true;
	}
	
}
