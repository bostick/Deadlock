package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.KeyListener;

public class QuadrantEditor implements KeyListener {
	
	public QuadrantEditorContentPane contentPane;
	
	int quadrantRows;
	int quadrantCols;
	
	AABB hilited;
	
	public QuadrantEditor() {
		
		contentPane = new QuadrantEditorContentPane(this);
		
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
	public void escKey() {
		
		MainMenu s = new MainMenu();
		
		APP.platform.setupAppScreen(s.contentPane);
		
		s.postDisplay();
		
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
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
	
	
	
}
