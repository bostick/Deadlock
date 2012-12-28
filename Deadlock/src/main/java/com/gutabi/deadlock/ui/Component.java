package com.gutabi.deadlock.ui;

public interface Component {
	
	int getWidth();
	
	int getHeight();
	
	java.awt.Component java();
	
	void pressed(InputEvent e);
	
	void released(InputEvent e);
	
	void dragged(InputEvent e);
	
	void moved(InputEvent e);
	
	void clicked(InputEvent e);
	
	void entered(InputEvent e);
	
	void exited(InputEvent e);
	
}
