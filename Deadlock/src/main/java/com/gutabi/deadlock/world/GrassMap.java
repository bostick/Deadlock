package com.gutabi.deadlock.world;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.geom.Shape;
import com.gutabi.deadlock.math.geom.ShapeUtils;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.sprites.AnimatedGrass;

public class GrassMap {
	
	List<AnimatedGrass> grass = new ArrayList<AnimatedGrass>();
	
	public void addGrass(AnimatedGrass g) {
		grass.add(g);
	}
	
	public void mowGrass(Shape s) {
		
		List<AnimatedGrass> toRemove = new ArrayList<AnimatedGrass>();
		for (AnimatedGrass g : grass) {
			if (ShapeUtils.intersect(g.aabb, s)) {
				toRemove.add(g);
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
		for (AnimatedGrass g : grass) {
			g.preStep(t);
		}
	}
	
	public void paintScene(RenderingContext ctxt) {
		for (AnimatedGrass g : grass) {
			g.paint(ctxt);
		}
	}
	
}
