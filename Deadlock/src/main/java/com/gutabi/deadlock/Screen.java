package com.gutabi.deadlock;

import java.awt.event.ActionListener;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;

public abstract class Screen implements ActionListener {
	
	public abstract void init();
	
	public abstract void canvasPostDisplay(Dim dim);
	
	public abstract void render();
	
	public abstract void repaint();
	
	public abstract void paint(PaintEvent ev);
	
	
	
	
	
	public abstract void qKey(InputEvent ev);
	
	public abstract void wKey(InputEvent ev);
	
	public abstract void gKey(InputEvent ev);
	
	public abstract void deleteKey(InputEvent ev);
	
	public abstract void insertKey(InputEvent ev);
	
	public abstract void escKey(InputEvent ev);
	
	public abstract void d1Key(InputEvent ev);
	
	public abstract void d2Key(InputEvent ev);
	
	public abstract void d3Key(InputEvent ev);
	
	public abstract void plusKey(InputEvent ev);
	
	public abstract void minusKey(InputEvent ev);
	
	public abstract void downKey(InputEvent ev);

	public abstract void upKey(InputEvent ev);
	
	public abstract void enterKey(InputEvent ev);
	
	public abstract void aKey(InputEvent ev);
	
	public abstract void sKey(InputEvent ev);
	
	public abstract void dKey(InputEvent ev);
	
	public abstract void ctrlSKey(InputEvent ev);
	
	
	public abstract void pressed(InputEvent ev);
	
	public abstract void moved(InputEvent ev);
	
	public abstract void dragged(InputEvent ev);
	
	public abstract void released(InputEvent ev);
	
	public abstract void clicked(InputEvent ev);
	
	public abstract void exited(InputEvent ev);
	
}
