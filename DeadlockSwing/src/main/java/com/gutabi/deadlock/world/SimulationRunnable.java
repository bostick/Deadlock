package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.world.World.WorldMode;

@SuppressWarnings("static-access")
public class SimulationRunnable implements Runnable {
	
	World world;
	
	static Logger logger = Logger.getLogger(SimulationRunnable.class);
	
	public SimulationRunnable(World world) {
		this.world = world;
	}
	
	public void run() {
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		world.preStart();
		
		outer:
		while (true) {
			
			if (world.mode == WorldMode.EDITING) {
				break outer;
			} else if (world.mode == WorldMode.PAUSED) {
				synchronized (world.pauseLock) {
					try {
						world.pauseLock.wait();
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
				
				world.integrate(t);
				
				accumulator -= APP.dt;
				t += APP.dt;
			}
			
			world.repaint();
			
		} // outer
		
		world.postStop();
		
		world.repaint();
		
	}
	
}
