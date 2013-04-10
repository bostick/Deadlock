package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class UIAnimationRunnable implements Runnable {
	
	public UIAnimationRunnable() {
		
	}
	
	public void run() {
		
		try {
			
			while (true) {
				
//				Menu menu;
//				if (APP.model instanceof RushHourWorld) {
//					menu = ((RushHourWorld)APP.model).winnerMenu;
//				} else {
//					menu = (Menu)APP.model;
//				}
				
				Thread.sleep(33);
				
				APP.appScreen.contentPane.repaint();
				
			} // outer
		
		} catch (InterruptedException e) {
			
		}
		
	}
	
}
