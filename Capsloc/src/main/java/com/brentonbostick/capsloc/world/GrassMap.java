package com.brentonbostick.capsloc.world;

import java.util.ArrayList;
import java.util.List;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.CapsuleSequence;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.geom.ShapeUtils;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.sprites.AnimatedGrass;

public class GrassMap {
	
	public List<AnimatedGrass> grass = new ArrayList<AnimatedGrass>();
	
	public void addGrass(AnimatedGrass g) {
		grass.add(g);
	}
	
	
	List<AnimatedGrass> toRemove = new ArrayList<AnimatedGrass>();
	
	public void mowGrass(Object s) {
		
		toRemove.clear();
		
		if (s instanceof CapsuleSequence) {
			
			CapsuleSequence cs = (CapsuleSequence)s;
			
			for (int i = 0; i < grass.size(); i++) {
				AnimatedGrass g = grass.get(i);
				if (cs.intersectA(g.aabb)) {
					toRemove.add(g);
				}
			}
			
		} else if (s instanceof Circle) {
			
			Circle c = (Circle)s;
			
			for (int i = 0; i < grass.size(); i++) {
				AnimatedGrass g = grass.get(i);
				if (ShapeUtils.intersectAC(g.aabb, c)) {
					toRemove.add(g);
				}
			}
			
		} else if (s instanceof AABB) {
			
			AABB a = (AABB)s;
			
			for (int i = 0; i < grass.size(); i++) {
				AnimatedGrass g = grass.get(i);
				if (ShapeUtils.intersectAA(g.aabb, a)) {
					toRemove.add(g);
				}
			}
			
		} else {
			
			for (int i = 0; i < grass.size(); i++) {
				AnimatedGrass g = grass.get(i);
				if (ShapeUtils.intersect(g.aabb, s)) {
					toRemove.add(g);
				}
			}
			
		}
		
		for (int i = 0; i < toRemove.size(); i++) {
			AnimatedGrass g = toRemove.get(i);
			grass.remove(g);
		}
		
	}
	
	public void preStart() {
		for (int i = 0; i < grass.size(); i++) {
			AnimatedGrass g = grass.get(i);
			g.preStart();
		}
	}
	
	public boolean step(double t) {
		boolean res = false;
		for (int i = 0; i < grass.size(); i++) {
			AnimatedGrass g = grass.get(i);
			res = res | g.step(t);
		}
		return res;
	}
	
	public void paintScene(RenderingContext ctxt) {
		for (int i = 0; i < grass.size(); i++) {
			AnimatedGrass g = grass.get(i);
			if (ShapeUtils.intersectAA(g.aabb, ctxt.cam.worldViewport)) {
				g.paint(ctxt);
			}
		}
	}
	
}
