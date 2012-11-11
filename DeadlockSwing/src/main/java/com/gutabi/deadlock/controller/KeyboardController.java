package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyboardController {
	
	public void init() {
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "deleteKeyAction");
		VIEW.panel.getActionMap().put("deleteKeyAction", deleteKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("INSERT"), "insertKeyAction");
		VIEW.panel.getActionMap().put("insertKeyAction", insertKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "qKeyAction");
		VIEW.panel.getActionMap().put("qKeyAction", qKeyAction);
		
//		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("TAB"), "tabKeyAction");
//		VIEW.panel.getActionMap().put("tabKeyAction", tabKeyAction);
//		
//		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undoKeyAction");
//		VIEW.panel.getActionMap().put("undoKeyAction", undoKeyAction);
//		
//		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redoKeyAction");
//		VIEW.panel.getActionMap().put("redoKeyAction", redoKeyAction);
		
	}
	
	@SuppressWarnings("serial")
	public Action deleteKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.deleteKey();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			CONTROLLER.renderAndPaint();
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action insertKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.insertKey();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			CONTROLLER.renderAndPaint();
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action qKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.qKey();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};
	
//	@SuppressWarnings("serial")
//	public Action tabKeyAction = new AbstractAction() {
//		@Override
//		public void actionPerformed(ActionEvent blah) {
//			
//			CONTROLLER.queue(new Runnable() {
//				public void run() {
//					CONTROLLER.tabKey();
//					MODEL.world.renderBackground();
//					VIEW.repaint();
//				}
//			});
//			
//		}
//	};
//	
//	@SuppressWarnings("serial")
//	public Action undoKeyAction = new AbstractAction() {
//		@Override
//		public void actionPerformed(ActionEvent blah) {
//			
//			CONTROLLER.queue(new Runnable() {
//				public void run() {
//					CONTROLLER.undoKey();
//					MODEL.world.renderBackground();
//					VIEW.repaint();
//				}
//			});
//			
//		}
//	};
//	
//	@SuppressWarnings("serial")
//	public Action redoKeyAction = new AbstractAction() {
//		@Override
//		public void actionPerformed(ActionEvent blah) {
//			
//			CONTROLLER.queue(new Runnable() {
//				public void run() {
//					CONTROLLER.redoKey();
//					MODEL.world.renderBackground();
//					VIEW.repaint();
//				}
//			});
//			
//		}
//	};
	
}
