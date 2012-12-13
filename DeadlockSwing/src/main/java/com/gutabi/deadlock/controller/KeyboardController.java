package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;

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
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.qKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void wKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.wKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void gKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.gKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void deleteKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.deleteKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void insertKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.insertKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void escKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.escKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void d1Key() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.d1Key();
			break;
		case MENU:
			break;
		}
	}
	
	public void d2Key() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.d2Key();
			break;
		case MENU:
			break;
		}
	}

	public void d3Key() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.d3Key();
			break;
		case MENU:
			break;
		}
	}
	
	public void plusKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.plusKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void minusKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.minusKey();
			break;
		case MENU:
			break;
		}
	}
	
	public void downKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			break;
		case MENU:
			APP.menu.downKey();
			break;
		}
	}

	public void upKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			break;
		case MENU:
			APP.menu.upKey();
			break;
		}
	}
	
	public void enterKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			break;
		case MENU:
			APP.menu.enterKey();
			break;
		}
		
	}
	
	public void aKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.aKey();
			break;
		case MENU:
			break;
		}
		
	}
	
	public void sKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.sKey();
			break;
		case MENU:
			break;
		}
		
	}
	
	public void dKey() {
		switch (CONTROLLER.mode) {
		case WORLD:
			APP.world.dKey();
			break;
		case MENU:
			break;
		}
		
	}
	
}
