package com.gutabi.deadlock.world.cars;

import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.sprites.Sheet.Sprite;

public class InteractiveCar extends Car {
	
	public InteractiveCar(World w) {
		super(w);
	}
	
	public static InteractiveCar createCar(World world, int r) {
		
		InteractiveCar c = new InteractiveCar(world);
//		c.driver = new InteractiveDriver(c);
		
		switch (r) {
		case 0:
			c.sprite = Sprite.CAR0;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 1:
			c.sprite = Sprite.CAR1;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 2:
			c.sprite = Sprite.CAR2;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 3:
			c.sprite = Sprite.CAR3;
			c.CAR_LENGTH = 4.0;
			c.CAR_WIDTH = 2.0;
			break;
		case 4:
			c.sprite = Sprite.CAR4;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 5:
			c.sprite = Sprite.CAR5;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 6:
			c.sprite = Sprite.CAR6;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 7:
			c.sprite = Sprite.CAR7;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 8:
			c.sprite = Sprite.CAR8;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 9:
			c.sprite = Sprite.CAR9;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 10:
			c.sprite = Sprite.CAR10;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 11:
			c.sprite = Sprite.CAR11;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		}
		
		return c;
	}
	
}
