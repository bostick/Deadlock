package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.RenderingContext;

public class WorldPanel extends PanelBase {
	
	WorldScreen screen;
	
	static Logger logger = Logger.getLogger(WorldPanel.class);
	
	public WorldPanel(final WorldScreen screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, 1384, 822);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
		screen.cam.panelWidth = (int)aabb.width;
		screen.cam.panelHeight = (int)aabb.height;
		
		screen.world.panelPostDisplay();
	}
	
	
	public Point panelToWorld(Point p) {
		return new Point(
				p.x / screen.cam.pixelsPerMeter + screen.cam.worldViewport.x,
				p.y / screen.cam.pixelsPerMeter + screen.cam.worldViewport.y);
	}
	
	public AABB panelToWorld(AABB aabb) {
		Point ul = panelToWorld(aabb.ul);
		Point br = panelToWorld(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point worldToPanel(Point p) {
		return new Point(
				(p.x - screen.cam.worldViewport.x) * screen.cam.pixelsPerMeter,
				(p.y - screen.cam.worldViewport.y) * screen.cam.pixelsPerMeter);
	}
	
	public AABB worldToPanel(AABB aabb) {
		Point ul = worldToPanel(aabb.ul);
		Point br = worldToPanel(aabb.br);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	Point lastClickedPanelPoint;
	
	public Point lastPressedWorldPoint;
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void pressed(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressedWorldPoint = panelToWorld(p);
		lastDraggedWorldPoint = null;
		
	}
	
	public void dragged(InputEvent ev) {
		
		lastMovedOrDraggedPanelPoint = ev.p;
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = panelToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = panelToWorld(p);
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
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastMovedWorldPoint = panelToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastMovedWorldPoint = panelToWorld(p);
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
		
		screen.world.paint_panel(ctxt);
		
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
