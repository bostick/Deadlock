package com.gutabi.deadlock.core.path;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Connector;
import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Driveable;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.EdgePosition;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;

/**
 * Purely spatial 
 */
public class GraphPositionPath {
	
	private GraphPosition start;
	private GraphPosition end;
	
//	List<? extends GraphPosition> origPoss;
	List<GraphPosition> poss;
	
	public final double[] cumulativeDistancesFromStart;
	private final double totalLength;
	
	int hash;
	
	private GraphPositionPath(List<GraphPosition> poss) {
		
		this.poss = poss;
		
		int h = 17;
		h = 37 * h + poss.hashCode();
		hash = h;
		
		this.start = poss.get(0);
		this.end = poss.get(poss.size()-1);
		
		cumulativeDistancesFromStart = new double[poss.size()];
		
		double length;
		double l = 0.0;
		for (int i = 0; i < poss.size(); i++) {
			if (i == 0) {
				cumulativeDistancesFromStart[i] = 0;
			} else {
				Point a  = poss.get(i-1).getPoint();
				Point b = poss.get(i).getPoint();
				length = Point.distance(a, b);
				l += length;
				cumulativeDistancesFromStart[i] = cumulativeDistancesFromStart[i-1] + length;
			}
		}
		
		totalLength = l;
		
		assert check();
	}
	
	public static GraphPositionPath createPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (MODEL.world.graph.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
				return null;
			}
		}
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
		
		assert origPoss.get(0).isBound();
		poss.add(origPoss.get(0));
		for (int i = 0; i < origPoss.size()-1; i++) {
			GraphPosition a = origPoss.get(i);
			GraphPosition b = origPoss.get(i+1);
			calculatePath(poss, a, b);
		}
		
		return new GraphPositionPath(poss);
	}
	
	public int hashCode() {
		return hash;
	}
	
	
	
	public GraphPosition getStart() {
		return start;
	}
	
	public GraphPosition getEnd() {
		return end;
	}
	
	public int size() {
		return poss.size();
	}
	
	public double getTotalLength() {
		return totalLength;
	}
	
