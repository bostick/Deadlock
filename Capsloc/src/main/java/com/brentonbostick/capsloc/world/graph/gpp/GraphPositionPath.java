package com.brentonbostick.capsloc.world.graph.gpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.brentonbostick.capsloc.Entity;
import com.brentonbostick.capsloc.geom.MutableOBB;
import com.brentonbostick.capsloc.geom.OBB;
import com.brentonbostick.capsloc.geom.ShapeUtils;
import com.brentonbostick.capsloc.math.DMath;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.world.cars.AutonomousDriver;
import com.brentonbostick.capsloc.world.cars.Driver;
import com.brentonbostick.capsloc.world.cars.VertexArrivalEvent;
import com.brentonbostick.capsloc.world.graph.Axis;
import com.brentonbostick.capsloc.world.graph.BypassBoardPosition;
import com.brentonbostick.capsloc.world.graph.Edge;
import com.brentonbostick.capsloc.world.graph.EdgePosition;
import com.brentonbostick.capsloc.world.graph.GraphPosition;
import com.brentonbostick.capsloc.world.graph.Merger;
import com.brentonbostick.capsloc.world.graph.MergerPosition;
import com.brentonbostick.capsloc.world.graph.Road;
import com.brentonbostick.capsloc.world.graph.RoadPosition;
import com.brentonbostick.capsloc.world.graph.Vertex;
import com.brentonbostick.capsloc.world.graph.VertexPosition;

public class GraphPositionPath {

	private final List<GraphPosition> poss;
	
	public final int size;
	public final GraphPosition start;
	public final GraphPosition end;
	
	public final double[] cumulativeDistancesFromStart;
	public final double totalLength;
	
	public final boolean hasLoop;
	
	public final GraphPositionPathPosition startPos;
	public final GraphPositionPathPosition endPos;
	
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
		
		/*
		 * a repeated vertex doesn't count as a loop
		 * but a repeated edge does count as a loop
		 */
		List<EdgePosition> visited = new ArrayList<EdgePosition>();
		boolean tmp = false;
		for (int i = 0; i < poss.size(); i++) {
			GraphPosition pos = poss.get(i);
			if (pos instanceof VertexPosition) {
				Vertex v = ((VertexPosition)pos).v;
				verticesMap.put(v, i);
			} else if (pos instanceof EdgePosition) {
				Edge e = (Edge)((EdgePosition)pos).entity;
				/*
				 * work to determine hasLoop
				 */
				if (!tmp) {
					if (visited.contains((EdgePosition)pos)) {
						tmp = true;
					} else {
						visited.add((EdgePosition)pos);
					}
				}
				/*
				 * work to determine list of edges
				 */
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
		
		startPos = new GraphPositionPathPosition(this, 0, 0.0);
		endPos = new GraphPositionPathPosition(this, poss.size()-1, 0.0);
		
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
				s = s + g + " ";
			} else if (g instanceof MergerPosition) {
				s = s + g + " ";
			} else if (g instanceof BypassBoardPosition) {
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
			GraphPositionPathPosition front = d.overallPos.travelForward(Math.min(d.c.length/2, d.overallPos.lengthToEndOfPath));
			if (p.combo >= front.combo && DMath.lessThanEquals(front.lengthTo(p), dist)) {
				return new VertexArrivalEvent(d, p);
			}
		}
		
		return null;
	}
	
	public int nextVertexIndex(int index, double param) {
		if (!(index < size-1)) {
			return -1;
		}
		int i = index+1;
		while (true) {
			GraphPosition gpos = poss.get(i);
			if (gpos instanceof VertexPosition) {
				return i;
			}
			if (!(i < size-1)) {
				return -1;
			}
			i++;
		}
	}
	
	public int prevVertexIndex(int index, double param) {
		if (!(DMath.equals(param, 0.0) ? index > 0 : true)) {
			return -1;
		}
		int i = DMath.equals(param, 0.0) ? index - 1 : index;
		while (true) {
			GraphPosition gpos = poss.get(i);
			if (gpos instanceof VertexPosition) {
				return i;
			}
			if (!(i > 0)) {
				return -1;
			}
			i--;
		}
	}
	
