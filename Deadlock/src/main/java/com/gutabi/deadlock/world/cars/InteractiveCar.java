package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
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
	
	public void preStep(double t) {
		
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (APP.CARTEXTURE_DRAW) {
			paintImage(ctxt);
		} else {
			ctxt.setColor(Color.BLUE);
			paintRect(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			
			ctxt.setColor(Color.DARKGREEN);
			APP.platform.createShapeEngine().createCircle(((InteractiveDriver)driver).overallPos.p, 0.2).paint(ctxt);
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.getAABB().draw(ctxt);
			
			paintID(ctxt);
		}
		
	}

}
