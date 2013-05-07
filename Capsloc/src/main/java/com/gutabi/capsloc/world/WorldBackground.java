package com.gutabi.capsloc.world;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class WorldBackground {
	
	World world;
	
	private Image img;
	
	int panelWidth;
	int panelHeight;
	
	public WorldBackground(World world) {
		this.world = world;
	}
	
	public void panelPostDisplay(int width, int height) {
		
		panelWidth = width;
		panelHeight = height;
		
	}
	
	
	public void render() {
		
		img = APP.platform.createImage((int)(world.quadrantMap.worldWidth * world.worldCamera.origPixelsPerMeter), (int)(world.quadrantMap.worldHeight * world.worldCamera.origPixelsPerMeter));
		
		RenderingContext ctxt = APP.platform.createRenderingContext();
		APP.platform.setRenderingContextFields1(ctxt, img);
		
//		AABB fill = new AABB(0, 0, img.getWidth(), img.getHeight());
//		ctxt.setColor(Color.RED);
//		fill.paint(ctxt);
		
		ctxt.cam = world.worldCamera;
		
		ctxt.scale(world.worldCamera.pixelsPerMeter);
//		ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
		
		world.quadrantMap.paint_panel(ctxt);
		world.graph.paint_panel(ctxt);
		
		ctxt.dispose();
	}
	
	public void paint_pixels(RenderingContext ctxt) {
		
//		AABB fill = new AABB(0, 0, panelWidth, panelHeight);
//		ctxt.setColor(Color.RED);
//		fill.paint(ctxt);
		
//		ctxt.pushTransform();
		
//		int x = (int)((world.worldCamera.origWorldViewport.x-world.worldCamera.worldViewport.x) * world.worldCamera.pixelsPerMeter);
//		int y = (int)((world.worldCamera.origWorldViewport.y-world.worldCamera.worldViewport.y) * world.worldCamera.pixelsPerMeter);
		
		Point p = Point.worldToBackgroundImage(world.worldCamera.worldViewport.x, world.worldCamera.worldViewport.y, world.worldCamera);
		
		ctxt.paintImage(img, 0, 0, panelWidth, panelHeight, (int)p.x, (int)p.y, (int)p.x+panelWidth, (int)p.y+panelHeight);
		
//		ctxt.popTransform();
	}
	
	public void paint_worldCoords(RenderingContext ctxt) {
		
//		ctxt.setColor(Color.RED);
//		world.worldEdge.draw(ctxt);
		
//		world.quadrantMap.paint_panel(ctxt);
//		world.graph.paint_panel(ctxt);
		
//		ctxt.paintImage(img, 0, 0, width, height, 0, 0, width, height);
//		ctxt.paintImage(img, 1.0, ctxt.cam.worldViewport.x, ctxt.cam.worldViewport.y, ctxt.cam.worldViewport.x+ctxt.cam.worldViewport.width, ctxt.cam.worldViewport.y+ctxt.cam.worldViewport.height, 0, 0, width, height);
	}
	
}
