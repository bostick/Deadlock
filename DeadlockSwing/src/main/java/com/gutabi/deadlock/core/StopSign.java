package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;

public class StopSign {
	
	Edge e;
	int dir;
	
	public static final int STOPSIGN_SIZE = 16;
	
	public StopSign(Edge e, int dir) {
		this.e = e;
		this.dir = dir;
	}
	
	@SuppressWarnings("static-access")
	public void paint(Graphics2D g2) {
		
		BufferedImage im = MODEL.world.stopSign;
		
		Point p;
		if (dir == 0) {
			p = e.getStartBorderPoint();
		} else {
			p = e.getEndBorderPoint();
		}
		
		g2.drawImage(im,
				(int)((p.x * MODEL.PIXELS_PER_METER)-STOPSIGN_SIZE/2),
				(int)((p.y * MODEL.PIXELS_PER_METER)-STOPSIGN_SIZE/2),
				(int)(STOPSIGN_SIZE),
				(int)(STOPSIGN_SIZE), null);
		
		g2.drawImage(im,
				(int)((p.x * MODEL.PIXELS_PER_METER)-STOPSIGN_SIZE/2),
				(int)((p.y * MODEL.PIXELS_PER_METER)-STOPSIGN_SIZE/2),
				(int)(STOPSIGN_SIZE),
				(int)(STOPSIGN_SIZE), null);
		
	}
	
}
