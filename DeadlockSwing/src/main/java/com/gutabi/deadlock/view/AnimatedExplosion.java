package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class AnimatedExplosion {
	
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
		
		AffineTransform origTransform = ctxt.g2.getTransform();
		
		ctxt.g2.translate(p.x, p.y);
		ctxt.g2.scale(MODEL.METERS_PER_PIXEL_DEBUG, MODEL.METERS_PER_PIXEL_DEBUG);
		
		ctxt.g2.drawImage(VIEW.explosionSheet,
				(int)(-71/2),
				(int)(-100/2),
				(int)(71/2),
				(int)(100/2),
				71 * index, 0, 71 * index + 71, 100, null);
		
		ctxt.g2.setTransform(origTransform);
		
	}
	
}
