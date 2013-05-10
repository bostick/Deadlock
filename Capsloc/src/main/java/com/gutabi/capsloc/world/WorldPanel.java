package com.gutabi.capsloc.world;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.ui.Panel;
import com.gutabi.capsloc.ui.paint.RenderingContext;

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
