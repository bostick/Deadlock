package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import org.apache.log4j.Logger;

@SuppressWarnings("static-access")
public class SimulationRunnable implements Runnable {
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	public void run() {
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		APP.world.preStart();
		
		outer:
		while (true) {
			
			if (APP.world.mode == WorldMode.IDLE) {
				break outer;
			} else if (APP.world.mode == WorldMode.PAUSED) {
				synchronized (APP.world.pauseLock) {
					try {
						APP.world.pauseLock.wait();
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
			if (frameTimeSeconds > 1 * APP.dt) {
				frameTimeSeconds = 1 * APP.dt;
			}
			
			accumulator += frameTimeSeconds;
			
			while (accumulator >= APP.dt) {
				
				APP.world.integrate(t);
				
				accumulator -= APP.dt;
				t += APP.dt;
			}
			
			VIEW.repaintCanvas();
			
		} // outer
		
		APP.world.postStop();
		
		VIEW.repaintCanvas();
		
	}
	
}
