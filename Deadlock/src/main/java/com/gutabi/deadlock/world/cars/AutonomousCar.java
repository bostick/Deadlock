package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Fixture;

public class AutonomousCar extends Car {
	
	public Fixture source;
	
	public AutonomousCar(World w, Fixture f) {
		super(w);
		
		this.source = f;
	}
	
	public static AutonomousCar createCar(World world, Fixture f, int r) {
		
		AutonomousCar c = new AutonomousCar(world, f);
		c.driver = new AutonomousDriver(c);
		
		switch (r) {
		case 0:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 0;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 1:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 32;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 2:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 64;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 3:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 128;
			c.sheetRowStart = 96;
			c.sheetRowEnd = c.sheetRowStart + 64;
			c.CAR_LENGTH = 4.0;
			c.CAR_WIDTH = 2.0;
			break;
		case 4:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 160;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 5:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 192;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 6:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 224;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 7:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 256;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 8:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 288;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 9:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 320;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 10:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 352;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 11:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 384;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		}
		
		return c;
	}
	
}
