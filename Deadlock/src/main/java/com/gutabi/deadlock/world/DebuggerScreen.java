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
	
	public void upKey() {
		
	}

	public void downKey() {
		
	}
	
	public void qKey() {
		
	}
	
	public void wKey() {
		
	}
	
	public void gKey() {
		
	}
	
	public void deleteKey() {
	}
	
	
	public void insertKey() {
		
	}
	
	public void escKey() {
		
	}
	
	public void d1Key() {
		
	}
	
	public void d2Key() {
		
	}
	
	public void d3Key() {
		
	}
	
	public void plusKey() {
		
	}
	
	public void minusKey() {
		
	}
	
	public void aKey() {
		
	}
	
	public void sKey() {
		
	}
	
	public void ctrlSKey() {
		
	}
	
	public void ctrlOKey() {
		
	}
	
	public void dKey() {
		
	}
	
	public void fKey() {
		
	}
	
	public void enterKey() {
		
	}
	
}
