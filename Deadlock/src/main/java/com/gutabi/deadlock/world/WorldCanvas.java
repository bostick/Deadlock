package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.RenderingContext;

public class WorldCanvas extends PanelBase {
	
	WorldScreen screen;
	
	static Logger logger = Logger.getLogger(WorldCanvas.class);
	
	public WorldCanvas(final WorldScreen screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, 1384, 822);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
		screen.world.canvasPostDisplay(new Dim(aabb.width, aabb.height));
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	Point lastClickedCanvasPoint;
	
	public Point lastPressedWorldPoint;
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressedWorldPoint = screen.canvasToWorld(p);
		lastDraggedWorldPoint = null;
		
	}
	
	public void dragged(InputEvent ev) {
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			screen.tool.dragged(ev);
			break;
		}
		}
		
	}
	
	public void released(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.released(ev);
			break;
		}
		
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastMovedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastMovedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			screen.tool.moved(ev);
			break;
		}
		}
	}
	
	public void exited(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.exited(ev);
			break;
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.cam = screen.cam;
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		screen.world.paint_canvas(ctxt);
		
		ctxt.scale(ctxt.cam.pixelsPerMeter);
		ctxt.translate(-ctxt.cam.worldViewport.x, -ctxt.cam.worldViewport.y);
		
		Entity hilitedCopy;
		synchronized (APP) {
			hilitedCopy = screen.hilited;
		}
		
		if (hilitedCopy != null) {
			hilitedCopy.paintHilite(ctxt);
		}
		
		screen.tool.draw(ctxt);
		
		if (APP.FPS_DRAW) {
			
			ctxt.translate(ctxt.cam.worldViewport.x, ctxt.cam.worldViewport.y);
			
			screen.stats.paint(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}

}
