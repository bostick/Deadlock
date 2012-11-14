package com.gutabi.deadlock.view;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

@SuppressWarnings("serial")
public class WorldCanvas extends Canvas {
	
	BufferStrategy bs;
	
	public WorldCanvas() {
		
		setSize(new Dimension(1427, 822));
		setPreferredSize(new Dimension(1427, 822));
		setMaximumSize(new Dimension(1427, 822));
		
	}
	
	public void postDisplay() {
		this.createBufferStrategy(2);
		bs = getBufferStrategy();
	}
	
	@Override
	public void paint(Graphics g) {
//		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		
		MODEL.paint(g2);
		
	}
	
}
