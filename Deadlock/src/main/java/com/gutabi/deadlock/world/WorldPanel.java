package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.AffineTransform;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class WorldPanel extends PanelBase {
	
	WorldScreen screen;
	
	static Logger logger = Logger.getLogger(WorldPanel.class);
	
	public WorldPanel(final WorldScreen screen) {
		this.screen = screen;
		
		aabb = APP.platform.createShapeEngine().createAABB(aabb.x, aabb.y, 1384, 822);
	}
	
	public void setLocation(double x, double y) {
		aabb = APP.platform.createShapeEngine().createAABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
		screen.world.panelPostDisplay();
	}
	
	
	public Point panelToWorld(Point p) {
		return new Point(
				p.x / screen.pixelsPerMeter + screen.worldViewport.x,
				p.y / screen.pixelsPerMeter + screen.worldViewport.y);
	}
	
	public AABB panelToWorld(AABB aabb) {
		Point ul = panelToWorld(aabb.ul);
		Point br = panelToWorld(aabb.br);
		return APP.platform.createShapeEngine().createAABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public Point worldToPanel(Point p) {
		return new Point(
				(p.x - screen.worldViewport.x) * screen.pixelsPerMeter,
				(p.y - screen.worldViewport.y) * screen.pixelsPerMeter);
	}
	
	public AABB worldToPanel(AABB aabb) {
		Point ul = worldToPanel(aabb.ul);
		Point br = worldToPanel(aabb.br);
		return APP.platform.createShapeEngine().createAABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	Point lastClickedPanelPoint;
	
	public void pressed(InputEvent ev) {
		
		switch (screen.mode) {
		case DIALOG:
			break;
		case PAUSED:
		case RUNNING:
		case EDITING:
			Point p = panelToWorld(ev.p);
			
			screen.world.pressed(new InputEvent(p));
			screen.tool.pressed(new InputEvent(p));
			
			break;
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			lastMovedOrDraggedPanelPoint = ev.p;
			
			Point p = panelToWorld(ev.p);
			
			screen.world.dragged(new InputEvent(p));
			screen.tool.dragged(new InputEvent(p));
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			lastMovedOrDraggedPanelPoint = ev.p;
			
			Point p = panelToWorld(ev.p);
			
			screen.world.dragged(new InputEvent(p));
			screen.tool.dragged(new InputEvent(p));
			break;
		}
		}
		
	}
	
	public void released(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
			screen.tool.released(ev);
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
			Point p = panelToWorld(ev.p);
			
			screen.world.moved(new InputEvent(p));
			screen.tool.moved(new InputEvent(p));
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = panelToWorld(ev.p);
			
			screen.world.moved(new InputEvent(p));
			screen.tool.moved(new InputEvent(p));
			break;
		}
		}
	}
	
	public void clicked(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = panelToWorld(ev.p);
			
			screen.world.clicked(new InputEvent(p));
			screen.tool.clicked(new InputEvent(p));
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = panelToWorld(ev.p);
			
			screen.world.clicked(new InputEvent(p));
			screen.tool.clicked(new InputEvent(p));
			break;
		}
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		screen.world.paint_panel(ctxt);
		
		ctxt.scale(screen.pixelsPerMeter);
		ctxt.translate(-screen.worldViewport.x, -screen.worldViewport.y);
		
		screen.tool.draw(ctxt);
		
		if (APP.FPS_DRAW) {
			
//			ctxt.translate(ctxt.screen.worldViewport.x, ctxt.screen.worldViewport.y);
			
			screen.stats.paint(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}

}
