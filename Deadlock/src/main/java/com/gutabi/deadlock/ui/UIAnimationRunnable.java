package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

public class UIAnimationRunnable implements Runnable {
	
	AtomicBoolean trigger;
	
	public UIAnimationRunnable(AtomicBoolean trigger) {
		this.trigger = trigger;
	}
	
	public void run() {
		
		try {
			
			while (true) {
				
				if (trigger.get() == false) {
					break;
				}
				
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
