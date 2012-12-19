package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.WorldCamera;

//@SuppressWarnings("static-access")
public class AnimatedExplosion {
	
	public static final double explosionWidth = 2.21875;
	public static final double explosionHeight = 3.125;
	
	WorldCamera cam;
//	public final World world;
	Point p;
	int lastFrame;
	double lastTime;
	
	public AnimatedExplosion(WorldCamera cam, Point p) {
		this.cam = cam;
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
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x - explosionWidth/2, p.y - explosionHeight/2);
		ctxt.paintImage(
				cam.pixelsPerMeter,
				VIEW.explosionSheet,
				0, 0, explosionWidth, explosionHeight,
				71 * index, 0, 71 * index + 71, 100);
		
		ctxt.setTransform(origTransform);
		
	}
	
}