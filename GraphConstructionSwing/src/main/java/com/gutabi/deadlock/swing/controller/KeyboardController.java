package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;
import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardController implements KeyListener {
	
	@Override
	public void keyTyped(KeyEvent e) {
		;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		switch (code) {
		case KeyEvent.VK_RIGHT:
			VIEW.moveCameraRight();
			PLATFORMVIEW.repaint();
			break;
		case KeyEvent.VK_LEFT:
			VIEW.moveCameraLeft();
			PLATFORMVIEW.repaint();
			break;
		case KeyEvent.VK_UP:
			VIEW.moveCameraUp();
			PLATFORMVIEW.repaint();
			break;
		case KeyEvent.VK_DOWN:
			VIEW.moveCameraDown();
			PLATFORMVIEW.repaint();
			break;
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_EQUALS:
			VIEW.zoomIn();
			PLATFORMVIEW.repaint();
			break;
		case KeyEvent.VK_MINUS:
			VIEW.zoomOut();
			PLATFORMVIEW.repaint();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		;
	}
	
}
