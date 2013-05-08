package com.gutabi.capsloc.world.graph;

import com.gutabi.capsloc.math.DMath;

public class BypassBoardPosition extends GraphPosition {
	
	public final double rowCombo;
	public final double colCombo;
	
	final int rowIndex;
	final int colIndex;
	
	final double rowParam;
	final double colParam;
	
	final boolean bound;
	
	private int hash;
	
	public BypassBoardPosition(BypassBoard b, double rowCombo, double colCombo) {
		super(b.point(rowCombo, colCombo), b);
		
		this.rowCombo = rowCombo;
		this.colCombo = colCombo;
		
		rowIndex = (int)Math.floor(rowCombo);
		colIndex = (int)Math.floor(colCombo);
		
		rowParam = rowCombo - rowIndex;
		colParam = colCombo - colIndex;
		
		if (DMath.lessThan(rowParam, 0.0) || DMath.greaterThanEquals(rowParam, 1.0)) {
			throw new IllegalArgumentException();
		}
		if (DMath.lessThan(colParam, 0.0) || DMath.greaterThanEquals(colParam, 1.0)) {
			throw new IllegalArgumentException();
		}
		
		bound = DMath.equals(rowParam, 0.0) && DMath.equals(colParam, 0.0);
	}
	
	public String toString() {
		return "board[r: " + rowCombo + ", c: " + colCombo + "]";
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof BypassBoardPosition)) {
			return false;
		} else {
			BypassBoardPosition b = (BypassBoardPosition)o;
			return DMath.equals(rowCombo, b.rowCombo) && DMath.equals(colCombo, b.colCombo);
		}
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + entity.hashCode();
			long l = Double.doubleToLongBits(rowCombo);
			int c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			l = Double.doubleToLongBits(colCombo);
			c = (int)(l ^ (l >>> 32));
			h = 37 * h + c;
			hash = h;
		}
		return hash;
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
			BypassBoard b = (BypassBoard)entity;
			
			BypassBoardPosition vpos = b.position(v.p);
			
			double boardDistance = distance / BypassStud.SIZE;
			
			if (DMath.equals(vpos.colCombo, colCombo)) {
				/*
				 * same col
				 */
				
				if (DMath.lessThan(vpos.rowCombo, rowCombo)) {
					
					return new BypassBoardPosition(b, rowCombo - boardDistance, colCombo);
					
				} else {
					assert DMath.greaterThan(vpos.rowCombo, rowCombo);
					
					return new BypassBoardPosition(b, rowCombo + boardDistance, colCombo);
				}
				
			} else {
				assert DMath.equals(vpos.rowCombo, rowCombo);
				/*
				 * same row
				 */
				
				if (DMath.lessThan(vpos.colCombo, colCombo)) {
					
					return new BypassBoardPosition(b, rowCombo, colCombo - boardDistance);
					
				} else {
					assert DMath.greaterThan(vpos.colCombo, colCombo);
					
					return new BypassBoardPosition(b, rowCombo, colCombo + boardDistance);
				}
				
			}
			
		} else {
			
			BypassBoardPosition bp = (BypassBoardPosition)p;
			
			assert entity == bp.entity;
			
			double studDist = distance / BypassStud.SIZE;
			
			if (DMath.equals(bp.rowCombo, rowCombo)) {
				/*
				 * same row
				 */
				
				if (bp.colCombo < colCombo) {
					
					double newCombo = colCombo - studDist;
					assert DMath.greaterThanEquals(newCombo, bp.colCombo);
					
					return new BypassBoardPosition((BypassBoard)entity, rowCombo, newCombo);
					
				} else {
					assert !DMath.equals(bp.colCombo, colCombo);
					
					double newCombo = colCombo + studDist;
					assert DMath.lessThanEquals(newCombo, bp.colCombo);
					
					return new BypassBoardPosition((BypassBoard)entity, rowCombo, newCombo);
				}
				
			} else {
				assert DMath.equals(bp.colCombo, colCombo);
				/*
				 * same column
				 */
				
				if (bp.rowCombo < rowCombo) {
					
					double newCombo = rowCombo - studDist;
					assert DMath.greaterThanEquals(newCombo, bp.rowCombo);
					
					return new BypassBoardPosition((BypassBoard)entity, newCombo, colCombo);
					
				} else {
					assert !DMath.equals(bp.rowCombo, rowCombo);
					
					double newCombo = rowCombo + studDist;
					assert DMath.lessThanEquals(newCombo, bp.rowCombo);
					
					return new BypassBoardPosition((BypassBoard)entity, newCombo, colCombo);
				}
				
			}
			
		}
		
	}
	
	public double goalGPPPCombo(int curPathIndex, double curPathParam, boolean pathForward, GraphPosition goalGP, GraphPositionPath debugPath, GraphPositionPathPosition debugPos, double debugDist) {
		
		BypassBoardPosition goalBP = (BypassBoardPosition)goalGP;
		
		if (DMath.equals(this.colCombo, goalBP.colCombo)) {
			assert DMath.equals(this.colCombo, goalBP.colCombo);
			/*
			 * same col
			 */
			if (this.rowCombo < goalBP.rowCombo) {
				/*
				 * same direction as board
				 */
				int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
				double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? goalBP.rowParam : goalBP.rowParam) : DMath.equals(curPathParam, 0.0) ? /*fixed*/1-goalBP.rowParam : /*fixed*/1-goalBP.rowParam;
				
				return retIndex+retParam;
				
			} else {
				
				int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
				double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? 1-goalBP.rowParam : 1-goalBP.rowParam) : DMath.equals(curPathParam, 0.0) ? /*fixed*/goalBP.rowParam : /*fixed*/goalBP.rowParam;
				
				return retIndex+retParam;
			}
			
		} else {
			assert DMath.equals(this.rowCombo, goalBP.rowCombo);
			/*
			 * same row
			 */
			if (this.colCombo < goalBP.colCombo) {
				/*
				 * same direction as board
				 */
				int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
				double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? goalBP.colParam : goalBP.colParam) : DMath.equals(curPathParam, 0.0) ? /*fixed*/1-goalBP.colParam : /*fixed*/1-goalBP.colParam;
				
				return retIndex+retParam;
				
			} else {
				
				int retIndex = pathForward ? (DMath.equals(curPathParam, 0.0) ? curPathIndex : curPathIndex) : DMath.equals(curPathParam, 0.0) ? curPathIndex-1 : curPathIndex;
				double retParam = pathForward ? (DMath.equals(curPathParam, 0.0) ? 1-goalBP.colParam : 1-goalBP.colParam) : DMath.equals(curPathParam, 0.0) ? /*fixed*/goalBP.colParam : /*fixed*/goalBP.colParam;
				
				return retIndex+retParam;
			}
		}
		
	}

}
