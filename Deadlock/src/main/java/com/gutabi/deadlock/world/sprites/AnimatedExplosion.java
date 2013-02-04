package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.sprites.Sheet.Sprite;

public class AnimatedExplosion {
	
	public static final double explosionWidth = 2.21875;
	public static final double explosionHeight = 3.125;
	
	World world;
	Point p;
	int lastFrame;
	double lastTime;
	
	public AnimatedExplosion(World world, Point p) {
		this.world = world;
		this.p = p;
	}
	
	public void preStart() {
		lastFrame = -1;
		lastTime = -1;	
	}
	
	public void preStep(double t) {
		if (lastTime == -1) {
			
			lastTime = t;
			lastFrame = 0;
			
		} else if ((t - lastTime) > 0.1) {
			
			if (lastFrame < 15) {
				lastFrame = lastFrame + 1;
				lastTime = t;
			}
		}
	}
	
	public boolean postStep(double t) {
		if (lastFrame == 15) {
			if ((t - lastTime) > 0.1) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}
	
	public void paint(RenderingContext ctxt) {
		paint(ctxt, lastFrame);
		
	}
	
	private void paint(RenderingContext ctxt, int index) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x - explosionWidth/2, p.y - explosionHeight/2);
		
		switch (index) {
		case 0:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION0, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 1:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION1, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 2:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION2, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 3:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION3, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 4:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION4, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 5:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION5, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 6:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION6, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 7:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION7, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 8:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION8, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 9:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION9, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 10:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION10, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 11:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION11, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 12:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION12, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 13:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION13, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 14:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION14, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 15:
			APP.explosionSheet.paint(ctxt, Sprite.EXPLOSION15, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		}
		
//		ctxt.paintImage(APP.explosionSheet, world.screen.pixelsPerMeter,
//				0, 0, explosionWidth, explosionHeight,
//				71 * index, 0, 71 * index + 71, 100);
//		APP.explosionSheet.paint(ctxt, s, world.screen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
		
		
		ctxt.setTransform(origTransform);
		
	}
	
}
