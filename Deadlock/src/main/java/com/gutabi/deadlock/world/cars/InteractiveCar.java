package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class InteractiveCar extends Car {
	
	public InteractiveCar(World w) {
		super(w);
	}
	
	public static InteractiveCar createCar(World world, int r) {
		
		InteractiveCar c = new InteractiveCar(world);
		c.driver = new InteractiveDriver(c);
		c.engine = new InteractiveEngine(world, c);
		
		c.computeCtorProperties(r);
		
		return c;
	}
	
	public void preStep(double t) {
		
		switch (state) {
		case IDLE:
		case DRAGGING:
			break;
		case COASTING: 
			((InteractiveDriver)driver).preStep(t);
			break;
		default:
			assert false;
			break;
		}
		
		engine.preStep(t);
		
	}
	
	public boolean postStep(double t) {
			
			switch (state) {
			case IDLE:
			case DRAGGING:
				return true;
			case COASTING:
				
				computeDynamicPropertiesAlways();
				computeDynamicPropertiesMoving();
				
				return true;
				
			default:
				assert false;
				return true;
			}
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
