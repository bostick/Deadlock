package com.gutabi.bypass.level;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.physics.PhysicsUtils;
import com.gutabi.deadlock.world.sprites.CarSheet.CarType;

public class BypassCar extends Car {
	
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
	
	public BypassCar(World w, CarType type) {
		super(w, type);
	}
	
	public static BypassCar createCar(World world, CarType type, int r) {
		
		BypassCar c = new BypassCar(world, type);
		c.driver = new BypassDriver(c);
		
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
		
		double d;
		if (dragVector == null) {
			d = 0;
		} else {
			d = Math.max(Point.dot(dragVector, (pathForward ? driver.overallPos.pathVector() : driver.overallPos.pathVector().negate())) / driver.overallPos.pathVector().length(), 0.0);
		}
		
		double vel;
		if (dragTimeMillis != 0.0) {
			vel = 0.1 * d / (0.0001 * dragTimeMillis);
		} else {
			vel = 0.0;
		}
		
		assert vel >= 0.0;
		
		if (vel > 15) {
			vel = 15;
		}
		
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
		
//		System.out.println(newPos.gp);
		
		if (DMath.equals(newPos.combo, driver.toolCoastingGoal.combo)) {
			
			coastingVel = 0;
			
			newPos = driver.toolCoastingGoal;
			state = CarStateEnum.IDLE;
			
			b2dBody.setTransform(PhysicsUtils.vec2(newPos.p), (float)(state == CarStateEnum.COASTING_FORWARD ? newPos.angle : newPos.angle));
			
			computeDynamicPropertiesAlways();
			computeDynamicPropertiesMoving();
			
			driver.setOverallPos(driver.toolCoastingGoal);
			if (!driver.toolCoastingGoal.isEndOfPath()) {
				((BypassWorld)world).handleZooming(this);
			}
			
			driver.toolOrigExitingVertexPos = null;
			driver.toolCoastingGoal = null;
			driver.prevOverallPos = null;
//			((InteractiveCarTool)APP.tool).car = null;
			
			return;
		}
		
		b2dBody.setTransform(PhysicsUtils.vec2(newPos.p), (float)(state == CarStateEnum.COASTING_FORWARD ? newPos.angle : newPos.angle));
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
				((BypassDriver)driver).postStep(t);
				
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
			APP.platform.createCircle(((BypassDriver)driver).overallPos.p, 0.2).paint(ctxt);
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.getAABB().draw(ctxt);
			
			paintID(ctxt);
		}
		
	}

}
