package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Panel;

public class QuadrantEditorContentPane implements ContentPane {
	
	ContentPane cp;
	public QuadrantEditor screen;
	
	public QuadrantEditorPanel panel;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		
		this.cp = APP.platform.createContentPane(this);
		
		panel = new QuadrantEditorPanel(screen) {{
			setLocation(0, 0);
		}};
		
		cp.getChildren().add(panel);
	}
	
	public void escKey() {
		
		MainMenu s = new MainMenu();
		
		APP.platform.setupScreen(s.contentPane);
		
		s.postDisplay();
		
		s.contentPane.panel.render();
		s.contentPane.repaint();
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

//	public void enableKeyListener() {
//		cp.enableKeyListener();
//	}
//
//	public void disableKeyListener() {
//		cp.disableKeyListener();
//	}

	public void ctrlOKey() {
		// TODO Auto-generated method stub
		
	}

	public void dKey() {
		// TODO Auto-generated method stub
		
	}

	public void upKey() {
		// TODO Auto-generated method stub
		
	}

	public void enterKey() {
		// TODO Auto-generated method stub
		
	}

	public void aKey() {
		// TODO Auto-generated method stub
		
	}

	public void sKey() {
		// TODO Auto-generated method stub
		
	}

	public void ctrlSKey() {
		// TODO Auto-generated method stub
		
	}

	public void downKey() {
		// TODO Auto-generated method stub
		
	}

	public void minusKey() {
		// TODO Auto-generated method stub
		
	}

	public void plusKey() {
		// TODO Auto-generated method stub
		
	}

	public void d3Key() {
		// TODO Auto-generated method stub
		
	}

	public void d2Key() {
		// TODO Auto-generated method stub
		
	}

	public void d1Key() {
		// TODO Auto-generated method stub
		
	}

	public void gKey() {
		// TODO Auto-generated method stub
		
	}

	public void wKey() {
		// TODO Auto-generated method stub
		
	}

	public void qKey() {
		// TODO Auto-generated method stub
		
	}

	public void deleteKey() {
		// TODO Auto-generated method stub
		
	}

	public void insertKey() {
		// TODO Auto-generated method stub
		
	}

	public void fKey() {
		// TODO Auto-generated method stub
		
	}

	public void postDisplay() {
		cp.postDisplay();
	}
}
