package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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
	public void paint(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.setStroke(VIEW.worldStroke);
		
		g2.translate(VIEW.worldAABBX, VIEW.worldAABBY);
		
		frameCount++;
		
		curTime = System.currentTimeMillis();
		
		if (curTime > lastTime + 1000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = curTime;
		}
		
		g2.setColor(Color.BLACK);
		
		Point p = new Point(1, 1).multiply(MODEL.PIXELS_PER_METER);
		g2.drawString("FPS: " + fps, (int)p.x, (int)p.y);
		
		g2.translate(0, MODEL.PIXELS_PER_METER);
		
		MODEL.world.paintStats(g2);
		
		g2.setTransform(origTransform);
	}
	
}
