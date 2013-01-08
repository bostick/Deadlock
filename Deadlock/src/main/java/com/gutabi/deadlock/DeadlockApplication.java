package com.gutabi.deadlock;

import java.net.URL;
import java.util.Random;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageEngine;

public class DeadlockApplication {
	
	public URL codebase;
	
	public RootPaneContainer container;
	
	public Platform platform;
	
	public boolean NORMALCAR = true;
	public boolean FASTCAR = true;
	public boolean REALLYCAR = true;
	public boolean TRUCK = true;
	
	public boolean FPS_DRAW = false;
	public boolean STOPSIGN_DRAW = true;
	public boolean CARTEXTURE_DRAW = true;
	public boolean EXPLOSIONS_DRAW = true;
	public boolean DEBUG_DRAW = false;
	
	public Random RANDOM = new Random(1);
	
	public Image carSheet;
	public Image spriteSheet;
	
	public Image explosionSheet;
	public Image titleBackground;
	public Image title_white;
	public Image copyright;
	
	public static DeadlockApplication APP = new DeadlockApplication();
	
	public void init() throws Exception {
		
		ImageEngine engine = platform.createImageEngine();
		
		carSheet = engine.readImage(DeadlockApplication.class.getResource("/img/carSheet.png"));
		spriteSheet = engine.readImage(DeadlockApplication.class.getResource("/img/spriteSheet.png"));
		explosionSheet = engine.readImage(DeadlockApplication.class.getResource("/img/explosionSheet.png"));
		titleBackground = engine.readImage(DeadlockApplication.class.getResource("/img/title_background.png"));
		title_white = engine.readImage(DeadlockApplication.class.getResource("/img/title_white.png"));
		copyright = engine.readImage(DeadlockApplication.class.getResource("/img/copyright.png"));
	}
	
	public void exit() {
		
//		System.exit(0);
		
		/*
		 * for some reason, the switch to using OpenGL has caused the java process to completely peg the CPU when it exits
		 * there is 1 thread running in the process, inside ftime64 doing something
		 * 
		 * so take extreme measures
		 */
		
		 try {
			 String pidstr = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		      String pid[] = pidstr.split("@");
		      Runtime.getRuntime().exec("taskkill /F /PID " + pid[0]).waitFor();
			  }
			  catch (Exception e) {
			  }
		
	}
	
}
