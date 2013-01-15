package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class AnimatedGrass {
	
	public static double GRASS_SIZE = 1.0;
	
	World world;
	Point p;
	int lastFrame;
	double lastTime;
	double phase;
	int startFrame;
	
	public AABB aabb;
	
	public AnimatedGrass(World world, Point p) {
		this.world = world;
		this.p = p;
		phase = APP.RANDOM.nextDouble();
		startFrame = APP.RANDOM.nextInt(4);
		lastFrame = startFrame;
		
		aabb = APP.platform.createShapeEngine().createAABB(null, p.x - GRASS_SIZE/2, p.y - GRASS_SIZE/2, GRASS_SIZE, GRASS_SIZE);
	}
	
	public void preStart() {
		lastFrame = -1;
		lastTime = -1;
	}
	
	public void preStep(double t) {
		
		if (lastTime == -1) {
			
			lastTime = t + phase;
			lastFrame = startFrame;
			
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
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2);
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				0, 0, GRASS_SIZE, GRASS_SIZE,
				0, 32, 0+32, 32+32);	
		
		ctxt.setTransform(origTransform);
		
	}
	
	private void paint1(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2);
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				0, 0, GRASS_SIZE, GRASS_SIZE,
				32, 32, 32+32, 32+32);
		
		ctxt.setTransform(origTransform);
		
	}
	
	private void paint2(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2);
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				0, 0, GRASS_SIZE, GRASS_SIZE,
				64, 32, 64+32, 32+32);
		
		ctxt.setTransform(origTransform);
		
	}
}
