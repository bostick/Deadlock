package com.brentonbostick.capsloc.world.sprites;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.sprites.ExplosionSheet.ExplosionSheetSprite;

public class AnimatedExplosion {
	
	public static final double explosionWidth = 2.21875;
	public static final double explosionHeight = 3.125;
	
	Point p;
	int lastFrame;
	double lastTime;
	
	public AnimatedExplosion(Point p) {
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
		
		ctxt.pushTransform();
		
		ctxt.translate(p.x - explosionWidth/2, p.y - explosionHeight/2);
		
		switch (index) {
		case 0:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION0, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 1:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION1, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 2:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION2, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 3:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION3, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 4:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION4, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 5:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION5, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 6:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION6, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 7:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION7, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 8:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION8, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 9:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION9, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 10:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION10, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 11:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION11, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 12:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION12, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 13:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION13, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 14:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION14, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		case 15:
			APP.explosionSheet.paint(ctxt, ExplosionSheetSprite.EXPLOSION15, ctxt.cam.pixelsPerMeter, 0, 0, explosionWidth, explosionHeight);
			break;
		}
		
		ctxt.popTransform();
		
	}
	
}
