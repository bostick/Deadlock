package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Matrix;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Geom;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.model.event.CarProximityEvent;
import com.gutabi.deadlock.model.event.DrivingEvent;
import com.gutabi.deadlock.model.event.VertexArrivalEvent;
import com.gutabi.deadlock.model.fixture.WorldSink;
import com.gutabi.deadlock.model.fixture.WorldSource;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	public static final double COMPLETE_STOP_WAIT_TIME = 1.0;
	
	public abstract double getMaxSpeed();
	
//	double steeringLookaheadDistance = CAR_LENGTH * 1.5;
//	double eventLookaheadDistance = getMaxSpeed() * 0.3;
//	/*
//	 * turning radius
//	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
//	 */
//	private double maxRadsPerMeter = 0.1;
//	private double cancelingForwardImpulseCoefficient = 0.01f;
//	private double cancelingLateralImpulseCoefficient = 0.05f;
//	private double cancelingAngularImpulseCoefficient = 0.1f;
//	
//	private double forwardImpulseCoefficient = 1.0f;
//	private double brakeImpulseCoefficient = 0.1f;
//	private double maxAcceleration = 0.1f;
	
	
	double steeringLookaheadDistance = CAR_LENGTH * 0.5;
//	double eventLookaheadDistance = CAR_LENGTH * 0.5;
	
	/*
	 * distance from center of this car to center of other car
	 */
	double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.2;
	double vertexArrivalLookahead = CAR_LENGTH * 0.5;
	
	/*
	 * turning radius
	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
	 */
	private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
	private double maxAcceleration = Double.POSITIVE_INFINITY;
	private double maxDeceleration = 0.0;
	
	private double frictionForwardImpulseCoefficient = 0.0f;
	private double frictionLateralImpulseCoefficient = 0.0f;
	private double frictionAngularImpulseCoefficient = 0.0f;
	
	private double driveForwardImpulseCoefficient = 1.0f;
	private double driveLateralImpulseCoefficient = 1.0f;
	
	private double brakeForwardImpulseCoefficient = 1.0f;
	private double brakeLateralImpulseCoefficient = 1.0f;
	
	private double turnAngularImpulseCoefficient = 1.0f;
	
	
	
	
	
	
	
	protected int sheetRow;
	protected int sheetCol;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public  WorldSource source;
	
	protected GraphPositionPath overallPath;
	
	public int id;
	
	public static int carCounter;
	
	Point startPoint;
	double startHeading;
	float mass;
	float momentOfInertia;
	
	Quad localQuad;
	protected Body b2dBody;
	protected PolygonShape b2dShape;
	protected Fixture b2dFixture;
	protected boolean b2dInited;
	
	/*
	 * dynamic properties
	 */
	Point p;
	Vec2 pVec2;
	Vec2 currentRightNormal;
	Vec2 currentUpNormal;
	Vec2 vel;
	Vec2 forwardVel;
	float angle;
	float angularVel;
	Matrix carTrans;
	float carAngle;
	boolean atleastPartiallyOnRoad;
	boolean inMerger;
	GraphPositionPathPosition overallPos;
	Quad worldQuad;
	Point prevWorldPoint0;
	Point prevWorldPoint3;
	
	/*
	 * 
	 */
//	DrivingEvent curDrivingEvent;
	List<DrivingEvent> drivingEvents = new ArrayList<DrivingEvent>();
	List<VertexArrivalEvent> vertexDepartureQueue = new ArrayList<VertexArrivalEvent>();
//	GraphPositionPathPosition curVertexPosition;
//	GraphPositionPathPosition curBorderMatchingPosition;
//	List<VertexArrivalEvent> oldVertexArrivalEvents = new ArrayList<VertexArrivalEvent>();
	double decelTime = -1;
