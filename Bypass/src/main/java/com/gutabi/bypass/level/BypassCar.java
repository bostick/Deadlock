package com.gutabi.bypass.level;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.Integratable;
import com.gutabi.capsloc.geom.Geom;
import com.gutabi.capsloc.geom.OBB;
import com.gutabi.capsloc.geom.ShapeUtils;
import com.gutabi.capsloc.geom.SweptOBB;
import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.cars.Car;
import com.gutabi.capsloc.world.cars.CarStateEnum;
import com.gutabi.capsloc.world.graph.BypassStud;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.sprites.CarSheet.CarType;

public class BypassCar extends Car {
	
	public char boardLetter;
	
	/*
	 * bypass the regular physics here.
	 * we just need the car to be constrained to the road, and accelerating
	 * 
	 * if the physics engine can ever nicely constraining motion to a track, then use it
	 * 
	 * doesn't need to be a vector, since it is snapped to a track
	 */
	public double coastingVel;
	final double coastingAcceleration = 0.5;
	
	public BypassCar(World w, CarType type) {
		super(w, type);
	}
	
	public static BypassCar createCar(World world, CarType type, int sheetIndex, char boardLetter) {
		
		BypassCar c = new BypassCar(world, type);
		c.driver = new BypassDriver(c);
		
		c.computeCtorProperties(sheetIndex);
		
		c.boardLetter = boardLetter;
		c.state = CarStateEnum.IDLE;
		
		return c;
	}
	
	public boolean preStep(double t) {
		
		switch (state) {
		case IDLE:
			return false;
		case DRAGGING:
			/*
			 * no goal or anything for dragging
			 */
			return true;
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			fakeCoastingStep(t);
			return true;
		default:
			assert false;
			return false;
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
		
		if (vel > 4) {
			vel = 4;
		}
		
		coastingVel = vel;
	}
	
	public void clearCoastingVel() {
		coastingVel = 0.0;
	}
	
	public void fakeCoastingStep(double t) {
		assert state == CarStateEnum.COASTING_FORWARD || state == CarStateEnum.COASTING_BACKWARD;
		
		double dv = coastingAcceleration * Integratable.DT;
		
		coastingVel += dv;
		
		double dist = coastingVel * Integratable.DT;
		
		GraphPositionPathPosition newPos;
		if (state == CarStateEnum.COASTING_FORWARD) {
			newPos = driver.overallPos.travelForward(Math.min(dist, driver.overallPos.lengthTo(driver.toolCoastingGoal)));
		} else {
			newPos = driver.overallPos.travelBackward(Math.min(dist, driver.overallPos.lengthTo(driver.toolCoastingGoal)));
		}
		
		if (DMath.equals(newPos.combo, driver.toolCoastingGoal.combo)) {
			
			coastingVel = 0;
			
			newPos = driver.toolCoastingGoal;
			state = CarStateEnum.IDLE;
			
			setTransform(newPos.p, (state == CarStateEnum.COASTING_FORWARD ? newPos.angle : newPos.angle));
			setPhysicsTransform();
			
			computeDynamicPropertiesAlways();
			computeDynamicPropertiesMoving();
			
			driver.setOverallPos(driver.toolCoastingGoal);
			if (!driver.toolCoastingGoal.isEndOfPath()) {
				((BypassWorld)world).handleZooming(this);
			}
			
			driver.toolOrigExitingVertexPos = null;
			driver.toolLastExitingVertexPos = null;
			driver.toolCoastingGoal = null;
			driver.prevOverallPos = null;
			
			return;
		} else {
			
			if (driver.overallPos.angle == newPos.angle) {
				OBB so = Geom.localToWorld(localAABB, driver.overallPos.angle, driver.overallPos.p);
				OBB eo = Geom.localToWorld(localAABB, newPos.angle, newPos.p);
				SweptOBB swept = new SweptOBB(so, eo);
				
				if (swept.isAABB) {
					double param = GraphPositionPathPosition.firstCollisionParam(this, swept);
					if (param != -1) {
						
						int studCount = (int)(length * Car.METERS_PER_CARLENGTH / BypassStud.SIZE);
						
						if (state == CarStateEnum.COASTING_FORWARD) {
							state = CarStateEnum.COASTING_BACKWARD;
							driver.toolCoastingGoal = driver.toolOrigExitingVertexPos.travelBackward(BypassStud.SIZE + 0.5 * studCount);
						} else {
							state = CarStateEnum.COASTING_FORWARD;
							driver.toolCoastingGoal = driver.toolOrigExitingVertexPos.travelForward(BypassStud.SIZE + 0.5 * studCount);
						}
						
						coastingVel = 0.0;
						/*
						 * doing setTransform(Point.point(driver.overallPos.p, newPos.p, param), newPos.angle);
						 * would be more correct here, but the resolving of overallPos from p in postStep starts searching from overallPos,
						 * so the correct point of impact would be in front of overallPos when doing a backward search, and
						 * in back of overallPos when doing a forward search, so the correct pos would never be found.
						 * 
						 * so we'd have to also set overallPos here (which is confusing), or have another state BOUNCING or something
						 * to indicate in the transition between COASTING FORWARD and BACKWARD, which seems overkill
						 * 
						 * no one is going to miss one time step
						 */
						return;
					}
				}
			}
			
		}
		
		setTransform(newPos.p, newPos.angle);
		setPhysicsTransform();
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
		
		if (!ShapeUtils.intersectAA(ctxt.cam.worldViewport, shape.aabb)) {
			return;
		}
		
		if (APP.CARTEXTURE_DRAW) {
			paintImage(ctxt);
		} else {
			ctxt.setColor(Color.BLUE);
			paintRect(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.aabb.draw(ctxt);
			
			paintID(ctxt);
		}
		
	}

}
