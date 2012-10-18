package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

@SuppressWarnings({"serial", "static-access"})
public class WorldPanel extends JPanel {
	
	Logger logger = Logger.getLogger(WorldPanel.class);
	
	public WorldPanel() {
		//setPreferredSize(new Dimension(1400, 822));
		setSize(new Dimension(1427, 822));
		setPreferredSize(new Dimension(1427, 822));
		setMaximumSize(new Dimension(1427, 822));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		int x = getWidth()/2 - (int)((MODEL.world.WORLD_WIDTH * MODEL.PIXELS_PER_METER)/2);
		int y = getHeight()/2 - (int)((MODEL.world.WORLD_HEIGHT * MODEL.PIXELS_PER_METER)/2);
		g2.translate(x, y);
		
		MODEL.world.paint(g2);
	}
	
}