//	double accelTime = -1;
	double stoppedTime = -1;
	Point goalPoint;
	
	protected Color color;
	protected Color hiliteColor;
	
	static Logger logger = Logger.getLogger(Car.class);
	static Logger pathingLogger = Logger.getLogger("com.gutabi.deadlock.model.Car.pathing");
			
	public Car(WorldSource s) {
		
		state = CarStateEnum.DRIVING;
		
		source = s;
		
		color = Color.BLUE;
		hiliteColor = Color.BLUE;
		
		Point p0 = new Point(-CAR_LENGTH / 2, CAR_LENGTH / 4);
		Point p1 = new Point(CAR_LENGTH / 2, CAR_LENGTH / 4);
		Point p2 = new Point(CAR_LENGTH / 2, -CAR_LENGTH / 4);
		Point p3 = new Point(-CAR_LENGTH / 2, -CAR_LENGTH / 4);
		localQuad = new Quad(this, p0, p1, p2, p3);
		
	}
	
	public String toString() {
		return Integer.toString(id);
	}
	
	protected void computeStartingProperties() {
		
		logger.debug("spawn");
		
		overallPos = new GraphPositionPathPosition(overallPath, 0, 0.0);
		GraphPosition closestGraphPos = overallPos.gpos;
		startPoint = closestGraphPos.p;
		
		if (pathingLogger.isDebugEnabled()) {
			pathingLogger.debug("overall path:");
			for (GraphPosition gp : overallPath.poss) {
				pathingLogger.debug(gp);
			}
			pathingLogger.debug("");
			
			pathingLogger.debug("overall path bounds:");
			GraphPositionPathPosition cur = overallPos;
			while (true) {
				pathingLogger.debug(cur);
				if (cur.isEndOfPath()) {
					break;
				}
				cur = cur.nextBound();
			}
			pathingLogger.debug("");
		}
		
		GraphPositionPathPosition next = overallPos.travel(getMaxSpeed() * MODEL.dt);
		
		Point nextDTGoalPoint = next.gpos.p;
		
		Point dp = new Point(nextDTGoalPoint.x-startPoint.x, nextDTGoalPoint.y-startPoint.y);
		
		startHeading = Math.atan2(dp.y, dp.x);
		
		b2dInit();
		
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		Mat22 r = b2dBody.getTransform().R;
		carTrans = new Matrix(r.col1.x, r.col2.x, r.col1.y, r.col2.y);
		
		worldQuad = Geom.localToWorld(localQuad, carTrans, p);
		shape = worldQuad;
		
		
		Vec2 v = dp.vec2();
		v.normalize();
		v.mulLocal((float)getMaxSpeed());
		
		b2dBody.setLinearVelocity(v);
		
		computeDynamicProperties();
	}
	
	private void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)startPoint.x, (float)startPoint.y);
		bodyDef.angle = (float)startHeading;
		bodyDef.allowSleep = true;
		bodyDef.awake = true;
		
		b2dBody = MODEL.world.b2dWorld.createBody(bodyDef);
		b2dBody.setUserData(this);
		
		b2dShape = new PolygonShape();
		/*
		 * do not know exactly what magic I have to call in order to get intertia setup properly when using set
		 * 
		 * so stick with setAsBox for now
		 */
