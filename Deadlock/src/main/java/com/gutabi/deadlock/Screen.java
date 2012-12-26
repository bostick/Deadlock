package com.gutabi.deadlock;

import java.awt.event.ActionListener;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;

public interface Screen extends ActionListener {
	
	void setup(RootPaneContainer container);
	
	void teardown(RootPaneContainer container);
	
//	void setupCanvasAndControlPanel(RootPaneContainer container);
//	
//	void teardownCanvasAndControlPanel(RootPaneContainer container);
	
	
	
	void postDisplay();
	
	void render();
	
	void paint(PaintEvent ev);
	
	
	
	void qKey(InputEvent ev);
	
	void wKey(InputEvent ev);
	
	void gKey(InputEvent ev);
	
	void deleteKey(InputEvent ev);
	
	void insertKey(InputEvent ev);
	
	void escKey(InputEvent ev);
	
	void d1Key(InputEvent ev);
	
	void d2Key(InputEvent ev);
	
	void d3Key(InputEvent ev);
	
	void plusKey(InputEvent ev);
	
	void minusKey(InputEvent ev);
	
	void downKey(InputEvent ev);

	void upKey(InputEvent ev);
	
	void enterKey(InputEvent ev);
	
	void aKey(InputEvent ev);
	
	void sKey(InputEvent ev);
	
	void dKey(InputEvent ev);
	
	void ctrlSKey(InputEvent ev);
	
	void ctrlOKey(InputEvent ev);
	
	
	
	void pressed(InputEvent ev);
	
	void moved(InputEvent ev);
	
	void dragged(InputEvent ev);
	
	void released(InputEvent ev);
	
	void clicked(InputEvent ev);
	
	void exited(InputEvent ev);
	
}
