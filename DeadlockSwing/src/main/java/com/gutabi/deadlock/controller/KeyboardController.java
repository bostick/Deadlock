package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyboardController {
	
	public void init() {
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "rightKeyAction");
		VIEW.panel.getActionMap().put("rightKeyAction", rightKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "leftKeyAction");
		VIEW.panel.getActionMap().put("leftKeyAction", leftKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "upKeyAction");
		VIEW.panel.getActionMap().put("upKeyAction", upKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "downKeyAction");
		VIEW.panel.getActionMap().put("downKeyAction", downKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("EQUALS"), "equalsKeyAction");
		VIEW.panel.getActionMap().put("equalsKeyAction", equalsKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("MINUS"), "minusKeyAction");
		VIEW.panel.getActionMap().put("minusKeyAction", minusKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("0"), "zeroKeyAction");
		VIEW.panel.getActionMap().put("zeroKeyAction", zeroKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "deleteKeyAction");
		VIEW.panel.getActionMap().put("deleteKeyAction", deleteKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undoKeyAction");
		VIEW.panel.getActionMap().put("undoKeyAction", undoKeyAction);
		
		VIEW.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redoKeyAction");
		VIEW.panel.getActionMap().put("redoKeyAction", redoKeyAction);
		
	}
	
	@SuppressWarnings("serial")
	public Action rightKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.moveCameraRight();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action leftKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.moveCameraLeft();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action upKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.moveCameraUp();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action downKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.moveCameraDown();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action equalsKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.zoomIn();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action minusKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.zoomOut();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action zeroKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.zoomReset();
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action deleteKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			CONTROLLER.queue(new Runnable() {
				public void run() {
					CONTROLLER.deleteKey();
					VIEW.renderBackground();
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
					VIEW.renderBackground();
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
					VIEW.renderBackground();
					VIEW.repaint();
				}
			});
			
		}
	};
	
	
}
