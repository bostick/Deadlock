package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.gutabi.deadlock.core.Point;

public class KeyboardController implements KeyListener {
	
	public void init() {
		VIEW.panel.addKeyListener(this);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		e.getKeyChar();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		e.getKeyChar();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		switch (code) {
		case KeyEvent.VK_RIGHT:
			CONTROLLER.cameraUpperLeft = new Point(CONTROLLER.cameraUpperLeft.x+10, CONTROLLER.cameraUpperLeft.y);
			VIEW.repaint();
			break;
		case KeyEvent.VK_LEFT:
			CONTROLLER.cameraUpperLeft = new Point(CONTROLLER.cameraUpperLeft.x-10, CONTROLLER.cameraUpperLeft.y);
			VIEW.repaint();
			break;
		default:
			break;
		}
		
	}
	
}
