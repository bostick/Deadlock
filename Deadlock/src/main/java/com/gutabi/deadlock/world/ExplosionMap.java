package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.sprites.AnimatedExplosion;

public class ExplosionMap {
	
	private List<AnimatedExplosion> explosions = new ArrayList<AnimatedExplosion>();
	
	public void add(AnimatedExplosion x) {
		if (explosions.size() == 100) {
			
			explosions.remove(0);
			
		} else {
			assert explosions.size() < 100;
		}
		
		explosions.add(x);
		
		assert explosions.size() <= 100;
	}
	
	public int size() {
		return explosions.size();
	}
	
	public void postStop() {
		explosions.clear();
	}
	
	public void preStep(double t) {
		for (AnimatedExplosion x : explosions) {
			x.preStep(t);
		}
	}
	
	public void postStep(double t) {
		List<AnimatedExplosion> exToBeRemoved = new ArrayList<AnimatedExplosion>();
		
		for (AnimatedExplosion e : explosions) {
			boolean shouldPersist = e.postStep(t);
			if (!shouldPersist) {
				exToBeRemoved.add(e);
			}
		}
		
		explosions.removeAll(exToBeRemoved);
	}
	
	public void paint(RenderingContext ctxt) {
		if (APP.EXPLOSIONS_DRAW) {
			for (AnimatedExplosion x : explosions) {
				x.paint(ctxt);
			}
		}
	}
	
}
