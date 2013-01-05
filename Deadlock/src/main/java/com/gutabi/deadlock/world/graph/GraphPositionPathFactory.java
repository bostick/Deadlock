package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.DMath;

public class GraphPositionPathFactory {
	
	public final Graph graph;
	
	public GraphPositionPathFactory(Graph graph) {
		this.graph = graph;
	}
	
	public GraphPositionPath createShortestVertexPath(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (graph.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
				return null;
			}
		}
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
		
		poss.add(new VertexPosition(origPoss.get(0)));
		for (int i = 0; i < origPoss.size()-1; i++) {
			VertexPosition a = new VertexPosition(origPoss.get(i));
			VertexPosition b = new VertexPosition(origPoss.get(i+1));
			calculateShortestVertexPath(poss, a, b);
		}
		
		return new GraphPositionPath(poss);
	}
	
	public GraphPositionPath createRandomVertexPath(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (graph.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
				return null;
			}
		}
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
		
		poss.add(new VertexPosition(origPoss.get(0)));
		for (int i = 0; i < origPoss.size()-1; i++) {
			VertexPosition a = new VertexPosition(origPoss.get(i));
			VertexPosition b = new VertexPosition(origPoss.get(i+1));
			calculateRandomVertexPath(poss, a, b);
		}
		
		return new GraphPositionPath(poss);
	}
	
//	public GraphPositionPath createShortestStudPath(List<RushHourStud> origPoss) {
//		
//		List<GraphPosition> poss = new ArrayList<GraphPosition>();
//		
//		RushHourStud s0 = origPoss.get(0);
//		poss.add(new RushHourBoardPosition(s0.board, s0.row, s0.col));
//		for (int i = 0; i < origPoss.size()-1; i++) {
//			RushHourStud a = origPoss.get(i);
//			RushHourStud b = origPoss.get(i+1);
//			calculateShortestStudPath(poss, new RushHourBoardPosition(a.board, a.row, a.col), new RushHourBoardPosition(b.board, b.row, b.col));
//		}
//		
//		return new GraphPositionPath(poss);
//	}
	
	/**
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private void calculateShortestVertexPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
		if (a.equals(b)) {
			
			assert false;
			
		} else {
			List<Edge> edges = Vertex.commonEdges(a.v, b.v);
			
			if (edges.isEmpty()) {
				
				Vertex choice = graph.shortestPathChoice(a.v, b.v);
				
				if (choice == null) {
					
					throw new IllegalArgumentException();
					
				} else {
					
					calculateShortestVertexPath(acc, a, new VertexPosition(choice));
					calculateShortestVertexPath(acc, new VertexPosition(choice), b);
					
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
	private void calculateRandomVertexPath(List<GraphPosition> acc, VertexPosition a, VertexPosition b) {
		
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
		
		Vertex choice = graph.randomPathChoice(prev, a.v, b.v);
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
		
		int n = APP.RANDOM.nextInt(edges.size());
		Edge e = edges.get(n);
		int dir;
		if (a.v == choicePos.v) {
			dir = APP.RANDOM.nextInt(2);
		} else {
			dir = 2;
		}
		fillin(acc, a, choicePos, e, dir);
		
		calculateRandomVertexPath(acc, choicePos, b);
		
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
