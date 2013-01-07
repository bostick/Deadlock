package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Panel;

//@SuppressWarnings("serial")
public class WorldScreenContentPane implements ContentPane {
	
	public ContentPane cp;
	public WorldScreen screen;
	
	public WorldPanel worldPanel;
	public ControlPanel controlPanel;
	
	static Logger logger = Logger.getLogger(WorldScreenContentPane.class);
	
	public WorldScreenContentPane(WorldScreen screen) {
		this.cp = APP.platform.createContentPane(this);
		this.screen = screen;
		
		worldPanel = new WorldPanel(screen) {{
			setLocation(0, 0);
		}};
		
		controlPanel = new ControlPanel(screen) {{
			setLocation(0 + worldPanel.aabb.width, 0);
		}};
		
		cp.getChildren().add(worldPanel);
		cp.getChildren().add(controlPanel);
	}
	
	public void qKey() {
		screen.tool.qKey();
	}
	
	public void wKey() {
		screen.tool.wKey();
	}
	
	public void gKey() {
		screen.tool.gKey();
	}
	
	public void deleteKey() {
		screen.tool.deleteKey();
	}
	
	public void insertKey() {
		screen.tool.insertKey();
	}
	
	public void escKey() {
		screen.tool.escKey();
	}
	
	public void d1Key() {
		screen.tool.d1Key();
	}
	
	public void d2Key() {
		screen.tool.d2Key();
	}
	
	public void d3Key() {
		screen.tool.d3Key();
	}
	
	public void plusKey() {
		screen.tool.plusKey();
	}
	
	public void minusKey() {
		screen.tool.minusKey();
	}
	
	public void aKey() {
		screen.tool.aKey();
	}
	
	public void sKey() {
		screen.tool.sKey();
	}
	
	public void ctrlSKey() {
		screen.tool.ctrlSKey();
	}
	
	public void ctrlOKey() {
		screen.tool.ctrlOKey();
	}
	
	public void dKey() {
		screen.tool.dKey();
	}
	
	public void fKey() {
		screen.tool.fKey();
	}
	
	public void enterKey() {
		screen.tool.enterKey();
	}
	
	public void repaint() {
		cp.repaint();
	}

	@Override
	public List<Panel> getChildren() {
		return cp.getChildren();
	}

	@Override
	public Point getLastMovedContentPanePoint() {
		return cp.getLastMovedContentPanePoint();
	}

	@Override
	public void enableKeyListener() {
		cp.enableKeyListener();
	}

	@Override
	public void disableKeyListener() {
		cp.disableKeyListener();
	}

	@Override
	public void upKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postDisplay() {
		cp.postDisplay();
	}
}
