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
		
		AffineTransform origTrans = g2.getTransform();
		
		g2.translate(VIEW.worldOriginX, VIEW.worldOriginY);
		g2.setStroke(VIEW.worldStroke);
		
		MODEL.world.paint(g2);
		
		if (MODEL.stroke != null) {
			MODEL.stroke.paint(g2);
		}
		
		if (MODEL.FPS_DRAW) {
			
			g2.setTransform(origTrans);
			g2.setStroke(VIEW.worldStroke);
			
			g2.translate(VIEW.worldAABBX, VIEW.worldAABBY);
			
			MODEL.stats.paint(g2);
			
			g2.setTransform(origTrans);
		}
		
		g2.setTransform(origTrans);
		
	}
	
}
