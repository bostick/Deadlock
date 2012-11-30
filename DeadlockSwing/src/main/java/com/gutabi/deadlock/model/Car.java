package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
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
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Geom;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.model.event.CarProximityEvent;
import com.gutabi.deadlock.model.event.DrivingEvent;
import com.gutabi.deadlock.model.event.VertexArrivalEvent;
import com.gutabi.deadlock.model.event.VertexEvent;
import com.gutabi.deadlock.model.event.VertexSpawnEvent;
import com.gutabi.deadlock.model.fixture.WorldSink;
import com.gutabi.deadlock.model.fixture.WorldSource;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	public static final double CAR_WIDTH = 0.5;
	
	public static final double BRAKE_SIZE = 0.25;
	
	public static final double COMPLETE_STOP_WAIT_TIME = 0.0;
	
	public abstract double getMaxSpeed();
	
	
	
//	double steeringLookaheadDistance = CAR_LENGTH * 1.5;
//	double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.4;
//	double vertexArrivalLookahead = CAR_LENGTH * 0.5 + CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.4;
//	/*
//	 * turning radius
//	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
//	 */
//	private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
//	private double maxAcceleration = getMaxSpeed() / 3.0;
//	private double maxDeceleration = -getMaxSpeed() / 3.0;
//	private double frictionForwardImpulseCoefficient = 0.0;
//	private double frictionLateralImpulseCoefficient = 0.0;
//	private double frictionAngularImpulseCoefficient = 0.0;
//	private double driveForwardImpulseCoefficient = 1.0;
//	private double driveLateralImpulseCoefficient = 1.0;
//	private double brakeForwardImpulseCoefficient = 1.0;
//	private double brakeLateralImpulseCoefficient = 1.0;
//	private double turnAngularImpulseCoefficient = 1.0;
	
	
	
	
	double steeringLookaheadDistance = CAR_LENGTH * 0.5;
	double carProximityLookahead = 0.5 * CAR_LENGTH + 0.5 * CAR_LENGTH + getMaxSpeed() * MODEL.dt + 0.8 * CAR_LENGTH;
	double vertexArrivalLookahead = CAR_LENGTH * 0.5;
	/*
	 * turning radius
	 * 3 car lengths for 180 deg = 3 meters for 3.14 radians
	 */
	private double maxRadsPerMeter = Double.POSITIVE_INFINITY;
	private double maxAcceleration = Double.POSITIVE_INFINITY;
	private double maxDeceleration = Double.NEGATIVE_INFINITY;
	private double frictionForwardImpulseCoefficient = 0.0;
	private double frictionLateralImpulseCoefficient = 0.0;
	private double frictionAngularImpulseCoefficient = 0.0;
	private double driveForwardImpulseCoefficient = 1.0;
	private double driveLateralImpulseCoefficient = 1.0;
	private double brakeForwardImpulseCoefficient = 1.0;
	private double brakeLateralImpulseCoefficient = 1.0;
	private double turnAngularImpulseCoefficient = 1.0;
	
	

	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public WorldSource source;
	
	protected GraphPositionPath overallPath;
	
	public GraphPositionPathPosition overallPos;
	
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
	double forwardSpeed;
	float angle;
	float angularVel;
	double[][] carTransArr;
	boolean atleastPartiallyOnRoad;
	boolean inMerger;
	
	Point prevWorldPoint0;
	Point prevWorldPoint3;
	
	/*
	 * 
	 */
	DrivingEvent curDrivingEvent;
	List<VertexEvent> vertexDepartureQueue = new ArrayList<VertexEvent>();
	double decelTime = -1;
	double stoppedTime = -1;
	Circle goalPoint;
	
	protected Color color;
	protected Color hiliteColor;
	
	private Quad shape;
	
	static Logger logger = Logger.getLogger(Car.class);
	static Logger pathingLogger = Logger.getLogger("com.gutabi.deadlock.model.Car.pathing");
	static Logger eventingLogger = Logger.getLogger("com.gutabi.deadlock.model.Car.eventing");
	
	public Car(WorldSource s) {
		
		state = CarStateEnum.DRIVING;
		
		source = s;
		
		color = Color.BLUE;
		hiliteColor = Color.BLUE;
		
		Point p0 = new Point(-CAR_LENGTH / 2, -CAR_LENGTH / 4);
		Point p1 = new Point(CAR_LENGTH / 2, -CAR_LENGTH / 4);
		Point p2 = new Point(CAR_LENGTH / 2, CAR_LENGTH / 4);
		Point p3 = new Point(-CAR_LENGTH / 2, CAR_LENGTH / 4);
		localQuad = new Quad(this, p0, p1, p2, p3);
		
		computeStartingProperties();
	}
	
	public String toString() {
		return Integer.toString(id);
	}
	
	protected abstract void computePath();
	
	protected abstract int getSheetRow();
	
	private void computeStartingProperties() {
		
		logger.debug("spawn");
		
		computePath();
		
		overallPos = new GraphPositionPathPosition(overallPath, 0, 0.0);
		GraphPosition closestGraphPos = overallPath.getGraphPosition(0, 0.0);
		startPoint = closestGraphPos.p;
		
		vertexDepartureQueue.add(new VertexSpawnEvent(overallPos));
		source.carQueue.add(this);
		
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
		carTransArr = new double[2][2];
		carTransArr[0][0] = r.col1.x;
		carTransArr[0][1] = r.col2.x;
		carTransArr[1][0] = r.col1.y;
		carTransArr[1][1] = r.col2.y;
		
		shape = Geom.localToWorld(localQuad, carTransArr, p);
		
		
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
		
//		prevWorldPoint0 = worldQuad.p0;
//		prevWorldPoint3 = worldQuad.p3;
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		
		if (logger.isDebugEnabled()) {
			logger.debug("p: " + p);
		}
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		vel = b2dBody.getLinearVelocity();
		angle = b2dBody.getAngle();
		assert !Double.isNaN(angle);
		
		if (logger.isDebugEnabled()) {
			logger.debug("angle: " + angle);
		}
		
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		forwardSpeed = Vec2.dot(vel, currentRightNormal);
		
		if (logger.isDebugEnabled()) {
			logger.debug("forwardSpeed: " + forwardSpeed);
		}
		
		Mat22 r = b2dBody.getTransform().R;
		carTransArr[0][0] = r.col1.x;
		carTransArr[0][1] = r.col2.x;
		carTransArr[1][0] = r.col1.y;
		carTransArr[1][1] = r.col2.y;
		
		shape = Geom.localToWorld(localQuad, carTransArr, p);
		
		Entity e = MODEL.world.pureGraphBestHitTest(this.shape);
		
		boolean wasInMerger = inMerger;
		if (e == null) {
			atleastPartiallyOnRoad = false;
			inMerger = false;
		} else {
			atleastPartiallyOnRoad = true;
			if (e instanceof Merger && ((Quad)shape).containedIn((Quad)e.getShape())) {
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
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
	public Shape getShape() {
		return shape;
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
			
			if (((VertexArrivalEvent)curDrivingEvent).sign.isEnabled()) {
				stoppedTime = -1;
				decelTime = -1;
			}
			
			((VertexArrivalEvent)curDrivingEvent).v.carQueue.remove(this);
			
			curDrivingEvent = null;
			
		} else if (curDrivingEvent instanceof CarProximityEvent) {
			
			stoppedTime = -1;
			decelTime = -1;
			
			curDrivingEvent = null;
			
		} else {
			assert false;
		}
		
		cleanupVertexDepartureQueue();
		
	}
	
	private void skid() {
		
		state = CarStateEnum.SKIDDED;
		
		overallPos = null;
		goalPoint = null;
		
//		source.outstandingCars--;
		
		if (curDrivingEvent == null) {
			
		} else if (curDrivingEvent instanceof VertexArrivalEvent) {
			
			if (((VertexArrivalEvent)curDrivingEvent).sign.isEnabled()) {
				stoppedTime = -1;
				decelTime = -1;
			}
			
			((VertexArrivalEvent)curDrivingEvent).v.carQueue.remove(this);
			
			curDrivingEvent = null;
			
		} else if (curDrivingEvent instanceof CarProximityEvent) {
			
			stoppedTime = -1;
			decelTime = -1;
			
			curDrivingEvent = null;
			
		} else {
			assert false;
		}
		
		cleanupVertexDepartureQueue();
		
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
			
			DrivingEvent newDrivingEvent = findNewDrivingEvent();
			
			processNewDrivingEvent(newDrivingEvent, t);
			
			handleCurrentDrivingEvent(t);
			
			cleanupVertexDepartureQueue();
			
		}
		
		updateFriction();
		
		switch (state) {
		case DRIVING:
			
			GraphPositionPathPosition next = overallPos.travel(Math.min(steeringLookaheadDistance, overallPos.lengthToEndOfPath));
			
			if (pathingLogger.isDebugEnabled()) {
				pathingLogger.debug("goalPoint: " + next.gpos);
			}
			
			goalPoint = new Circle(null, next.gpos.p, 0.1);
			updateDrive(t);
			break;
		case BRAKING:
			goalPoint = null;
			updateBrake(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
			break;
		}
	}
	
	private DrivingEvent findNewDrivingEvent() {
		
		DrivingEvent newDrivingEvent = null;
		
		Car carProx = overallPath.carProximityTest(overallPos, Math.min(carProximityLookahead, overallPos.lengthToEndOfPath));
		if (carProx != null) {
			newDrivingEvent = new CarProximityEvent(this, carProx);
		} else {
			VertexArrivalEvent vertexArr = overallPath.vertexArrivalTest(overallPos, Math.min(vertexArrivalLookahead, overallPos.lengthToEndOfPath));
			if (vertexArr != null) {
//				logger.debug("setting curEvent: " + events + "     " + oldEvents);
				
				if (!vertexDepartureQueue.contains(vertexArr)) {
					newDrivingEvent = vertexArr;
					
					if (!vertexArr.v.carQueue.contains(this)) {
						vertexArr.v.carQueue.add(this);
					}
				}
				
			}
		}
		
		return newDrivingEvent;
	}
	
	private void processNewDrivingEvent(DrivingEvent newDrivingEvent, double t) {
		
		if (newDrivingEvent != null) {
			if (curDrivingEvent == null) {
				curDrivingEvent = newDrivingEvent;
				if (eventingLogger.isDebugEnabled()) {
					eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
				}
			} else {
				if (curDrivingEvent.equals(newDrivingEvent)) {
					
				} else {
					
					if (curDrivingEvent instanceof CarProximityEvent && newDrivingEvent instanceof VertexArrivalEvent) {
						
						stoppedTime = -1;
						curDrivingEvent = newDrivingEvent;
						
					} else if (curDrivingEvent instanceof VertexArrivalEvent && newDrivingEvent instanceof CarProximityEvent) {
						
						stoppedTime = -1;
						curDrivingEvent = newDrivingEvent;
						if (eventingLogger.isDebugEnabled()) {
							eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
						}
						
						/*
						 * may have been at intersection with no stop sign, so make sure to be braking now
						 */
						
						state = CarStateEnum.BRAKING;
						
					} else if (curDrivingEvent instanceof CarProximityEvent && newDrivingEvent instanceof CarProximityEvent &&
							((CarProximityEvent)curDrivingEvent).otherCar == ((CarProximityEvent)newDrivingEvent).otherCar) {
						
						stoppedTime = -1;
						curDrivingEvent = newDrivingEvent;
						if (eventingLogger.isDebugEnabled()) {
							eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
						}
						
					} else if (curDrivingEvent instanceof CarProximityEvent && newDrivingEvent instanceof CarProximityEvent &&
							((CarProximityEvent)curDrivingEvent).otherCar != ((CarProximityEvent)newDrivingEvent).otherCar) {
						
						stoppedTime = -1;
						curDrivingEvent = newDrivingEvent;
						if (eventingLogger.isDebugEnabled()) {
							eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
						}
						
					} else {
						assert false;
					}
					
				}
			}
		}
		
	}
	
	private void handleCurrentDrivingEvent(double t) {
		
		if (curDrivingEvent == null) {
			return;
		}
		
		if (curDrivingEvent instanceof CarProximityEvent) {
			
			if (decelTime == -1) {
				// start braking
				
				assert state == CarStateEnum.DRIVING;
				state = CarStateEnum.BRAKING;
				
			} else {
				
				Car otherCar = ((CarProximityEvent) curDrivingEvent).otherCar;
				
				if (stoppedTime != -1 && t > stoppedTime + COMPLETE_STOP_WAIT_TIME) {
					
					GraphPositionPathPosition otherPosition = null;
					if (otherCar.overallPos != null) {
						otherPosition = overallPath.hitTest(otherCar.overallPos.gpos, overallPos);
					}
					
					if (otherPosition == null || DMath.greaterThan(overallPos.distanceTo(otherPosition), carProximityLookahead)) {
						
						// start driving
						
						Car carProx2 = overallPath.carProximityTest(overallPos, Math.min(carProximityLookahead, overallPos.lengthToEndOfPath));
						assert carProx2 == null;
						
						assert state == CarStateEnum.BRAKING;
						state = CarStateEnum.DRIVING;
						
						stoppedTime = -1;
						decelTime = -1;
						
						curDrivingEvent = null;
						if (eventingLogger.isDebugEnabled()) {
							eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
						}
						
					}
					
				}
				
			}
			
		} else if (curDrivingEvent instanceof VertexArrivalEvent) {
			
			if (((VertexArrivalEvent)curDrivingEvent).sign.isEnabled()) {
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
					
					vertexDepartureQueue.add((VertexArrivalEvent)curDrivingEvent);
					
					curDrivingEvent = null;
					if (eventingLogger.isDebugEnabled()) {
						eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
					}
					
				}
			} else {
				
				/*
				 * could have simply been passing through, so never braking
				 * or could have ben already had CarProximityEvent, it went away and was masking a VertexArrivalevent, so immediately go to that
				 * so already braking without a stop sign
				 * 
				 * all that matters now is to be driving
				 */
				
				state = CarStateEnum.DRIVING;
				
				stoppedTime = -1;
				decelTime = -1;
				
				vertexDepartureQueue.add((VertexArrivalEvent)curDrivingEvent);
				
				curDrivingEvent = null;
				if (eventingLogger.isDebugEnabled()) {
					eventingLogger.debug("t: " + t + " car " + this + ": " + curDrivingEvent);
				}
				
			}
			
		} else {
			assert false;
		}
	}
	
	private void cleanupVertexDepartureQueue() {
		
		if (!vertexDepartureQueue.isEmpty()) {
			
			List<VertexEvent> toRemove = new ArrayList<VertexEvent>();
			for (VertexEvent e : vertexDepartureQueue) {
				if (overallPos == null || e.carPastExitPosition == null || overallPos.combo >= e.carPastExitPosition.combo) {
					/*
					 * driving past exit of intersection, so cleanup
					 */
					
					e.v.carQueue.remove(this);
					
					toRemove.add(e);
					
				}
			}
			for (VertexEvent e : toRemove) {
				vertexDepartureQueue.remove(e);
			}
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
		
		double goalForwardVel = (float)getMaxSpeed();
		
		double dv;
		if (goalForwardVel > forwardSpeed) {
			dv = goalForwardVel - forwardSpeed;
		} else {
			dv = 0.0f;
		}
		if (dv < 0) {
			assert false;
		}
		if (dv > maxAcceleration * MODEL.dt) {
			dv = maxAcceleration * MODEL.dt;
		}
		
//		logger.debug("acc for driving: " + acc);
		
		float forwardImpulse = (float)(driveForwardImpulseCoefficient * mass * dv);
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(forwardImpulse), b2dBody.getWorldCenter());
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		float driveLateralImpulse = (float)(driveLateralImpulseCoefficient * Vec2.dot(currentUpNormal, cancelingImpulse));
		
		b2dBody.applyLinearImpulse(currentUpNormal.mul(driveLateralImpulse), b2dBody.getWorldCenter());
		
		
		
		Point dp = new Point(goalPoint.center.x-p.x, goalPoint.center.y-p.y);
		
		double goalAngle = Math.atan2(dp.y, dp.x);
		
		if (logger.isDebugEnabled()) {
			logger.debug("updateTurn: goalAngle: " + goalAngle);
		}
		
		double dw = ((float)goalAngle) - angle;
		
		while (dw > Math.PI) {
			dw -= 2*Math.PI;
		}
		while (dw < -Math.PI) {
			dw += 2*Math.PI;
		}
		
		/*
		 * turning radius
		 */
		
		double actualDistance = Math.abs(forwardSpeed * MODEL.dt);
		double maxRads = maxRadsPerMeter * actualDistance;
		double negMaxRads = -maxRads;
		if (dw > maxRads) {
			dw = maxRads;
		} else if (dw < negMaxRads) {
			dw = negMaxRads;
		}
		
		if (dw > 0.52) {
			String.class.getName();
		} else if (dw < -0.52) {
			String.class.getName();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("updateTurn: dw: " + dw);
		}
		
		float goalAngVel = (float)(dw / MODEL.dt);
		
		float angImpulse = (float)(turnAngularImpulseCoefficient * momentOfInertia * (goalAngVel - angularVel));
		
		b2dBody.applyAngularImpulse(angImpulse);
		
	}
	
	private void updateBrake(double t) {
		
		if (decelTime == -1) {
			
			logger.debug("decel");
			
			decelTime = t;
		}
		
		double goalForwardVel = 0.0;
		
		double dv = (goalForwardVel - forwardSpeed);
		if (dv < maxDeceleration * MODEL.dt) {
			dv = (maxDeceleration * MODEL.dt);
		}
		if (dv > 0) {
//			assert false;
		}
		
		//logger.debug("acc for braking: " + -forwardVel.length());
		
		Vec2 cancelingVel = vel.mul(-1);
		double cancelingRightVel = Vec2.dot(cancelingVel, currentRightNormal);
		double cancelingUpVel = Vec2.dot(cancelingVel, currentUpNormal);
		
		if (dv != cancelingRightVel) {
//			assert false;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("braking dv: " + dv);
		}
		
		float rightBrakeImpulse = (float)(brakeForwardImpulseCoefficient * dv * mass);
		float upBrakeImpulse = (float)(brakeLateralImpulseCoefficient * cancelingUpVel * mass);
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(rightBrakeImpulse), pVec2);
		b2dBody.applyLinearImpulse(currentUpNormal.mul(upBrakeImpulse), pVec2);
		
		
		double goalAngVel = 0.0;
		
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
			
			WorldSink s = (WorldSink)overallPath.end.entity;
			boolean sinked = false;
			if (Point.distance(p, s.p) < MODEL.world.SINK_EPSILON) {
				sinked = true;
			}
			
			if (sinked) {
				
				logger.debug("sink");
				
				overallPos = null;
				goalPoint = null;
				
				cleanupVertexDepartureQueue();
				
				s.matchingSource.outstandingCars--;
				s.carQueue.remove(this);
				state = CarStateEnum.SINKED;
				
				return false;
			}
			break;
		}
		case BRAKING:
			
			if (stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("stopped: " + t);
					}
					stoppedTime = t;
					
				}
				
