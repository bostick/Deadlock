package com.gutabi.deadlock.ui;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class PlatformContentPane {
	
	protected List<Panel> children = new ArrayList<Panel>();
	
	public PlatformContentPane() {
		
	}
	
	public List<Panel> getChildren() {
		return children;
	}
	
	private Point lastMovedContentPanePoint;
	private Point lastPressedContentPanePoint;
	
	public Point getLastMovedContentPanePoint() {
		return lastMovedContentPanePoint;
	}
	
	public void movedDriver(Point p) {
		lastMovedContentPanePoint = p;
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.moved(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void clickedDriver(Point p) {
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.clicked(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void pressedDriver(Point p) {
		lastPressedContentPanePoint = p;
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.pressed(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void releasedDriver(Point p) {
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.released(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void draggedDriver(Point p) {
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.dragged(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void postDisplay() {
		for (Panel child : children) {
			child.postDisplay();
		}
	}
	
	public abstract void repaint();
	
	public void paint(RenderingContext ctxt) {
		
		for (Panel child : getChildren()) {
			child.paint(ctxt);
		}
		
	}
	
}
