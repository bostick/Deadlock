package com.gutabi.capsloc.world.sprites;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.ShapeUtils;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.sprites.SpriteSheet.SpriteSheetSprite;

public class AnimatedGrass {
	
	public static double GRASS_SIZE = 1.0;
	
	Point p;
	int lastFrame;
	double lastTime;
	double phase;
	int startFrame;
	
	public AABB aabb;
	
	public AnimatedGrass(Point p) {
		this.p = p;
		phase = APP.RANDOM.nextDouble();
		startFrame = APP.RANDOM.nextInt(4);
		lastFrame = startFrame;
		
		aabb = new AABB(p.x - GRASS_SIZE/2, p.y - GRASS_SIZE/2, GRASS_SIZE, GRASS_SIZE);
	}
	
	public void preStart() {
		lastFrame = -1;
		lastTime = -1;
	}
	
	public boolean step(double t) {
		
		if (lastTime == -1) {
			
			lastTime = t + phase;
			lastFrame = startFrame;
			
			return true;
			
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
			
			return true;
			
		} else {
			return false;
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (!ShapeUtils.intersectAA(aabb, ctxt.cam.worldViewport)) {
			return;
		}
		
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
		
		ctxt.pushTransform();
		
		ctxt.translate(p.x - AnimatedGrass.GRASS_SIZE/2, p.y - AnimatedGrass.GRASS_SIZE/2);
		
		switch (index) {
		case 0:
			APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASS0, ctxt.cam.pixelsPerMeter, 0, 0, GRASS_SIZE, GRASS_SIZE);
			break;
		case 1:
			APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASS1, ctxt.cam.pixelsPerMeter, 0, 0, GRASS_SIZE, GRASS_SIZE);
			break;
		case 2:
			APP.spriteSheet.paint(ctxt, SpriteSheetSprite.GRASS2, ctxt.cam.pixelsPerMeter, 0, 0, GRASS_SIZE, GRASS_SIZE);
			break;
		}
		
		ctxt.popTransform();
	}
	
}
