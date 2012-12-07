package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("static-access")
public class AnimatedGrass {
	
	public static double GRASS_SIZE = 1.0;
	
	Point p;
	int lastFrame;
	double lastTime;
	double phase;
	
	public AnimatedGrass(Point p) {
		this.p = p;
		
		phase = MODEL.world.RANDOM.nextDouble();
		
	}
	
	public void preStart() {
		lastFrame = -1;
		lastTime = -1;
	}
	
	public void preStep(double t) {
		
		if (lastTime == -1) {
			
			lastTime = t + phase;
			lastFrame = 0;
			
		} else if ((t - lastTime) > 1.0) {
			
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
			
			lastTime = t;
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		switch (lastFrame) {
		case 0:
			paint0(ctxt);
			break;
		case 1:
		case 3:
			paint1(ctxt);
			break;
		case 2:
			paint2(ctxt);
			break;
		}
		
	}
	
	private void paint0(RenderingContext ctxt) {
		
		ctxt.paintWorldImage(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2, VIEW.sheet,
				0,
				0,
				(int)Math.round(MODEL.world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				(int)Math.round(MODEL.world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				0, 256, 0+32, 256+32);
		
	}
	
	private void paint1(RenderingContext ctxt) {
		
		ctxt.paintWorldImage(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2, VIEW.sheet,
				0,
				0,
				(int)Math.round(MODEL.world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				(int)Math.round(MODEL.world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				32, 256, 32+32, 256+32);
		
	}
	
	private void paint2(RenderingContext ctxt) {
		
		ctxt.paintWorldImage(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2, VIEW.sheet,
				0,
				0,
				(int)Math.round(MODEL.world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				(int)Math.round(MODEL.world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				64, 256, 64+32, 256+32);
		
	}
}
