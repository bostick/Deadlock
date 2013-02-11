package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

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
		
		aabb = APP.platform.createShapeEngine().createAABB(p.x - GRASS_SIZE/2, p.y - GRASS_SIZE/2, GRASS_SIZE, GRASS_SIZE);
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
			paint(ctxt, 0);
			break;
		case 1:
		case 3:
			paint(ctxt, 1);
			break;
		case 2:
			paint(ctxt, 2);
			break;
		}
		
	}
	
	private void paint(RenderingContext ctxt, int index) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2);
		
//		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
//				0, 0, GRASS_SIZE, GRASS_SIZE,
//				0, 32, 0+32, 32+32);	
		
		switch (index) {
		case 0:
			APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASS0, world.screen.pixelsPerMeter, 0, 0, GRASS_SIZE, GRASS_SIZE);
			break;
		case 1:
			APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASS1, world.screen.pixelsPerMeter, 0, 0, GRASS_SIZE, GRASS_SIZE);
			break;
		case 2:
			APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASS2, world.screen.pixelsPerMeter, 0, 0, GRASS_SIZE, GRASS_SIZE);
			break;
		}
		
		ctxt.setTransform(origTransform);
		
	}
	
}
