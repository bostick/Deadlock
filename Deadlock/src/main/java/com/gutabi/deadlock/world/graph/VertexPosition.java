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
		
		if (p instanceof VertexPosition) {
			assert false;
			return null;
		} else if (p instanceof EdgePosition) {
			EdgePosition pe = (EdgePosition)p;
			
			if (v == ((Edge)pe.entity).getReferenceVertex(pe.axis)) {
				
				return ((Edge)pe.entity).travelFromReferenceVertex(pe.axis, distance);
				
			} else {
				assert v == ((Edge)pe.entity).getOtherVertex(pe.axis);
				
				return ((Edge)pe.entity).travelFromOtherVertex(pe.axis, distance);
			}
		} else if (p instanceof RushHourBoardPosition) {
			RushHourBoardPosition rpos = (RushHourBoardPosition)p;
			
			RushHourBoard b = (RushHourBoard)rpos.entity;
			
			double boardDistance = distance / RushHourStud.SIZE;
			
			RushHourBoardPosition vpos = b.position(v.p);
			
			if (DMath.equals(vpos.colCombo, rpos.colCombo)) {
				
				if (DMath.lessThan(vpos.rowCombo, rpos.rowCombo)) {
					
					return new RushHourBoardPosition(b, vpos.rowCombo + boardDistance, vpos.colCombo);
					
				} else {
					assert DMath.greaterThan(vpos.rowCombo, rpos.rowCombo);
					
					return new RushHourBoardPosition(b, vpos.rowCombo - boardDistance, vpos.colCombo);
				}
				
			} else {
				assert DMath.equals(vpos.rowCombo, rpos.rowCombo);
				
				if (DMath.lessThan(vpos.colCombo, rpos.colCombo)) {
					
					return new RushHourBoardPosition(b, vpos.rowCombo, vpos.colCombo + boardDistance);
					
				} else {
					assert DMath.greaterThan(vpos.colCombo, rpos.colCombo);
					
					return new RushHourBoardPosition(b, vpos.rowCombo, vpos.colCombo - boardDistance);
				}
				
			}
			
		} else {
			assert false;
		}
		
		assert false;
		return null;
		
	}

}
