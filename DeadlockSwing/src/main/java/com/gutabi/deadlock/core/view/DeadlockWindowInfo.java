package com.gutabi.deadlock.core.view;

import com.gutabi.deadlock.core.Dim;

public interface DeadlockWindowInfo {
	
	int windowWidth();
	
	int windowHeight();
	
	Dim windowDim();
	
	int windowX();
	
	int windowY();
	
	int screenWidth();
	
	int screenHeight();
	
	Dim screenDim();
	
}
