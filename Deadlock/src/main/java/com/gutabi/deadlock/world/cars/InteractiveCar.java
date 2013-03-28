package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.physics.PhysicsUtils;
import com.gutabi.deadlock.world.tools.InteractiveCarTool;

public class InteractiveCar extends Car {
	
	/*
	 * bypass the regular physics here.
	 * we just need the car to be constrained to the road, and accelerating
	 * 
	 * if the physics engine can ever nicely constraining motion to a track, then use it
	 * 
	 * doesn't need to be a vector, since it is snapped to a track
	 */
	private double coastingVel;
	final double coastingAcceleration = 1.0;
	
	public InteractiveCar(World w) {
		super(w);
	}
	
	public static InteractiveCar createCar(World world, int r) {
		
		InteractiveCar c = new InteractiveCar(world);
		c.driver = new InteractiveDriver(c);
		
		c.computeCtorProperties(r);
		
		return c;
	}
	
	public void preStep(double t) {
		
		switch (state) {
		case IDLE:
		case DRAGGING:
			/*
			 * no goal or anything for dragging
			 */
			break;
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			fakeCoastingStep(t);
			break;
		default:
			assert false;
			break;
		}
		
	}
	
	public void setCoastingVelFromDrag(Point dragVector, long dragTimeMillis, boolean pathForward) {
		
		double vel = 0.1 * Math.max(Point.dot(dragVector, (pathForward ? driver.overallPos.pathVector() : driver.overallPos.pathVector().negate())), 0.0) / driver.overallPos.pathVector().length() / (0.0001 * dragTimeMillis);
		
		assert vel >= 0.0;
		coastingVel = vel;
	}
	
	public void clearCoastingVel() {
		coastingVel = 0.0;
	}
	
	public void fakeCoastingStep(double t) {
		assert state == CarStateEnum.COASTING_FORWARD || state == CarStateEnum.COASTING_BACKWARD;
		
		double dv = coastingAcceleration * world.DT;
		
		coastingVel += dv;
		
		double dist = coastingVel * world.DT;
		
		GraphPositionPathPosition newPos;
		if (state == CarStateEnum.COASTING_FORWARD) {
			newPos = driver.overallPos.travelForward(Math.min(dist, driver.overallPos.lengthTo(driver.toolCoastingGoal)));
		} else {
			newPos = driver.overallPos.travelBackward(Math.min(dist, driver.overallPos.lengthTo(driver.toolCoastingGoal)));
		}
		
		((InteractiveCarTool)APP.tool).handleZooming();
		
		if (DMath.equals(newPos.combo, driver.toolCoastingGoal.combo)) {
			
			coastingVel = 0;
			
			newPos = driver.toolCoastingGoal;
			state = CarStateEnum.IDLE;
			
			b2dBody.setTransform(PhysicsUtils.vec2(newPos.p), (float)(state == CarStateEnum.COASTING_FORWARD ? newPos.angle() : newPos.angle()));
			
			computeDynamicPropertiesAlways();
			computeDynamicPropertiesMoving();
			
			driver.setOverallPos(driver.toolCoastingGoal);
			
			driver.toolOrigExitingVertexPos = null;
			driver.toolCoastingGoal = null;
			
			return;
		}
		
		b2dBody.setTransform(PhysicsUtils.vec2(newPos.p), (float)(state == CarStateEnum.COASTING_FORWARD ? newPos.angle() : newPos.angle()));
	}
	
	public boolean postStep(double t) {
			
			switch (state) {
			case IDLE:
				return true;
			case DRAGGING:
				
				/*
				 * setting of overallPos and physics is handled in CarTool
				 */
				
				return true;
			case COASTING_FORWARD:
			case COASTING_BACKWARD:
				
				computeDynamicPropertiesAlways();
				computeDynamicPropertiesMoving();
				((InteractiveDriver)driver).postStep(t);
				
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
