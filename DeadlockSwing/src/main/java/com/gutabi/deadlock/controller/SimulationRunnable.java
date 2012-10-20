package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import org.apache.log4j.Logger;

@SuppressWarnings("static-access")
public class SimulationRunnable implements Runnable {
	
	ControlMode modeCopy;
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	@Override
	public void run() {
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		MODEL.world.preStart();
		
		outer:
		while (true) {
			
			modeCopy = MODEL.getMode();
			if (modeCopy == ControlMode.IDLE) {
				break outer;
			} else if (modeCopy == ControlMode.PAUSED) {
				synchronized (MODEL.pauseLock) {
					try {
						MODEL.pauseLock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				currentTimeMillis = System.currentTimeMillis();
				accumulator = 0;
			}
			
			newTimeMillis = System.currentTimeMillis();
			long frameTimeMillis = newTimeMillis - currentTimeMillis;
			//long frameTime = newTime - currentTime - 10;
//			if (frameTime < 1) {
//				frameTime = 1;
//			}
			
			currentTimeMillis = newTimeMillis;
			
			double frameTimeSeconds = ((double)frameTimeMillis) / 1000;
			
			accumulator += frameTimeSeconds;
			
			while (accumulator >= MODEL.dt) {
				MODEL.world.integrate(t);
				accumulator -= MODEL.dt;
				t += MODEL.dt;
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
