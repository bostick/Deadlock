package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

/**
 * Purely spatial 
 */
public class Path {
	
	private GraphPosition start;
	private GraphPosition end;
	
	List<? extends GraphPosition> origPoss;
	List<GraphPosition> poss = new ArrayList<GraphPosition>();
	
	final double[] cumulativeDistancesFromStart;
	private final double totalLength;
	
	int hash;
	
	private Path(List<? extends GraphPosition> origPoss) {
		
		this.origPoss = origPoss;
		
		assert origPoss.get(0).isBound();
		poss.add(origPoss.get(0));
		for (int i = 0; i < origPoss.size()-1; i++) {
			GraphPosition a = origPoss.get(i);
			GraphPosition b = origPoss.get(i+1);
			calculatePath(a, b);
		}
		
		int h = 17;
		h = 37 * h + origPoss.hashCode();
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
	
	public static Path createPathFromSkeleton(List<Vertex> origPoss) {
		
		for (int i = 0; i < origPoss.size()-1; i++) {
			Vertex a = origPoss.get(i);
			Vertex b = origPoss.get(i+1);
			if (MODEL.world.distanceBetweenVertices(a, b) == Double.POSITIVE_INFINITY) {
				return null;
			}
		}
		
		return new Path(origPoss);
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
	
	public Point getPoint(int index) {
		return poss.get(index).getPoint();
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
	private void calculatePath(GraphPosition a, GraphPosition b) {
		
		if (a instanceof EdgePosition) {
			EdgePosition aa = (EdgePosition)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				if (aa.getEdge() == bb.getEdge()) {
					
					if (aa.equals(bb)) {
						
						assert false;
						
					} else if (aa.getIndex() == bb.getIndex()) {
						
						assert bb.isBound();
						poss.add(bb);
						
					} else if (aa.nextBoundToward(bb).equals(bb)) {
						
						assert bb.isBound();
						poss.add(bb);
						
					} else if (bb.nextBoundToward(aa).equals(aa)) {
						
						assert bb.isBound();
						poss.add(bb);
						
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
							poss.add(cur);
							if (cur.equals(bEnd)) {
								break;
							}
						}
						if (!bEnd.equals(bb)) {
							assert bb.isBound();
							poss.add(bb);
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
						
						calculatePath(aa, aStart);
						calculatePath(aStart, bb);
						
					} else {
						
						calculatePath(aa, aEnd);
						calculatePath(aEnd, bb);
						
					}
					
				}
				
			} else {
				Vertex bb = (Vertex)b;
				
				if (bb == aa.getEdge().getStart() || bb == aa.getEdge().getEnd()) {
					
					if (aa.nextBoundToward(bb).equals(bb)) {
						
						assert bb.isBound();
						poss.add(bb);
						
					} else {
						
						GraphPosition cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bb);
							assert cur.isBound();
							poss.add(cur);
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
						
						calculatePath(aa, aStart);
						calculatePath(aStart, bb);
						
					} else {
						
						calculatePath(aa, aEnd);
						calculatePath(aEnd, bb);
						
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
						poss.add(bb);
						
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
							poss.add(cur);
							if (cur.equals(bEnd)) {
								break;
							}
						}
						if (!bEnd.equals(bb)) {
							assert bb.isBound();
							poss.add(bb);
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
						
						calculatePath(aa, bStart);
						calculatePath(bStart, bb);
						
					} else {
						
						calculatePath(aa, bEnd);
						calculatePath(bEnd, bb);
						
					}
					
				}
				
			} else {
				Vertex bb = (Vertex)b;
				
				if (aa == bb) {
					
					assert false;
					
				} else {
					List<Connector> coms = Vertex.commonConnectors(aa, bb);
					
					if (coms.isEmpty()) {
						
						Vertex choice = MODEL.world.shortestPathChoice(aa, bb);
						
						if (choice == null) {
							
							throw new IllegalArgumentException();
							
						} else {
							
							calculatePath(aa, choice);
							calculatePath(choice, bb);
							
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
								
							poss.add(cur);
							
							while (true) {
								if (cur.equals(bb)) {
									break;
								}
								cur = cur.nextBoundToward(bb);
								assert cur.isBound();
								poss.add(cur);
							}
							
						} else {
							
							GraphPosition cur = EdgePosition.nextBoundfromEnd(shortest);
							
							poss.add(cur);
							
							while (true) {
								
								if (cur.equals(bb)) {
									break;
								}
								
								cur = cur.nextBoundToward(bb);
								assert cur.isBound();
								poss.add(cur);
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
