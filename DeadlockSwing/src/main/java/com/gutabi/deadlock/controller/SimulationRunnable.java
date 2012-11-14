package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import org.apache.log4j.Logger;

@SuppressWarnings("static-access")
public class SimulationRunnable implements Runnable {
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	@Override
	public void run() {
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		MODEL.world.preStart();
		
//		MODEL.world.renderBackground();
//		VIEW.repaint();
		
		outer:
		while (true) {
			
			if (CONTROLLER.mode == ControlMode.IDLE) {
				break outer;
			} else if (CONTROLLER.mode == ControlMode.PAUSED) {
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
			
			currentTimeMillis = newTimeMillis;
			
			double frameTimeSeconds = ((double)frameTimeMillis) / 1000;
			/*
			 * this max value is a heuristic
			 */
			if (frameTimeSeconds > 1 * MODEL.dt) {
				frameTimeSeconds = 1 * MODEL.dt;
			}
			
			accumulator += frameTimeSeconds;
			
			while (accumulator >= MODEL.dt) {
				
				MODEL.world.integrate(t);
				
				accumulator -= MODEL.dt;
				t += MODEL.dt;
			}
			
			VIEW.repaint();
			
		} // outer
		
		MODEL.world.postStop();
		
		VIEW.repaint();
		
	}
	
}
