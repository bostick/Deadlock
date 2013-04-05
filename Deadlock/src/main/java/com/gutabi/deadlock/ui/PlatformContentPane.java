package com.gutabi.deadlock.ui;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class PlatformContentPane implements MotionListener {
	
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
	
	public void moved(InputEvent e) {
		lastMovedContentPanePoint = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(e.p)) {
				child.moved(new InputEvent(Point.contentPaneToPanel(e.p, child)));
				return;
			}
		}
	}
	
	public void clicked(InputEvent e) {
		for (Panel child : children) {
			if (child.aabb.hitTest(e.p)) {
				child.clicked(new InputEvent(Point.contentPaneToPanel(e.p, child)));
				return;
			}
		}
	}
	
	public void pressed(InputEvent e) {
		lastPressedContentPanePoint = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.pressed(new InputEvent(Point.contentPaneToPanel(e.p, child)));
				return;
			}
		}
	}
	
	public void released(InputEvent e) {
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.released(new InputEvent(Point.contentPaneToPanel(e.p, child)));
				return;
			}
		}
	}
	
	public void dragged(InputEvent e) {
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.dragged(new InputEvent(Point.contentPaneToPanel(e.p, child)));
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
