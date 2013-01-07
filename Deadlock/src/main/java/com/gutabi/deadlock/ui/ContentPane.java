package com.gutabi.deadlock.ui;

import java.util.List;

import com.gutabi.deadlock.math.Point;

//@SuppressWarnings("serial")
public interface ContentPane {

	public List<Panel> getChildren();
	
	public Point getLastMovedContentPanePoint();
	
	public abstract void enableKeyListener();
	
	public abstract void disableKeyListener();
	
	public void ctrlOKey();

	public void dKey();

	public void upKey();

	public void enterKey();

	public void aKey();

	public void sKey();

	public void ctrlSKey();

	public void downKey();

	public void minusKey();

	public void plusKey();

	public void d3Key();

	public void d2Key();

	public void d1Key();

	public void gKey();

	public void wKey();

	public void qKey();

	public void escKey();

	public void deleteKey();

	public void insertKey();
	
	public void fKey();
	
	public void postDisplay();
	
	public void repaint();
	
}
