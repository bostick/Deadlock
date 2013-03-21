package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;

public class AutonomousCar extends Car {
	
	public Fixture source;
	
	public AutonomousCar(World w, Fixture f) {
		super(w);
		
		this.source = f;
	}
	
	public static AutonomousCar createCar(World world, Fixture f, int r) {
		
		AutonomousCar c = new AutonomousCar(world, f);
		c.driver = new AutonomousDriver(c);
		c.engine = new AutonomousEngine(world, c);
		
		c.computeCtorProperties(r);
		
		return c;
	}
	
	public void computeStartingProperties() {
		
		((AutonomousDriver)driver).computeStartingProperties();
		
		center = ((AutonomousDriver)driver).overallPos.p;
		
		/*
		 * if angle was not initialized, do it now
		 */
		if (Double.isNaN(angle)) {
			
			GraphPositionPathPosition next = ((AutonomousDriver)driver).overallPos.nextBound();
			
			Point nextDTGoalPoint = next.p;
			
			Point dp = new Point(nextDTGoalPoint.x-center.x, nextDTGoalPoint.y-center.y);
			
			angle = Math.atan2(dp.y, dp.x);
			
		}
		
		setTransform(center, angle);
	}
	
	public void crash() {
		state = CarStateEnum.CRASHED;
		((AutonomousDriver)driver).clear();
	}
	
	public void preStep(double t) {
		
		switch (state) {
		case DRIVING: 
		case BRAKING:
			((AutonomousDriver)driver).preStep(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
			break;
		case IDLE:
		case DRAGGING:
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			break;
		}
		
		engine.preStep(t);
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep(double t) {
		
		switch (state) {
		case DRIVING: {
			
			computeDynamicPropertiesAlways();
			computeDynamicPropertiesMoving();
			
			Fixture s = (Fixture)((AutonomousDriver)driver).overallPath.end.entity;
			boolean sinked = false;
			if (Point.distance(center, s.p) < SINK_EPSILON) {
				sinked = true;
			}
			
			if (sinked) {
				
				((AutonomousDriver)driver).clear();
				
				s.match.outstandingCars--;
				state = CarStateEnum.SINKED;
				
				return false;
			}
			return true;
		}
		case BRAKING:
			
			computeDynamicPropertiesAlways();
			
			if (((AutonomousDriver)driver).stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = t;
					
				}
				
				world.roadMarkMap.addRoadMark(prevWorldPoint0, shape.p0);
				world.roadMarkMap.addRoadMark(prevWorldPoint3, shape.p3);
				
				computeDynamicPropertiesMoving();
				
			} else {
				
				//computeDynamicPropertiesMoving();
				
			}
			
			return true;
		case CRASHED:
			
			computeDynamicPropertiesAlways();
			
			if (((AutonomousDriver)driver).stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = t;
					
				}
				
				world.quadrantMap.grassMap.mowGrass(shape);
				
				computeDynamicPropertiesMoving();
				
			} else {
				
				if (!DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = -1;
					((AutonomousDriver)driver).deadlocked = false;
					
					computeDynamicPropertiesMoving();
					
				}
				
			}
			
			if (!world.quadrantMap.contains(shape)) {
				return false;
			}
			
			return true;
		case SKIDDED:
			
			computeDynamicPropertiesAlways();
			
			if (((AutonomousDriver)driver).stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = t;
					
				}
				
				world.quadrantMap.grassMap.mowGrass(shape);
				
				synchronized (APP) {
					world.grassMarkMap.addGrassMark(prevWorldPoint0, shape.p0);
					world.grassMarkMap.addGrassMark(prevWorldPoint3, shape.p3);
				}
				
				computeDynamicPropertiesMoving();
				
			} else {
				
				if (!DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = -1;
					((AutonomousDriver)driver).deadlocked = false;
					
					computeDynamicPropertiesMoving();
					
				}
				
			}
			
			if (!world.quadrantMap.contains(shape)) {
				return false;
			}
			
			return true;
		case SINKED:
			
			assert false;
			
			return true;
			
		case IDLE:
		case DRAGGING:
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			return true;
		}
		
		return true;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (APP.CARTEXTURE_DRAW) {
			paintImage(ctxt);
		} else {
			if (!((AutonomousDriver)driver).deadlocked) {
				switch (state) {
				case BRAKING:
					ctxt.setColor(Color.BLUE);
					break;
				case CRASHED:
					ctxt.setColor(Color.ORANGE);
					break;
				case DRIVING:
					ctxt.setColor(Color.BLUE);
					break;
				case SINKED:
					ctxt.setColor(Color.BLUE);
					break;
				case SKIDDED:
					ctxt.setColor(Color.GREEN);
					break;
				case IDLE:
				case DRAGGING:
				case COASTING_FORWARD:
				case COASTING_BACKWARD:
					ctxt.setColor(Color.BLUE);
					break;
				}
			} else {
				switch (state) {
				case BRAKING:
					ctxt.setColor(Color.RED);
					break;
				case CRASHED:
					ctxt.setColor(Color.redOrange);
					break;
				case DRIVING:
					ctxt.setColor(Color.RED);
					break;
				case SINKED:
					ctxt.setColor(Color.RED);
					break;
				case SKIDDED:
					ctxt.setColor(Color.RED);
					break;
				case IDLE:
				case DRAGGING:
				case COASTING_FORWARD:
				case COASTING_BACKWARD:
					ctxt.setColor(Color.RED);
					break;
				}
			}
			paintRect(ctxt);
		}
		
		if (state == CarStateEnum.BRAKING) {
			paintBrakes(ctxt);
		}
		
		if (APP.DEBUG_DRAW) {
			
			if (((AutonomousDriver)driver).overallPos != null) {
				ctxt.setColor(Color.DARKGREEN);
				APP.platform.createShapeEngine().createCircle(((AutonomousDriver)driver).overallPos.p, 0.2).paint(ctxt);
			}
			
			if (((AutonomousDriver)driver).goalPoint != null) {
				ctxt.setColor(Color.GREEN);
				APP.platform.createShapeEngine().createCircle(((AutonomousDriver)driver).goalPoint, 0.2).paint(ctxt);
			}
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.getAABB().draw(ctxt);
			
			paintID(ctxt);
		}
		
	}
	
}
