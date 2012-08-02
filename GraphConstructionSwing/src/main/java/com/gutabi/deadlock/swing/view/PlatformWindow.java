package com.gutabi.deadlock.swing.view;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;

import com.gutabi.core.Dim;
import com.gutabi.deadlock.core.view.DeadlockWindow;

public class PlatformWindow implements DeadlockWindow {
	
	private Insets screenInsets;
	private Dimension screenDim;
	
	{
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations()[0];
		Insets si = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		screenInsets = si;
		screenDim = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public int windowWidth() {
		return screenDim.width - screenInsets.left - screenInsets.right;
	}
	
	public int windowHeight() {
		return screenDim.height - screenInsets.top - screenInsets.bottom;
	}
	
	public Dim windowDim() {
		return new Dim(screenDim.width - screenInsets.left - screenInsets.right, screenDim.height - screenInsets.top - screenInsets.bottom);
	}
	
	public int windowX() {
		return screenInsets.left;
	}
	
	public int windowY() {
		return screenInsets.top;
	}
	
	public int screenWidth() {
		return screenDim.width;
	}
	
	public int screenHeight() {
		return screenDim.height;
	}
	
	public Dim screenDim() {
		return new Dim(screenDim.width, screenDim.height);
	}
	
}
