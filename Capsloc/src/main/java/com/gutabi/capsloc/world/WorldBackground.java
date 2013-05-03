package com.gutabi.capsloc.world;

import com.gutabi.capsloc.ui.paint.RenderingContext;

public class WorldBackground {
	
	World world;
	
//	private Image img;
	int width;
	int height;
	
	public WorldBackground(World world) {
		this.world = world;
	}
	
	public void panelPostDisplay(int width, int height) {
		
		this.width = width;
		this.height = height;
		
//		img = APP.platform.createImage(width+100, height+100);
		
	}
	
	public void render() {
		
//		RenderingContext ctxt = APP.platform.createRenderingContext();
//		APP.platform.setRenderingContextFields1(ctxt, img);
//		
//		ctxt.cam = world.worldCamera;
//		
//		ctxt.setColor(Color.LIGHT_GRAY);
//		ctxt.fillRect(0, 0, (int)world.worldCamera.panelAABB.width+100, (int)world.worldCamera.panelAABB.height+100);
//		
//		ctxt.translate(50, 50);
//		
//		ctxt.scale(world.worldCamera.pixelsPerMeter);
//		ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
//		
//		world.quadrantMap.paint_panel(ctxt);
//		world.graph.paint_panel(ctxt);
//		
//		ctxt.dispose();
	}
	
	public void paint_pixels(RenderingContext ctxt) {
//		ctxt.setColor(Color.LIGHT_GRAY);
//		ctxt.fillRect(0, 0, width, height);
		
//		ctxt.pushTransform();
//		
//		int x = (int)((world.worldCamera.origWorldViewport.x-world.worldCamera.worldViewport.x) * world.worldCamera.pixelsPerMeter);
//		int y = (int)((world.worldCamera.origWorldViewport.y-world.worldCamera.worldViewport.y) * world.worldCamera.pixelsPerMeter);
//		
//		ctxt.translate(-50, -50);
//		
//		ctxt.paintImage(img, x, y, x+width, y+height, 0, 0, width, height);
//		
//		ctxt.popTransform();
	}
	
	public void paint_worldCoords(RenderingContext ctxt) {
		
		world.quadrantMap.paint_panel(ctxt);
		world.graph.paint_panel(ctxt);
		
//		ctxt.paintImage(img, 0, 0, width, height, 0, 0, width, height);
//		ctxt.paintImage(img, 1.0, ctxt.cam.worldViewport.x, ctxt.cam.worldViewport.y, ctxt.cam.worldViewport.x+ctxt.cam.worldViewport.width, ctxt.cam.worldViewport.y+ctxt.cam.worldViewport.height, 0, 0, width, height);
	}
	
}
