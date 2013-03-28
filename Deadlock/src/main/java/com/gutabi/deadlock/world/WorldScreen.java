package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.DLSFileChooser;
import com.gutabi.deadlock.ui.KeyListener;

public class WorldScreen extends AppScreen implements KeyListener {
	
	public enum WorldScreenMode {
		EDITING,
		RUNNING,
		PAUSED,
		DIALOG
	}
	
	public double origPixelsPerMeter = 32.0;
	public double pixelsPerMeter = 32.0;
	public AABB worldViewport;
	public AABB origWorldViewport;
	
	/**
	 * move physics forward by dt seconds
	 */
	public double DT = 0.0166666;
	
	public WorldScreenContentPane contentPane;
	
	public World world;
	
	public WorldScreenMode mode;
	
	public final Object pauseLock = new Object();
	
	public Stats stats;
	
	public DLSFileChooser fc;
	
	
	
	public WorldScreen() {
		
		contentPane = new WorldScreenContentPane(this);
		
		mode = WorldScreenMode.EDITING;
		
		stats = new Stats(this);
		
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
	
	
	public void startRunning() {
		
		mode = WorldScreenMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable());
		t.start();
		
	}
	
	public void stopRunning() {
		
		mode = WorldScreenMode.EDITING;
		
//		tool = new RegularTool(this, APP.debuggerScreen);
		
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
	
	public void upKey() {
		
	}

	public void downKey() {
		
	}
	
	public void qKey() {
		APP.tool.qKey();
	}
	
	public void wKey() {
		APP.tool.wKey();
	}
	
	public void gKey() {
		APP.tool.gKey();
	}
	
	public void deleteKey() {
		APP.tool.deleteKey();
	}
	
	public void insertKey() {
		APP.tool.insertKey();
	}
	
	public void escKey() {
		APP.tool.escKey();
	}
	
	public void d1Key() {
		APP.tool.d1Key();
	}
	
	public void d2Key() {
		APP.tool.d2Key();
	}
	
	public void d3Key() {
		APP.tool.d3Key();
	}
	
	public void plusKey() {
		APP.tool.plusKey();
	}
	
	public void minusKey() {
		APP.tool.minusKey();
	}
	
	public void aKey() {
		APP.tool.aKey();
	}
	
	public void sKey() {
		APP.tool.sKey();
	}
	
	public void ctrlSKey() {
		APP.tool.ctrlSKey();
	}
	
	public void ctrlOKey() {
		APP.tool.ctrlOKey();
	}
	
	public void dKey() {
		APP.tool.dKey();
	}
	
	public void fKey() {
		APP.tool.fKey();
	}
	
	public void enterKey() {
		APP.tool.enterKey();
	}
	
	
	
}
