package com.gutabi.deadlock.world;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class Stats {
	
	World world;
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	public Stats(World world) {
		this.world = world;
	}
	
	/**
	 * 
	 * @param g2
	 */
	public void paint(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		
		AffineTransform origTransform1 = ctxt.getTransform();
		
		ctxt.translate(1, 1);
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		ctxt.paintString(0, 0, 1.0 / world.PIXELS_PER_METER_DEBUG, "FPS: " + fps);
		
		ctxt.translate(0, 1);
		
		world.paintStats(ctxt);
		
		ctxt.setTransform(origTransform1);
	}
	
}