//	public Point getPoint(int index) {
//		return poss.get(index).getPoint();
//	}
	
	public GraphPosition getGraphPosition(int index) {
		return poss.get(index);
	}
	
	public GraphPositionPathPosition hitTest(GraphPosition pos) {
		
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
					return new GraphPositionPathPosition(this, i, ep.getParam());
				} else if (ep1.equals(test2) && ep2.equals(test1)) {
					return new GraphPositionPathPosition(this, i, 1-ep.getParam());
				}
			}
			return null;
		}
		
	}
	
	/**
	 * 
	 */
	public GraphPosition getGraphPosition(int pathIndex, double pathParam) {
		
		GraphPosition p1 = poss.get(pathIndex);
		
		if (DMath.equals(pathParam, 0.0)) {
			if (p1 instanceof EdgePosition) {
				assert DMath.equals(((EdgePosition)p1).getParam(), pathParam);
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
				
				if (pp1.getIndex() < pp2.getIndex() || (pp1.getIndex() == pp2.getIndex() && DMath.lessThan(pp1.getParam(), pp2.getParam()))) {
					// same direction as edge
					
					graphIndex = pp1.getIndex();
					graphParam = pathParam;
					
				} else {
					
					graphIndex = pp2.getIndex();
					graphParam = 1-pathParam;
					
				}
				
			} else {
				Vertex pp2 = (Vertex)p2;
				
				if (pp2 == pp1.getEdge().getEnd()) {
					// same direction as edge
					
					graphIndex = pp1.getIndex();
					graphParam = pathParam;
					
				} else {
					
					graphIndex = 0;
					graphParam = 1-pathParam;
					
				}
				
			}
			
			EdgePosition newE = new EdgePosition(((EdgePosition)p1).getEdge(), graphIndex, graphParam);
			
			return newE;
			
		} else {
			Vertex pp1 = (Vertex)p1;
			
			if (p2 instanceof EdgePosition) {
				EdgePosition pp2 = (EdgePosition)p2;
				
				EdgePosition p2e = (EdgePosition)p2;
				
				if (pp1 == pp2.getEdge().getStart()) {
					// same direction as edge
					
					graphIndex = 0;
					graphParam = pathParam;
					
				} else {
					
					graphIndex = pp2.getIndex();
					graphParam = 1-pathParam;
					
				}
				
				EdgePosition newE = new EdgePosition(p2e.getEdge(), graphIndex, graphParam);
				
				return newE;
				
			} else {
				
				Vertex pp2 = (Vertex)p2;
				
				List<Connector> cons = Vertex.commonConnectors(pp1, pp2);
				
				Edge e = null;
				
				for (Connector c : cons) {
					Edge ce = (Edge)c;
					if (ce.size() == 2) {
						e = ce;
						break;
					}
				}
				assert e != null;
				
				graphParam = (pp1 == e.getStart()) ? pathParam : 1-pathParam;
				
				Vertex p1v = (Vertex)p1;
				
				if (p1v == e.getStart()) {
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
	
	public Driveable getDriveable(int index) {
		return poss.get(index).getDriveable();
	}
	
	/**
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private static void calculatePath(List<GraphPosition> acc, GraphPosition a, GraphPosition b) {
		
		if (a instanceof EdgePosition) {
			EdgePosition aa = (EdgePosition)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				if (aa.getEdge() == bb.getEdge()) {
					
					if (aa.equals(bb)) {
						
						assert false;
						
					} else if (aa.getIndex() == bb.getIndex()) {
						
						assert bb.isBound();
						acc.add(bb);
						
					} else if (aa.nextBoundToward(bb).equals(bb)) {
						
						assert bb.isBound();
						acc.add(bb);
						
					} else if (bb.nextBoundToward(aa).equals(aa)) {
						
						assert bb.isBound();
						acc.add(bb);
						
					} else {
						
						GraphPosition bEnd;
						if (!bb.isBound()) {
							bEnd = bb.nextBoundToward(aa);
						} else {
							bEnd = bb;
						}
						
						GraphPosition cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bEnd);
							assert cur.isBound();
							acc.add(cur);
							if (cur.equals(bEnd)) {
								break;
							}
						}
						if (!bEnd.equals(bb)) {
							assert bb.isBound();
							acc.add(bb);
						}
					
					}
					
				} else {
					// different edges
					
					Vertex aStart = aa.getEdge().getStart();
					Vertex aEnd = aa.getEdge().getEnd();
					
					double aStartDist = aa.distanceToStartOfEdge() + aStart.distanceTo(b);
					double aEndDist = aa.distanceToEndOfEdge() + aEnd.distanceTo(b);
					
					if (aStartDist == Double.POSITIVE_INFINITY || aEndDist == Double.POSITIVE_INFINITY) {
						
						throw new IllegalArgumentException();
						
					} else if (DMath.lessThanEquals(aStartDist, aEndDist)) {
						
						calculatePath(acc, aa, aStart);
						calculatePath(acc, aStart, bb);
						
					} else {
						
						calculatePath(acc, aa, aEnd);
						calculatePath(acc, aEnd, bb);
						
					}
					
				}
				
			} else {
				Vertex bb = (Vertex)b;
				
				if (bb == aa.getEdge().getStart() || bb == aa.getEdge().getEnd()) {
					
					if (aa.nextBoundToward(bb).equals(bb)) {
						
						assert bb.isBound();
						acc.add(bb);
						
					} else {
						
						GraphPosition cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bb);
							assert cur.isBound();
							acc.add(cur);
							if (cur.equals(bb)) {
								break;
							}
						}
						
					}
					
				} else {
					// not connected
					
					Vertex aStart = aa.getEdge().getStart();
					Vertex aEnd = aa.getEdge().getEnd();
					
					double aStartDist = aa.distanceToStartOfEdge() + aStart.distanceTo(b);
					double aEndDist = aa.distanceToEndOfEdge() + aEnd.distanceTo(b);
					
					if (aStartDist == Double.POSITIVE_INFINITY || aEndDist == Double.POSITIVE_INFINITY) {
						
						throw new IllegalArgumentException();
						
					} else if (DMath.lessThanEquals(aStartDist, aEndDist)) {
						
						calculatePath(acc, aa, aStart);
						calculatePath(acc, aStart, bb);
						
					} else {
						
						calculatePath(acc, aa, aEnd);
						calculatePath(acc, aEnd, bb);
						
					}
					
				}
				
			}
			
		} else {
			Vertex aa = (Vertex)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				if (aa == bb.getEdge().getStart() || aa == bb.getEdge().getEnd()) {
					
					if (bb.nextBoundToward(aa).equals(aa)) {
						
						assert bb.isBound();
						acc.add(bb);
						
					} else {
						
						GraphPosition bEnd;
						if (!bb.isBound()) {
							bEnd = bb.nextBoundToward(aa);
						} else {
							bEnd = bb;
						}
						
						GraphPosition cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bEnd);
							assert cur.isBound();
							acc.add(cur);
							if (cur.equals(bEnd)) {
								break;
							}
						}
						if (!bEnd.equals(bb)) {
							assert bb.isBound();
							acc.add(bb);
						}
						
					}
					
				} else {
					// not connected
					
					Vertex bStart = bb.getEdge().getStart();
					Vertex bEnd = bb.getEdge().getEnd();
					
					double bStartDist = bb.distanceToStartOfEdge() + bStart.distanceTo(a);
					double bEndDist = bb.distanceToEndOfEdge() + bEnd.distanceTo(a);
					
					if (bStartDist == Double.POSITIVE_INFINITY || bEndDist == Double.POSITIVE_INFINITY) {
						
						throw new IllegalArgumentException();
						
					} else if (DMath.lessThanEquals(bStartDist, bEndDist)) {
						
						calculatePath(acc, aa, bStart);
						calculatePath(acc, bStart, bb);
						
					} else {
						
						calculatePath(acc, aa, bEnd);
						calculatePath(acc, bEnd, bb);
						
					}
					
				}
				
			} else {
				Vertex bb = (Vertex)b;
				
				if (aa == bb) {
					
					assert false;
					
				} else {
					List<Connector> coms = Vertex.commonConnectors(aa, bb);
					
					if (coms.isEmpty()) {
						
						Vertex choice = MODEL.world.graph.shortestPathChoice(aa, bb);
						
						if (choice == null) {
							
							throw new IllegalArgumentException();
							
						} else {
							
							calculatePath(acc, aa, choice);
							calculatePath(acc, choice, bb);
							
						}
						
					} else {
						
						Edge shortest = null;
						for (Connector c : coms) {
							Edge e = (Edge)c;
							if (shortest == null || DMath.lessThan(e.getTotalLength(), shortest.getTotalLength())) {
								shortest = e;
							}
						}
						
						if (aa == shortest.getStart()) {
							
							GraphPosition cur = EdgePosition.nextBoundfromStart(shortest);
								
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
							
							GraphPosition cur = EdgePosition.nextBoundfromEnd(shortest);
							
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