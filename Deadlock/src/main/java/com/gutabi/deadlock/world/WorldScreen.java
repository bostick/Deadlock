package com.gutabi.deadlock.world;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.DLSFileChooser;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.world.tools.RegularTool;
import com.gutabi.deadlock.world.tools.Tool;

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
	
	public void upKey() {
		
	}

	public void downKey() {
		
	}
	
	public void qKey() {
		tool.qKey();
	}
	
	public void wKey() {
		tool.wKey();
	}
	
	public void gKey() {
		tool.gKey();
	}
	
	public void deleteKey() {
		tool.deleteKey();
	}
	
	public void insertKey() {
		tool.insertKey();
	}
	
	public void escKey() {
		tool.escKey();
	}
	
	public void d1Key() {
		tool.d1Key();
	}
	
	public void d2Key() {
		tool.d2Key();
	}
	
	public void d3Key() {
		tool.d3Key();
	}
	
	public void plusKey() {
		tool.plusKey();
	}
	
	public void minusKey() {
		tool.minusKey();
	}
	
	public void aKey() {
		tool.aKey();
	}
	
	public void sKey() {
		tool.sKey();
	}
	
	public void ctrlSKey() {
		tool.ctrlSKey();
	}
	
	public void ctrlOKey() {
		tool.ctrlOKey();
	}
	
	public void dKey() {
		tool.dKey();
	}
	
	public void fKey() {
		tool.fKey();
	}
	
	public void enterKey() {
		tool.enterKey();
	}
	
	
	
}
