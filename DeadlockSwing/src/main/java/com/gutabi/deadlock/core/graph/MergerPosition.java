package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public class MergerPosition extends GraphPosition {
	
	public final Merger m;
	/**
	 * dir is 0 or 1
	 * 0 indicates traveling from left to right
	 * 1 indicates traveling from top to bottom
	 */
	public final int dir;
	public final double param;
	
	public final double distanceToLeftOfMerger;
	public final double distanceToRightOfMerger;
	public final double distanceToTopOfMerger;
	public final double distanceToBottomOfMerger;
	
	public MergerPosition(Merger m, int dir, double param) {
		super((dir==0) ? Point.point(m.left.p, m.right.p, param) : Point.point(m.top.p, m.bottom.p, param));
		
		if (DMath.lessThanEquals(param, 0.0) || DMath.greaterThanEquals(param, 1.0)) {
			throw new IllegalArgumentException();
		}
		
		this.m = m;
		this.dir = dir;
		this.param = param;
		
		if (dir == 0) {
			distanceToLeftOfMerger = param * Merger.MERGER_WIDTH;
			distanceToRightOfMerger = 1-distanceToLeftOfMerger;
			distanceToTopOfMerger = -1;
			distanceToBottomOfMerger = -1;
		} else {
			distanceToTopOfMerger = param * Merger.MERGER_HEIGHT;
			distanceToBottomOfMerger = 1-distanceToTopOfMerger;
			distanceToLeftOfMerger = -1;
			distanceToRightOfMerger = -1;
		}
		
	}
	
	public GraphPosition floor() {
		return (dir==0) ? new VertexPosition(m.left) : new VertexPosition(m.top);
	}
	
	public GraphPosition ceiling() {
		return (dir==0) ? new VertexPosition(m.right) : new VertexPosition(m.bottom);
	}
	
	public boolean isBound() {
		return false;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		VertexPosition vg = (VertexPosition)goal;
		if (dir == 0) {
			assert vg.v == m.left || vg.v == m.right;
			return vg;
		} else {
			assert vg.v == m.top || vg.v == m.bottom;
			return vg;
		}
	}
	
	public Entity getEntity() {
		return m;
	}
	
	public double distanceTo(GraphPosition goal) {
		
		if (goal instanceof VertexPosition) {
			VertexPosition vg = (VertexPosition)goal;
			
			if (dir == 0) {
				if (vg.v == m.left) {
					return distanceToLeftOfMerger;
				} else if (vg.v == m.right) {
					return distanceToRightOfMerger;
				}
				
				double leftPath = MODEL.world.distanceBetweenVertices(m.left, vg.v);
				double rightPath = MODEL.world.distanceBetweenVertices(m.right, vg.v);
				
				double dist = Math.min(leftPath + distanceToLeftOfMerger, rightPath + distanceToRightOfMerger);
				
				assert DMath.greaterThanEquals(dist, 0.0);
				
				return dist;
				
			} else {
				if (vg.v == m.top) {
					return distanceToTopOfMerger;
				} else if (vg.v == m.bottom) {
					return distanceToBottomOfMerger;
				}
				
				double topPath = MODEL.world.distanceBetweenVertices(m.top, vg.v);
				double bottomPath = MODEL.world.distanceBetweenVertices(m.bottom, vg.v);
				
				double dist = Math.min(topPath + distanceToTopOfMerger, bottomPath + distanceToBottomOfMerger);
				
				assert DMath.greaterThanEquals(dist, 0.0);
				
				return dist;
			}
			
		} else {
			EdgePosition eg = (EdgePosition)goal;
			
			if (dir == 0) {
				
//				if (aa.e == eg.e) {
//					return Math.abs(aa.distanceToStartOfEdge() - eg.distanceToStartOfEdge());
//				}
				
				double leftStartPath = MODEL.world.distanceBetweenVertices(m.left, eg.e.start);
				double leftEndPath = MODEL.world.distanceBetweenVertices(m.left, eg.e.end);
				double rightStartPath = MODEL.world.distanceBetweenVertices(m.right, eg.e.start);
				double rightEndPath = MODEL.world.distanceBetweenVertices(m.right, eg.e.end);
				
				double leftStartDistance = leftStartPath + distanceToLeftOfMerger + eg.distanceToStartOfEdge();
				double leftEndDistance = leftEndPath + distanceToLeftOfMerger + eg.distanceToEndOfEdge();
				double rightStartDistance = rightStartPath + distanceToRightOfMerger + eg.distanceToStartOfEdge();
				double rightEndDistance = rightEndPath + distanceToRightOfMerger + eg.distanceToEndOfEdge();
				
				double dist = Math.min(Math.min(leftStartDistance, leftEndDistance), Math.min(rightStartDistance, rightEndDistance));
				
				assert DMath.greaterThanEquals(dist, 0.0);
				
				return dist;
				
			} else {
				
//				if (aa.e == eg.e) {
//					return Math.abs(aa.distanceToStartOfEdge() - eg.distanceToStartOfEdge());
//				}
				
				double topStartPath = MODEL.world.distanceBetweenVertices(m.top, eg.e.start);
				double topEndPath = MODEL.world.distanceBetweenVertices(m.top, eg.e.end);
				double bottomStartPath = MODEL.world.distanceBetweenVertices(m.bottom, eg.e.start);
				double bottomEndPath = MODEL.world.distanceBetweenVertices(m.bottom, eg.e.end);
				
				double topStartDistance = topStartPath + distanceToTopOfMerger + eg.distanceToStartOfEdge();
				double topEndDistance = topEndPath + distanceToTopOfMerger + eg.distanceToEndOfEdge();
				double bottomStartDistance = bottomStartPath + distanceToBottomOfMerger + eg.distanceToStartOfEdge();
				double bottomEndDistance = bottomEndPath + distanceToBottomOfMerger + eg.distanceToEndOfEdge();
				
				double dist = Math.min(Math.min(topStartDistance, topEndDistance), Math.min(bottomStartDistance, bottomEndDistance));
				
				assert DMath.greaterThanEquals(dist, 0.0);
				
				return dist;
				
			}
		}
		
	}
	
	
}
