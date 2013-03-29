package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.menu.MainMenuScreen;
import com.gutabi.deadlock.ui.KeyListener;

public class QuadrantEditor extends AppScreen implements KeyListener {
	
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
		
		MainMenuScreen s = new MainMenuScreen();
		APP.setAppScreen(s);
		
		APP.platform.setupAppScreen(s.contentPane.cp);
		
		s.postDisplay();
		
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
	public void ctrlOKey() {
		
	}

	public void dKey() {
				
	}

	public void upKey() {
				
	}

	public void enterKey() {
				
	}

	public void aKey() {
				
	}

	public void sKey() {
				
	}

	public void ctrlSKey() {
				
	}

	public void downKey() {
				
	}

	public void minusKey() {
				
	}

	public void plusKey() {
				
	}

	public void d3Key() {
				
	}

	public void d2Key() {
				
	}

	public void d1Key() {
				
	}

	public void gKey() {
				
	}

	public void wKey() {
				
	}

	public void qKey() {
				
	}

	public void deleteKey() {
				
	}

	public void insertKey() {
				
	}

	public void fKey() {
				
	}
	
}
