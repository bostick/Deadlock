package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;


@SuppressWarnings("serial")
public class WorldPanel extends JPanel {
	
	Logger logger = Logger.getLogger("deadlock");
	
	public WorldPanel() {
		//setPreferredSize(new Dimension(1400, 822));
		setSize(new Dimension(1427, 822));
		setPreferredSize(new Dimension(1427, 822));
		setMaximumSize(new Dimension(1427, 822));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Dimension d = getSize();
		
		Graphics2D g2 = (Graphics2D)g;
		
//		g2.scale(VIEW.getZoom(), VIEW.getZoom());
//		g2.translate((double)-VIEW.worldViewLoc.getX(), (double)-VIEW.worldViewLoc.getY());
		
		g2.drawImage(VIEW.backgroundImage, 0, 0, d.width, d.height, null);
		
		VIEW.drawScene(g2);
		
	}
	
}
