package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;

@SuppressWarnings("static-access")
public class AnimatedGrass {
	
	public static double GRASS_SIZE = 1.0;
	
	public final World world;
	Point p;
	int lastFrame;
	double lastTime;
	double phase;
	
	public AnimatedGrass(World world, Point p) {
		this.world = world;
		this.p = p;
		phase = world.RANDOM.nextDouble();
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
		ctxt.paintImage(
				p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2, 1/world.PIXELS_PER_METER_DEBUG,
				VIEW.sheet,
				0, 0, (int)Math.round(world.PIXELS_PER_METER_DEBUG * GRASS_SIZE), (int)Math.round(world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				0, 256, 0+32, 256+32);	
	}
	
	private void paint1(RenderingContext ctxt) {
		ctxt.paintImage(
				p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2, 1/world.PIXELS_PER_METER_DEBUG,
				VIEW.sheet,
				0, 0, (int)Math.round(world.PIXELS_PER_METER_DEBUG * GRASS_SIZE), (int)Math.round(world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				32, 256, 32+32, 256+32);	
	}
	
	private void paint2(RenderingContext ctxt) {
		ctxt.paintImage(
				p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2, 1/world.PIXELS_PER_METER_DEBUG,
				VIEW.sheet,
				0, 0, (int)Math.round(world.PIXELS_PER_METER_DEBUG * GRASS_SIZE), (int)Math.round(world.PIXELS_PER_METER_DEBUG * GRASS_SIZE),
				64, 256, 64+32, 256+32);	
	}
}
