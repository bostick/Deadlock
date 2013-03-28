package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.AppScreen;
import com.gutabi.deadlock.ui.KeyListener;

public class WorldScreen extends AppScreen implements KeyListener {
	
	public WorldScreenContentPane contentPane;
	
	public WorldScreen() {
		contentPane = new WorldScreenContentPane(this);
	}
	
	public void postDisplay() {
		contentPane.postDisplay();
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
