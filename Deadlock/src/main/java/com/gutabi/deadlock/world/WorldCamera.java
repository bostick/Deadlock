package com.gutabi.deadlock.world;

import com.gutabi.deadlock.core.geom.AABB;

public class WorldCamera {
	
	public AABB worldViewport;
	
	public int canvasWidth;
	public int canvasHeight;
	
	public double origPixelsPerMeter = 32.0;
	public double pixelsPerMeter = 32.0;
	
}
