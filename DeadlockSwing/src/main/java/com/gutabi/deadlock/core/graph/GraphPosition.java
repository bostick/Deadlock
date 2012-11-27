package com.gutabi.deadlock.core.graph;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity entity;
	public final Axis axis;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, Entity e, Axis a) {
		this.p = p;
		this.entity = e;
		this.axis = a;
	}
	
	public abstract boolean isBound();
	
	public GraphPosition travelToNeighbor(GraphPosition p, double distance) {
		
		if (DMath.equals(distance, 0.0)) {
			return this;
		}
		
		assert !equals(p);
		assert p.isBound();
		
		if (this instanceof VertexPosition) {
			
			if (p instanceof VertexPosition) {
				assert false;
				return null;
			} else {
				EdgePosition pe = (EdgePosition)p;
				
				if (pe.getIndex() == 1) {
					
					return ((Edge)pe.entity).travelFromReferenceVertex(pe.axis, distance);
					
				} else {
					assert pe.getIndex() == ((Edge)pe.entity).pointCount()-2;
					
					return ((Edge)pe.entity).travelFromOtherVertex(pe.axis, distance);
				}
			}
		} else {
			EdgePosition ee = (EdgePosition)this;
			
			if (p instanceof VertexPosition) {
				
				if (DMath.lessThan(0.0, ee.getCombo()) && DMath.lessThanEquals(ee.getCombo(), 1.0)) {
					
					return ee.travelToReferenceVertex(ee.axis, distance);
					
				} else {
					assert ee.getIndex() == ((Edge)ee.entity).pointCount()-2;
					
					return ee.travelToOtherVertex(ee.axis, distance);
				}
				
			} else {
				EdgePosition pe = (EdgePosition)p;
				
				assert ee.entity == pe.entity;
				assert ee.axis == pe.axis;
				
				if (ee.getCombo() < pe.getCombo()) {
					return ee.travelToOtherVertex(ee.axis, distance);
				} else {
					return ee.travelToReferenceVertex(ee.axis, distance);
				}
				
			}
		}
		
	}
	
}
