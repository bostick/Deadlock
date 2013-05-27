package com.brentonbostick.capsloc.world;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.Image;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public class WorldBackground {
	
	public enum RenderMethod {
		MONOLITHIC,
		DYNAMIC,
		RENDERED_GRAPH,
		RENDERED_ROADS,
		RENDERED_ROADS_VERTICES,
		RENDERED_ROADS_VERTICES_BOARDS
		
	}
	
	public final RenderMethod method = RenderMethod.DYNAMIC;
	
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
		
		switch (method) {
		case MONOLITHIC: {
			
			img = APP.platform.createImage((int)(world.quadrantMap.worldAABB.width * world.worldCamera.origPixelsPerMeter), (int)(world.quadrantMap.worldAABB.height * world.worldCamera.origPixelsPerMeter));
			
			RenderingContext ctxt = APP.platform.createRenderingContext();
			APP.platform.setRenderingContextFields1(ctxt, img);
			
			ctxt.cam = world.worldCamera;
			
			ctxt.scale(world.worldCamera.pixelsPerMeter, world.worldCamera.pixelsPerMeter);
			ctxt.translate(0, 0);
			
			world.quadrantMap.paint_panel(ctxt);
			world.graph.paint_panel(ctxt);
			
			ctxt.dispose();
			
			break;
		}
		case DYNAMIC:
			world.graph.render();
			break;
		case RENDERED_GRAPH:
			world.graph.render();
			break;
		case RENDERED_ROADS:
		case RENDERED_ROADS_VERTICES:
		case RENDERED_ROADS_VERTICES_BOARDS:
			world.graph.render();
			break;
		}
	}
	
	public void paint_pixels(RenderingContext ctxt) {
		
		switch (method) {
		case MONOLITHIC: {
			
			Point p = new Point(world.worldCamera.worldViewport.x * world.worldCamera.pixelsPerMeter, world.worldCamera.worldViewport.y * world.worldCamera.pixelsPerMeter);
			
			ctxt.paintImage(img, 0, 0, panelWidth, panelHeight, (int)p.x, (int)p.y, (int)p.x+panelWidth, (int)p.y+panelHeight);
			
			break;
		}
		case DYNAMIC:
			break;
		case RENDERED_GRAPH:
//			world.quadrantMap.paint_panel(ctxt);
			break;
		case RENDERED_ROADS:
		case RENDERED_ROADS_VERTICES:
		case RENDERED_ROADS_VERTICES_BOARDS:
//			world.quadrantMap.paint_panel(ctxt);
			break;
		}
	}
	
	public void paint_worldCoords(RenderingContext ctxt) {
		
		switch (method) {
		case MONOLITHIC:
			break;
		case DYNAMIC:
			world.quadrantMap.paint_panel(ctxt);
			world.graph.paint_panel(ctxt);
			break;
		case RENDERED_GRAPH:
			world.quadrantMap.paint_panel(ctxt);
			world.graph.paint_panel(ctxt);
			break;
		case RENDERED_ROADS:
		case RENDERED_ROADS_VERTICES:
		case RENDERED_ROADS_VERTICES_BOARDS:
			world.quadrantMap.paint_panel(ctxt);
			world.graph.paint_panel(ctxt);
			break;
		}
		
	}
	
}
