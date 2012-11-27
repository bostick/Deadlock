package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class Stats {
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	/**
	 * 
	 * @param g2
	 */
	public void paint(RenderingContext ctxt) {
		
		ctxt.g2.setColor(Color.BLACK);
		ctxt.g2.setStroke(VIEW.worldStroke);
		
		AffineTransform origTransform = ctxt.g2.getTransform();
		
		ctxt.g2.translate(10, 10);
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		ctxt.g2.drawString("FPS: " + fps, 0, 0);
		
		ctxt.g2.translate(0, 10);
		
		MODEL.world.paintStats(ctxt);
		
		ctxt.g2.setTransform(origTransform);
	}
	
}
