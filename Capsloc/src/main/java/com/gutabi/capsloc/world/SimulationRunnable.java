package com.gutabi.capsloc.world;

import static com.gutabi.capsloc.CapslocApplication.APP;

public class SimulationRunnable implements Runnable {
	
	public SimulationRunnable() {
		
	}
	
	public void run() {
		
		World world = (World)APP.model;
		
		double t = world.t;
		double accumulator = 0;
		
		long currentTimeMillis = APP.platform.monotonicClockMillis();
		long newTimeMillis = APP.platform.monotonicClockMillis();
		
		try {
			
			outer:
				while (true) {
					
					if (world.mode == WorldMode.EDITING) {
						break outer;
					}
					
					newTimeMillis = APP.platform.monotonicClockMillis();
					long frameTimeMillis = newTimeMillis - currentTimeMillis;
					if (frameTimeMillis < 0) {
						throw new AssertionError();
					}
					
					currentTimeMillis = newTimeMillis;
					
					double frameTimeSeconds = ((double)frameTimeMillis) / 1000;
					/*
					 * this max value is a heuristic
					 */
					if (frameTimeSeconds > 1 * world.DT) {
						frameTimeSeconds = 1 * world.DT;
					}
					if (frameTimeSeconds < 0.5 * world.DT) {
						Thread.sleep(frameTimeMillis);
						frameTimeSeconds += frameTimeSeconds;
					}
					
					accumulator += frameTimeSeconds;
					
					while (accumulator >= world.DT) {
						
						world.integrate(t);
						
						accumulator -= world.DT;
						t += world.DT;
					}
					
				} // outer
			
		} catch (InterruptedException e) {
			
		}
		
	}
	
}