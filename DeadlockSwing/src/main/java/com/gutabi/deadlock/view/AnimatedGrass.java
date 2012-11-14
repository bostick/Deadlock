package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;

import com.gutabi.deadlock.model.World;

@SuppressWarnings("static-access")
public class AnimatedGrass {
	
	int lastFrame;
	double lastTime;
	
	public void preStart() {
		
		lastFrame = -1;
		lastTime = -1;
		
	}
	
	public void paint(Graphics2D g2) {
		
		if (lastTime == -1) {
			
			lastTime = MODEL.world.t;
			lastFrame = 0;
			
		} else if ((MODEL.world.t - lastTime) > 1.0) {
			
			switch (lastFrame) {
			case 0:
				lastFrame = 1;
				break;
			case 1:
				lastFrame = 2;
				break;
			case 2:
				lastFrame = 3;
				break;
			case 3:
				lastFrame = 0;
				break;
			}
			
			lastTime = MODEL.world.t;
		}
		
		switch (lastFrame) {
		case 0:
			paint0(g2);
			break;
		case 1:
		case 3:
			paint1(g2);
			break;
		case 2:
			paint2(g2);
			break;
		}
		
	}
	
	private void paint0(Graphics2D g2) {
		g2.drawImage(VIEW.sheet,
				(int)(World.WORLD_WIDTH/2 * MODEL.PIXELS_PER_METER) - 16,
				(int)(World.WORLD_HEIGHT/2 * MODEL.PIXELS_PER_METER) - 16,
				(int)(World.WORLD_WIDTH/2 * MODEL.PIXELS_PER_METER) + 16,
				(int)(World.WORLD_HEIGHT/2 * MODEL.PIXELS_PER_METER) + 16,
				0, 256, 0+32, 256+32, null);
	}
	
	private void paint1(Graphics2D g2) {
		g2.drawImage(VIEW.sheet,
				(int)(World.WORLD_WIDTH/2 * MODEL.PIXELS_PER_METER) - 16,
				(int)(World.WORLD_HEIGHT/2 * MODEL.PIXELS_PER_METER) - 16,
				(int)(World.WORLD_WIDTH/2 * MODEL.PIXELS_PER_METER) + 16,
				(int)(World.WORLD_HEIGHT/2 * MODEL.PIXELS_PER_METER) + 16,
				32, 256, 32+32, 256+32, null);
	}
	
	private void paint2(Graphics2D g2) {
		g2.drawImage(VIEW.sheet,
				(int)(World.WORLD_WIDTH/2 * MODEL.PIXELS_PER_METER) - 16,
				(int)(World.WORLD_HEIGHT/2 * MODEL.PIXELS_PER_METER) - 16,
				(int)(World.WORLD_WIDTH/2 * MODEL.PIXELS_PER_METER) + 16,
				(int)(World.WORLD_HEIGHT/2 * MODEL.PIXELS_PER_METER) + 16,
				64, 256, 64+32, 256+32, null);
	}
}
