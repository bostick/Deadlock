package com.gutabi.deadlock.geom;

import java.util.List;

import com.gutabi.deadlock.math.Point;

public class MutableCapsuleSequence implements SweeperShape {
	
	public List<Capsule> caps;
	
	public double radius;
	
	public MutableAABB aabb = new MutableAABB();
	
	public void setCapSeq(List<Capsule> caps) {
		
		this.caps = caps;
		
		if (!caps.isEmpty()) {
			radius = caps.get(0).r;
		} else {
			radius = 0.0;
		}
		
		aabb.reset();
		for (Capsule c : caps) {
			aabb.union(c.aabb);
		}
//		aabb = new AABB(aabbTmp.x, aabbTmp.y, aabbTmp.width, aabbTmp.height);
		
	}
	
	public Point getPoint(int index, double param) {
		if (index == caps.size()) {
			assert param == 0.0;
			return caps.get(index-1).b;
		} else {
			return caps.get(index).getPoint(param);
		}
	}
	
	public double getRadius() {
		return radius;
	}
	
	public Capsule getCapsule(int index) {
		return caps.get(index);
	}
	
	public Circle getStart() {
		return caps.get(0).ac;
	}
	
	
}
