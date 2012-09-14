package com.gutabi.deadlock.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PreviewPanel extends JPanel {
	
	public PreviewPanel() {
		
		//Dimension panelSize = VIEW.panel.getSize();
		
		setSize(new Dimension(100, 100));
		setPreferredSize(new Dimension(100, 100));
		setMaximumSize(new Dimension(100, 100));
		//setBackground(Color.YELLOW);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.RED);
		g.fillRect(0, 0, 100, 100);
		
		g.setColor(Color.BLUE);
		g.fillRect((int)(VIEW.worldViewLoc.getX() * 100)/2048, (int)(VIEW.worldViewLoc.getY() * 100)/2048, (int)(VIEW.worldViewDim.width * 100)/2048, (int)(VIEW.worldViewDim.height * 100)/2048);
	}
	
	
	
}
