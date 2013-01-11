package com.gutabi.deadlock.ui;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public interface ContentPane extends MotionListener {

	public List<Panel> getChildren();
	
	public Point getLastMovedContentPanePoint();
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);
	
	public void postDisplay();
	
	public void repaint();
	
	public void paint(RenderingContext ctxt);
}
