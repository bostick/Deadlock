package com.gutabi.capsloc.world;

import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class WorldBackground {
	
	World world;
	
//	private Image img;
	int width;
	int height;
	
	public WorldBackground(World world) {
		this.world = world;
	}
	
	public void panelPostDisplay() {
		
//		img = APP.platform.createImage(
//				(int)world.worldScreen.contentPane.worldPanel.aabb.width,
//				(int)world.worldScreen.contentPane.worldPanel.aabb.height);
		
	}
	
	public void render() {
		
//		RenderingContext ctxt = APP.platform.createRenderingContext(img);
//		
//		ctxt.setColor(Color.LIGHT_GRAY);
//		ctxt.fillRect(0, 0, (int)world.worldScreen.contentPane.worldPanel.aabb.width, (int)world.worldScreen.contentPane.worldPanel.aabb.height);
//		
//		ctxt.scale(world.worldScreen.pixelsPerMeter);
//		ctxt.translate(-world.worldScreen.worldViewport.x, -world.worldScreen.worldViewport.y);
//		
//		world.quadrantMap.render_panel(ctxt);
//		world.graph.render_panel(ctxt);
//		
//		ctxt.dispose();
	}
	
	public void paint_pixels(RenderingContext ctxt) {
		ctxt.setColor(Color.LIGHT_GRAY);
		ctxt.fillRect(0, 0, width, height);
	}
	
	public void paint_worldCoords(RenderingContext ctxt) {
		
//		ctxt.paintImage(
//				img,
//				world.worldScreen.origPixelsPerMeter,
//				world.worldScreen.origWorldViewport.x,
//				world.worldScreen.origWorldViewport.y,
//				world.worldScreen.origWorldViewport.brX,
//				world.worldScreen.origWorldViewport.brY,
//				0, 0, img.getWidth(), img.getHeight());
		
		world.quadrantMap.render_panel(ctxt);
		world.graph.render_panel(ctxt);
		
	}
	
}
