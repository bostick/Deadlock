package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class SpriteSheet extends Sheet {
	
	public SpriteSheet() {
		res = APP.platform.imageResource("spritesheet");
	}
	
	public enum SpriteSheetSprite implements Sprite {
		
		INNERSTUD { public int xStart() { return 0; } public int yStart() { return 0; } public int xEnd() { return 0+32; } public int yEnd() { return 0+32; } },
		TOPSTUD { public int xStart() { return 32; } public int yStart() { return 0; } public int xEnd() { return 32+32; } public int yEnd() { return 0+32; } },
		RIGHTSTUD { public int xStart() { return 64; } public int yStart() { return 0; } public int xEnd() { return 64+32; } public int yEnd() { return 0+32; } },
		BOTTOMSTUD { public int xStart() { return 96; } public int yStart() { return 0; } public int xEnd() { return 96+32; } public int yEnd() { return 0+32; } },
		LEFTSTUD { public int xStart() { return 128; } public int yStart() { return 0; } public int xEnd() { return 128+32; } public int yEnd() { return 0+32; } },
		
		GRASSTILE { public int xStart() { return 0; } public int yStart() { return 32; } public int xEnd() { return 0+32; } public int yEnd() { return 32+32; } },
		GRASS0 { public int xStart() { return 32; } public int yStart() { return 32; } public int xEnd() { return 32 + 32; } public int yEnd() { return 32+32; } },
		GRASS1 { public int xStart() { return 64; } public int yStart() { return 32; } public int xEnd() { return 64 + 32; } public int yEnd() { return 32+32; } },
		GRASS2 { public int xStart() { return 96; } public int yStart() { return 32; } public int xEnd() { return 96 + 32; } public int yEnd() { return 32+32; } },
		
		
		
		BLUEARROW { public int xStart() { return 128; } public int yStart() { return 0; } public int xEnd() { return 128+32; } public int yEnd() { return 0+32; } },
		
		BRAKE { public int xStart() { return 0; } public int yStart() { return 64; } public int xEnd() { return 0+8; } public int yEnd() { return 64+8; } },
		
		FIXTUREARROW { public int xStart() { return 96; } public int yStart() { return 0; } public int xEnd() { return 96+32; } public int yEnd() { return 0+32; } },
		
		STOPSIGN { public int xStart() { return 32; } public int yStart() { return 0; } public int xEnd() { return 32+32; } public int yEnd() { return 0+32; } },
		
	}
	
}
