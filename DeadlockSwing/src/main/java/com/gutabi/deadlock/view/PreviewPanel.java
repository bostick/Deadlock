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
		
		if (VIEW.previewImage != null) {
			
			g.drawImage(VIEW.previewImage, 0, 0, null);
			
			int x = (int)(VIEW.worldViewport.x / MODEL.world.worldWidth * 100.);
			int y = (int)(VIEW.worldViewport.y / MODEL.world.worldHeight * 100.);
			
			int width = (int)(VIEW.worldViewport.width / MODEL.world.worldWidth * 100.);
			int height = (int)(VIEW.worldViewport.height / MODEL.world.worldHeight * 100.);
			
			g.setColor(Color.BLUE);
			g.drawRect(x, y, width, height);
			
		}
		
	}

}
