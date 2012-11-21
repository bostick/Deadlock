package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.gutabi.deadlock.core.DMath;

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

//		int x = (int)(VIEW.worldOriginX / 1427 * (1427 / 10));
//		int y = (int)(VIEW.worldOriginY / 822 * (822 / 10));
		int x = (int)(VIEW.worldOriginX / 10);
		int y = (int)(VIEW.worldOriginY / 10);
		int width;
		if (DMath.lessThanEquals(MODEL.world.WORLD_WIDTH * MODEL.PIXELS_PER_METER, VIEW.canvas.getWidth())) {
			width = (int)((MODEL.world.WORLD_WIDTH / MODEL.world.WORLD_WIDTH) * (1427 / 10));
		} else {
			width = (int)((MODEL.world.QUADRANT_WIDTH / MODEL.world.WORLD_WIDTH) * (1427 / 10));
		}
		int height;
		if (DMath.lessThanEquals(MODEL.world.WORLD_HEIGHT * MODEL.PIXELS_PER_METER, VIEW.canvas.getHeight())) {
			height = (int)((MODEL.world.WORLD_HEIGHT / MODEL.world.WORLD_HEIGHT) * (822 / 10));
		} else {
			height = (int)((MODEL.world.QUADRANT_HEIGHT / MODEL.world.WORLD_HEIGHT) * (822 / 10));
		}
		
		g.setColor(Color.BLUE);
		g.fillRect(x, y, width, height);
	}

}
