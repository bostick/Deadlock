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
		
		if (world.previewImage != null) {
			world.render_preview();
		}
	}

	public void plusKey() {
		World world = (World)APP.model;
		
		world.worldCamera.zoomRelative(1.1);
		
		world.quadrantMap.computeGridSpacing(world.worldCamera);
		
		world.render_worldPanel();
		world.render_preview();
		
	}

	public void minusKey() {
		World world = (World)APP.model;
		
		world.worldCamera.zoomRelative(0.9);
		
		world.quadrantMap.computeGridSpacing(world.worldCamera);
		
		world.render_worldPanel();
		
		if (world.previewImage != null) {
			world.render_preview();
		}
	}
	
	public Motion lastMotion;
	
	public void pressed(InputEvent ev) {
		World world = (World)APP.model;
		
		lastMotion = Motion.PRESSED;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.pressed(new InputEvent(ev.panel, p));
		
	}
	
	public void dragged(InputEvent ev) {
		World world = (World)APP.model;
		
		lastMotion = Motion.DRAGGED;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.dragged(new InputEvent(ev.panel, p));
		
	}
	
	public void released(InputEvent ev) {
		
		lastMotion = Motion.RELEASED;
		
	}
	
	public void moved(InputEvent ev) {
		World world = (World)APP.model;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.moved(new InputEvent(ev.panel, p));
		
	}
	
	public void clicked(InputEvent ev) {
		World world = (World)APP.model;
		
		Point p = Point.panelToWorld(ev.p, world.worldCamera);
		
		world.clicked(new InputEvent(ev.panel, p));
		
	}

}
