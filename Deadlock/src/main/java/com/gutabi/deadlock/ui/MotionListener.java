package com.gutabi.deadlock.ui;

public interface MotionListener {
	
	void clicked(InputEvent ev);
	
	void pressed(InputEvent ev);
	
	void released(InputEvent ev);
	
	void moved(InputEvent ev);
	
	void dragged(InputEvent ev);
	
}
