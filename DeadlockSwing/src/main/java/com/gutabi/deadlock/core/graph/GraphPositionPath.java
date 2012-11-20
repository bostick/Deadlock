package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.model.event.VertexArrivalEvent;

@SuppressWarnings("static-access")
public class GraphPositionPath {
	
	public final List<GraphPosition> poss;
	
	public final int size;
	public final GraphPosition start;
	public final GraphPosition end;
	
	public final double[] cumulativeDistancesFromStart;
	public final double totalLength;
	
	List<GraphPositionPathPosition> borderPositions;
	
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
	
	
	/**
	 * return list of events starting from position pos, and going distance dist
	 */
	public VertexArrivalEvent vertexArrivalTest(GraphPositionPathPosition pos, double dist) {
		
//		List<VertexArrivalEvent> acc = new ArrayList<VertexArrivalEvent>();
		for (GraphPositionPathPosition p : borderPositions) {
			if (p.combo >= pos.combo && DMath.lessThanEquals(pos.distanceTo(p), dist)) {
				return new VertexArrivalEvent(p);
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
	public GraphPositionPathPosition findClosestGraphPositionPathPosition(Point p, GraphPositionPathPosition min, GraphPositionPathPosition max) {
		
		GraphPositionPathPosition closest = null;
		double closestDistance = Double.POSITIVE_INFINITY;
		
		if (min.equals(max)) {
			return min;
		} else if (min.index == max.index) {
			
			double u;
			u = Point.u(min.floor().gpos.p, p, max.ceiling().gpos.p);
			if (u < min.param) {
				u = min.param;
			}
			if (u > max.param) {
				u = max.param;
			}
			
			closest = new GraphPositionPathPosition(this, min.index, u);
			
			return closest;
		}
		
		GraphPositionPathPosition a = min;
		GraphPositionPathPosition minCeiling = min.ceiling();
		GraphPositionPathPosition maxFloor = max.floor();
		GraphPositionPathPosition b;
		if (!minCeiling.equals(min)) {
			b = minCeiling;
			
			double u = Point.u(a.floor().gpos.p, p, b.gpos.p);
			if (u < a.param) {
				u = a.param;
			}
			if (u > 1.0) {
				u = 1.0;
			}
			
			if (DMath.equals(u, 1.0)) {
				if (!b.equals(max)) {
					/*
					 * the next iteration will pick this up
					 */
				} else {
					/*
					 * last iteration, deal with now
					 */
					double dist = Point.distance(p, b.gpos.p);
					if (dist < closestDistance) {
						closest = b;
						closestDistance = dist;
					}
				}
			} else {
				
				Point pOnPath = Point.point(a.gpos.p, b.gpos.p, u);
				double dist = Point.distance(p, pOnPath);
				
				if (dist < closestDistance) {
					closest = new GraphPositionPathPosition(this, a.index, u);
					closestDistance = dist;
				}
			}
			
			a = b;
		}
		while (true) {
			
			if (a.equals(maxFloor)) {
				break;
			}
			
			b = a.nextBound();
			
			double u = Point.u(a.gpos.p, p, b.gpos.p);
			if (u < 0.0) {
				u = 0.0;
			}
			if (u > 1.0) {
				u = 1.0;
			}
			
			if (DMath.equals(u, 1.0)) {
				if (!b.equals(max)) {
					/*
					 * the next iteration will pick this up
					 */
				} else {
					/*
					 * last iteration, deal with now
					 */
					double dist = Point.distance(p, b.gpos.p);
					if (dist < closestDistance) {
						closest = b;
						closestDistance = dist;
					}
				}
			} else {
				Point pOnPath = Point.point(a.gpos.p, b.gpos.p, u);
				double dist = Point.distance(p, pOnPath);
				if (dist < closestDistance) {
					closest = new GraphPositionPathPosition(this, a.index, u);
					closestDistance = dist;
				}
			}
			
			a = b;
		}
		if (!maxFloor.equals(max)) {
			b = max;
			
			GraphPositionPathPosition bCeil = b.ceiling();
			
			double u = Point.u(a.gpos.p, p, bCeil.gpos.p);
			if (u > b.param) {
				u = b.param;
			}
			if (u < 0.0) {
				u = 0.0;
			}
			
			Point pOnPath = Point.point(a.gpos.p, b.gpos.p, u);
			double dist = Point.distance(p, pOnPath);
			if (dist < closestDistance) {
				closest = new GraphPositionPathPosition(this, a.index, u);
				closestDistance = dist;
			}
			
		}
		
		assert closest != null;
		return closest;
	}
	
	/**
	 * 
	 */
	public GraphPosition getGraphPosition(int pathIndex, double pathParam) {
		
		GraphPosition p1 = poss.get(pathIndex);
		
		if (DMath.equals(pathParam, 0.0)) {
			return p1;
		}
		
		GraphPosition p2 = poss.get(pathIndex+1);
		
		double dist = p1.distanceTo(p2);
		
		return p1.travelTo(p2, pathParam * dist);
	}
	
	/**
	 * test gp for a position on this path, but only start looking after startingPosition
	 * this is to help handle loops
	 */
	public GraphPositionPathPosition hitTest(GraphPosition gp, GraphPositionPathPosition startingPosition) {
		
		if (poss.get(0) instanceof VertexPosition) {
			if (gp.entity == ((VertexPosition)poss.get(0)).v) {
				assert getGraphPosition(0, 0.0).equals(gp);
				if (DMath.greaterThanEquals(0+0.0, startingPosition.combo)) {
					return new GraphPositionPathPosition(this, 0, 0.0);
				}
			}
		}
		
		for (int i = 0; i < poss.size()-1; i++) {
			GraphPosition a = poss.get(i);
			GraphPosition b = poss.get(i+1);
			
			double combo;
			double aCombo;
			double bCombo;
			if (a instanceof VertexPosition) {
				if (b instanceof VertexPosition) {
					assert false;
					aCombo = -1;
					bCombo = -1;
					combo = ((EdgePosition)gp).getCombo();
				} else {
					
					Edge e = (Edge)((EdgePosition)b).entity;
					
					if (gp.entity != e || gp.axis != b.axis) {
						continue;
					}
					
					combo = ((EdgePosition)gp).getCombo();
					
					if (((VertexPosition)a).v == b.entity.getVertices(b.axis).get(0)) {
						aCombo = 0.0;
						bCombo = ((EdgePosition)b).getCombo();
						if (!(DMath.lessThanEquals(aCombo, combo) && DMath.lessThanEquals(combo, bCombo))) {
							continue;
						}
					} else {
						assert ((VertexPosition)a).v == b.entity.getVertices(b.axis).get(1);
						aCombo = (e.pointCount()-1)+0.0;
						bCombo = ((EdgePosition)b).getCombo();
						if (!(DMath.greaterThanEquals(aCombo, combo) && DMath.greaterThanEquals(combo, bCombo))) {
							continue;
						}
					}
				}
				
			} else {
				if (b instanceof VertexPosition) {
					
					if (gp.entity == ((VertexPosition)b).v) {
						assert getGraphPosition(i+1, 0.0).equals(gp);
						if (DMath.greaterThanEquals(i+1+0.0, startingPosition.combo)) {
							return new GraphPositionPathPosition(this, i+1, 0.0);
						}
						
						continue;
					}
					
					Edge e = (Edge)((EdgePosition)a).entity;
					
					if (gp.entity != e || gp.axis != a.axis) {
						continue;
					}
					
					combo = ((EdgePosition)gp).getCombo();
					
					if (((VertexPosition)b).v == a.entity.getVertices(a.axis).get(0)) {
						aCombo = ((EdgePosition)a).getCombo();
						bCombo = 0.0;
						if (!(DMath.greaterThanEquals(aCombo, combo) && DMath.greaterThanEquals(combo, bCombo))) {
							continue;
						}
						
					} else {
						assert ((VertexPosition)b).v == a.entity.getVertices(a.axis).get(1);
						aCombo = ((EdgePosition)a).getCombo();
						bCombo = (e.pointCount()-1)+0.0;
						if (!(DMath.lessThanEquals(aCombo, combo) && DMath.lessThanEquals(combo, bCombo))) {
							continue;
						}
					}
					
				} else {
					
					Edge e = (Edge)((EdgePosition)a).entity;
					assert e == b.entity;
					assert a.axis == b.axis;
					
					if (gp.entity != e || gp.axis != a.axis) {
						continue;
					}
					
					combo = ((EdgePosition)gp).getCombo();
					
					aCombo = ((EdgePosition)a).getCombo();
					bCombo = ((EdgePosition)b).getCombo();
					if (DMath.lessThan(aCombo, bCombo)) {
						if (!(DMath.lessThanEquals(aCombo, combo) && DMath.lessThanEquals(combo, bCombo))) {
							continue;
						}
					} else {
						if (!(DMath.greaterThanEquals(aCombo, combo) && DMath.greaterThanEquals(combo, bCombo))) {
							continue;
						}
					}
					
				}
			}
			
			double abCombo = (combo - aCombo) / (bCombo - aCombo);
			assert DMath.lessThanEquals(0.0, abCombo) && DMath.lessThanEquals(abCombo, 1.0);
			
			int gpppIndex = (int)Math.floor(i + abCombo);
			double gpppParam = (i + abCombo)-gpppIndex;
			
			assert getGraphPosition(gpppIndex, gpppParam).equals(gp);
			
			if (DMath.greaterThanEquals(gpppIndex+gpppParam, startingPosition.combo)) {
				return new GraphPositionPathPosition(this, gpppIndex, gpppParam);
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
				
				Vertex choice = MODEL.world.shortestPathChoice(a.v, b.v);
				
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
						
						if ((aIsStart?(e.getDirection(Axis.NONE) != EdgeDirection.ENDTOSTART):(e.getDirection(Axis.NONE) != EdgeDirection.STARTTOEND))) {
							if (shortest == null || DMath.lessThan(e.getTotalLength(a.v, b.v), shortest.getTotalLength(a.v, b.v))) {
								shortest = e;
							}
						}
						
					} else {
						Merger m = (Merger)e;
						boolean aIsStart = a.v == m.top || a.v == m.left;
						
						if (a.v == m.top || a.v == m.bottom) {
							
							if ((aIsStart?(e.getDirection(Axis.TOPBOTTOM) != EdgeDirection.ENDTOSTART):(e.getDirection(Axis.TOPBOTTOM) != EdgeDirection.STARTTOEND))) {
								if (shortest == null || DMath.lessThan(e.getTotalLength(a.v, b.v), shortest.getTotalLength(a.v, b.v))) {
									shortest = e;
								}
							}
							
						} else {
							
							if ((aIsStart?(e.getDirection(Axis.LEFTRIGHT) != EdgeDirection.ENDTOSTART):(e.getDirection(Axis.LEFTRIGHT) != EdgeDirection.STARTTOEND))) {
								if (shortest == null || DMath.lessThan(e.getTotalLength(a.v, b.v), shortest.getTotalLength(a.v, b.v))) {
									shortest = e;
								}
							}
							
						}
						
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
			GraphPosition pen = acc.get(acc.size()-2);
			if (pen instanceof RoadPosition) {
				prev = ((RoadPosition)pen).r;
			} else {
				prev = ((MergerPosition)pen).m;
			}
		}
		
		Vertex choice = MODEL.world.randomPathChoice(prev, a.v, b.v);
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
				
				if ((aIsStart?(e.getDirection(Axis.NONE) != EdgeDirection.ENDTOSTART):(e.getDirection(Axis.NONE) != EdgeDirection.STARTTOEND))) {
					
				} else {
					toRemove.add(e);
				}
				
			} else {
				Merger m = (Merger)e;
				boolean aIsStart = a.v == m.top || a.v == m.left;
				
				if (a.v == m.top || a.v == m.bottom) {
					
					if ((aIsStart?(e.getDirection(Axis.TOPBOTTOM) != EdgeDirection.ENDTOSTART):(e.getDirection(Axis.TOPBOTTOM) != EdgeDirection.STARTTOEND))) {
						
					} else {
						toRemove.add(e);
					}
					
				} else {
					
					if ((aIsStart?(e.getDirection(Axis.LEFTRIGHT) != EdgeDirection.ENDTOSTART):(e.getDirection(Axis.LEFTRIGHT) != EdgeDirection.STARTTOEND))) {
						
					} else {
						toRemove.add(e);
					}
					
				}
				
			}
			
		}
		
		edges.removeAll(toRemove);
		
		int n = MODEL.world.RANDOM.nextInt(edges.size());
		Edge e = edges.get(n);
		fillin(acc, a, choicePos, e);
		
		calculateRandomPath(acc, choicePos, b);
		
	}
	
	private static void fillin(List<GraphPosition> acc, VertexPosition aa, VertexPosition bb, Edge e) {
		
		if (e instanceof Road) {
			Road r = (Road)e;
			
			if (aa.v == r.start) {
				assert bb.v == r.end;
				
				GraphPosition cur = RoadPosition.nextBoundfromStart(r);
				
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
				assert bb.v == r.start;
				assert aa.v == r.end;
				
				GraphPosition cur = RoadPosition.nextBoundfromEnd(r);
				
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
			
		} else {
			Merger m = (Merger)e;
			
			if (aa.v == m.top) {
				assert bb.v == m.bottom;
				
				GraphPosition cur = MergerPosition.nextBoundFromTop(m);
				
				acc.add(cur);
				
				while (true) {
					if (cur.equals(bb)) {
						break;
					}
					cur = cur.nextBoundToward(bb);
					assert cur.isBound();
					acc.add(cur);
				}
				
			} else if (aa.v == m.left) {
				assert bb.v == m.right;
				
				GraphPosition cur = MergerPosition.nextBoundFromLeft(m);
				
				acc.add(cur);
				
				while (true) {
					if (cur.equals(bb)) {
						break;
					}
					cur = cur.nextBoundToward(bb);
					assert cur.isBound();
					acc.add(cur);
				}
				
			} else if (aa.v == m.right) {
				assert bb.v == m.left;
				
				GraphPosition cur = MergerPosition.nextBoundFromRight(m);
				
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
				assert aa.v == m.bottom;
				assert bb.v == m.top;
				
				GraphPosition cur = MergerPosition.nextBoundFromBottom(m);
				
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
		
	}
	
	private boolean check() {
		for (int i = 0; i < poss.size(); i++) {
			GraphPosition a = poss.get(i);
			assert a.isBound();
		}
		return true;
	}
	
}
