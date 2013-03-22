package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.sprites.ExplosionSheet.ExplosionSheetSprite;

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
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION0, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 1:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION1, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 2:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION2, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 3:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION3, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 4:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION4, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 5:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION5, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 6:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION6, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 7:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION7, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 8:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION8, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 9:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION9, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 10:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION10, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 11:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION11, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 12:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION12, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 13:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION13, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 14:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION14, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 15:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION15, world.worldScreen.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		}
		
		ctxt.setTransform(origTransform);
		
	}
	
}
