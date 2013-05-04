package com.gutabi.capsloc.world.cars;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.Entity;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.ShapeUtils;
import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.graph.Fixture;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.graph.Merger;
import com.gutabi.capsloc.world.sprites.CarSheet.CarType;

public class AutonomousCar extends Car {
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public Fixture source;
	
	public AutonomousCar(World w, CarType type, Fixture f) {
		super(w, type);
		
		this.source = f;
	}
	
	public static AutonomousCar createCar(World world, CarType type, Fixture f, int r) {
		
		AutonomousCar c = new AutonomousCar(world, type, f);
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
	
	public boolean preStep(double t) {
		
		switch (state) {
		case DRIVING: 
		case BRAKING:
			((AutonomousDriver)driver).preStep(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
			break;
		default:
			assert false;
			break;
		}
		
		engine.preStep(t);
		
		return false;
	}
	
	/**
	 * return true if car should persist after time step
	 */
	public boolean postStep(double t) {
		
		switch (state) {
		case DRIVING: {
			
			computeDynamicPropertiesAlways();
			computeDynamicPropertiesMovingAndMore(t);
			
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
				
//				world.roadMarkMap.addRoadMark(prevWorldPoint0, shape.p0);
//				world.roadMarkMap.addRoadMark(prevWorldPoint3, shape.p3);
				
				computeDynamicPropertiesMovingAndMore(t);
				
			} else {
				
			}
			
			return true;
		case CRASHED:
			
			computeDynamicPropertiesAlways();
			
			if (((AutonomousDriver)driver).stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = t;
					
				}
				
				world.quadrantMap.grassMap.mowGrass(shape);
				
				computeDynamicPropertiesMovingAndMore(t);
				
			} else {
				
				if (!DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = -1;
					((AutonomousDriver)driver).deadlocked = false;
					
					computeDynamicPropertiesMovingAndMore(t);
					
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
//					world.grassMarkMap.addGrassMark(prevWorldPoint0, shape.p0);
//					world.grassMarkMap.addGrassMark(prevWorldPoint3, shape.p3);
				}
				
				computeDynamicPropertiesMovingAndMore(t);
				
			} else {
				
				if (!DMath.equals(vel.lengthSquared(), 0.0)) {
					
					((AutonomousDriver)driver).stoppedTime = -1;
					((AutonomousDriver)driver).deadlocked = false;
					
					computeDynamicPropertiesMovingAndMore(t);
					
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
	
	public void computeDynamicPropertiesMovingAndMore(double t) {
		
		computeDynamicPropertiesMoving();
		
		switch (state) {
		case DRIVING:
		case BRAKING:
			
			((AutonomousDriver)driver).postStep(t);
			
			Entity hit = ((AutonomousDriver)driver).overallPath.pureGraphIntersectOBB(this.shape, ((AutonomousDriver)driver).overallPos);
			
			boolean wasInMerger = inMerger;
			if (hit == null) {
				atleastPartiallyOnRoad = false;
				inMerger = false;
			} else {
				atleastPartiallyOnRoad = true;
				if (hit instanceof Merger && ShapeUtils.containsAO(((Merger)hit).getShape(), shape)) {
					inMerger = true;
				} else {
					inMerger = false;
				}
			}
			
			if (!atleastPartiallyOnRoad) {
				skid();
			}
			
			if (inMerger == !wasInMerger) {
				if (inMerger) {
					setB2dCollisions(false);
				} else {
					setB2dCollisions(true);
				}
			}
			
			break;
		case SINKED:
		case SKIDDED:
		case CRASHED:
			break;
		default:
			assert false;
			break;
		}
		
	}
	
	public void skid() {
		state = CarStateEnum.SKIDDED;
		((AutonomousDriver)driver).clear();
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
				ctxt.paintCircle(new Circle(((AutonomousDriver)driver).overallPos.p, 0.2));
			}
			
			if (((AutonomousDriver)driver).goalPoint != null) {
				ctxt.setColor(Color.GREEN);
				ctxt.paintCircle(new Circle(((AutonomousDriver)driver).goalPoint, 0.2));
			}
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.aabb.draw(ctxt);
			
			paintID(ctxt);
		}
		
	}
	
}
