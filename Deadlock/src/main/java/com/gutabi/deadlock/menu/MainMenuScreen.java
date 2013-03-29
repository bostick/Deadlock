package com.gutabi.deadlock.menu;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.ui.KeyListener;

public class MainMenuScreen extends AppScreen implements KeyListener {
	
	public MainMenuContentPane contentPane;
	
	public MainMenuScreen() {
		
		contentPane = new MainMenuContentPane(this);
		
		contentPane.panel.menu = new MainMenu(contentPane.panel);
		
	}
	
	public void postDisplay() {
		contentPane.postDisplay();
	}
	
	public void downKey() {
		
		if (contentPane.panel.menu.hilited == null) {
			
			contentPane.panel.menu.hilited = contentPane.panel.menu.firstMenuItem;
			
		} else {
			
			contentPane.panel.menu.hilited = contentPane.panel.menu.hilited.down;
			
		}
		
		while (!contentPane.panel.menu.hilited.active) {
			contentPane.panel.menu.hilited = contentPane.panel.menu.hilited.down;
		}
		
		contentPane.repaint();
	}
	
	public void upKey() {
		
		if (contentPane.panel.menu.hilited == null) {
			
			contentPane.panel.menu.hilited = contentPane.panel.menu.firstMenuItem;
			
		} else {
			
			contentPane.panel.menu.hilited = contentPane.panel.menu.hilited.up;
			
		}
		
		while (!contentPane.panel.menu.hilited.active) {
			contentPane.panel.menu.hilited = contentPane.panel.menu.hilited.up;
		}
		
		contentPane.repaint();
	}
	
	public void enterKey() {
		
		if (contentPane.panel.menu.hilited != null && contentPane.panel.menu.hilited.active) {
			contentPane.panel.menu.hilited.action();
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
	
}
