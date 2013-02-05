package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ui.Image;
import com.gutabi.deadlock.ui.ImageEngine;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class Sheet {
	
	public enum Sprite {
		BLUEARROW { public int xStart() { return 128; } public int yStart() { return 0; } public int xEnd() { return 128+32; } public int yEnd() { return 0+32; } },
		GRASSTILE { public int xStart() { return 0; } public int yStart() { return 0; } public int xEnd() { return 0+32; } public int yEnd() { return 0+32; } },
		BRAKE { public int xStart() { return 0; } public int yStart() { return 64; } public int xEnd() { return 0+8; } public int yEnd() { return 64+8; } },
		
		CAR0 { public int xStart() { return 0; } public int yStart() { return 0; } public int xEnd() { return 0+64; } public int yEnd() { return 0+32; } },
		CAR1 { public int xStart() { return 0; } public int yStart() { return 32; } public int xEnd() { return 0+64; } public int yEnd() { return 32+32; } },
		CAR2 { public int xStart() { return 0; } public int yStart() { return 64; } public int xEnd() { return 0+64; } public int yEnd() { return 64+32; } },
		CAR3 { public int xStart() { return 0; } public int yStart() { return 96; } public int xEnd() { return 0+128; } public int yEnd() { return 96+64; } },
		CAR4 { public int xStart() { return 0; } public int yStart() { return 160; } public int xEnd() { return 0+64; } public int yEnd() { return 160+32; } },
		CAR5 { public int xStart() { return 0; } public int yStart() { return 192; } public int xEnd() { return 0+96; } public int yEnd() { return 192+32; } },
		CAR6 { public int xStart() { return 0; } public int yStart() { return 224; } public int xEnd() { return 0+96; } public int yEnd() { return 224+32; } },
		CAR7 { public int xStart() { return 0; } public int yStart() { return 256; } public int xEnd() { return 0+64; } public int yEnd() { return 256+32; } },
		CAR8 { public int xStart() { return 0; } public int yStart() { return 288; } public int xEnd() { return 0+96; } public int yEnd() { return 288+32; } },
		CAR9 { public int xStart() { return 0; } public int yStart() { return 320; } public int xEnd() { return 0+64; } public int yEnd() { return 320+32; } },
		CAR10 { public int xStart() { return 0; } public int yStart() { return 352; } public int xEnd() { return 0+96; } public int yEnd() { return 352+32; } },
		CAR11 { public int xStart() { return 96; } public int yStart() { return 0; } public int xEnd() { return 96+32; } public int yEnd() { return 0+32; } },
		
		FIXTUREARROW { public int xStart() { return 96; } public int yStart() { return 0; } public int xEnd() { return 96+32; } public int yEnd() { return 0+32; } },
		
		STUD { public int xStart() { return 160; } public int yStart() { return 0; } public int xEnd() { return 160+32; } public int yEnd() { return 0+32; } },
		
		STOPSIGN { public int xStart() { return 32; } public int yStart() { return 0; } public int xEnd() { return 32+32; } public int yEnd() { return 0+32; } },
		
		EXPLOSION0 { public int xStart() { return 71 * 0; } public int yStart() { return 0; } public int xEnd() { return 71 * 0 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION1 { public int xStart() { return 71 * 1; } public int yStart() { return 0; } public int xEnd() { return 71 * 1 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION2 { public int xStart() { return 71 * 2; } public int yStart() { return 0; } public int xEnd() { return 71 * 2 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION3 { public int xStart() { return 71 * 3; } public int yStart() { return 0; } public int xEnd() { return 71 * 3 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION4 { public int xStart() { return 71 * 4; } public int yStart() { return 0; } public int xEnd() { return 71 * 4 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION5 { public int xStart() { return 71 * 5; } public int yStart() { return 0; } public int xEnd() { return 71 * 5 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION6 { public int xStart() { return 71 * 6; } public int yStart() { return 0; } public int xEnd() { return 71 * 6 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION7 { public int xStart() { return 71 * 7; } public int yStart() { return 0; } public int xEnd() { return 71 * 7 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION8 { public int xStart() { return 71 * 8; } public int yStart() { return 0; } public int xEnd() { return 71 * 8 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION9 { public int xStart() { return 71 * 9; } public int yStart() { return 0; } public int xEnd() { return 71 * 9 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION10 { public int xStart() { return 71 * 10; } public int yStart() { return 0; } public int xEnd() { return 71 * 10 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION11 { public int xStart() { return 71 * 11; } public int yStart() { return 0; } public int xEnd() { return 71 * 11 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION12 { public int xStart() { return 71 * 12; } public int yStart() { return 0; } public int xEnd() { return 71 * 12 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION13 { public int xStart() { return 71 * 13; } public int yStart() { return 0; } public int xEnd() { return 71 * 13 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION14 { public int xStart() { return 71 * 14; } public int yStart() { return 0; } public int xEnd() { return 71 * 14 + 71; } public int yEnd() { return 0+100; } },
		EXPLOSION15 { public int xStart() { return 71 * 15; } public int yStart() { return 0; } public int xEnd() { return 71 * 15 + 71; } public int yEnd() { return 0+100; } },
		
		GRASS0 { public int xStart() { return 32 * 0; } public int yStart() { return 32; } public int xEnd() { return 32 * 0 + 32; } public int yEnd() { return 32+32; } },
		GRASS1 { public int xStart() { return 32 * 1; } public int yStart() { return 32; } public int xEnd() { return 32 * 1 + 32; } public int yEnd() { return 32+32; } },
		GRASS2 { public int xStart() { return 32 * 2; } public int yStart() { return 32; } public int xEnd() { return 32 * 2 + 32; } public int yEnd() { return 32+32; } };
		
		
		
		public abstract int xStart();
		public abstract int yStart();
		public abstract int xEnd();
		public abstract int yEnd();
	}
	
	Resource res;
	Image img;
	
	public Sheet(Resource res) {
		this.res = res;
	}
	
	public void load() throws Exception {
		ImageEngine iEngine = APP.platform.createImageEngine();
		img = iEngine.readImage(res);
	}
	
	public void paint(RenderingContext ctxt, Sprite s, int dx1, int dy1, int dx2, int dy2) {
		ctxt.paintImage(img, dx1, dy1, dx2, dy2, s.xStart(), s.yStart(), s.xEnd(), s.yEnd());
	}
	
	public void paint(RenderingContext ctxt, Sprite s, double orig, double dx1, double dy1, double dx2, double dy2) {
		ctxt.paintImage(img, orig, dx1, dy1, dx2, dy2, s.xStart(), s.yStart(), s.xEnd(), s.yEnd());
	}

}
