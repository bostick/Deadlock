package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.util.ArrayList;
import java.util.List;

/**
 * Purely spatial 
 */
public class Path {
	
	private Position start;
	private Position end;
	
	List<Position> origPoss;
	List<Position> poss = new ArrayList<Position>();
	
	int hash;
	
	public Path(List<Position> origPoss) {
		
		this.origPoss = origPoss;
		
		poss.add(origPoss.get(0));
		for (int i = 0; i < origPoss.size()-1; i++) {
			Position a = origPoss.get(i);
			Position b = origPoss.get(i+1);
			calculatePath(a, b);
		}
		
		int h = 17;
		h = 37 * h + origPoss.hashCode();
		hash = h;
		
		this.start = poss.get(0);
		this.end = poss.get(poss.size()-1);
	}
	
	public int hashCode() {
		return hash;
	}
	
	/**
	 * Position a has already been added
	 * 
	 * add all Positions up to b, inclusive
	 */
	private void calculatePath(Position a, Position b) {
		
		if (a instanceof EdgePosition) {
			EdgePosition aa = (EdgePosition)a;
			
			if (b instanceof EdgePosition) {
				EdgePosition bb = (EdgePosition)b;
				
				if (aa.getEdge() == bb.getEdge()) {
					
					if (aa.equals(bb)) {
						
						/*
						 * TODO: fix this, space-only Paths shouldn't have repeated points, but it makes debugging easier for now
						 */
						poss.add(bb);
						
					} else if (aa.getIndex() == bb.getIndex()) {
						
						poss.add(bb);
						
					} else if (aa.nextBoundToward(bb).equals(bb)) {
						
						poss.add(bb);
						
					} else if (bb.nextBoundToward(aa).equals(aa)) {
						
						poss.add(bb);
						
					} else {
						
						Position bEnd;
						if (!bb.isBound()) {
							bEnd = bb.nextBoundToward(aa);
						} else {
							bEnd = bb;
						}
						
						Position cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bEnd);
							poss.add(cur);
							if (cur.equals(bEnd)) {
								break;
							}
						}
						if (!bEnd.equals(bb)) {
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
						
						poss.add(bb);
						
					} else {
						
						Position cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bb);
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
						
						poss.add(bb);
						
					} else {
						
						Position bEnd;
						if (!bb.isBound()) {
							bEnd = bb.nextBoundToward(aa);
						} else {
							bEnd = bb;
						}
						
						Position cur = aa;
						while (true) {
							cur = cur.nextBoundToward(bEnd);
							poss.add(cur);
							if (cur.equals(bEnd)) {
								break;
							}
						}
						if (!bEnd.equals(bb)) {
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
					
					/*
					 * TODO: fix this, space-only Paths shouldn't have repeated points, but it makes debugging easier for now
					 */
					poss.add(bb);
					
				} else {
					List<Connector> coms = Vertex.commonConnectors(aa, bb);
					
					if (coms.isEmpty()) {
						
						Vertex choice = MODEL.shortestPathChoice(aa, bb);
						
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
							
							Position cur = EdgePosition.nextBoundfromStart(shortest);
							poss.add(cur);
							
							while (true) {
								cur = cur.nextBoundToward(bb);
								poss.add(cur);
								if (cur.equals(bb)) {
									break;
								}
							}
							
						} else {
							
							Position cur = EdgePosition.nextBoundfromEnd(shortest);
							poss.add(cur);
							
							while (true) {
								cur = cur.nextBoundToward(bb);
								poss.add(cur);
								if (cur.equals(bb)) {
									break;
								}
							}
							
						}
					}
				}
				
			}
			
		}
		
	}
	
}
