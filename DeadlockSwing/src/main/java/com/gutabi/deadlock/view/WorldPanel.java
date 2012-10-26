package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

@SuppressWarnings({"serial", "static-access"})
public class WorldPanel extends JPanel {
	
	Logger logger = Logger.getLogger(WorldPanel.class);
	
	public WorldPanel() {
		setSize(new Dimension(1427, 822));
		setPreferredSize(new Dimension(1427, 822));
		setMaximumSize(new Dimension(1427, 822));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		AffineTransform origTrans = (AffineTransform)g2.getTransform().clone();
		
		g2.translate(VIEW.worldOriginX, VIEW.worldOriginY);
		
		MODEL.world.paint(g2);
		
		if (MODEL.FPS_DRAW) {
			MODEL.stats.paint(g2);
		}
		
		g2.setTransform(origTrans);
		
		MODEL.stroke.paint(g2);
		
	}
	
}
