package com.gutabi.deadlock;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.core.geom.AABB;

public abstract class Screen {
	
	public double origPixelsPerMeter = 32.0;
	public double pixelsPerMeter = 32.0;
	public AABB worldViewport;
	
	public abstract void setup(RootPaneContainer container);
	
}
