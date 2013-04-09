package com.gutabi.deadlock.world.sprites;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class CarSheet extends Sheet {
	
	public CarSheet() {
		res = APP.platform.imageResource("carsheet");
	}
	
	public enum CarSheetSprite implements Sprite {
		
		CAR0 {
			public int xStart() { return 0; }
			public int yStart() { return 0; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 0+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
			
		CAR1 { 
			public int xStart() { return 0; }
			public int yStart() { return 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR2 {
			public int xStart() { return 0; }
			public int yStart() { return 64; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 64+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
//		TANK {
//			public int xStart() { return 0; }
//			public int yStart() { return 96; }
//			public int xEnd() { return 0+128; }
//			public int yEnd() { return 96+64; }
//			
//			public int carLength() { return 4; }
//			public int carWidth() { return 2; }
//			
//			public CarType type() { return CarType.TANK; }
//		},
		
		CAR4 {
			public int xStart() { return 0; }
			public int yStart() { return 160; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 160+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR5 {
			public int xStart() { return 0; }
			public int yStart() { return 192; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 192+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		CAR6 {
			public int xStart() { return 0; }
			public int yStart() { return 224; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 224+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		RED {
			public int xStart() { return 0; }
			public int yStart() { return 256; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 256+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.RED; }
		},
		
		CAR8 {
			public int xStart() { return 0; }
			public int yStart() { return 288; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 288+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		CAR9 {
			public int xStart() { return 0; }
			public int yStart() { return 320; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 320+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR10 {
			public int xStart() { return 0; }
			public int yStart() { return 352; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 352+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		CAR11 {
			public int xStart() { return 0; }
			public int yStart() { return 384; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 384+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR12 {
			public int xStart() { return 0; }
			public int yStart() { return 416; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 416+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR13 {
			public int xStart() { return 0; }
			public int yStart() { return 448; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 448+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		};
		
		public abstract int carLength();
		public abstract int carWidth();
		public abstract CarType type();
		
	}
	
	public enum CarType {
		TWO, THREE, RED
	}
	
	public static CarSheetSprite sprite(int i) {
		switch (i) {
		case 0:
			return CarSheetSprite.CAR0;
		case 1:
			return CarSheetSprite.CAR1;
		case 2:
			return CarSheetSprite.CAR2;
		case 4:
			return CarSheetSprite.CAR4;
		case 5:
			return CarSheetSprite.CAR5;
		case 6:
			return CarSheetSprite.CAR6;
		case 7:
			return CarSheetSprite.RED;
		case 8:
			return CarSheetSprite.CAR8;
		case 9:
			return CarSheetSprite.CAR9;
		case 10:
			return CarSheetSprite.CAR10;
		case 11:
			return CarSheetSprite.CAR11;
		case 12:
			return CarSheetSprite.CAR12;
		case 13:
			return CarSheetSprite.CAR13;
		default:
			assert false;
			return null;
		}
	}
	
	public static int sheetIndex(CarType type, int curTypeCount) {
		
		switch (type) {
		case TWO:
			switch (curTypeCount) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 4;
			case 4:
				return 9;
			case 5:
				return 11;
			case 6:
				return 12;
			default:
				assert false;
				return -1;
			}
		case THREE:
			switch (curTypeCount) {
			case 0:
				return 5;
			case 1:
				return 6;
			case 2:
				return 8;
			case 3:
				return 10;
			case 4:
				return 13;
			case 5:
				return 5;
			case 6:
				return 6;
			default:
				assert false;
				return -1;
			}
		case RED:
			return 7;
		}
		
		assert false;
		return -1;
	}
	
}
