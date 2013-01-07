package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Panel;

//@SuppressWarnings("serial")
public class MainMenuContentPane implements ContentPane {
	
	public ContentPane cp;
	public MainMenu screen;
	
	public MenuPanel panel;
	
	public MainMenuContentPane(MainMenu screen) {
		this.cp = APP.platform.createContentPane(this);
		this.screen = screen;
		
		panel = new MenuPanel(screen) {{
			setLocation(0, 0);
		}};
		
		
		cp.getChildren().add(panel);
		
	}
	
	public List<Panel> getChildren() {
		return cp.getChildren();
	}
	
	public void downKey() {
		
		if (screen.hilited == null) {
			
			screen.hilited = screen.firstMenuItem;
			
		} else {
			
			screen.hilited = screen.hilited.down;
			
		}
		
		while (!screen.hilited.active) {
			screen.hilited = screen.hilited.down;
		}
		
		screen.contentPane.repaint();
	}
	
	public void upKey() {
		
		if (screen.hilited == null) {
			
			screen.hilited = screen.firstMenuItem;
			
		} else {
			
			screen.hilited = screen.hilited.up;
			
		}
		
		while (!screen.hilited.active) {
			screen.hilited = screen.hilited.up;
		}
		
		screen.contentPane.repaint();
	}
	
	public void enterKey() {
		
		if (screen.hilited != null && screen.hilited.active) {
			screen.hilited.action();
		}
		
	}
	
	public void ctrlOKey() {
		;
	}

	public void dKey() {
		;
	}

	public void aKey() {
		;
	}

	public void sKey() {
		;
	}

	public void ctrlSKey() {
		;
	}

	public void minusKey() {
		;
	}

	public void plusKey() {
		;
	}

	public void d3Key() {
		;
	}

	public void d2Key() {
		;
	}

	public void d1Key() {
		;
	}

	public void gKey() {
		;
	}

	public void wKey() {
		;
	}

	public void qKey() {
		;
	}

	public void escKey() {
		;
	}

	public void deleteKey() {
		;
	}

	public void insertKey() {
		;
	}
	
	public void fKey() {
		
	}
	
	public void enableKeyListener() {
		cp.enableKeyListener();
	}
	
	public void disableKeyListener() {
		cp.enableKeyListener();
	}
	
	public void postDisplay() {
		cp.postDisplay();
	}
	
	public void repaint() {
		cp.repaint();
	}
	
	public Point getLastMovedContentPanePoint() {
		return cp.getLastMovedContentPanePoint();
	}
}
