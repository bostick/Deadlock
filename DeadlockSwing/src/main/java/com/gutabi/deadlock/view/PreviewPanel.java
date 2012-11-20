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
		setSize(new Dimension(1427 / 10, 822 / 10));
		setPreferredSize(new Dimension(1427 / 10, 822 / 10));
		setMaximumSize(new Dimension(1427 / 10, 822 / 10));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.RED);
		g.fillRect(0, 0, 1427 / 10, 822 / 10);

		g.setColor(Color.BLUE);
		g.fillRect(
				(int)(VIEW.worldOriginX / 10),
				(int)(VIEW.worldOriginY / 10),
				(int)(MODEL.world.QUADRANT_HEIGHT/MODEL.world.WORLD_HEIGHT * (1427 / 10)),
				(int)(MODEL.world.QUADRANT_HEIGHT/MODEL.world.WORLD_HEIGHT * (822 / 10)));
	}

}
