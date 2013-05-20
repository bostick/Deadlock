package com.brentonbostick.capsloc.ui;

import java.util.ArrayList;
import java.util.List;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public abstract class PlatformContentPane implements Paintable {
	
	protected List<Panel> children = new ArrayList<Panel>();
	
	public PlatformContentPane() {
		
	}
	
	public List<Panel> getChildren() {
		return children;
	}
	
	private Point lastPressedContentPanePoint;
	private Point lastMovedContentPanePoint;
	private Point lastDraggedContentPanePoint;
	
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
		if (p == null) {
			throw new IllegalArgumentException();
		}
		lastPressedContentPanePoint = p;
		lastDraggedContentPanePoint = null;
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.pressed(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void releasedDriver(Point p) {
		if (lastPressedContentPanePoint == null) {
			throw new IllegalStateException();
		}
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.released(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void canceledDriver() {
		if (lastPressedContentPanePoint == null) {
			throw new IllegalStateException();
		}
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.canceled(new InputEvent(child, null));
				return;
			}
		}
	}
	
	public void draggedDriver(Point p) {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		if (lastPressedContentPanePoint == null) {
			throw new IllegalStateException();
		}
		if (lastDraggedContentPanePoint == null) {
			if (p.equals(lastPressedContentPanePoint)) {
				return;
			}
		} else {
			if (p.equals(lastDraggedContentPanePoint)) {
				return;
			}
		}
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.dragged(new InputEvent(child, Point.contentPaneToPanel(p, child)));
				return;
			}
		}
	}
	
	public void postDisplay(int width, int height) {
		for (Panel child : children) {
			child.postDisplay(width, height);
		}
	}
	
	public abstract void repaint();
	
	public void paint(RenderingContext ctxt) {
		
		for (int i = 0; i < children.size(); i++) {
			Panel child = children.get(i);
			child.paint(ctxt);
		}
		
	}
	
}
