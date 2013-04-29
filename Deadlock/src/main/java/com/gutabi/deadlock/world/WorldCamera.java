package com.gutabi.deadlock.world;

import java.io.Serializable;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.MutableAABB;
import com.gutabi.deadlock.math.Point;

public class WorldCamera implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public double pixelsPerMeter = -1;
	public double origPixelsPerMeter = -1;
	public MutableAABB worldViewport = new MutableAABB();
	public AABB origWorldViewport;
	
	public AABB previewAABB = new AABB(5, 400, 100, 100);
	public double previewPixelsPerMeter;
	
	public AABB panelAABB;
	
	public WorldCamera() {
		
	}
	
	public void zoomRelative(double factor) {
		
		pixelsPerMeter = factor * pixelsPerMeter;
		
		double newWidth =  panelAABB.width / pixelsPerMeter;
		double newHeight = panelAABB.height / pixelsPerMeter;
		
		worldViewport.setShape(
				(worldViewport.x + worldViewport.width/2) - newWidth/2,
				(worldViewport.y + worldViewport.height/2) - newHeight/2, newWidth, newHeight);
	}
	
	public void zoomAbsolute(double factor) {
		
		pixelsPerMeter = factor * origPixelsPerMeter;
		
		double newWidth =  panelAABB.width / pixelsPerMeter;
		double newHeight = panelAABB.height / pixelsPerMeter;
		
		worldViewport.setShape(
				(worldViewport.x + worldViewport.width/2) - newWidth/2,
				(worldViewport.y + worldViewport.height/2) - newHeight/2, newWidth, newHeight);
	}
	
	public void panRelative(Point worldDP) {
		
		worldViewport.setShape( 
				worldViewport.x + worldDP.x,
				worldViewport.y + worldDP.y,
				worldViewport.width,
				worldViewport.height);
	}
	
	public void panAbsolute(Point worldDP) {
		
		worldViewport.setShape( 
				worldDP.x,
				worldDP.y,
				worldViewport.width,
				worldViewport.height);
	}
	
	public void panAbsolute(double x, double y) {
		
		worldViewport.setShape( 
				x,
				y,
				worldViewport.width,
				worldViewport.height);
	}
}
