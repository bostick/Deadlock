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
	
//	public abstract void repaint();
	
}
