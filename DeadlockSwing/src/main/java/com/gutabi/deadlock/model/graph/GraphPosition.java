package com.gutabi.deadlock.model.graph;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public abstract class GraphPosition {
	
	public final Point p;
	public final Entity entity;
//	public final Axis axis;
	
	static Logger logger = Logger.getLogger(GraphPosition.class);
	
	public GraphPosition(Point p, Entity e) {
		this.p = p;
		this.entity = e;
//		this.axis = a;
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
				
				if (((VertexPosition)this).v == ((Edge)pe.entity).getReferenceVertex(pe.axis)) {
					
					return ((Edge)pe.entity).travelFromReferenceVertex(pe.axis, distance);
					
				} else {
					assert ((VertexPosition)this).v == ((Edge)pe.entity).getOtherVertex(pe.axis);
					
					return ((Edge)pe.entity).travelFromOtherVertex(pe.axis, distance);
				}
			}
		} else {
			EdgePosition ee = (EdgePosition)this;
			
			if (p instanceof VertexPosition) {
				
				if (((VertexPosition)p).v == ((Edge)ee.entity).getReferenceVertex(ee.axis)) {
					
					return ee.travelToReferenceVertex(ee.axis, distance);
					
				} else {
					assert ((VertexPosition)p).v == ((Edge)ee.entity).getOtherVertex(ee.axis);
					
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
