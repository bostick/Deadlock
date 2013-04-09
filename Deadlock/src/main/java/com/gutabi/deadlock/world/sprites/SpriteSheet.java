package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class SpriteSheet extends Sheet {
	
	public SpriteSheet() {
		res = APP.platform.imageResource("spritesheet");
	}
	
	public enum SpriteSheetSprite implements Sprite {
		BLUEARROW { public int xStart() { return 128; } public int yStart() { return 0; } public int xEnd() { return 128+32; } public int yEnd() { return 0+32; } },
		GRASSTILE { public int xStart() { return 0; } public int yStart() { return 0; } public int xEnd() { return 0+32; } public int yEnd() { return 0+32; } },
		BRAKE { public int xStart() { return 0; } public int yStart() { return 64; } public int xEnd() { return 0+8; } public int yEnd() { return 64+8; } },
		
		FIXTUREARROW { public int xStart() { return 96; } public int yStart() { return 0; } public int xEnd() { return 96+32; } public int yEnd() { return 0+32; } },
		
		INNERSTUD { public int xStart() { return 160; } public int yStart() { return 0; } public int xEnd() { return 160+32; } public int yEnd() { return 0+32; } },
		TOPSTUD { public int xStart() { return 192; } public int yStart() { return 0; } public int xEnd() { return 192+32; } public int yEnd() { return 0+32; } },
		RIGHTSTUD { public int xStart() { return 224; } public int yStart() { return 0; } public int xEnd() { return 224+32; } public int yEnd() { return 0+32; } },
		BOTTOMSTUD { public int xStart() { return 256; } public int yStart() { return 0; } public int xEnd() { return 256+32; } public int yEnd() { return 0+32; } },
		LEFTSTUD { public int xStart() { return 288; } public int yStart() { return 0; } public int xEnd() { return 288+32; } public int yEnd() { return 0+32; } },
		
		STOPSIGN { public int xStart() { return 32; } public int yStart() { return 0; } public int xEnd() { return 32+32; } public int yEnd() { return 0+32; } },
		
		GRASS0 { public int xStart() { return 32 * 0; } public int yStart() { return 32; } public int xEnd() { return 32 * 0 + 32; } public int yEnd() { return 32+32; } },
		GRASS1 { public int xStart() { return 32 * 1; } public int yStart() { return 32; } public int xEnd() { return 32 * 1 + 32; } public int yEnd() { return 32+32; } },
		GRASS2 { public int xStart() { return 32 * 2; } public int yStart() { return 32; } public int xEnd() { return 32 * 2 + 32; } public int yEnd() { return 32+32; } };
		
	}
	
}
