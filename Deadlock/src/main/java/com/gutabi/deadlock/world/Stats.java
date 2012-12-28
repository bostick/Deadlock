package com.gutabi.deadlock.world;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class Stats {
	
	WorldScreen screen;
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	public Stats(WorldScreen screen) {
		this.screen = screen;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.DARK_GRAY);
		ctxt.setPixelStroke(1.0);
		
		AffineTransform origTransform1 = ctxt.getTransform();
		
		ctxt.translate(1, 1);
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		ctxt.paintString(0, 0, 1.0, "FPS: " + fps);
		
		ctxt.translate(0, 1);
		
		screen.world.paintStats(ctxt);
		
		ctxt.setTransform(origTransform1);
	}
	
}
