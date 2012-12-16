package com.gutabi.deadlock.view;


public abstract class Component {
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract java.awt.Component java();
	
	
	
	
	public abstract void pressed(InputEvent e);
	
	public abstract void released(InputEvent e);
	
	public abstract void dragged(InputEvent e);
	
	public abstract void moved(InputEvent e);
	
	public abstract void clicked(InputEvent e);
	
	public abstract void entered(InputEvent e);
	
	public abstract void exited(InputEvent e);
	
	
//	public abstract void qKey(InputEvent ev);
//	
//	public abstract void wKey(InputEvent ev);
//	
//	public abstract void gKey(InputEvent ev);
//	
//	public abstract void deleteKey(InputEvent ev);
//	
//	public abstract void insertKey(InputEvent ev);
//	
//	public abstract void escKey(InputEvent ev);
//	
//	public abstract void d1Key(InputEvent ev);
//	
//	public abstract void d2Key(InputEvent ev);
//	
//	public abstract void d3Key(InputEvent ev);
//	
//	public abstract void plusKey(InputEvent ev);
//	
//	public abstract void minusKey(InputEvent ev);
//	
//	public abstract void downKey(InputEvent ev);
//
//	public abstract void upKey(InputEvent ev);
//	
//	public abstract void enterKey(InputEvent ev);
//	
//	public abstract void aKey(InputEvent ev);
//	
//	public abstract void sKey(InputEvent ev);
//	
//	public abstract void dKey(InputEvent ev);
	
}
