package com.gutabi.deadlock.world;

import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class WorldBackground {
	
	World world;
	
//	private Image img;
	
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
	
	public void clear(RenderingContext ctxt) {
		ctxt.setColor(Color.LIGHT_GRAY);
		ctxt.fillRect(0, 0, (int)world.worldScreen.contentPane.worldPanel.aabb.width, (int)world.worldScreen.contentPane.worldPanel.aabb.height);
	}
	
	public void paint(RenderingContext ctxt) {
		
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
