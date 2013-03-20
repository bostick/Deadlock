package com.gutabi.deadlock.world;

import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.world.WorldScreen.WorldScreenMode;
import com.gutabi.deadlock.world.tools.RegularTool;

public class DebuggerScreen implements KeyListener {
	
	WorldScreen worldScreen;
	
	public DebuggerScreenContentPane contentPane;
	
	public DebuggerScreen(WorldScreen worldScreen) {
		
		this.worldScreen = worldScreen;
		
		contentPane = new DebuggerScreenContentPane(this, worldScreen);
		
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
	
	
	public void startRunning() {
		
		worldScreen.mode = WorldScreenMode.RUNNING;
		
		Thread t = new Thread(new SimulationRunnable(worldScreen));
		t.start();
		
	}
	
	public void stopRunning() {
		
		worldScreen.mode = WorldScreenMode.EDITING;
		
		worldScreen.tool = new RegularTool();
		
	}
	
	public void pauseRunning() {
		
		worldScreen.mode = WorldScreenMode.PAUSED;
	}
	
	public void unpauseRunning() {
		
		worldScreen.mode = WorldScreenMode.RUNNING;
		
		synchronized (worldScreen.pauseLock) {
			worldScreen.pauseLock.notifyAll();
		}
	}
	
	public void upKey() {
		
	}

	public void downKey() {
		
	}
	
	public void qKey() {
		worldScreen.tool.qKey();
	}
	
	public void wKey() {
		worldScreen.tool.wKey();
	}
	
	public void gKey() {
		worldScreen.tool.gKey();
	}
	
	public void deleteKey() {
		worldScreen.tool.deleteKey();
	}
	
	public void insertKey() {
		worldScreen.tool.insertKey();
	}
	
	public void escKey() {
		worldScreen.tool.escKey();
	}
	
	public void d1Key() {
		worldScreen.tool.d1Key();
	}
	
	public void d2Key() {
		worldScreen.tool.d2Key();
	}
	
	public void d3Key() {
		worldScreen.tool.d3Key();
	}
	
	public void plusKey() {
		worldScreen.tool.plusKey();
	}
	
	public void minusKey() {
		worldScreen.tool.minusKey();
	}
	
	public void aKey() {
		worldScreen.tool.aKey();
	}
	
	public void sKey() {
		worldScreen.tool.sKey();
	}
	
	public void ctrlSKey() {
		worldScreen.tool.ctrlSKey();
	}
	
	public void ctrlOKey() {
		worldScreen.tool.ctrlOKey();
	}
	
	public void dKey() {
		worldScreen.tool.dKey();
	}
	
	public void fKey() {
		worldScreen.tool.fKey();
	}
	
	public void enterKey() {
		worldScreen.tool.enterKey();
	}
	
	
	
}
