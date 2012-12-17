package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.WorldScreen.WorldScreenMode;

//@SuppressWarnings("static-access")
public class SimulationRunnable implements Runnable {
	
	WorldScreen screen;
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	public SimulationRunnable(WorldScreen screen) {
		this.screen = screen;
	}
	
	public void run() {
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		screen.world.preStart();
		
		outer:
		while (true) {
			
			if (screen.mode == WorldScreenMode.EDITING) {
				break outer;
			} else if (screen.mode == WorldScreenMode.PAUSED) {
				synchronized (screen.pauseLock) {
					try {
						screen.pauseLock.wait();
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
				
				screen.world.integrate(t);
				
				accumulator -= APP.dt;
				t += APP.dt;
			}
			
			screen.repaint();
			
		} // outer
		
		screen.world.postStop();
		
		screen.repaint();
	}
	
}
