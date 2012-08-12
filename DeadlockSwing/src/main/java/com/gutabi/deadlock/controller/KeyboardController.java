package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;




public class KeyboardController {
	
	@SuppressWarnings("serial")
	public Action rightKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.moveCameraRight();
			VIEW.repaint();
		}
	};
	
	@SuppressWarnings("serial")
	public Action leftKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.moveCameraLeft();
			VIEW.repaint();
		}
	};
	
	@SuppressWarnings("serial")
	public Action upKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.moveCameraUp();
			VIEW.repaint();
		}
	};
	
	@SuppressWarnings("serial")
	public Action downKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.moveCameraDown();
			VIEW.repaint();
		}
	};
	
	@SuppressWarnings("serial")
	public Action equalsKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.zoomIn();
			VIEW.repaint();
		}
	};
	
	@SuppressWarnings("serial")
	public Action minusKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.zoomOut();
			VIEW.repaint();
		}
	};
	
	@SuppressWarnings("serial")
	public Action zeroKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			CONTROLLER.zoomReset();
			VIEW.repaint();
		}
	};
	
}
