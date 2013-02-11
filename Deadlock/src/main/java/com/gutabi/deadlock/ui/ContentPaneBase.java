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
	public Point getLastMovedContentPanePoint() {
		return lastMovedContentPanePoint;
	}
	
	public void moved(InputEvent e) {
		Point p = e.p;
		lastMovedContentPanePoint = p;
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.moved(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void clicked(InputEvent e) {
		Point p = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.clicked(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void pressed(InputEvent e) {
		Point p = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.pressed(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void released(InputEvent e) {
		Point p = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.released(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void dragged(InputEvent e) {
		Point p = e.p;
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.dragged(new InputEvent(p.minus(child.aabb.ul)));
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
