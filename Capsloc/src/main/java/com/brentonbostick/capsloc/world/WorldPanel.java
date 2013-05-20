package com.brentonbostick.capsloc.world;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.ui.Panel;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public class WorldPanel extends Panel {
	
	public WorldPanel() {
		
	}
	
	public void postDisplay(int width, int height) {
		
		World world = (World)APP.model;
		
		aabb = new AABB(aabb.x, aabb.y, width, height);
		
		world.worldCamera.panelAABB = aabb;
		
		world.postDisplay(width, height);
	}
	
	public void paint(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		ctxt.cam = world.worldCamera;
		
		ctxt.pushTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		world.paint(ctxt);
		
		APP.tool.paint_panel(ctxt);
		
		ctxt.popTransform();
	}

}
