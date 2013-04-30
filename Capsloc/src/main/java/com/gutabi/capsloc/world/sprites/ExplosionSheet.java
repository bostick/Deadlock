package com.gutabi.capsloc.world.sprites;

import static com.gutabi.capsloc.CapslocApplication.APP;

public class ExplosionSheet extends Sheet {
	
	public ExplosionSheet() {
		res = APP.platform.imageResource("explosionsheet");
	}
	
	public enum ExplosionSheetSprite implements Sprite {
		
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
		EXPLOSION15 { public int xStart() { return 71 * 15; } public int yStart() { return 0; } public int xEnd() { return 71 * 15 + 71; } public int yEnd() { return 0+100; } };
		
	}
	
}
