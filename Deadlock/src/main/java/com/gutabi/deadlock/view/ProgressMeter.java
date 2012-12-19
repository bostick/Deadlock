package com.gutabi.deadlock.view;

import java.awt.Color;

import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.world.World;

public class ProgressMeter {
	
	public final World world;
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	
	public final AABB aabb;
	
	private AABB progressAABB;
	
	private double p;
	
	public ProgressMeter(World world, double x, double y, double width, double height) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		aabb = new AABB(x, y, width, height);
		
		progressAABB = new AABB(x, y, 0.0, height);
		
	}
	
	public void setProgress(double p) {
		
		this.p = p;
		
		progressAABB = new AABB(x, y, Math.min(Math.max(0.0, p), 1.0) * width, height);
		
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.setColor(Color.BLACK);
		ctxt.setPixelStroke(world.cam.pixelsPerMeter, 1);
		aabb.draw(ctxt);
		if (p >= 0.0) {
			if (p <= 1.0) {
				ctxt.setColor(Color.BLUE);
				progressAABB.paint(ctxt);
			} else {
				ctxt.setColor(Color.RED);
				progressAABB.paint(ctxt);
			}
		}
	}
	
}
