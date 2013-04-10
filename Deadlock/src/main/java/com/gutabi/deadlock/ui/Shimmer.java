package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Shimmer {
	
	MenuItem item;
	
	long startMillis = -1;
	
	public Shimmer(MenuItem item) {
		this.item = item;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (startMillis == -1) {
			startMillis = System.currentTimeMillis();
		}
		
		long timeToTraverse = (long)item.aabb.width * 1000 / 100;
		
		double t = System.currentTimeMillis();
		while (t > startMillis + timeToTraverse) {
			startMillis = startMillis + timeToTraverse;
		}
		
		double param = (t - startMillis) / timeToTraverse;
		assert param >= 0.0;
		assert param <= 1.0;
		
		double x = item.aabb.x + param * (item.aabb.brX - item.aabb.x - 1);
		
		AABB aabb = new AABB(x, item.aabb.y, 1, item.aabb.height);
		
		ctxt.setColor(Color.RED);
		aabb.paint(ctxt);
	}
}
