package com.gutabi.deadlock;

import java.awt.event.ActionListener;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;

public abstract class Screen implements ActionListener {
	
	public abstract void init() throws Exception;
	
	public abstract void canvasPostDisplay();
	
	public abstract void render();
	
	public abstract void repaint();
	
	public abstract void paint(PaintEvent ev);
	
	
	
	
	
	public abstract void qKey();
	
	public abstract void wKey();
	
	public abstract void gKey();
	
	public abstract void deleteKey();
	
	public abstract void insertKey();
	
	public abstract void escKey();
	
	public abstract void d1Key();
	
	public abstract void d2Key();
	
	public abstract void d3Key();
	
	public abstract void plusKey();
	
	public abstract void minusKey();
	
	public abstract void downKey();

	public abstract void upKey();
	
	public abstract void enterKey();
	
	public abstract void aKey();
	
	public abstract void sKey();
	
	public abstract void dKey();
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);
	
	public abstract void exited(InputEvent ev);
	
}
