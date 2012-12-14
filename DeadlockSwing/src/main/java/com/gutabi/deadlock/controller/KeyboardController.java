package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//@SuppressWarnings("serial")
public class KeyboardController implements KeyListener {
	
	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_INSERT) {
			insertKey();
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteKey();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			escKey();
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			qKey();
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			wKey();
		} else if (e.getKeyCode() == KeyEvent.VK_G) {
			gKey();
		} else if (e.getKeyCode() == KeyEvent.VK_1) {
			d1Key();
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			d2Key();
		} else if (e.getKeyCode() == KeyEvent.VK_3) {
			d3Key();
		} else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS) {
			plusKey();
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			minusKey();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			downKey();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			upKey();
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			enterKey();
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			aKey();
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			sKey();
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			dKey();
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public void qKey() {
		APP.screen.qKey();
	}
	
	public void wKey() {
		APP.screen.wKey();
	}
	
	public void gKey() {
		APP.screen.gKey();
	}
	
	public void deleteKey() {
		APP.screen.deleteKey();
	}
	
	public void insertKey() {
		APP.screen.insertKey();
	}
	
	public void escKey() {
		APP.screen.escKey();
	}
	
	public void d1Key() {
		APP.screen.d1Key();
	}
	
	public void d2Key() {
		APP.screen.d2Key();
	}
	
	public void d3Key() {
		APP.screen.d3Key();
	}
	
	public void plusKey() {
		APP.screen.plusKey();
	}
	
	public void minusKey() {
		APP.screen.minusKey();
	}
	
	public void downKey() {
		APP.screen.downKey();
	}

	public void upKey() {
		APP.screen.upKey();
	}
	
	public void enterKey() {
		APP.screen.enterKey();
	}
	
	public void aKey() {
		APP.screen.aKey();
	}
	
	public void sKey() {
		APP.screen.sKey();
	}
	
	public void dKey() {
		APP.screen.dKey();
	}
	
}
