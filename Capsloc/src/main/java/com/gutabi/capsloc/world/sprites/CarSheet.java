package com.gutabi.capsloc.world.sprites;

import static com.gutabi.capsloc.CapslocApplication.APP;

public class CarSheet extends Sheet {
	
	public CarSheet() {
		res = APP.platform.imageResource("carsheet");
	}
	
	public enum CarSheetSprite implements Sprite {
		
		RED {
			public int xStart() { return 0; }
			public int yStart() { return 0; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 0+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.RED; }
		},
		
		CAR0 {
			public int xStart() { return 0; }
			public int yStart() { return 1 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 1 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
			
		CAR1 { 
			public int xStart() { return 0; }
			public int yStart() { return 2 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 2 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR2 {
			public int xStart() { return 0; }
			public int yStart() { return 3 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 3 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR3 {
			public int xStart() { return 0; }
			public int yStart() { return 4 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 4 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR4 {
			public int xStart() { return 0; }
			public int yStart() { return 5 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 5 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR5 {
			public int xStart() { return 0; }
			public int yStart() { return 6 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 6 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		CAR6 {
			public int xStart() { return 0; }
			public int yStart() { return 7 * 32; }
			public int xEnd() { return 0+64; }
			public int yEnd() { return 7 * 32+32; }
			
			public int carLength() { return 2; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.TWO; }
		},
		
		TRUCK0 {
			public int xStart() { return 0; }
			public int yStart() { return 8 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 8 * 32+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		TRUCK1 {
			public int xStart() { return 0; }
			public int yStart() { return 9 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 9 * 32+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		TRUCK2 {
			public int xStart() { return 0; }
			public int yStart() { return 10 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 10 * 32+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		TRUCK3 {
			public int xStart() { return 0; }
			public int yStart() { return 11 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 11 * 32+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		TRUCK4 {
			public int xStart() { return 0; }
			public int yStart() { return 12 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 12 * 32+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		TRUCK5 {
			public int xStart() { return 0; }
			public int yStart() { return 13 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 13 * 32+32; }
			
			public int carLength() { return 3; }
			public int carWidth() { return 1; }
			
			public CarType type() { return CarType.THREE; }
		},
		
		TRUCK6 {
			public int xStart() { return 0; }
			public int yStart() { return 14 * 32; }
			public int xEnd() { return 0+96; }
			public int yEnd() { return 14 * 32+32; }
			
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
			return CarSheetSprite.RED;
		case 1:
			return CarSheetSprite.CAR0;
		case 2:
			return CarSheetSprite.CAR1;
		case 3:
			return CarSheetSprite.CAR2;
		case 4:
			return CarSheetSprite.CAR3;
		case 5:
			return CarSheetSprite.CAR4;
		case 6:
			return CarSheetSprite.CAR5;
		case 7:
			return CarSheetSprite.CAR6;
		case 8:
			return CarSheetSprite.TRUCK0;
		case 9:
			return CarSheetSprite.TRUCK1;
		case 10:
			return CarSheetSprite.TRUCK2;
		case 11:
			return CarSheetSprite.TRUCK3;
		case 12:
			return CarSheetSprite.TRUCK4;
		case 13:
			return CarSheetSprite.TRUCK5;
		case 14:
			return CarSheetSprite.TRUCK6;
		default:
			assert false;
			return null;
		}
	}
	
	public static int sheetIndex(CarType type, int curTypeCount) {
		
		switch (type) {
		case RED:
			return 0;
		case TWO:
			switch (curTypeCount) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;
			case 4:
				return 5;
			case 5:
				return 6;
			case 6:
				return 7;
			default:
				assert false;
				return -1;
			}
		case THREE:
			switch (curTypeCount) {
			case 0:
				return 8;
			case 1:
				return 9;
			case 2:
				return 10;
			case 3:
				return 11;
			case 4:
				return 12;
			case 5:
				return 13;
			case 6:
				return 14;
			default:
				assert false;
				return -1;
			}
		}
		
		assert false;
		return -1;
	}
	
}
