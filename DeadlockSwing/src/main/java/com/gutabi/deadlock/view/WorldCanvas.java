package com.gutabi.deadlock.view;


import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;



@SuppressWarnings("serial")
public class WorldCanvas extends Canvas {
	
	BufferStrategy bs;
//	Graphics2D canvasGraphics2D;
	
	public WorldCanvas() {
		
		setSize(new Dimension(1427, 822));
		setPreferredSize(new Dimension(1427, 822));
		setMaximumSize(new Dimension(1427, 822));
		
	}
	
	public void postDisplay() {
		this.createBufferStrategy(2);
		bs = getBufferStrategy();
//		canvasGraphics2D = (Graphics2D)bs.getDrawGraphics();
	}
	
	
	
//	@Override
	public void paint(Graphics g) {
		
//		super.paint(g);
//		Graphics2D g2 = (Graphics2D)g;
		
		bs.show();
		
//
//		
	}
	
}
