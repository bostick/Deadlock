package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ResourceEngine;

public class CarSheet extends Sheet {
	
	public CarSheet() {
		
//		ImageEngine iEngine = APP.platform.createImageEngine();
		ResourceEngine rEngine = APP.platform.createResourceEngine();
		
		res = rEngine.imageResource("carsheet");
	}
	
	public enum CarSheetSprite implements Sprite {
		
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
		CAR11 { public int xStart() { return 0; } public int yStart() { return 384; } public int xEnd() { return 0+64; } public int yEnd() { return 384+32; } };
		
	}
	
}
