package com.gutabi.bypass.ui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;

import com.gutabi.deadlock.math.Dim;
import com.gutabi.deadlock.math.Point;

public class WindowInfo {
	
	private static Insets screenInsets;
	private static Dimension screenDim;
	
	static {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations()[0];
		Insets si = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		screenInsets = si;
		screenDim = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public static Dim windowDim() {
		return new Dim(screenDim.width - screenInsets.left - screenInsets.right, screenDim.height - screenInsets.top - screenInsets.bottom);
	}
	
	public static Point windowLoc() {
		return new Point(screenInsets.left, screenInsets.top);
	}
	
	public static Dim screenDim() {
		return new Dim(screenDim.width, screenDim.height);
	}
	
}