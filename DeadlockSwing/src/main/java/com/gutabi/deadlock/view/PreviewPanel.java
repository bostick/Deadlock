package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

//@SuppressWarnings({"serial", "static-access"})
@SuppressWarnings({"serial"})
public class PreviewPanel extends JPanel {
	
	public PreviewPanel() {
		setSize(new Dimension(100, 100));
		setPreferredSize(new Dimension(100, 100));
		setMaximumSize(new Dimension(100, 100));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(VIEW.previewImage, 0, 0, null);
		
		int x = (int)(VIEW.viewport.x / MODEL.world.worldWidth * 100.);
		int y = (int)(VIEW.viewport.y / MODEL.world.worldHeight * 100.);
		
		int width = (int)(VIEW.viewport.width / MODEL.world.worldWidth * 100.);
		int height = (int)(VIEW.viewport.height / MODEL.world.worldHeight * 100.);
		
		g.setColor(Color.BLUE);
		g.drawRect(x, y, width, height);
	}

}
