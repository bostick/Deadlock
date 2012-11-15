package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class AnimatedExplosion {
	
	Point p;
	Point ul;
	int lastFrame;
	double lastTime;
	
	public AnimatedExplosion(Point p) {
		this.p = p;
		ul = p.minus(new Point(71.0/2 * MODEL.METERS_PER_PIXEL, 100.0/2 * MODEL.METERS_PER_PIXEL));
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
	
	public void paint(Graphics2D g2) {
		paint(g2, lastFrame);
		
	}
	
	private void paint(Graphics2D g2, int index) {
		
		g2.drawImage(VIEW.explosionSheet,
				(int)(ul.x * MODEL.PIXELS_PER_METER) ,
				(int)(ul.y * MODEL.PIXELS_PER_METER),
				(int)(ul.x * MODEL.PIXELS_PER_METER) + 71,
				(int)(ul.y * MODEL.PIXELS_PER_METER) + 100,
				71 * index, 0, 71 * index + 71, 100, null);
	}
	
}
