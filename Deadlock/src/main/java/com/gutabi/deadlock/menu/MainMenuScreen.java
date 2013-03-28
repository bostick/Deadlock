package com.gutabi.deadlock.menu;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.ui.KeyListener;

public class MainMenuScreen extends AppScreen implements KeyListener {
	
	public MainMenuContentPane contentPane;
	
	public MainMenu menu;
	
	public MainMenuScreen() {
		
		contentPane = new MainMenuContentPane(this);
		
		menu = new MainMenu();
		
	}
	
	public void postDisplay() {
		contentPane.postDisplay();
	}
	
	public void downKey() {
		
		if (menu.hilited == null) {
			
			menu.hilited = menu.firstMenuItem;
			
		} else {
			
			menu.hilited = menu.hilited.down;
			
		}
		
		while (!menu.hilited.active) {
			menu.hilited = menu.hilited.down;
		}
		
		contentPane.repaint();
	}
	
	public void upKey() {
		
		if (menu.hilited == null) {
			
			menu.hilited = menu.firstMenuItem;
			
		} else {
			
			menu.hilited = menu.hilited.up;
			
		}
		
		while (!menu.hilited.active) {
			menu.hilited = menu.hilited.up;
		}
		
		contentPane.repaint();
	}
	
	public void enterKey() {
		
		if (menu.hilited != null && menu.hilited.active) {
			menu.hilited.action();
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
