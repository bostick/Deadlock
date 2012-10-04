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
			
			long newTime = System.currentTimeMillis();
	        long frameTime = newTime - currentTime;
	        currentTime = newTime;
			
	        accumulator += frameTime;
	        
	        while (accumulator >= MODEL.world.dt) {
	        	MODEL.world.integrate(t);
	        	accumulator -= MODEL.world.dt;
	            t += MODEL.world.dt;
	        }
			
			VIEW.repaint();
			
		} // outer
		
		MODEL.world.postStop();
		
	}
	
}
