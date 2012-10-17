package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
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
		VIEW.paint(g2);
	}
	
}