//		b2dShape.set(new Vec2[] {
//				new Vec2((float)p1.getX(), (float)p1.getY()),
//				new Vec2((float)p2.getX(), (float)p2.getY()),
//				new Vec2((float)p3.getX(), (float)p3.getY()),
//				new Vec2((float)p4.getX(), (float)p4.getY())}, 4);
		((PolygonShape)b2dShape).setAsBox((float)(CAR_LENGTH / 2), (float)(CAR_LENGTH / 4));
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = b2dShape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.8f;
		b2dFixture = b2dBody.createFixture(fixtureDef);
		
		mass = b2dBody.getMass();
		momentOfInertia = b2dBody.getInertia();
		
	}
	
	
	Vec2 right = new Vec2(1, 0);
	Vec2 up = new Vec2(0, 1);
	Filter normalCarFilter = new Filter();
	Filter mergingCarFilter = new Filter();
	{
		normalCarFilter.categoryBits = 1;
		normalCarFilter.maskBits = 1;
		mergingCarFilter.categoryBits = 2;
		mergingCarFilter.maskBits = 0;
	}
	
	private void computeDynamicProperties() {
		
		prevWorldPoint0 = worldQuad.p0;
		prevWorldPoint3 = worldQuad.p3;
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		vel = b2dBody.getLinearVelocity();
		angle = b2dBody.getAngle();
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		
		if (logger.isDebugEnabled()) {
			logger.debug("vel: " + forwardVel);
		}
		
		Mat22 r = b2dBody.getTransform().R;
		carTrans = new Matrix(r.col1.x, r.col2.x, r.col1.y, r.col2.y);
		carAngle = b2dBody.getAngle();
		
		worldQuad = Geom.localToWorld(localQuad, carTrans, p);
		shape = worldQuad;
		
		Entity e = MODEL.world.graphBestHitTest(shape);
		
		boolean wasInMerger = inMerger;
		if (e == null) {
			atleastPartiallyOnRoad = false;
			inMerger = false;
		} else {
			atleastPartiallyOnRoad = true;
			if (e instanceof Merger && ((Quad)shape).containedIn((Quad)e.shape)) {
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
				b2dFixture.setFilterData(mergingCarFilter);
			} else {
				b2dFixture.setFilterData(normalCarFilter);
			}
		}
		
	}
	
	
	public void destroy() {
		b2dCleanup();
	}
	
	private void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
	}
	
	
	public boolean isDeleteable() {
		return true;
	}
	
	public void crash() {
		
		state = CarStateEnum.CRASHED;
		
		overallPos = null;
		goalPoint = null;
		
//		source.outstandingCars--;
		
		if (curDrivingEvent == null) {
			
		} else if (curDrivingEvent instanceof VertexArrivalEvent) {
			
			if (((VertexArrivalEvent)curDrivingEvent).sign != null) {
				stoppedTime = -1;
				decelTime = -1;
//				accelTime = -1;
			}
			
			((VertexArrivalEvent)curDrivingEvent).v.carQueue.remove(this);
			
			curDrivingEvent = null;
			
		} else if (curDrivingEvent instanceof CarProximityEvent) {
			
			stoppedTime = -1;
			decelTime = -1;
//			accelTime = -1;
			
			curDrivingEvent = null;
			
		} else {
			assert false;
		}
		
	}
	
	private void skid() {
		
		state = CarStateEnum.SKIDDED;
		
		overallPos = null;
		goalPoint = null;
		
//		source.outstandingCars--;
		
		if (curDrivingEvent == null) {
			
		} else if (curDrivingEvent instanceof VertexArrivalEvent) {
			
			if (((VertexArrivalEvent)curDrivingEvent).sign != null) {
				stoppedTime = -1;
				decelTime = -1;
//				accelTime = -1;
			}
			
			((VertexArrivalEvent)curDrivingEvent).v.carQueue.remove(this);
			
			curDrivingEvent = null;
			
		} else if (curDrivingEvent instanceof CarProximityEvent) {
			
			stoppedTime = -1;
			decelTime = -1;
//			accelTime = -1;
			
			curDrivingEvent = null;
			
		} else {
			assert false;
		}
		
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	public void preStep(double t) {
		
		if (state == CarStateEnum.CRASHED || state == CarStateEnum.SKIDDED || state == CarStateEnum.SINKED) {
			
		} else {
			
			/*
			 * calculate current overall position on graph
			 */
			GraphPositionPathPosition max = overallPos.travel(Math.min(2 * CAR_LENGTH, overallPos.lengthToEndOfPath));
			overallPos = overallPath.findClosestGraphPositionPathPosition(p, overallPos, max);
			
			if (pathingLogger.isDebugEnabled()) {
				pathingLogger.debug("overallPos: " + overallPos.gpos);
			}
			
//			pathingLogger.debug("next bound after overallPos: " + overallPos.nextBound().gpos);
			
			DrivingEvent newDrivingEvent = null;
			Car carProx = MODEL.world.carProximityTest(this, overallPos, Math.min(carProximityLookahead, overallPos.lengthToEndOfPath));
			if (carProx != null) {
				newDrivingEvent = new CarProximityEvent(this, carProx, overallPos.travel(CAR_LENGTH / 2));
			} else {
				VertexArrivalEvent vertexArr = overallPath.vertexArrivalTest(overallPos, Math.min(vertexArrivalLookahead, overallPos.lengthToEndOfPath));
				/*
				 * since we deal with events before actually reaching them (front of car reaches them vs center of car where position is counted),
				 * simply keep a list of already processed events to use to filter
				 */
//				vertexArrivalEvents.removeAll(oldVertexArrivalEvents);
				if (vertexArr != null) {
//					logger.debug("setting curEvent: " + events + "     " + oldEvents);
					
					if (!vertexDepartureQueue.contains(vertexArr)) {
						newDrivingEvent = vertexArr;
						
//						oldVertexArrivalEvents.add(((VertexArrivalEvent)newDrivingEvent));
						
						if (!vertexArr.v.carQueue.contains(this)) {
							vertexArr.v.carQueue.add(this);
						}
					}
					
				}
			}
			
			if (newDrivingEvent != null) {
				if (curDrivingEvent == null) {
					curDrivingEvent = newDrivingEvent;
				} else {
					if (curDrivingEvent.equals(newDrivingEvent)) {
//						assert newDrivingEvent == null;
					} else {
//						assert newDrivingEvent == null;
					}
				}
			}
			
			if (curDrivingEvent != null) {
				
				if (curDrivingEvent instanceof CarProximityEvent) {
					
					if (decelTime == -1) {
						// start braking
						
						assert state == CarStateEnum.DRIVING;
						state = CarStateEnum.BRAKING;
						
					} else {
						
						Car otherCar = ((CarProximityEvent) curDrivingEvent).otherCar;
						
						if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME && (otherCar.overallPos == null || overallPos.distanceTo(otherCar.overallPos) > 1.2 * CAR_LENGTH)) {
							// start driving
							
							assert state == CarStateEnum.BRAKING;
							state = CarStateEnum.DRIVING;
							
							stoppedTime = -1;
							decelTime = -1;
//							accelTime = -1;
							
							curDrivingEvent = null;
							
						}
						
					}
					
				} else if (curDrivingEvent instanceof VertexArrivalEvent) {
					
					if (((VertexArrivalEvent)curDrivingEvent).sign != null) {
						if (decelTime == -1) {
							// start braking
							
							assert state == CarStateEnum.DRIVING;
							state = CarStateEnum.BRAKING;
							
						} else if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME && ((VertexArrivalEvent)curDrivingEvent).v.carQueue.get(0) == this) {
							// start driving
							
							assert state == CarStateEnum.BRAKING;
							state = CarStateEnum.DRIVING;
							
							stoppedTime = -1;
							decelTime = -1;
//							accelTime = -1;
							
//							assert vertexDepartureQueue.isEmpty();
							vertexDepartureQueue.add((VertexArrivalEvent)curDrivingEvent);
							
							curDrivingEvent = null;
							
						}
					}
					
				} else {
					assert false;
				}
				
			} else {
				assert state == CarStateEnum.DRIVING;
			}
			
			if (!vertexDepartureQueue.isEmpty()) {
				
				List<VertexArrivalEvent> toRemove = new ArrayList<VertexArrivalEvent>();
				for (VertexArrivalEvent e : vertexDepartureQueue) {
					if (e.borderMatchingPosition != null && overallPos.combo >= e.borderMatchingPosition.combo) {
						/*
						 * driving past exit of intersection, so cleanup
						 */
						
						e.v.carQueue.remove(this);
						
						toRemove.add(e);
						
					}
				}
				for (VertexArrivalEvent e : toRemove) {
					vertexDepartureQueue.remove(e);
				}
			}
			
		}
		
		updateFriction();
		
		switch (state) {
		case DRIVING:
			
			double lookaheadDistance = Math.min(steeringLookaheadDistance, overallPos.lengthToEndOfPath);
			
			GraphPositionPathPosition next = overallPos.travel(lookaheadDistance);
			
			if (pathingLogger.isDebugEnabled()) {
				pathingLogger.debug("goalPoint: " + next.gpos);
			}
			
			double nextDist = overallPos.distanceTo(next);
			assert DMath.equals(nextDist, lookaheadDistance);
			
			goalPoint = next.gpos.p;
//			assert DMath.greaterThan(Point.distance(goalPoint, p), CAR_LENGTH/4) : "heuristic failed";
			
			updateDrive(t);
			break;
		case BRAKING:
			goalPoint = curDrivingEvent.getGraphPositionPathPosition().gpos.p;
//			assert DMath.greaterThan(Point.distance(goalPoint, p), CAR_LENGTH/4) : "heuristic failed";
			
			updateBrake(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
			break;
		}
	}
	
	private void updateFriction() {
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		
		float cancelingForwardImpulse = (float)(frictionForwardImpulseCoefficient * Vec2.dot(currentRightNormal, cancelingImpulse));
		float cancelingLateralImpulse = (float)(frictionLateralImpulseCoefficient * Vec2.dot(currentUpNormal, cancelingImpulse));
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
		
		float cancelingAngImpulse = (float)(frictionAngularImpulseCoefficient * momentOfInertia * -angularVel);
		
		b2dBody.applyAngularImpulse(cancelingAngImpulse);
	}
	
	private void updateDrive(double t) {
		
//		if (stoppedTime != -1) {
//			
////			logger.debug("accel");
//			
////			accelTime = t;
//		}
		
		double goalForwardVel = (float)getMaxSpeed();
		
		double forwardSpeed = forwardVel.length();
		
		float dv;
		if (goalForwardVel > forwardSpeed) {
			dv = (float)(goalForwardVel - forwardSpeed);
		} else {
			dv = 0.0f;
		}
//		if (acc < 0) {
//			assert false;
//		}
		if (dv > maxAcceleration * MODEL.dt) {
			dv = (float)(maxAcceleration * MODEL.dt);
		}
		
//		logger.debug("acc for driving: " + acc);
		
		float forwardImpulse = (float)(driveForwardImpulseCoefficient * mass * dv);
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(forwardImpulse), b2dBody.getWorldCenter());
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		float driveLateralImpulse = (float)(driveLateralImpulseCoefficient * Vec2.dot(currentUpNormal, cancelingImpulse));
		
		b2dBody.applyLinearImpulse(currentUpNormal.mul(driveLateralImpulse), b2dBody.getWorldCenter());
		
		updateTurn();
		
	}
	
	private void updateBrake(double t) {
		
		double forwardSpeed = forwardVel.length();
		
		if (decelTime == -1) {
			
			logger.debug("decel");
			
			decelTime = t;
		}
		
		double goalForwardVel = 0.0;
		
		float dv = (float)(goalForwardVel - forwardSpeed);
		if (dv < maxDeceleration * MODEL.dt) {
			dv = (float)(maxDeceleration * MODEL.dt);
		}
		if (dv > 0) {
			assert false;
		}
		
		//logger.debug("acc for braking: " + -forwardVel.length());
		
		Vec2 cancelingVel = vel.mul(-1);
		
		float rightBrakeImpulse = (float)(brakeForwardImpulseCoefficient * Vec2.dot(cancelingVel, currentRightNormal) * mass);
		
		float upBrakeImpulse = (float)(brakeLateralImpulseCoefficient * Vec2.dot(cancelingVel, currentUpNormal) * mass);
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(rightBrakeImpulse), pVec2);
		b2dBody.applyLinearImpulse(currentUpNormal.mul(upBrakeImpulse), pVec2);
		
		updateTurn();
	}
	
	private void updateTurn() {
		
		Point dp = new Point(goalPoint.x-p.x, goalPoint.y-p.y);
		double forwardSpeed = forwardVel.length();
		
		double nextDTGoalAngle = Math.atan2(dp.y, dp.x);
		double dw = ((float)nextDTGoalAngle) - angle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		/*
		 * turning radius
		 */
		double actualDistance = forwardSpeed * MODEL.dt;
		double maxRads = maxRadsPerMeter * actualDistance;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < -maxRads) {
			dw = -maxRads;
		}
		
