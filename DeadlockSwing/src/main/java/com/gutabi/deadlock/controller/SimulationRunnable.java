package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import org.apache.log4j.Logger;

public class SimulationRunnable implements Runnable {
	
	ControlMode modeCopy;
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	@Override
	public void run() {
		
		long t = 0;
		long currentTime = System.currentTimeMillis();
		long accumulator = 0;
		
		long newTime = System.currentTimeMillis();
		
		outer:
		while (true) {
			
			synchronized (MODEL) {
				modeCopy = MODEL.getMode();
				if (modeCopy == ControlMode.IDLE) {
					break outer;
				} else if (modeCopy == ControlMode.PAUSED) {
					try {
						MODEL.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentTime = System.currentTimeMillis();
					accumulator = 0;
				}
			}
			
			newTime = System.currentTimeMillis();
			long frameTime = newTime - currentTime;
			//long frameTime = newTime - currentTime - 10;
//			if (frameTime < 1) {
//				frameTime = 1;
//			}
			
			currentTime = newTime;
			
			accumulator += frameTime;
			
			while (accumulator >= MODEL.world.dt) {
				MODEL.world.integrate(t);
				accumulator -= MODEL.world.dt;
				t += MODEL.world.dt;
			}
			
			VIEW.repaint();
			
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		} // outer
		
		MODEL.world.postStop();
		
	}
	
}
