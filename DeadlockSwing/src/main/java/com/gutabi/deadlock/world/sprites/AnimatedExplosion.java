package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;

//@SuppressWarnings("static-access")
public class AnimatedExplosion {
	
	public static final double explosionWidth = 2.21875;
	public static final double explosionHeight = 3.125;
	
	public final World world;
	Point p;
	int lastFrame;
	double lastTime;
	
	public AnimatedExplosion(World world, Point p) {
		this.world = world;
		this.p = p;
	}
	
	public void preStart() {
		lastFrame = -1;
		lastTime = -1;	
	}
	
	public void preStep(double t) {
		if (lastTime == -1) {
			
			lastTime = t;
			lastFrame = 0;
			
		} else if ((t - lastTime) > 0.1) {
			
			if (lastFrame < 15) {
				lastFrame = lastFrame + 1;
				lastTime = t;
			}
		}
	}
	
	public boolean postStep(double t) {
		if (lastFrame == 15) {
			if ((t - lastTime) > 0.1) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}
	
	public void paint(RenderingContext ctxt) {
		paint(ctxt, lastFrame);
		
	}
	
	private void paint(RenderingContext ctxt, int index) {
		
		ctxt.paintImage(
				p.x - explosionWidth/2, p.y - explosionHeight/2, 1/APP.PIXELS_PER_METER,
				VIEW.explosionSheet,
				0, 0, (int)Math.round(APP.PIXELS_PER_METER * explosionWidth), (int)Math.round(APP.PIXELS_PER_METER * explosionHeight),
				71 * index, 0, 71 * index + 71, 100);
		
	}
	
}
