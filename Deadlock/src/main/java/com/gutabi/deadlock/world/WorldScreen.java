package com.gutabi.deadlock.world;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.Screen;
import com.gutabi.deadlock.ui.DLSFileChooser;
import com.gutabi.deadlock.world.tools.RegularTool;
import com.gutabi.deadlock.world.tools.Tool;

public class WorldScreen extends Screen {
	
	public enum WorldScreenMode {
		EDITING,
		RUNNING,
		PAUSED,
		DIALOG
	}
	
	/**
	 * move physics forward by dt seconds
	 */
	public double DT = 0.01;
	
	public WorldScreenContentPane contentPane;
	
	public World world;
	
	public Tool tool;
	
	public WorldScreenMode mode;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	public DLSFileChooser fc;
	
	
	
	public WorldScreen() {
		
		contentPane = new WorldScreenContentPane(this);
		
		mode = WorldScreenMode.EDITING;
		
		tool = new RegularTool(this);
		
		stats = new Stats(this);
		
	}
	
	public void setup(RootPaneContainer container) {
		
		contentPane.setLayout(null);
		
		container.setContentPane(contentPane);
		contentPane.setFocusable(true);
		contentPane.requestFocusInWindow();
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
	
	
	public void startRunning() {
		
		mode = WorldScreenMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable(this));
		t.start();
		
	}
	
	public void stopRunning() {
		
		mode = WorldScreenMode.EDITING;
		
		tool = new RegularTool(this);
		
	}
	
	public void pauseRunning() {
		
		mode = WorldScreenMode.PAUSED;
	}
	
	public void unpauseRunning() {
		
		mode = WorldScreenMode.RUNNING;
		
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
}
