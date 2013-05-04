package com.gutabi.capsloc;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.concurrent.atomic.AtomicBoolean;

import com.gutabi.capsloc.Integratable;

public class SimulationRunnable implements Runnable {
	
	AtomicBoolean trigger;
	
	public SimulationRunnable(AtomicBoolean trigger) {
		this.trigger = trigger;
	}
	
	public void run() {
		
		Integratable iable = APP.model;
		
		double t = iable.getTime();
		double accumulator = 0;
		
		long currentTimeMillis = APP.platform.monotonicClockMillis();
		long newTimeMillis = APP.platform.monotonicClockMillis();
		
		try {
			
			outer:
				while (true) {
					
					if (trigger.get() == false) {
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
					if (frameTimeSeconds > 1 * Integratable.DT) {
						frameTimeSeconds = 1 * Integratable.DT;
					}
					if (frameTimeSeconds < 0.5 * Integratable.DT) {
						Thread.sleep(frameTimeMillis);
						frameTimeSeconds += frameTimeSeconds;
					}
//					Thread.sleep(frameTimeMillis);
					
					accumulator += frameTimeSeconds;
					
					boolean paint = false;
					while (accumulator >= Integratable.DT) {
						
						paint = paint | iable.integrate(t);
						
						accumulator -= Integratable.DT;
						t += Integratable.DT;
					}
					
					if (paint) {
						APP.appScreen.contentPane.repaint();
					}
					
				} // outer
			
		} catch (InterruptedException e) {
			
		}
		
	}
	
}
