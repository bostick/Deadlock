package com.gutabi.deadlock.view;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

@SuppressWarnings("serial")
public class WorldCanvas extends Canvas {
	
	BufferStrategy bs;
	
	public WorldCanvas() {
		
		setSize(new Dimension(1427, 822));
		setPreferredSize(new Dimension(1427, 822));
		setMaximumSize(new Dimension(1427, 822));
		
	}
	
	public void postDisplay() {
		this.createBufferStrategy(3);
		bs = getBufferStrategy();
	}
	
	public void paint(Graphics g) {
		bs.show();
	}
	
}
