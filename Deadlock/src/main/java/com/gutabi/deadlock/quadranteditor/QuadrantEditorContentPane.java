package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class QuadrantEditorContentPane implements ContentPane {
	
	public ContentPane cp;
	public QuadrantEditor screen;
	
	public QuadrantEditorPanel panel;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		
		this.cp = APP.platform.createContentPane(screen);
		
		panel = new QuadrantEditorPanel(screen) {{
			setLocation(0, 0);
		}};
		
		cp.getChildren().add(panel);
	}
	
	public void repaint() {
		cp.repaint();
	}

	public List<Panel> getChildren() {
		return cp.getChildren();
	}

	public Point getLastMovedContentPanePoint() {
		return cp.getLastMovedContentPanePoint();
	}
	
	public void clicked(InputEvent ev) {
		cp.clicked(ev);
	}
	
	public void postDisplay() {
		cp.postDisplay();
	}
	
	public void paint(RenderingContext ctxt) {
		cp.paint(ctxt);
	}
	
	
	@Override
	public void pressed(InputEvent ev) {
		cp.pressed(ev);
	}

	@Override
	public void released(InputEvent ev) {
		cp.released(ev);
	}

	@Override
	public void moved(InputEvent ev) {
		cp.moved(ev);
	}

	@Override
	public void dragged(InputEvent ev) {
		cp.dragged(ev);
	}
	
}
