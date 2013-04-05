package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class WorldPanel extends Panel {
	
	public WorldPanel() {
		aabb = new AABB(aabb.x, aabb.y, APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT);
	}
	
	public void postDisplay() {
		World world = (World)APP.model;
		
		world.worldCamera.worldPanel = this;
		
		world.worldCamera.worldViewport = new AABB(
				-(aabb.width / world.worldCamera.pixelsPerMeter) / 2 + world.quadrantMap.worldWidth/2 ,
				-(aabb.height / world.worldCamera.pixelsPerMeter) / 2 + world.quadrantMap.worldHeight/2,
				aabb.width / world.worldCamera.pixelsPerMeter,
				aabb.height / world.worldCamera.pixelsPerMeter);
		
		world.worldCamera.origWorldViewport = world.worldCamera.worldViewport;
		
		world.background.width = (int)aabb.width;
		world.background.height = (int)aabb.height;
		
		world.panelPostDisplay();
	}
	
	public void paint(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		ctxt.cam = world.worldCamera;
		
		Transform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		world.paint_panel(ctxt);
		
		APP.tool.paint_panel(ctxt);
		
		ctxt.setTransform(origTrans);
	}

}
