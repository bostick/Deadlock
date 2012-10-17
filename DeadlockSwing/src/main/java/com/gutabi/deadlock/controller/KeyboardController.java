package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyboardController {
	
	public void init() {
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "deleteKeyAction");
		VIEW.panel.getActionMap().put("deleteKeyAction", deleteKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("TAB"), "tabKeyAction");
		VIEW.panel.getActionMap().put("tabKeyAction", tabKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undoKeyAction");
		VIEW.panel.getActionMap().put("undoKeyAction", undoKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redoKeyAction");
		VIEW.panel.getActionMap().put("redoKeyAction", redoKeyAction);
		
	}
	
	@SuppressWarnings("serial")
	public Action deleteKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.deleteKey();
					MODEL.world.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action tabKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.tabKey();
					MODEL.world.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action undoKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.undoKey();
					MODEL.world.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action redoKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.redoKey();
					MODEL.world.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
}
