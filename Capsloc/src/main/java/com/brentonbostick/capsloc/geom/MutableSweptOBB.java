package com.brentonbostick.capsloc.geom;

import com.brentonbostick.capsloc.math.Point;

public class MutableSweptOBB {
	
	public MutableOBB start;
	public MutableOBB end;
	
	public boolean isAABB;
	public MutableAABB aabb = new MutableAABB();
	
	public double dist;
	public Point dir;
	
	int hash;
	
	public void setShape(MutableOBB start, MutableOBB end) {
		this.start = start;
		this.end = end;
		
		if (!(start.getN01().equals(end.getN01()) &&
				start.getN12().equals(end.getN12()))) {
			throw new IllegalArgumentException();
		}
		
		Point disp = end.center.minus(start.center);
		dist = disp.length();
		dir = disp.normalize();
		
		if (start.getN01().isRightAngleNormal() && start.getN12().isRightAngleNormal()) {
			if (dir.equals(start.getN01())) {
				isAABB = true;
				// end is above
				aabb.setShape(end.aabb.x, end.aabb.y, end.aabb.width, ((start.aabb.y+start.aabb.height) - end.aabb.y));
			} else if (dir.equals(start.getN12())) {
				isAABB = true;
				// end is to right
				aabb.setShape(start.aabb.x, start.aabb.y, ((end.aabb.x+end.aabb.width)-start.aabb.x), start.aabb.height);
			} else if (dir.equals(start.getN01().negate())) {
				isAABB = true;
				// end is below
				aabb.setShape(start.aabb.x, start.aabb.y, start.aabb.width, ((end.aabb.y+end.aabb.height) - start.aabb.y));
			} else if (dir.equals(start.getN12().negate())) {
				isAABB = true;
				// end is to left
				aabb.setShape(end.aabb.x, end.aabb.y, ((start.aabb.x+start.aabb.width)-end.aabb.x), end.aabb.height);
			} else {
				isAABB = false;
				double ulX = Math.min(Math.min(Math.min(start.p0.x, start.p1.x), Math.min(start.p2.x, start.p3.x)), Math.min(Math.min(end.p0.x, end.p1.x), Math.min(end.p2.x, end.p3.x)));
				double ulY = Math.min(Math.min(Math.min(start.p0.y, start.p1.y), Math.min(start.p2.y, start.p3.y)), Math.min(Math.min(end.p0.y, end.p1.y), Math.min(end.p2.y, end.p3.y)));
				double brX = Math.min(Math.max(Math.max(start.p0.x, start.p1.x), Math.max(start.p2.x, start.p3.x)), Math.max(Math.max(end.p0.x, end.p1.x), Math.max(end.p2.x, end.p3.x)));
				double brY = Math.min(Math.max(Math.max(start.p0.y, start.p1.y), Math.max(start.p2.y, start.p3.y)), Math.max(Math.max(end.p0.y, end.p1.y), Math.max(end.p2.y, end.p3.y)));
				
				aabb.setShape(ulX, ulY, (brX - ulX), (brY - ulY));
			}
		} else {
			isAABB = false;
			double ulX = Math.min(Math.min(Math.min(start.p0.x, start.p1.x), Math.min(start.p2.x, start.p3.x)), Math.min(Math.min(end.p0.x, end.p1.x), Math.min(end.p2.x, end.p3.x)));
			double ulY = Math.min(Math.min(Math.min(start.p0.y, start.p1.y), Math.min(start.p2.y, start.p3.y)), Math.min(Math.min(end.p0.y, end.p1.y), Math.min(end.p2.y, end.p3.y)));
			double brX = Math.min(Math.max(Math.max(start.p0.x, start.p1.x), Math.max(start.p2.x, start.p3.x)), Math.max(Math.max(end.p0.x, end.p1.x), Math.max(end.p2.x, end.p3.x)));
			double brY = Math.min(Math.max(Math.max(start.p0.y, start.p1.y), Math.max(start.p2.y, start.p3.y)), Math.max(Math.max(end.p0.y, end.p1.y), Math.max(end.p2.y, end.p3.y)));
			
			aabb.setShape(ulX, ulY, (brX - ulX), (brY - ulY));
		}
		
		hash = 0;
		
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof SweptOBB)) {
			return false;
		} else {
			SweptOBB b = (SweptOBB)o;
			return start.equals(b.start) && end.equals(b.end);
		}
	}
	
	public int hashCode() {
		if (hash == 0) {
			int h = 17;
			h = 37 * h + start.hashCode();
			h = 37 * h + end.hashCode();
			hash = h;
		}
		return hash;
	}
	
}
