package com.gutabi.deadlock.world.graph;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.world.World;

@SuppressWarnings("static-access")
public class GraphPositionPathFactory {
	
	public final World world;
	
	public GraphPositionPathFactory(World world) {
		this.world = world;
	}
	
	public GraphPositionPath createShortestPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (world.graph.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
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
	
	public GraphPositionPath createRandomPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (world.graph.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
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
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private void calculateShortestPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
		if (a.equals(b)) {
			
			assert false;
			
		} else {
			List<Edge> edges = Vertex.commonEdges(a.v, b.v);
			
			if (edges.isEmpty()) {
				
				Vertex choice = world.graph.shortestPathChoice(a.v, b.v);
				
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
	private void calculateRandomPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
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
		
		Vertex choice = world.graph.randomPathChoice(prev, a.v, b.v);
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
		
		int n = world.RANDOM.nextInt(edges.size());
		Edge e = edges.get(n);
		int dir;
		if (a.v == choicePos.v) {
			dir = world.RANDOM.nextInt(2);
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
	
}
