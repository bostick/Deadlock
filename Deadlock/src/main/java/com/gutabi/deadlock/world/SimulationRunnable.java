package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.world.WorldScreen.WorldScreenMode;

public class SimulationRunnable implements Runnable {
	
	final WorldScreen worldScreen;
	
	public SimulationRunnable() {
		
		worldScreen = (WorldScreen)APP.appScreen;
		
	}
	
	public void run() {
		
		double t = 0;
		double accumulator = 0;
		
		long currentTimeMillis = System.currentTimeMillis();
		long newTimeMillis = System.currentTimeMillis();
		
		worldScreen.world.preStart();
		
		try {
			
			outer:
				while (true) {
					
					if (worldScreen.mode == WorldScreenMode.EDITING) {
						break outer;
					} else if (worldScreen.mode == WorldScreenMode.PAUSED) {
						synchronized (worldScreen.pauseLock) {
							try {
								worldScreen.pauseLock.wait();
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
					if (frameTimeSeconds > 1 * worldScreen.DT) {
						frameTimeSeconds = 1 * worldScreen.DT;
					}
					if (frameTimeSeconds < 0.5 * worldScreen.DT) {
//						String.class.getName();
						Thread.sleep(frameTimeMillis);
						frameTimeSeconds += frameTimeSeconds;
					}
					
					accumulator += frameTimeSeconds;
					
					while (accumulator >= worldScreen.DT) {
						
						worldScreen.world.integrate(t);
						
						accumulator -= worldScreen.DT;
						t += worldScreen.DT;
					}
					
					worldScreen.contentPane.repaint();
					
				} // outer
			
		} catch (InterruptedException e) {
			
		}
		
		worldScreen.world.postStop();
		
		worldScreen.contentPane.repaint();
		
	}
	
}
