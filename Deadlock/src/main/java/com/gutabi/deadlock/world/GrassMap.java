package com.gutabi.deadlock.world;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.geom.CapsuleSequence;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.sprites.AnimatedGrass;

public class GrassMap {
	
	List<AnimatedGrass> grass = new ArrayList<AnimatedGrass>();
	
	public void addGrass(AnimatedGrass g) {
		grass.add(g);
	}
	
	public void mowGrass(Shape s) {
		
		List<AnimatedGrass> toRemove = new ArrayList<AnimatedGrass>();
		
		if (s instanceof CapsuleSequence) {
			
			CapsuleSequence cs = (CapsuleSequence)s;
			
			for (AnimatedGrass g : grass) {
				if (cs.intersectA(g.aabb)) {
					toRemove.add(g);
				}
			}
			
		} else {
			
			for (AnimatedGrass g : grass) {
				if (ShapeUtils.intersect(g.aabb, s)) {
					toRemove.add(g);
				}
			}
			
		}
		
		grass.removeAll(toRemove);
		
	}
	
	public void preStart() {
		for (AnimatedGrass g : grass) {
			g.preStart();
		}
	}
	
	public void preStep(double t) {
		for (int i = 0; i < grass.size(); i++) {
			AnimatedGrass g = grass.get(i);
			g.preStep(t);
		}
	}
	
	public void paintScene(RenderingContext ctxt) {
		for (int i = 0; i < grass.size(); i++) {
			AnimatedGrass g = grass.get(i);
			g.paint(ctxt);
		}
	}
	
}
