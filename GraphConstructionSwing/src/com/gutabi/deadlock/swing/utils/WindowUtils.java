package com.gutabi.deadlock.swing.utils;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;

public class WindowUtils {
	
	private static Insets screenInsets;
	private static Dimension screenDim;
	
	static {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations()[0];
		Insets si = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		screenInsets = si;
		screenDim = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public static int windowWidth() {
		return screenDim.width - screenInsets.left - screenInsets.right;
	}
	
	public static int windowHeight() {
		return screenDim.height - screenInsets.top - screenInsets.bottom;
	}
	
	public static Dimension windowDim() {
		return new Dimension(screenDim.width - screenInsets.left - screenInsets.right, screenDim.height - screenInsets.top - screenInsets.bottom);
	}
	
	public static int windowX() {
		return screenInsets.left;
	}
	
	public static int windowY() {
		return screenInsets.top;
	}
	
	public static int screenWidth() {
		return screenDim.width;
	}
	
	public static int screenHeight() {
		return screenDim.height;
	}
	
	public static Dimension screenDim() {
		return screenDim;
	}
}
