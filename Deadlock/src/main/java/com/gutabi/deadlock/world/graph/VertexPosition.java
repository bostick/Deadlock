package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.math.DMath;

public class VertexPosition extends GraphPosition {
	
	public final Vertex v;
	
	private int hash;
	
	public VertexPosition(Vertex v) {
		super(v.p, v);
		this.v = v;
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + v.hashCode();
			hash = h;
		}
		return hash;
	}
	
	public Entity getEntity() {
		return v;
	}
	
	public boolean isBound() {
		return true;
	}
	
	public String toString() {
		return v.toString();
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof VertexPosition)) {
			return false;
		} else {
			VertexPosition b = (VertexPosition)o;
			return (v == b.v);
		}
	}
	
	public GraphPosition approachNeighbor(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
		
		if (p instanceof EdgePosition) {
			EdgePosition pe = (EdgePosition)p;
			
			if (v == ((Edge)pe.entity).getReferenceVertex(pe.axis)) {
				
				return ((Edge)pe.entity).travelFromReferenceVertex(pe.axis, distance);
				
			} else {
				assert v == ((Edge)pe.entity).getOtherVertex(pe.axis);
				
				return ((Edge)pe.entity).travelFromOtherVertex(pe.axis, distance);
			}
		} else {
			assert p instanceof RushHourBoardPosition;
			
			RushHourBoardPosition rpos = (RushHourBoardPosition)p;
			
			RushHourBoard b = (RushHourBoard)rpos.entity;
			
			double boardDistance = distance / RushHourStud.SIZE;
			
			RushHourBoardPosition vpos = b.position(v.p);
			
			if (DMath.equals(vpos.colCombo, rpos.colCombo)) {
				/*
				 * same col
				 */
				
				if (DMath.lessThan(vpos.rowCombo, rpos.rowCombo)) {
					
					return new RushHourBoardPosition(b, vpos.rowCombo + boardDistance, vpos.colCombo);
					
				} else {
					assert DMath.greaterThan(vpos.rowCombo, rpos.rowCombo);
					
					return new RushHourBoardPosition(b, vpos.rowCombo - boardDistance, vpos.colCombo);
				}
				
			} else {
				assert DMath.equals(vpos.rowCombo, rpos.rowCombo);
				/*
				 * same row
				 */
				
				if (DMath.lessThan(vpos.colCombo, rpos.colCombo)) {
					
					return new RushHourBoardPosition(b, vpos.rowCombo, vpos.colCombo + boardDistance);
					
				} else {
					assert DMath.greaterThan(vpos.colCombo, rpos.colCombo);
					
					return new RushHourBoardPosition(b, vpos.rowCombo, vpos.colCombo - boardDistance);
				}
				
			}
			
		}
		
	}
	
	public double goalGPPPCombo(int curPathIndex, double curPathParam, boolean pathForward, GraphPosition goalGP, GraphPositionPath debugPath, GraphPositionPathPosition debugPos, double debugDist) {
		assert curPathParam == 0.0;
		
		if (goalGP instanceof EdgePosition) {
			EdgePosition goalEP = (EdgePosition)goalGP;
			
			Edge edge = (Edge)goalEP.entity;
			
			Vertex nextBoundRefVertex = edge.getReferenceVertex(goalEP.axis);
			Vertex nextBoundOtherVertex = edge.getOtherVertex(goalEP.axis);
			
			if (pathForward ? v == nextBoundRefVertex : v == nextBoundOtherVertex) {
				// same direction as edge
				int retIndex = pathForward ? curPathIndex : curPathIndex-1;
				double retParam = goalEP.getParam();
				
				return retIndex+retParam;
				
			} else {
				assert pathForward ? v == nextBoundOtherVertex : v == nextBoundRefVertex;
				int retIndex = pathForward ? curPathIndex : curPathIndex-1;
				double retParam = 1-goalEP.getParam();
				
				return retIndex+retParam;
			}
			
		} else {
			assert goalGP instanceof RushHourBoardPosition;
			
			RushHourBoardPosition goalBP = (RushHourBoardPosition)goalGP;
			
			RushHourBoard board = (RushHourBoard)goalBP.entity;
			
			RushHourBoardPosition vpos = board.position(v.p);
			
			if (DMath.equals(vpos.colCombo, goalBP.colCombo)) {
				assert DMath.equals(vpos.colCombo, goalBP.colCombo);
				/*
				 * same col
				 */
				
				if (vpos.rowCombo < goalBP.rowCombo) {
					/*
					 * same direction as board
					 */
					int retIndex = pathForward ? curPathIndex : curPathIndex-1;
					double retParam = pathForward ? goalBP.rowParam : 1-goalBP.rowParam;
					
					return retIndex+retParam;
					
				} else {
					
					int retIndex = pathForward ? curPathIndex : curPathIndex-1;
					double retParam = pathForward ? 1-goalBP.rowParam : goalBP.rowParam;
					
					return retIndex+retParam;
				}
				
			} else {
				assert DMath.equals(vpos.rowCombo, goalBP.rowCombo);
				/*
				 * same row
				 */
				if (vpos.colCombo < goalBP.colCombo) {
					/*
					 * same direction as board
					 */
					int retIndex = pathForward ? curPathIndex : curPathIndex-1;
					double retParam = pathForward ? goalBP.colParam : 1-goalBP.colParam;
					
					return retIndex+retParam;
					
				} else {
					
					int retIndex = pathForward ? curPathIndex : curPathIndex-1;
					double retParam = pathForward ? 1-goalBP.colParam : goalBP.colParam;
					
					return retIndex+retParam;
				}

			}
		}
		
	}
	
}
