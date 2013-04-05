package com.gutabi.deadlock;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public abstract class Tool {
	
	public void qKey() {
		
	}
	
	public void wKey() {
		
	}
	
	public void aKey() {
		
	}
	
	public void sKey() {
		
	}
	
	public void dKey() {
		
	}
	
	public void fKey() {
		
	}
	
	public void gKey() {
		
	}
	
	public void insertKey() {
		
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
	
	public void upKey() {
		
	}
	public void downKey() {
		
	}
	
	public void escKey() {
		
	}
	
	public void deleteKey() {
		
	}
	
	public void ctrlSKey() {
		
	}
	
	public void ctrlOKey() {
		
	}
	
	public void enterKey() {
		
	}
	
	/**
	 * in panel coords
	 */
	public void pressed(InputEvent ev) {
		
	}
	
	/**
	 * in panel coords
	 */
	public void released(InputEvent ev) {
		
	}
	
	/**
	 * in panel coords
	 */
	public void dragged(InputEvent ev) {
		
	}
	
	/**
	 * in panel coords
	 */
	public void moved(InputEvent ev) {
		
	}
	
	/**
	 * in panel coords
	 */
	public void clicked(InputEvent ev) {
		
	}
	
	
	public Point p;
	
	public abstract void setPoint(Point p);
	
	public Point getPoint() {
		return p;
	}
	
	public abstract void paint_panel(RenderingContext ctxt);
	
}
