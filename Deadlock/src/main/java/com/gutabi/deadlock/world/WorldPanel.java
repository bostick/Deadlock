package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class WorldPanel extends Panel {
	
	public WorldPanel() {
		
	}
	
	public void postDisplay(int width, int height) {
		World world = (World)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		world.worldCamera.panelAABB = aabb;
		
		world.panelPostDisplay(width, height);
	}
	
	public void paint(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		ctxt.cam = world.worldCamera;
		
		ctxt.pushTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		world.paint_panel(ctxt);
		
		APP.tool.paint_panel(ctxt);
		
		ctxt.popTransform();
	}

}
