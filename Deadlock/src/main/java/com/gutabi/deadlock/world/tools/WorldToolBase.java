package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Tool;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.world.World;

public abstract class WorldToolBase extends Tool {
	
	public WorldToolBase() {
		
	}
	
	public abstract Shape getShape();
	
	public void gKey() {
		World world = (World)APP.model;
		
		world.quadrantMap.toggleGrid();
		
		setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
		world.render_worldPanel();
//		APP.appScreen.contentPane.repaint();
		
		if (world.previewImage != null) {
			world.render_preview();
//			APP.debuggerScreen.contentPane.repaint();
		}
	}

	public void plusKey() {
		World world = (World)APP.model;
		
		world.worldCamera.zoomRelative(1.1);
		
//		screen.contentPane.moved(screen.contentPane.getLastMovedContentPanePoint());
		
		world.quadrantMap.computeGridSpacing(world.worldCamera);
		
		world.render_worldPanel();
		world.render_preview();
		
//		APP.appScreen.contentPane.repaint();
//		APP.debuggerScreen.contentPane.repaint();
		
	}

	public void minusKey() {
		World world = (World)APP.model;
		
		world.worldCamera.zoomRelative(0.9);
		
//		screen.contentPane.moved(screen.contentPane.getLastMovedContentPanePoint());
		
		world.quadrantMap.computeGridSpacing(world.worldCamera);
		
		world.render_worldPanel();
//		APP.appScreen.contentPane.repaint();
		
		if (world.previewImage != null) {
			world.render_preview();
//			APP.debuggerScreen.contentPane.repaint();
		}
	}
	
	public void pressed(InputEvent ev) {
		World world = (World)APP.model;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.pressed(new InputEvent(ev.panel, p));
		
//		switch (world.mode) {
//		case PAUSED:
//		case RUNNING:
//		case EDITING:
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.pressed(new InputEvent(p));
//			
//			APP.tool.pressed(new InputEvent(p));
//			
//			break;
//		}
		
	}
	
	public void dragged(InputEvent ev) {
		World world = (World)APP.model;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.dragged(new InputEvent(ev.panel, p));
		
//		switch (world.mode) {
//		case RUNNING:
//		case PAUSED: {
//			lastMovedOrDraggedPanelPoint = ev.p;
//			
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.dragged(new InputEvent(p));
//			APP.tool.dragged(new InputEvent(p));
//			break;
//		}
//		case EDITING: {
//			lastMovedOrDraggedPanelPoint = ev.p;
//			
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.dragged(new InputEvent(p));
//			APP.tool.dragged(new InputEvent(p));
//			break;
//		}
//		}
		
	}
	
	public void released(InputEvent ev) {
//		World world = (World)APP.model;
//		
//		Point p = Point.panelToWorld(ev.p, world.worldCamera);
//		
//		world.released(new InputEvent(p));
		
//		switch (world.mode) {
//		case RUNNING:
//		case PAUSED:
//			APP.tool.released(ev);
//		case EDITING:
//			APP.tool.released(ev);
//			break;
//		}
		
	}
	
	public void moved(InputEvent ev) {
		World world = (World)APP.model;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.moved(new InputEvent(ev.panel, p));
		
//		lastMovedPanelPoint = ev.p;
//		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
//		
//		switch (world.mode) {
//		case RUNNING:
//		case PAUSED: {
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.moved(new InputEvent(p));
//			APP.tool.moved(new InputEvent(p));
//			break;
//		}
//		case EDITING: {
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.moved(new InputEvent(p));
//			APP.tool.moved(new InputEvent(p));
//			break;
//		}
//		}
	}
	
	public void clicked(InputEvent ev) {
		World world = (World)APP.model;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.clicked(new InputEvent(ev.panel, p));
		
//		switch (world.mode) {
//		case RUNNING:
//		case PAUSED: {
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.clicked(new InputEvent(p));
//			APP.tool.clicked(new InputEvent(p));
//			break;
//		}
//		case EDITING: {
//			Point p = Point.panelToWorld(ev.p, worldCamera);
//			
//			world.clicked(new InputEvent(p));
//			APP.tool.clicked(new InputEvent(p));
//			break;
//		}
//		}
	}

}
