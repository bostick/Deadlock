package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class Stats {
	
	long lastTime;
	long curTime;
	int frameCount;
	int fps;
	
	/**
	 * 
	 * @param g2
	 */
	public void paintFPS(Graphics2D g2) {
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		g2.setColor(Color.WHITE);
		
		Point p = new Point(1, 1).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("FPS: " + fps, (int)p.x, (int)p.y);
		
		p = new Point(1, 2).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("time: " + MODEL.world.t, (int)p.x, (int)p.y);
		
		p = new Point(1, 3).multiply(MODEL.PIXELS_PER_METER);		
		g2.drawString("body count: " + MODEL.world.b2dWorld.getBodyCount(), (int)p.x, (int)p.y);
		
		p = new Point(1, 4).multiply(MODEL.PIXELS_PER_METER);		
		g2.drawString("edge count: " + MODEL.world.graph.getEdges().size(), (int)p.x, (int)p.y);
		
		p = new Point(1, 5).multiply(MODEL.PIXELS_PER_METER);		
		g2.drawString("vertex count: " + MODEL.world.graph.getAllVertices().size(), (int)p.x, (int)p.y);
		
	}
	
}
