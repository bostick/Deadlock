package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.List;

import javax.swing.JFrame;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.Panel;

//@SuppressWarnings("serial")
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
		
		APP.platform.setupScreen(APP.container, s.contentPane);
		((JFrame)APP.container).setVisible(true);
		s.postDisplay();
		
		s.contentPane.panel.render();
		s.contentPane.repaint();
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
	public void ctrlOKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void upKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctrlSKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void minusKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void plusKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void d3Key() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void d2Key() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void d1Key() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void qKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fKey() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postDisplay() {
		cp.postDisplay();
	}
}
