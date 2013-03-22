package com.gutabi.deadlock.world;

import com.gutabi.deadlock.ui.KeyListener;

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
	
	
	
//	public void startRunning() {
//		
//		worldScreen.mode = WorldScreenMode.RUNNING;
//		
//		Thread t = new Thread(new SimulationRunnable());
//		t.start();
//		
//	}
	
//	public void stopRunning() {
//		
//		worldScreen.mode = WorldScreenMode.EDITING;
//		
////		APP.tool = new RegularTool(worldScreen, this);
//		
//	}
	
//	public void pauseRunning() {
//		
//		worldScreen.mode = WorldScreenMode.PAUSED;
//	}
	
//	public void unpauseRunning() {
//		
//		worldScreen.mode = WorldScreenMode.RUNNING;
//		
//		synchronized (worldScreen.pauseLock) {
//			worldScreen.pauseLock.notifyAll();
//		}
//	}
	
	public void upKey() {
		
	}

	public void downKey() {
		
	}
	
	public void qKey() {
//		APP.tool.qKey();
	}
	
	public void wKey() {
//		APP.tool.wKey();
	}
	
	public void gKey() {
//		APP.tool.gKey();
	}
	
	public void deleteKey() {
//		APP.tool.deleteKey();
	}
	
	public void insertKey() {
//		APP.tool.insertKey();
	}
	
	public void escKey() {
//		APP.tool.escKey();
	}
	
	public void d1Key() {
//		APP.tool.d1Key();
	}
	
	public void d2Key() {
//		APP.tool.d2Key();
	}
	
	public void d3Key() {
//		APP.tool.d3Key();
	}
	
	public void plusKey() {
//		APP.tool.plusKey();
	}
	
	public void minusKey() {
//		APP.tool.minusKey();
	}
	
	public void aKey() {
//		APP.tool.aKey();
	}
	
	public void sKey() {
//		APP.tool.sKey();
	}
	
	public void ctrlSKey() {
//		APP.tool.ctrlSKey();
	}
	
	public void ctrlOKey() {
//		APP.tool.ctrlOKey();
	}
	
	public void dKey() {
//		APP.tool.dKey();
	}
	
	public void fKey() {
//		APP.tool.fKey();
	}
	
	public void enterKey() {
//		APP.tool.enterKey();
	}
	
	
	
}
