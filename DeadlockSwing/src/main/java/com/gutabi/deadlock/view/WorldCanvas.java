package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

//@SuppressWarnings("serial")
@SuppressWarnings({"serial", "static-access"})
public class WorldCanvas extends Canvas {
	
	BufferStrategy bs;
	
	public WorldCanvas() {
		
		setSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		setPreferredSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		setMaximumSize(new Dimension(VIEW.CANVAS_WIDTH, VIEW.CANVAS_HEIGHT));
		
	}
	
	public void postDisplay() {
		createBufferStrategy(2);
		bs = getBufferStrategy();
	}
	
	public void paint(Graphics g) {
		bs.show();
	}
	
}