//				MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
//				MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
				
			}
			
			break;
		case CRASHED:
			
			if (!MODEL.world.completelyContains(shape)) {
				return false;
			}
			
			break;
		case SKIDDED:
			
//			MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
//			MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
			
			if (!MODEL.world.completelyContains(shape)) {
				return false;
			}
			
			break;
		case SINKED:
			break;
		}
		
		return true;	
	}
	
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(RenderingContext ctxt) {
		
		paintImage(ctxt);
		
		if (MODEL.DEBUG_DRAW) {
			
			paintID(ctxt);
			
			if (goalPoint != null) {
				goalPoint.paint(ctxt);
			}
			
			ctxt.setColor(Color.BLACK);
			ctxt.setPixelStroke();
			shape.getAABB().draw(ctxt);
			
		}
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(RenderingContext ctxt) {
		paintRect(ctxt);
	}
	
	
	static Composite aComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	static Composite normalComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	
	protected void paintImage(RenderingContext ctxt) {
		
		int sheetRow = getSheetRow();
		
		AffineTransform origTransform = ctxt.getTransform();
		
		Composite origComposite = null;
		if (inMerger) {
			origComposite = ctxt.getComposite();
			ctxt.setComposite(aComp);
		}
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		
		ctxt.paintImage(-Car.CAR_LENGTH/2, -Car.CAR_WIDTH/2, VIEW.sheet,
				0, 0, VIEW.metersToPixels(CAR_LENGTH), VIEW.metersToPixels(CAR_WIDTH),
				64, sheetRow, 64+32, sheetRow+16);
		
		if (state == CarStateEnum.BRAKING) {
			
			ctxt.paintImage(-Car.CAR_LENGTH/2 - Car.BRAKE_SIZE/2, -Car.CAR_WIDTH/4 - Car.BRAKE_SIZE/2,
					VIEW.sheet,
					0,
					0,
					VIEW.metersToPixels(BRAKE_SIZE),
					VIEW.metersToPixels(BRAKE_SIZE),
					0, 288, 0+8, 288+8);
			
			ctxt.paintImage(-Car.CAR_LENGTH/2 - Car.BRAKE_SIZE/2, Car.CAR_WIDTH/4 - Car.BRAKE_SIZE/2, VIEW.sheet,
					0,
					0,
					VIEW.metersToPixels(BRAKE_SIZE),
					VIEW.metersToPixels(BRAKE_SIZE),
					0, 288, 0+8, 288+8);
			
		}
		
		if (inMerger) {
			ctxt.setComposite(origComposite);
		}
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintRect(RenderingContext ctxt) {
		
		ctxt.setColor(hiliteColor);
		
		shape.paint(ctxt);
	}
	
	public void paintID(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setPixelStroke();
		ctxt.paintString(-CAR_LENGTH/2, 0.0, Integer.toString(id));
		
	}

}
