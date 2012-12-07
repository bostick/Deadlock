package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import com.gutabi.deadlock.core.Point;

//@SuppressWarnings("static-access")
public class AnimatedExplosion {
	
	public static final double explosionWidth = 2.21875;
	public static final double explosionHeight = 3.125;
	
	Point p;
	int lastFrame;
	double lastTime;
	
	public AnimatedExplosion(Point p) {
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
		
		ctxt.paintWorldImage(p.x - explosionWidth/2, p.y - explosionHeight/2, VIEW.explosionSheet,
				0,
				0,
				(int)Math.round(APP.world.PIXELS_PER_METER_DEBUG * explosionWidth),
				(int)Math.round(APP.world.PIXELS_PER_METER_DEBUG * explosionHeight),
				71 * index, 0, 71 * index + 71, 100);
		
	}
	
}
