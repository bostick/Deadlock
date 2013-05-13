package com.gutabi.capsloc.ui;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class Panel {
	
	public AABB aabb = new AABB();
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	public Point lastMovedMenuPoint;
	Point lastClickedPanelPoint;
	public Point lastPressedPanelPoint;
	public Point lastDraggedPanelPoint;
	
	public void pressed(InputEvent ev) {
		lastPressedPanelPoint = ev.p;
		lastDraggedPanelPoint = null;
		
		APP.tool.pressed(ev);
	}
	
	public void released(InputEvent ev) {
		
		APP.tool.released(ev);
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = ev.p;
		
		APP.tool.moved(ev);
	}
	
	public void dragged(InputEvent ev) {
		
		lastDraggedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = ev.p;
		
		APP.tool.dragged(ev);
	}
	
	public void clicked(InputEvent ev) {
		
		lastClickedPanelPoint = ev.p;
		
		APP.tool.clicked(ev);
	}
	
	public void canceled(InputEvent ev) {
		
		APP.tool.canceled(ev);
	}
	
	public abstract void postDisplay(int width, int height);
	
	public abstract void paint(RenderingContext ctxt);
	
}
