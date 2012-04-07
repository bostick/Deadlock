package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

public class DeadlockController {
	
	public static final DeadlockController CONTROLLER = new DeadlockController();
	
	public MouseController mouseController;
	
	public void init() {
		if (!VIEW.inited) {
			throw new AssertionError();
		}
		
		mouseController = new MouseController();
		mouseController.init();
	}
	
}
