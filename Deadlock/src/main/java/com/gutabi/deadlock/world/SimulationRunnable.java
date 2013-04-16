package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class SimulationRunnable implements Runnable {
	
	public SimulationRunnable() {
		
	}
	
	public void run() {
		
		World world = (World)APP.model;
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		world.preStart();
		
		try {
			
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
					
//					APP.appScreen.contentPane.repaint();
					
				} // outer
			
		} catch (InterruptedException e) {
			
		}
		
		world.postStop();
		
//		APP.appScreen.contentPane.repaint();
		
	}
	
}
