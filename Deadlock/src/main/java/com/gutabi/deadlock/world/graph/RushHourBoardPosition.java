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
	
	public boolean isBound() {
		return bound;
	}
	
	public GraphPosition travelToNeighbor(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
		assert p.isBound();
		
		RushHourBoardPosition bp = (RushHourBoardPosition)p;
		
		assert entity == bp.entity;
		
		double studDist = distance / RushHourStud.SIZE;
		
		if (bp.rowIndex == rowIndex) {
			assert DMath.equals(rowParam, 0.0);
			
			if (bp.colCombo < colCombo) {
				
				double newCombo = colCombo - studDist;
				assert DMath.greaterThanEquals(newCombo, bp.colCombo);
				
				return new RushHourBoardPosition((RushHourBoard)entity, rowIndex, newCombo);
				
			} else {
				assert !DMath.equals(bp.colCombo, colCombo);
				
				double newCombo = colCombo + studDist;
				assert DMath.lessThanEquals(newCombo, bp.colCombo);
				
				return new RushHourBoardPosition((RushHourBoard)entity, rowIndex, newCombo);
			}
			
		} else {
			assert bp.colIndex == colIndex;
			assert DMath.equals(colParam, 0.0);
			
			if (bp.rowCombo < rowCombo) {
				
				double newCombo = rowCombo - studDist;
				assert DMath.greaterThanEquals(newCombo, bp.rowCombo);
				
				return new RushHourBoardPosition((RushHourBoard)entity, newCombo, colIndex);
				
			} else {
				assert !DMath.equals(bp.rowCombo, rowCombo);
				
				double newCombo = rowCombo + studDist;
				assert DMath.lessThanEquals(newCombo, bp.rowCombo);
				
				return new RushHourBoardPosition((RushHourBoard)entity, newCombo, colIndex);
			}
			
		}
		
	}
	
}
