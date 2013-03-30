package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class DebuggerScreenContentPane implements ContentPane {
	
	public PlatformContentPane pcp;
	
	public ControlPanel controlPanel;
	
	public DebuggerScreenContentPane(DebuggerScreen debuggerScreen, final WorldScreen worldScreen) {
		this.pcp = APP.platform.createPlatformContentPane(debuggerScreen);
		
		controlPanel = new ControlPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(controlPanel);
	}
	
	public void repaint() {
		pcp.repaint();
	}

	public List<Panel> getChildren() {
		return pcp.getChildren();
	}

	public Point getLastMovedContentPanePoint() {
		return pcp.getLastMovedContentPanePoint();
	}

	public void postDisplay() {
		pcp.postDisplay();
	}
	
	public void clicked(InputEvent ev) {
		pcp.clicked(ev);
	}
	
	public void paint(RenderingContext ctxt) {
		pcp.paint(ctxt);
	}
	
	
	@Override
	public void pressed(InputEvent ev) {
		pcp.pressed(ev);
	}

	@Override
	public void released(InputEvent ev) {
		pcp.released(ev);
	}

	@Override
	public void moved(InputEvent ev) {
		pcp.moved(ev);
	}

	@Override
	public void dragged(InputEvent ev) {
		pcp.dragged(ev);
	}
	
	
}
