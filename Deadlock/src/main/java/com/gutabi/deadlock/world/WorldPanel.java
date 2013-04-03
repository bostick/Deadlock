package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class WorldPanel extends PanelBase {
	
	public World world;
	
	public WorldCamera worldCamera;
	
	public Stats stats;
	
	public WorldPanel() {
		
		worldCamera = new WorldCamera(this);
		
		stats = new Stats(this);
		
		aabb = new AABB(aabb.x, aabb.y, APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
		world.panelPostDisplay(worldCamera);
		
		worldCamera.worldViewport = new AABB( 
				-(aabb.width / worldCamera.pixelsPerMeter) / 2 + world.quadrantMap.worldWidth/2 ,
				-(aabb.height / worldCamera.pixelsPerMeter) / 2 + world.quadrantMap.worldHeight/2,
				aabb.width / worldCamera.pixelsPerMeter,
				aabb.height / worldCamera.pixelsPerMeter);
		
		worldCamera.origWorldViewport = worldCamera.worldViewport;
	}
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	Point lastClickedPanelPoint;
	
	public void pressed(InputEvent ev) {
		
		switch (world.mode) {
		case PAUSED:
		case RUNNING:
		case EDITING:
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.pressed(new InputEvent(p));
			
			APP.tool.pressed(new InputEvent(p));
			
			break;
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		switch (world.mode) {
		case RUNNING:
		case PAUSED: {
			lastMovedOrDraggedPanelPoint = ev.p;
			
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.dragged(new InputEvent(p));
			APP.tool.dragged(new InputEvent(p));
			break;
		}
		case EDITING: {
			lastMovedOrDraggedPanelPoint = ev.p;
			
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.dragged(new InputEvent(p));
			APP.tool.dragged(new InputEvent(p));
			break;
		}
		}
		
	}
	
	public void released(InputEvent ev) {
		
		switch (world.mode) {
		case RUNNING:
		case PAUSED:
			APP.tool.released(ev);
		case EDITING:
			APP.tool.released(ev);
			break;
		}
		
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
		
		switch (world.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.moved(new InputEvent(p));
			APP.tool.moved(new InputEvent(p));
			break;
		}
		case EDITING: {
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.moved(new InputEvent(p));
			APP.tool.moved(new InputEvent(p));
			break;
		}
		}
	}
	
	public void clicked(InputEvent ev) {
		
		switch (world.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.clicked(new InputEvent(p));
			APP.tool.clicked(new InputEvent(p));
			break;
		}
		case EDITING: {
			Point p = Point.panelToWorld(ev.p, worldCamera);
			
			world.clicked(new InputEvent(p));
			APP.tool.clicked(new InputEvent(p));
			break;
		}
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.cam = worldCamera;
		
		Transform origTrans = ctxt.getTransform();
		
		world.paint_panel_pixels(ctxt);
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.scale(worldCamera.pixelsPerMeter);
		ctxt.translate(-worldCamera.worldViewport.x, -worldCamera.worldViewport.y);
		
		world.paint_panel_worldCoords(ctxt);
		
		APP.tool.draw(ctxt);
		
		if (APP.FPS_DRAW) {
			
			stats.paint(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
		APP.tool.draw_pixels(ctxt);
		
	}

}
