package com.gutabi.deadlock.world;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;

public class WorldCamera {
	
	public double pixelsPerMeter = 60.0;
	public double origPixelsPerMeter = pixelsPerMeter;
	public AABB worldViewport;
	public AABB origWorldViewport;
	
	public AABB previewAABB = new AABB(5, 400, 100, 100);
	public double previewPixelsPerMeter;
	
	public WorldPanel worldPanel;
	
	public WorldCamera() {
		
	}
	
	public void zoomRelative(double factor) {
		
		pixelsPerMeter = factor * pixelsPerMeter;
		
		double newWidth =  worldPanel.aabb.width / pixelsPerMeter;
		double newHeight = worldPanel.aabb.height / pixelsPerMeter;
		
		worldViewport = new AABB(
				worldViewport.center.x - newWidth/2,
				worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public void zoomAbsolute(double factor) {
		
		pixelsPerMeter = factor * origPixelsPerMeter;
		
		double newWidth =  worldPanel.aabb.width / pixelsPerMeter;
		double newHeight = worldPanel.aabb.height / pixelsPerMeter;
		
		worldViewport = new AABB(
				worldViewport.center.x - newWidth/2,
				worldViewport.center.y - newHeight/2, newWidth, newHeight);
	}
	
	public void previewPan(Point worldDP) {
		
		worldViewport = new AABB( 
				worldViewport.x + worldDP.x,
				worldViewport.y + worldDP.y,
				worldViewport.width,
				worldViewport.height);
	}
	
}
