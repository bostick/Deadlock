package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.math.DMath;

public class RushHourBoardPosition extends GraphPosition {
	
	public double rowCombo;
	public double colCombo;
	
	int rowIndex;
	int colIndex;
	
	double rowParam;
	double colParam;
	
	boolean bound;
	
	public RushHourBoardPosition(RushHourBoard b, double rowCombo, double colCombo) {
		super(b.point(rowCombo, colCombo), b);
		
		this.rowCombo = rowCombo;
		this.colCombo = colCombo;
		
		rowIndex = (int)rowCombo;
		colIndex = (int)colCombo;
		
		rowParam = rowCombo - rowIndex;
		colParam = colCombo - colIndex;
		
		bound = DMath.equals(rowParam, 0.0) && DMath.equals(colParam, 0.0);
	}
	
	public String toString() {
		return "board[r: " + rowCombo + ", c: " + colCombo + "]";
	}
	
	public boolean isBound() {
		return bound;
	}
	
	public GraphPosition approachNeighbor(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
		
		if (p instanceof VertexPosition) {
			
			Vertex v = (Vertex)p.entity;
			RushHourBoard b = (RushHourBoard)entity;
			
			RushHourBoardPosition vpos = b.position(v.p);
			
			double boardDistance = distance / RushHourStud.SIZE;
			
			if (DMath.equals(vpos.colCombo, colCombo)) {
				/*
				 * same col
				 */
				
				if (DMath.lessThan(vpos.rowCombo, rowCombo)) {
					
					return new RushHourBoardPosition(b, rowCombo - boardDistance, colCombo);
					
				} else {
					assert DMath.greaterThan(vpos.rowCombo, rowCombo);
					
					return new RushHourBoardPosition(b, rowCombo + boardDistance, colCombo);
				}
				
			} else {
				assert DMath.equals(vpos.rowCombo, rowCombo);
				/*
				 * same row
				 */
				
				if (DMath.lessThan(vpos.colCombo, colCombo)) {
					
					return new RushHourBoardPosition(b, rowCombo, colCombo - boardDistance);
					
				} else {
					assert DMath.greaterThan(vpos.colCombo, colCombo);
					
					return new RushHourBoardPosition(b, rowCombo, colCombo + boardDistance);
				}
				
			}
			
		} else {
			
			RushHourBoardPosition bp = (RushHourBoardPosition)p;
			
			assert entity == bp.entity;
			
			double studDist = distance / RushHourStud.SIZE;
			
			if (DMath.equals(bp.rowCombo, rowCombo)) {
				/*
				 * same row
				 */
				
				if (bp.colCombo < colCombo) {
					
					double newCombo = colCombo - studDist;
					assert DMath.greaterThanEquals(newCombo, bp.colCombo);
					
					return new RushHourBoardPosition((RushHourBoard)entity, rowCombo, newCombo);
					
				} else {
					assert !DMath.equals(bp.colCombo, colCombo);
					
					double newCombo = colCombo + studDist;
					assert DMath.lessThanEquals(newCombo, bp.colCombo);
					
					return new RushHourBoardPosition((RushHourBoard)entity, rowCombo, newCombo);
				}
				
			} else {
				assert DMath.equals(bp.colCombo, colCombo);
				/*
				 * same column
				 */
				
				if (bp.rowCombo < rowCombo) {
					
					double newCombo = rowCombo - studDist;
					assert DMath.greaterThanEquals(newCombo, bp.rowCombo);
					
					return new RushHourBoardPosition((RushHourBoard)entity, newCombo, colCombo);
					
				} else {
					assert !DMath.equals(bp.rowCombo, rowCombo);
					
					double newCombo = rowCombo + studDist;
					assert DMath.lessThanEquals(newCombo, bp.rowCombo);
					
					return new RushHourBoardPosition((RushHourBoard)entity, newCombo, colCombo);
				}
				
			}
			
		}
		
	}
	
	public double goalGPPPCombo(int curPathIndex, double curPathParam, boolean pathForward, GraphPosition goalGP, GraphPosition nextBoundGP) {
		
		RushHourBoardPosition goalBP = (RushHourBoardPosition)goalGP;
		
		assert false;
		return -1;
		
	}

}