	/**
	 * assumes gp is on path
	 */
	public GraphPositionPathPosition findGraphPositionPathPosition(GraphPosition gp, double angle) {
		assert angle == 0.0 * Math.PI || angle == 0.5 * Math.PI;
		
		GraphPositionPathPosition start = startPos;
		GraphPositionPathPosition end = endPos;
		
		if (gp.equals(start.gp)) {
			return start;
		}
		
		if (start.equals(end)) {
			
			assert gp.equals(start.gp);
			return start;
			
		} else if (start.index == end.index) {
			
			double u = Point.uNoProjection(start.floor().p, gp.p, end.ceil().p);
			if (u == -1) {
				assert false;
			} else if (DMath.lessThan(u, start.param)) {
				assert false;
			} else if (DMath.greaterThan(u, end.param)) {
				assert false;
			} else {
				
				GraphPositionPathPosition ret = new GraphPositionPathPosition(this, start.index, u);
				assert ret.gp.equals(gp);
				assert DMath.anglesCompatible(ret.angle, angle);
				return ret;
			}
			
		} else if (end.index == start.index+1 && DMath.equals(end.param, 0.0)) {
					
			double u = Point.uNoProjection(start.floor().p, gp.p, end.p);
			if (u == -1) {
				assert false;
			} else if (DMath.lessThan(u, start.param)) {
				assert false;
			} else {
				
				GraphPositionPathPosition ret = new GraphPositionPathPosition(this, start.index, u);
				assert ret.gp.equals(gp);
				assert DMath.anglesCompatible(ret.angle, angle);
				return ret;
			}
					
		}
		
		GraphPositionPathPosition a = start;
		GraphPositionPathPosition startCeiling = start.ceil();
		GraphPositionPathPosition b;
		GraphPositionPathPosition endFloor = end.floor();
		
		if (!startCeiling.equals(a)) {
			b = startCeiling;
			
			double u = Point.uNoProjection(a.floor().p, gp.p, b.p);
			if (u == -1) {
				
			} else if (DMath.lessThan(u, a.param)) {
				
			} else {
				
				GraphPositionPathPosition ret = new GraphPositionPathPosition(this, a.index, u);
				
				if (DMath.anglesCompatible(ret.angle, angle)) {
					assert ret.gp.equals(gp);
					return ret;
				}
				
			}
			
			a = b;
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b = a.nextBound();
			
			double u = Point.uNoProjection(a.p, gp.p, b.p);
			if (u == -1) {
				
			} else {
				
				GraphPositionPathPosition ret = new GraphPositionPathPosition(this, a.index, u);
				
				if (DMath.anglesCompatible(ret.angle, angle)) {
					assert ret.gp.equals(gp);
					return ret;
				}
				
			}
			
			a = b;
		}
		if (!a.equals(end)) {
			b = end;
					
			double u = Point.uNoProjection(a.p, gp.p, b.ceil().p);
			if (u == -1) {
				
			} else if (DMath.greaterThan(u, b.param)) {
				
			} else {
				
				GraphPositionPathPosition ret = new GraphPositionPathPosition(this, a.index, u);
				
				if (DMath.anglesCompatible(ret.angle, angle)) {
					assert ret.gp.equals(gp);
					return ret;
				}
				
			}
			
		}
		
		assert false;
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
			
			GraphPosition gp = d.overallPos.gp;
			
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
			double centerCenterDist = center.lengthTo(otherCarCenter);
			double centerBackDist = centerCenterDist - d.c.length/2;
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
	
	public Entity pureGraphIntersectOBB(OBB o, GraphPositionPathPosition min) {
		
		for (Entry<Vertex, Integer> ent : verticesMap.entrySet()) {
			int i = ent.getValue();
			Vertex v = ent.getKey();
			if (i >= min.combo && ShapeUtils.intersectCO(v.shape, o)) {
				return v;
			}
		}
		for (Entry<Edge, Integer> ent : edgesMap.entrySet()) {
			int i = ent.getValue();
			Edge e = ent.getKey();
			if (i + e.pointCount() >= min.combo) {
				if (e instanceof Road) {
					if (((Road)e).shape.intersect(o)) {
						return e;
					}
				} else {
					if (ShapeUtils.intersectAO(((Merger)e).shape, o)) {
						return e;
					}
				}
			}
		}
		return null;
	}
	
	public Entity pureGraphIntersectOBB(MutableOBB o, GraphPositionPathPosition min) {
		
		for (Entry<Vertex, Integer> ent : verticesMap.entrySet()) {
			int i = ent.getValue();
			Vertex v = ent.getKey();
			if (i >= min.combo && ShapeUtils.intersectCO(v.shape, o)) {
				return v;
			}
		}
		for (Entry<Edge, Integer> ent : edgesMap.entrySet()) {
			int i = ent.getValue();
			Edge e = ent.getKey();
			if (i + e.pointCount() >= min.combo) {
				if (e instanceof Road) {
					if (((Road)e).shape.intersect(o)) {
						return e;
					}
				} else {
					if (ShapeUtils.intersectAO(((Merger)e).shape, o)) {
						return e;
					}
				}
			}
		}
		return null;
	}

}
