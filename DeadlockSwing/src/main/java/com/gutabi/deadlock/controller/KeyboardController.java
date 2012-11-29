package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class KeyboardController implements KeyListener {
	
	public void init() {
		
		VIEW.canvas.addKeyListener(this);
		VIEW.controlPanel.addKeyListener(this);
		VIEW.previewPanel.addKeyListener(this);
		
	}
	
	@SuppressWarnings("serial")
	public Action deleteKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.deleteKey();
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action insertKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.insertKey();
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action qKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.qKey();
			
		}
	};

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_INSERT) {
			CONTROLLER.insertKey();
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			CONTROLLER.deleteKey();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			CONTROLLER.escKey();
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			CONTROLLER.qKey();
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			CONTROLLER.wKey();
		} else if (e.getKeyCode() == KeyEvent.VK_G) {
			CONTROLLER.gKey();
		} else if (e.getKeyCode() == KeyEvent.VK_1) {
			CONTROLLER.d1Key();
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			CONTROLLER.d2Key();
		} else if (e.getKeyCode() == KeyEvent.VK_3) {
			CONTROLLER.d3Key();
		} else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS) {
			CONTROLLER.plusKey();
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			CONTROLLER.minusKey();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}
