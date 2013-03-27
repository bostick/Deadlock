package com.gutabi.deadlock.ui;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class ContentPaneBase implements ContentPane, MotionListener {
	
	protected List<Panel> children = new ArrayList<Panel>();
	
	public ContentPaneBase() {
		
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
				child.moved(new InputEvent(e.p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void clicked(InputEvent e) {
		for (Panel child : children) {
			if (child.aabb.hitTest(e.p)) {
				child.clicked(new InputEvent(e.p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void pressed(InputEvent e) {
		lastPressedContentPanePoint = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.pressed(new InputEvent(e.p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void released(InputEvent e) {
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.released(new InputEvent(e.p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void dragged(InputEvent e) {
		for (Panel child : children) {
			if (child.aabb.hitTest(lastPressedContentPanePoint)) {
				child.dragged(new InputEvent(e.p.minus(child.aabb.ul)));
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
