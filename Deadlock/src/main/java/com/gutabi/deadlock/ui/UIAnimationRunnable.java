package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class UIAnimationRunnable implements Runnable {
	
	public UIAnimationRunnable() {
		
	}
	
	public void run() {
		
		try {
			
			while (true) {
				
				Thread.sleep(33);
				
				APP.appScreen.contentPane.repaint();
				
				if (APP.debuggerScreen != null) {
					APP.debuggerScreen.contentPane.repaint();
				}
				
			} // outer
		
		} catch (InterruptedException e) {
			
		}
		
	}
	
}
