package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;
import com.gutabi.deadlock.ui.KeyListener;

public class AppScreen implements KeyListener {
	
	public ContentPane contentPane;
	
	public void postDisplay() {
		contentPane.postDisplay();
	}
	
	public void downKey() {
		APP.tool.downKey();
	}
	
	public void upKey() {
		APP.tool.upKey();
	}
	
	public void enterKey() {
		APP.tool.enterKey();
	}
	
	public void ctrlOKey() {
		APP.tool.ctrlOKey();
	}

	public void dKey() {
		APP.tool.dKey();
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

	public void minusKey() {
		APP.tool.minusKey();
	}

	public void plusKey() {
		APP.tool.plusKey();
	}

	public void d3Key() {
		APP.tool.d3Key();
	}

	public void d2Key() {
		APP.tool.d2Key();
	}

	public void d1Key() {
		APP.tool.d1Key();
	}

	public void gKey() {
		APP.tool.gKey();
	}

	public void wKey() {
		APP.tool.wKey();
	}

	public void qKey() {
		APP.tool.qKey();
	}

	public void escKey() {
		APP.tool.escKey();
	}

	public void deleteKey() {
		APP.tool.deleteKey();
	}

	public void insertKey() {
		APP.tool.insertKey();
	}
	
	public void fKey() {
		APP.tool.fKey();
	}
	
}
