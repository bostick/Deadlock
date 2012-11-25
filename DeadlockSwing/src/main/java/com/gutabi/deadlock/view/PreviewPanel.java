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
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 100);
		
		g.setColor(Color.GREEN);
		for (int i = 0; i < MODEL.world.quadrants[0].length; i++) {
			for (int j = 0; j < MODEL.world.quadrants.length; j++) {
				if (MODEL.world.quadrants[i][j] == 1) {
					g.fillRect(i * 33, j * 33, 33, 33);
				}
			}
		}
		
		
//		int x = (int)(VIEW.worldOriginX / 1427 * (1427 / 10));
//		int y = (int)(VIEW.worldOriginY / 822 * (822 / 10));
		
		int width;
//		if (DMath.lessThanEquals(MODEL.world.WORLD_WIDTH * MODEL.PIXELS_PER_METER, VIEW.canvas.getWidth())) {
//			width = (int)((MODEL.world.WORLD_WIDTH / MODEL.world.WORLD_WIDTH) * (1427 / 10));
//		} else {
//			
//		}
		
		width = (int)(1427 * 100 / (MODEL.world.getWorldWidth() * MODEL.PIXELS_PER_METER));
		int height;
//		if (DMath.lessThanEquals(MODEL.world.WORLD_HEIGHT * MODEL.PIXELS_PER_METER, VIEW.canvas.getHeight())) {
//			height = (int)((MODEL.world.WORLD_HEIGHT / MODEL.world.WORLD_HEIGHT) * (822 / 10));
//		} else {
//			height = (int)((MODEL.world.QUADRANT_HEIGHT / MODEL.world.WORLD_HEIGHT) * (822 / 10));
//		}
		height = (int)(822 * 100 / (MODEL.world.getWorldHeight() * MODEL.PIXELS_PER_METER));
		
		/*
		 * when worldOriginX is 0, x is 0
		 * when worldOriginX is worldWidth*pixelsperMeter-1427, x is 100 - width
		 */
		
		int x = (int)(VIEW.worldOriginX * (100 - width) / (MODEL.world.getWorldWidth() * MODEL.PIXELS_PER_METER - 1427));
		int y = (int)(VIEW.worldOriginY * (100 - height) / (MODEL.world.getWorldHeight() * MODEL.PIXELS_PER_METER - 822));
		
		g.setColor(Color.BLUE);
		g.drawRect(x, y, width, height);
	}

}
