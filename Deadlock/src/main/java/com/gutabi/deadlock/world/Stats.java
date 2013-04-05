package com.gutabi.deadlock.world;

import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Stats {
	
	World world;
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	public Stats(World world) {
		this.world = world;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.DARK_GRAY);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		Transform origTransform1 = ctxt.getTransform();
		
		ctxt.translate(1, 1);
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		ctxt.paintString(0, 0, 1.0/world.worldCamera.pixelsPerMeter, "FPS: " + fps);
		
		ctxt.translate(0, 1);
		
		world.paintStats(ctxt);
		
		ctxt.setPaintMode();
		
		ctxt.setTransform(origTransform1);
		
	}
	
}
