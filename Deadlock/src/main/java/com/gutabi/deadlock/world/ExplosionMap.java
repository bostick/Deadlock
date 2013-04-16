package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.ui.paint.RenderingContext;
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
		for (int i = 0; i < explosions.size(); i++) {
			AnimatedExplosion x = explosions.get(i);
			x.preStep(t);
		}
	}
	
	
	List<AnimatedExplosion> exToBeRemoved = new ArrayList<AnimatedExplosion>();
	
	public void postStep(double t) {
		
		exToBeRemoved.clear();
		
		for (int i = 0; i < explosions.size(); i++) {
			AnimatedExplosion x = explosions.get(i);
			boolean shouldPersist = x.postStep(t);
			if (!shouldPersist) {
				exToBeRemoved.add(x);
			}
		}
		
		for (int i = 0; i < exToBeRemoved.size(); i++) {
			AnimatedExplosion x = exToBeRemoved.get(i);
			explosions.remove(x);
		}
	}
	
	public void paint(RenderingContext ctxt) {
		if (APP.EXPLOSIONS_DRAW) {
			for (int i = 0; i < explosions.size(); i++) {
				AnimatedExplosion x = explosions.get(i);
				x.paint(ctxt);
			}
		}
	}
	
}