//		if (dw > 0.52) {
//			String.class.getName();
//		} else if (dw < -0.52) {
//			String.class.getName();
//		}
		
		float goalAngVel = (float)(dw / MODEL.dt);
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * momentOfInertia * (goalAngVel - angularVel));
		
		b2dBody.applyAngularImpulse(angImpulse);
		
	}
	
	/**
	 * return true if car should persist after time step
	 */
	@Override
	public boolean postStep(double t) {
		
		computeDynamicProperties();
		
		switch (state) {
		case DRIVING: {
			
			WorldSink s = (WorldSink)overallPath.end.e;
			boolean sinked = false;
			if (Point.distance(p, s.p) < MODEL.world.SINK_EPSILON) {
				sinked = true;
			}
			
			if (sinked) {
				
				logger.debug("sink");
				
				overallPos = null;
				goalPoint = null;
				
				s.matchingSource.outstandingCars--;
				s.carQueue.remove(this);
				state = CarStateEnum.SINKED;
				return false;
			}
			break;
		}
		case BRAKING:
			
			if (stoppedTime == -1) {
				
				if (DMath.equals(vel.length(), 0.0)) {
					
//					logger.debug("stopped: " + t);
					stoppedTime = t;
					
				}
				
//				MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
//				MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
				
			}
			
			break;
		case CRASHED:
			break;
		case SKIDDED:
			
//			MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
//			MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
			
			break;
		case SINKED:
			break;
		}
		
		return true;	
	}
	
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(Graphics2D g2) {
		
		paintImage(g2);
		
		if (MODEL.DEBUG_DRAW) {
			
			paintID(g2);
			
			if (goalPoint != null) {
				g2.setColor(Color.WHITE);
				g2.fillOval((int)(goalPoint.x * MODEL.PIXELS_PER_METER) - 2, (int)(goalPoint.y * MODEL.PIXELS_PER_METER) - 2, 4, 4);
			}
			
			paintAABB(g2);
			
		}
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(Graphics2D g2) {
		paintRect(g2);
	}
	
	
	static Composite aComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	static Composite normalComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	
	protected void paintImage(Graphics2D g2) {
		
		AffineTransform origTransform = g2.getTransform();
		
		Composite origComposite = g2.getComposite();
		
		g2.scale(MODEL.PIXELS_PER_METER, MODEL.PIXELS_PER_METER);
		
		g2.translate(p.x, p.y);
		g2.rotate(carAngle);
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		if (inMerger) {
			g2.setComposite(aComp);
		} else {
			g2.setComposite(normalComp);
		}
		
		g2.drawImage(VIEW.sheet,
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
				sheetCol, sheetRow, sheetCol+64, sheetRow+32,
				null);
		
//		g2.drawImage(MODEL.world.sheet,
//				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
//				(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
//				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
//				(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
//				sheetCol+64, sheetRow, sheetCol+64+64, sheetRow+32,
//				null);
//		
		if (state == CarStateEnum.BRAKING) {
			g2.drawImage(VIEW.sheet,
					(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
					(int)(-CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
					(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.5),
					(int)(CAR_LENGTH * MODEL.PIXELS_PER_METER * 0.25),
					sheetCol+64+64, sheetRow, sheetCol+64+64+64, sheetRow+32,
					null);
		}
		
		g2.setComposite(origComposite);
		g2.setTransform(origTransform);
		
	}
	
	private void paintRect(Graphics2D g2) {
		
		g2.scale(MODEL.METERS_PER_PIXEL, MODEL.METERS_PER_PIXEL);
		
		g2.setColor(hiliteColor);
		
		worldQuad.paint(g2);
	}
	
	public void paintID(Graphics2D g2) {
		
		g2.setColor(Color.WHITE);
		
		Point worldPoint = p.minus(new Point(CAR_LENGTH/2, 0));
		Point panelPoint = worldPoint.multiply(MODEL.PIXELS_PER_METER);
		
		AffineTransform origTransform = g2.getTransform();
		
		g2.translate((int)(panelPoint.x), (int)(panelPoint.y));
		g2.scale(2.0, 2.0);
		
		g2.drawString(Integer.toString(id), 0, 0);
		
		g2.setTransform(origTransform);
		
//		g2.drawString(Integer.toString(carQueue.size()), (int)(panelPoint.x + 10), (int)(panelPoint.y));
	}

}
