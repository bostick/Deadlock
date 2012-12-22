package com.gutabi.deadlock.world.car;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Geom;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldCamera;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.Merger;

//@SuppressWarnings("static-access")
public abstract class Car extends Entity {
	
	public static final double CAR_LENGTH = 1.0;
	public static final double CAR_WIDTH = 0.5;
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public abstract double getMaxSpeed();
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	WorldCamera cam;
	public World world;
	public Fixture source;
	
	public final Driver driver;
	public Engine engine;
	
	public int id;
	
	public static int carCounter;
	
	Point startPoint;
	double startHeading;
	float mass;
	float momentOfInertia;
	
	Quad localQuad;
	protected Body b2dBody;
	protected PolygonShape b2dShape;
	protected org.jbox2d.dynamics.Fixture b2dFixture;
	
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
	
	private Quad shape;
	
	static Logger logger = Logger.getLogger(Car.class);
	static Logger pathingLogger = Logger.getLogger(logger.getName()+".pathing");
	static Logger eventingLogger = Logger.getLogger(logger.getName()+".eventing");
	
	public Car(WorldCamera cam, World world, Fixture s) {
		
		this.cam = cam;
		this.world = world;
		this.source = s;
		
		state = CarStateEnum.DRIVING;
		
		driver = new Driver(this);
		engine = new Engine(this);
		
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
	
	private void computeStartingProperties() {
		
		logger.debug("spawn");
		
		driver.computeStartingProperties();
		
		startPoint = driver.overallPos.p;
		
		GraphPositionPathPosition next = driver.overallPos.travel(getMaxSpeed() * APP.dt);
		
		Point nextDTGoalPoint = next.p;
		
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
		
		
//		Vec2 v = dp.vec2();
//		v.normalize();
//		v.mulLocal((float)getMaxSpeed());
		
//		b2dBody.setLinearVelocity(v);
		
		computeDynamicPropertiesAlways();
		computeDynamicPropertiesMoving();
	}
	
	private void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)startPoint.x, (float)startPoint.y);
		bodyDef.angle = (float)startHeading;
		bodyDef.allowSleep = true;
		bodyDef.awake = true;
		
		b2dBody = world.b2dWorld.createBody(bodyDef);
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
	
	private void computeDynamicPropertiesAlways() {
		vel = b2dBody.getLinearVelocity();
	}
	
	private void computeDynamicPropertiesMoving() {
		
		prevWorldPoint0 = shape.p0;
		prevWorldPoint3 = shape.p3;
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("p: " + p);
//		}
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		
		angle = b2dBody.getAngle();
		assert !Double.isNaN(angle);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("angle: " + angle);
//		}
		
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		forwardSpeed = Vec2.dot(vel, currentRightNormal);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("forwardSpeed: " + forwardSpeed);
//		}
		
		Mat22 r = b2dBody.getTransform().R;
		carTransArr[0][0] = r.col1.x;
		carTransArr[0][1] = r.col2.x;
		carTransArr[1][0] = r.col1.y;
		carTransArr[1][1] = r.col2.y;
		
		shape = Geom.localToWorld(localQuad, carTransArr, p);
		
		switch (state) {
		case DRIVING:
		case BRAKING:
			
			driver.computeDynamicPropertiesMoving();
			
			Entity hit = driver.overallPath.pureGraphIntersectQuad(this.shape, driver.overallPos);
			
			boolean wasInMerger = inMerger;
			if (hit == null) {
				atleastPartiallyOnRoad = false;
				inMerger = false;
			} else {
				atleastPartiallyOnRoad = true;
				if (hit instanceof Merger && ((Quad)hit.getShape()).completelyContains(shape)) {
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
			
			break;
		case SINKED:
		case SKIDDED:
		case CRASHED:
			break;
		}
		
	}
	
	public void destroy() {
		b2dCleanup();
	}
	
	private void b2dCleanup() {
		b2dBody.destroyFixture(b2dFixture);
		world.b2dWorld.destroyBody(b2dBody);
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
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void crash() {
		state = CarStateEnum.CRASHED;
		driver.clear();
	}
	
	public void skid() {
		state = CarStateEnum.SKIDDED;
		driver.clear();
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	public void preStep(double t) {
		
		switch (state) {
		case DRIVING: 
		case BRAKING:
			driver.preStep(t);
			break;
		case CRASHED:
		case SKIDDED:
		case SINKED:
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
			
			Fixture s = (Fixture)driver.overallPath.end.entity;
			boolean sinked = false;
			if (Point.distance(p, s.p) < SINK_EPSILON) {
				sinked = true;
			}
			
			if (sinked) {
				
				logger.debug("sink");
				
				driver.clear();
				
				s.match.outstandingCars--;
				state = CarStateEnum.SINKED;
				
				return false;
			}
			break;
		}
		case BRAKING:
			
			computeDynamicPropertiesAlways();
			
			if (driver.stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("stopped: " + t);
					}
					driver.stoppedTime = t;
					
				}
				
//				MODEL.world.addSkidMarks(prevWorldPoint0, worldQuad.p0);
//				MODEL.world.addSkidMarks(prevWorldPoint3, worldQuad.p3);
				world.roadMarkMap.addRoadMark(prevWorldPoint0, shape.p0);
				world.roadMarkMap.addRoadMark(prevWorldPoint3, shape.p3);
				
				computeDynamicPropertiesMoving();
				
			} else {
				
				//computeDynamicPropertiesMoving();
				
			}
			
			break;
		case CRASHED:
			
			computeDynamicPropertiesAlways();
			
			if (driver.stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("stopped: " + t);
					}
					driver.stoppedTime = t;
					
				}
				
				world.quadrantMap.grassMap.mowGrass(shape);
				
				computeDynamicPropertiesMoving();
				
			} else {
				
				if (!DMath.equals(vel.lengthSquared(), 0.0)) {
					
					driver.stoppedTime = -1;
					driver.deadlocked = false;
					
					computeDynamicPropertiesMoving();
					
				}
				
			}
			
			if (!world.quadrantMap.completelyContains(shape)) {
				return false;
			}
			
			break;
			
		case SKIDDED:
			computeDynamicPropertiesAlways();
			
			if (driver.stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("stopped: " + t);
					}
					driver.stoppedTime = t;
					
				}
				
				world.quadrantMap.grassMap.mowGrass(shape);
				
				world.grassMarkMap.addGrassMark(prevWorldPoint0, shape.p0);
				world.grassMarkMap.addGrassMark(prevWorldPoint3, shape.p3);
				
				computeDynamicPropertiesMoving();
				
			} else {
				
				if (!DMath.equals(vel.lengthSquared(), 0.0)) {
					
					driver.stoppedTime = -1;
					driver.deadlocked = false;
					
					computeDynamicPropertiesMoving();
					
				}
				
			}
			
			if (!world.quadrantMap.completelyContains(shape)) {
				return false;
			}
			
			break;
		case SINKED:
			
			assert false;
			
			break;
		}
		
		return true;	
	}
	
	
	static Color redOrange = new Color(255, 67, 0);
	
	/**
	 * @param g2 in world coords
	 */
	public void paint(RenderingContext ctxt) {
		
		switch (state) {
		case BRAKING:
			
			if (APP.CARTEXTURE_DRAW) {
				paintImage(ctxt);
			} else {
				if (!driver.deadlocked) {
					ctxt.setColor(Color.BLUE);
					paintRect(ctxt);
				} else {
					ctxt.setColor(Color.RED);
					paintRect(ctxt);
				}
			}
			
			paintBrakes(ctxt);
			
			if (APP.DEBUG_DRAW) {
				
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke(cam.pixelsPerMeter, 1);
				shape.getAABB().draw(ctxt);
				
				paintID(ctxt);
				
			}
			
			break;
		case DRIVING:
		case SINKED:
			
			if (APP.CARTEXTURE_DRAW) {
				paintImage(ctxt);
			} else {
				ctxt.setColor(Color.BLUE);
				paintRect(ctxt);
			}
			
			if (APP.DEBUG_DRAW) {
				
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke(cam.pixelsPerMeter, 1);
				shape.getAABB().draw(ctxt);
				
				paintID(ctxt);
				
			}
			break;
			
		case SKIDDED:
			
			if (APP.CARTEXTURE_DRAW) {
				paintImage(ctxt);
			} else {
				ctxt.setColor(Color.GREEN);
				paintRect(ctxt);
			}
			
			if (APP.DEBUG_DRAW) {
				
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke(cam.pixelsPerMeter, 1);
				shape.getAABB().draw(ctxt);
				
				paintID(ctxt);
				
			}
			break;
			
		case CRASHED:
			
			if (APP.CARTEXTURE_DRAW) {
				paintImage(ctxt);
			} else {
				if (!driver.deadlocked) {
					ctxt.setColor(Color.ORANGE);
					paintRect(ctxt);
				} else {
					ctxt.setColor(redOrange);
					paintRect(ctxt);
				}
			}
			
			if (APP.DEBUG_DRAW) {
				
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke(cam.pixelsPerMeter, 1);
				shape.getAABB().draw(ctxt);
				
				paintID(ctxt);
				
			}
			break;
		}
		
	}
	
	/**
	 * @param g2 in world coords
	 */
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(Color.BLUE);
		paintRect(ctxt);
	}
	
	
	static Composite aComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	static Composite normalComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	
	public static final double CAR_LOCALX = -CAR_LENGTH / 2;
	public static final double CAR_LOCALY = -CAR_WIDTH / 2;
	
	public static final double BRAKE_SIZE = 0.25;
	public static final double BRAKE_LOCALX = -BRAKE_SIZE / 2;
	public static final double BRAKE_LOCALY = -BRAKE_SIZE / 2;
	
	public static final double CAR_BRAKE1X = CAR_LOCALX + BRAKE_LOCALX;
	public static final double CAR_BRAKE1Y = CAR_LOCALY + CAR_WIDTH/4 + BRAKE_LOCALY;
	
	public static final double CAR_BRAKE2X = CAR_LOCALX + BRAKE_LOCALX;
	public static final double CAR_BRAKE2Y = CAR_LOCALY + 3 * CAR_WIDTH/4 + BRAKE_LOCALY;
	
	public static final int brakeRowStart = 288;
	public static final int brakeRowEnd = brakeRowStart + 8;
	
	protected int sheetRowStart;
	protected int sheetRowEnd;
	
	protected void paintImage(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		Composite origComposite = null;
		if (inMerger) {
			origComposite = ctxt.getComposite();
			ctxt.setComposite(aComp);
		}
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		ctxt.translate(CAR_LOCALX, CAR_LOCALY);
		
		ctxt.paintImage(
				cam.pixelsPerMeter,
				VIEW.sheet,
				0, 0, CAR_LENGTH, CAR_WIDTH,
				64, sheetRowStart, 64+32, sheetRowEnd);
		
		if (inMerger) {
			ctxt.setComposite(origComposite);
		}
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintRect(RenderingContext ctxt) {
		shape.paint(ctxt);
	}
	
	private void paintBrakes(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		
		AffineTransform brakeTransform = ctxt.getTransform();
		
		ctxt.translate(CAR_BRAKE1X, CAR_BRAKE1Y);
		ctxt.paintImage(
				cam.pixelsPerMeter,
				VIEW.sheet,
				0, 0, BRAKE_SIZE, BRAKE_SIZE,
				0, brakeRowStart, 0+8, brakeRowEnd);
		
		ctxt.setTransform(brakeTransform);
		
		ctxt.translate(CAR_BRAKE2X, CAR_BRAKE2Y);
		ctxt.paintImage(
				cam.pixelsPerMeter,
				VIEW.sheet,
				0, 0, BRAKE_SIZE, BRAKE_SIZE,
				0, brakeRowStart, 0+8, brakeRowEnd);
		
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintID(RenderingContext ctxt) {
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x, p.y);
		
		ctxt.setColor(Color.WHITE);
		ctxt.paintString(cam.pixelsPerMeter, CAR_LOCALX, 0.0, 2.0, Integer.toString(id));
		
		ctxt.setTransform(origTransform);
	}

}
