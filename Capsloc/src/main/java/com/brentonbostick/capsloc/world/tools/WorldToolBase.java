package com.brentonbostick.capsloc.world.tools;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.Tool;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.InputEvent;
import com.brentonbostick.capsloc.world.World;

public abstract class WorldToolBase extends Tool {
	
	public WorldToolBase() {
		
	}
	
	public abstract Object getShape();
	
	public void gKey() {
		World world = (World)APP.model;
		
		world.quadrantMap.toggleGrid();
		
		setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
		world.render();
		
//		if (world.previewImage != null) {
//			world.render_preview();
//		}
	}

	public void plusKey() {
		World world = (World)APP.model;
		
		world.worldCamera.zoomRelative(1.1);
		
		world.quadrantMap.computeGridSpacing(world.worldCamera);
		
		world.render();
//		if (world.previewImage != null) {
//			world.render_preview();
//		}
		
	}

	public void minusKey() {
		World world = (World)APP.model;
		
		world.worldCamera.zoomRelative(0.9);
		
		world.quadrantMap.computeGridSpacing(world.worldCamera);
		
		world.render();
		
//		if (world.previewImage != null) {
//			world.render_preview();
//		}
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
		World world = (World)APP.model;
		
		lastMotion = Motion.RELEASED;
		
		world.released(new InputEvent(ev.panel, null));
	}
	
	public void canceled(InputEvent ev) {
		World world = (World)APP.model;
		
		lastMotion = Motion.CANCELED;
		
		world.canceled(new InputEvent(ev.panel, null));
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
