package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

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
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setWorldPixelStroke(1);
		
		AffineTransform origTransform1 = ctxt.getTransform();
		
		ctxt.translate(1, 1);
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		ctxt.paintWorldString(0, 0, 1.0, "FPS: " + fps);
		
		ctxt.translate(0, 1);
		
		APP.world.paintStats(ctxt);
		
		ctxt.setTransform(origTransform1);
	}
	
}
