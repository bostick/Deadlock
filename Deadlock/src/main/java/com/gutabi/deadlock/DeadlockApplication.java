package com.gutabi.deadlock;

import java.net.URL;
import java.util.Random;

public class DeadlockApplication {
	
	public URL codebase;
	
	/**
	 * move physics forward by dt seconds
	 */
	public double dt = 0.01;
	
	public boolean FPS_DRAW = false;
	public boolean STOPSIGN_DRAW = true;
	public boolean CARTEXTURE_DRAW = true;
	public boolean EXPLOSIONS_DRAW = true;
	public boolean DEBUG_DRAW = false;
	
	public Random RANDOM = new Random(1);
	
	public Screen screen;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void exit() {
		
		 try {
			 String pidstr = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		      String pid[] = pidstr.split("@");
		      Runtime.getRuntime().exec("taskkill /F /PID " + pid[0]).waitFor();
			  }
			  catch (Exception e) {
			  }
		
	}
	
}
