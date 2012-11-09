package com.gutabi.deadlock.core.graph;

import java.util.ArrayList;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;

public class MergerPosition extends EdgePosition {
	
	public final Merger m;
	/**
	 * dir is 0 or 1
	 * 0 indicates traveling from left to right
	 * 1 indicates traveling from top to bottom
	 */
	public final Axis dir;
	public final double param;
	
	public final double distanceToLeftOfMerger;
	public final double distanceToRightOfMerger;
	public final double distanceToTopOfMerger;
	public final double distanceToBottomOfMerger;
	
	private final int hash;
	
	public MergerPosition(Merger m, Axis dir, double param) {
		super((dir==Axis.LEFTRIGHT) ? Point.point(m.left.p, m.right.p, param) : Point.point(m.top.p, m.bottom.p, param));
		
		if (DMath.lessThanEquals(param, 0.0) || DMath.greaterThanEquals(param, 1.0)) {
			throw new IllegalArgumentException();
		}
		
		this.m = m;
		this.dir = dir;
		this.param = param;
		
		int h = 17;
		h = 37 * h + m.hashCode();
		h = 37 * h + dir.hashCode();
		long l = Double.doubleToLongBits(param);
		int c = (int)(l ^ (l >>> 32));
		h = 37 * h + c;
		hash = h;
		
		if (dir==Axis.LEFTRIGHT) {
			vs = new ArrayList<Vertex>();
			vs.add(m.left);
			vs.add(m.right);
		} else {
			vs = new ArrayList<Vertex>();
			vs.add(m.top);
			vs.add(m.bottom);
		}
		
		if (dir==Axis.LEFTRIGHT) {
			distanceToLeftOfMerger = param * Merger.MERGER_WIDTH;
			distanceToRightOfMerger = Merger.MERGER_WIDTH-distanceToLeftOfMerger;
			distanceToTopOfMerger = -1;
			distanceToBottomOfMerger = -1;
		} else {
			distanceToTopOfMerger = param * Merger.MERGER_HEIGHT;
			distanceToBottomOfMerger = Merger.MERGER_HEIGHT-distanceToTopOfMerger;
			distanceToLeftOfMerger = -1;
			distanceToRightOfMerger = -1;
		}
		
	}
	
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphPosition)) {
			throw new IllegalArgumentException();
		} else if (!(o instanceof MergerPosition)) {
			return false;
		} else {
			MergerPosition b = (MergerPosition)o;
			return (m == b.m) && (dir == b.dir) && DMath.equals(param, b.param);
		}
	}
	
	public GraphPosition floor() {
		return (dir==Axis.LEFTRIGHT) ? new VertexPosition(m.left) : new VertexPosition(m.top);
	}
	
	public GraphPosition ceiling() {
		return (dir==Axis.LEFTRIGHT) ? new VertexPosition(m.right) : new VertexPosition(m.bottom);
	}
	
	public boolean isBound() {
		return false;
	}
	
	public int getIndex() {
		return 0;
	}
	
	public double getParam() {
		return param;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		VertexPosition vg = (VertexPosition)goal;
		if (dir==Axis.LEFTRIGHT) {
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
	
	public double distanceToConnectedVertex(Vertex v) {
		assert vs.contains(v);
		if (dir == Axis.LEFTRIGHT) {
			if (v == m.left) {
				return distanceToLeftOfMerger;
			} else {
				return distanceToRightOfMerger;
			}
		} else {
			if (v == m.top) {
				return distanceToTopOfMerger;
			} else {
				return distanceToBottomOfMerger;
			}
		}
	}
	
	public GraphPosition travelToConnectedVertex(Vertex v, double dist) {
		
		if (v == m.top) {
			
			return new MergerPosition(m, Axis.TOPBOTTOM, param - dist / Merger.MERGER_HEIGHT);
			
		} else if (v == m.left) {
			
			return new MergerPosition(m, Axis.LEFTRIGHT, param - dist / Merger.MERGER_WIDTH);
			
		} else if (v == m.right) {
			
			return new MergerPosition(m, Axis.LEFTRIGHT, param + dist / Merger.MERGER_WIDTH);
			
		} else {
			assert v == m.bottom;
			
			return new MergerPosition(m, Axis.TOPBOTTOM, param + dist / Merger.MERGER_HEIGHT);
			
		}
		
	}
	
//	public double distanceTo(GraphPosition goal) {
//		
//		if (goal instanceof VertexPosition) {
//			VertexPosition vg = (VertexPosition)goal;
//			
//			if (dir==MergerDirection.LEFTRIGHT) {
//				if (vg.v == m.left) {
//					return distanceToLeftOfMerger;
//				} else if (vg.v == m.right) {
//					return distanceToRightOfMerger;
//				}
//				
//				double leftPath = MODEL.world.distanceBetweenVertices(m.left, vg.v);
//				double rightPath = MODEL.world.distanceBetweenVertices(m.right, vg.v);
//				
//				double dist = Math.min(leftPath + distanceToLeftOfMerger, rightPath + distanceToRightOfMerger);
//				
//				assert DMath.greaterThanEquals(dist, 0.0);
//				
//				return dist;
//				
//			} else {
//				if (vg.v == m.top) {
//					return distanceToTopOfMerger;
//				} else if (vg.v == m.bottom) {
//					return distanceToBottomOfMerger;
//				}
//				
//				double topPath = MODEL.world.distanceBetweenVertices(m.top, vg.v);
//				double bottomPath = MODEL.world.distanceBetweenVertices(m.bottom, vg.v);
//				
//				double dist = Math.min(topPath + distanceToTopOfMerger, bottomPath + distanceToBottomOfMerger);
//				
//				assert DMath.greaterThanEquals(dist, 0.0);
//				
//				return dist;
//			}
//			
//		} else if (goal instanceof RoadPosition) {
//			RoadPosition eg = (RoadPosition)goal;
//			
//			if (dir==MergerDirection.LEFTRIGHT) {
//				
//				double leftStartPath = MODEL.world.distanceBetweenVertices(m.left, eg.r.start);
//				double leftEndPath = MODEL.world.distanceBetweenVertices(m.left, eg.r.end);
//				double rightStartPath = MODEL.world.distanceBetweenVertices(m.right, eg.r.start);
//				double rightEndPath = MODEL.world.distanceBetweenVertices(m.right, eg.r.end);
//				
//				double leftStartDistance = leftStartPath + distanceToLeftOfMerger + eg.distanceToStartOfRoad();
//				double leftEndDistance = leftEndPath + distanceToLeftOfMerger + eg.distanceToEndOfRoad();
//				double rightStartDistance = rightStartPath + distanceToRightOfMerger + eg.distanceToStartOfRoad();
//				double rightEndDistance = rightEndPath + distanceToRightOfMerger + eg.distanceToEndOfRoad();
//				
//				double dist = Math.min(Math.min(leftStartDistance, leftEndDistance), Math.min(rightStartDistance, rightEndDistance));
//				
//				assert DMath.greaterThanEquals(dist, 0.0);
//				
//				return dist;
//				
//			} else {
//				
//				double topStartPath = MODEL.world.distanceBetweenVertices(m.top, eg.r.start);
//				double topEndPath = MODEL.world.distanceBetweenVertices(m.top, eg.r.end);
//				double bottomStartPath = MODEL.world.distanceBetweenVertices(m.bottom, eg.r.start);
//				double bottomEndPath = MODEL.world.distanceBetweenVertices(m.bottom, eg.r.end);
//				
//				double topStartDistance = topStartPath + distanceToTopOfMerger + eg.distanceToStartOfRoad();
//				double topEndDistance = topEndPath + distanceToTopOfMerger + eg.distanceToEndOfRoad();
//				double bottomStartDistance = bottomStartPath + distanceToBottomOfMerger + eg.distanceToStartOfRoad();
//				double bottomEndDistance = bottomEndPath + distanceToBottomOfMerger + eg.distanceToEndOfRoad();
//				
//				double dist = Math.min(Math.min(topStartDistance, topEndDistance), Math.min(bottomStartDistance, bottomEndDistance));
//				
//				assert DMath.greaterThanEquals(dist, 0.0);
//				
//				return dist;
//				
//			}
//			
//		} else {
//			MergerPosition mg = (MergerPosition)goal;
//			
//			if (dir==MergerDirection.LEFTRIGHT) {
//				
//				if (m == mg.m && mg.dir == MergerDirection.LEFTRIGHT) {
//					return Math.abs(distanceToLeftOfMerger - mg.distanceToLeftOfMerger);
//				}
//				
//				if (mg.dir == MergerDirection.LEFTRIGHT) {
//					
//					double leftLeftPath = MODEL.world.distanceBetweenVertices(m.left, eg.r.end);
//					double leftRightPath = MODEL.world.distanceBetweenVertices(m.left, eg.r.end);
//					double rightLeftPath = MODEL.world.distanceBetweenVertices(m.right, eg.r.end);
//					double rightRightPath = MODEL.world.distanceBetweenVertices(m.right, eg.r.end);
//					
//					double leftLeftDistance = leftEndPath + distanceToLeftOfMerger + eg.distanceToEndOfRoad();
//					double leftRightDistance = leftEndPath + distanceToLeftOfMerger + eg.distanceToEndOfRoad();
//					double rightLeftDistance = rightEndPath + distanceToRightOfMerger + eg.distanceToEndOfRoad();
//					double rightRightDistance = rightEndPath + distanceToRightOfMerger + eg.distanceToEndOfRoad();
//					
//					double dist = Math.min(Math.min(leftStartDistance, leftEndDistance), Math.min(rightStartDistance, rightEndDistance));
//					
//					assert DMath.greaterThanEquals(dist, 0.0);
//					
//					return dist;
//					
//				} else {
//					
//					double leftTopPath = MODEL.world.distanceBetweenVertices(m.left, eg.r.start);
//					double leftBottomPath = MODEL.world.distanceBetweenVertices(m.left, eg.r.end);
//					double rightTopPath = MODEL.world.distanceBetweenVertices(m.right, eg.r.start);
//					double rightBottomPath = MODEL.world.distanceBetweenVertices(m.right, eg.r.end);
//					
//					double leftTopDistance = leftStartPath + distanceToLeftOfMerger + eg.distanceToStartOfRoad();
//					double leftBottomDistance = leftEndPath + distanceToLeftOfMerger + eg.distanceToEndOfRoad();
//					double rightTopDistance = rightStartPath + distanceToRightOfMerger + eg.distanceToStartOfRoad();
//					double rightBottomDistance = rightEndPath + distanceToRightOfMerger + eg.distanceToEndOfRoad();
//					
//					double dist = Math.min(Math.min(leftStartDistance, leftEndDistance), Math.min(rightStartDistance, rightEndDistance));
//					
//					assert DMath.greaterThanEquals(dist, 0.0);
//					
//					return dist;
//					
//				}
//				
//			} else {
//				
//				if (m == mg.m && mg.dir == MergerDirection.TOPBOTTOM) {
//					return Math.abs(distanceToTopOfMerger - mg.distanceToTopOfMerger);
//				}
//				
//				double topStartPath = MODEL.world.distanceBetweenVertices(m.top, eg.r.start);
//				double topEndPath = MODEL.world.distanceBetweenVertices(m.top, eg.r.end);
//				double bottomStartPath = MODEL.world.distanceBetweenVertices(m.bottom, eg.r.start);
//				double bottomEndPath = MODEL.world.distanceBetweenVertices(m.bottom, eg.r.end);
//				
//				double topStartDistance = topStartPath + distanceToTopOfMerger + eg.distanceToStartOfRoad();
//				double topEndDistance = topEndPath + distanceToTopOfMerger + eg.distanceToEndOfRoad();
//				double bottomStartDistance = bottomStartPath + distanceToBottomOfMerger + eg.distanceToStartOfRoad();
//				double bottomEndDistance = bottomEndPath + distanceToBottomOfMerger + eg.distanceToEndOfRoad();
//				
//				double dist = Math.min(Math.min(topStartDistance, topEndDistance), Math.min(bottomStartDistance, bottomEndDistance));
//				
//				assert DMath.greaterThanEquals(dist, 0.0);
//				
//				return dist;
//				
//			}
//
//		}
//		
//	}
	
	
}
