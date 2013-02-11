package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ResourceEngine;

public class SpriteSheet extends Sheet {
	
	public SpriteSheet() {
		ResourceEngine rEngine = APP.platform.createResourceEngine();
		
		res = rEngine.imageResource("spritesheet");
	}
	
	public enum SpriteSheetSprite implements Sprite {
		BLUEARROW { public int xStart() { return 128; } public int yStart() { return 0; } public int xEnd() { return 128+32; } public int yEnd() { return 0+32; } },
		GRASSTILE { public int xStart() { return 0; } public int yStart() { return 0; } public int xEnd() { return 0+32; } public int yEnd() { return 0+32; } },
		BRAKE { public int xStart() { return 0; } public int yStart() { return 64; } public int xEnd() { return 0+8; } public int yEnd() { return 64+8; } },
		
		FIXTUREARROW { public int xStart() { return 96; } public int yStart() { return 0; } public int xEnd() { return 96+32; } public int yEnd() { return 0+32; } },
		
		STUD { public int xStart() { return 160; } public int yStart() { return 0; } public int xEnd() { return 160+32; } public int yEnd() { return 0+32; } },
		
		STOPSIGN { public int xStart() { return 32; } public int yStart() { return 0; } public int xEnd() { return 32+32; } public int yEnd() { return 0+32; } },
		
		GRASS0 { public int xStart() { return 32 * 0; } public int yStart() { return 32; } public int xEnd() { return 32 * 0 + 32; } public int yEnd() { return 32+32; } },
		GRASS1 { public int xStart() { return 32 * 1; } public int yStart() { return 32; } public int xEnd() { return 32 * 1 + 32; } public int yEnd() { return 32+32; } },
		GRASS2 { public int xStart() { return 32 * 2; } public int yStart() { return 32; } public int xEnd() { return 32 * 2 + 32; } public int yEnd() { return 32+32; } };
		
	}
	
}
