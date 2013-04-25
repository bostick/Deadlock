package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class WorldPanel extends Panel {
	
	public WorldPanel() {
		
	}
	
	public void postDisplay(int width, int height) {
		World world = (World)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		world.worldCamera.worldPanel = this;
		
		world.worldCamera.worldViewport.setShape(
				-(aabb.width / world.worldCamera.pixelsPerMeter) / 2 + world.quadrantMap.worldWidth/2 ,
				-(aabb.height / world.worldCamera.pixelsPerMeter) / 2 + world.quadrantMap.worldHeight/2,
				aabb.width / world.worldCamera.pixelsPerMeter,
				aabb.height / world.worldCamera.pixelsPerMeter);
		
		world.worldCamera.origWorldViewport = world.worldCamera.worldViewport.copy();
		
		world.background.width = (int)aabb.width;
		world.background.height = (int)aabb.height;
		
		world.panelPostDisplay();
	}
	
	
	Transform origTransform = APP.platform.createTransform();
	
	public void paint(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		ctxt.cam = world.worldCamera;
		
		ctxt.getTransform(origTransform);
		
		ctxt.translate(aabb.x, aabb.y);
		
		world.paint_panel(ctxt);
		
		APP.tool.paint_panel(ctxt);
		
		ctxt.setTransform(origTransform);
	}

}
