package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings({"serial", "static-access"})
public class PreviewPanel extends JPanel {
	
	public PreviewPanel() {
		setSize(new Dimension(100, 100));
		setPreferredSize(new Dimension(100, 100));
		setMaximumSize(new Dimension(100, 100));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(VIEW.previewBackgroundImage, 0, 0, null);
		
		int width = (int)(1427 * 100 / (MODEL.world.worldWidth * MODEL.PIXELS_PER_METER));
		int height = (int)(822 * 100 / (MODEL.world.worldHeight * MODEL.PIXELS_PER_METER));
		
		int x = (int)(VIEW.worldOriginX * (100 - width) / (MODEL.world.worldWidth * MODEL.PIXELS_PER_METER - 1427));
		int y = (int)(VIEW.worldOriginY * (100 - height) / (MODEL.world.worldHeight * MODEL.PIXELS_PER_METER - 822));
		
		g.setColor(Color.BLUE);
		g.drawRect(x, y, width, height);
	}

}
