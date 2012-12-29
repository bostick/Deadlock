package com.gutabi.deadlock;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ui.InputEvent;

public interface Screen {
	
	void setup(RootPaneContainer container);
	
	void teardown(RootPaneContainer container);
	
	
	void qKey();
	
	void wKey();
	
	void gKey();
	
	void deleteKey();
	
	void insertKey();
	
	void escKey();
	
	void d1Key();
	
	void d2Key();
	
	void d3Key();
	
	void plusKey();
	
	void minusKey();
	
	void downKey();

	void upKey();
	
	void enterKey();
	
	void aKey();
	
	void sKey();
	
	void dKey();
	
	void ctrlSKey();
	
	void ctrlOKey();
	
	
	
	void pressed(InputEvent ev);
	
	void moved(InputEvent ev);
	
	void dragged(InputEvent ev);
	
	void released(InputEvent ev);
	
	void clicked(InputEvent ev);
	
	void exited(InputEvent ev);
	
}
