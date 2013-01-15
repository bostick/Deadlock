package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.Quad;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.Merger;

public abstract class Car extends Entity {
	
	public double CAR_LENGTH = -1;
	public double CAR_WIDTH = -1;
	
	public double maxSpeed = -1;
	
	/*
	 * distance that center of a car has to be from center of a sink in order to be sinked
	 */
	public static final double SINK_EPSILON = 0.5f;
	
	public CarStateEnum state;
	
	public double startingTime;
	public double crashingTime;
	
	public World world;
	
	public Driver driver;
	public Engine engine;
	
	public int id;
	
	public static int carCounter;
	
	float mass;
	float momentOfInertia;
	
	public Quad localQuad;
	public Body b2dBody;
	protected PolygonShape b2dShape;
	public org.jbox2d.dynamics.Fixture b2dFixture;
	
	
	public Point p;
	public double angle;
	private double[][] carTransArr = new double[2][2];
	public Quad shape;
	
	
	
	/*
	 * dynamic properties
	 */
	Vec2 pVec2;
	Vec2 currentRightNormal;
	Vec2 currentUpNormal;
	Vec2 vel;
	Vec2 forwardVel;
	double forwardSpeed;
	float angularVel;
	boolean atleastPartiallyOnRoad;
	boolean inMerger;
	
	Point prevWorldPoint0;
	Point prevWorldPoint3;
	
	public Point toolOrigP;
	public double toolOrigAngle;
	public Quad toolOrigShape;
	
	public boolean destroyed;
	
//	static Logger logger = Logger.getLogger(Car.class);
//	static Logger pathingLogger = Logger.getLogger(logger.getName()+".pathing");
//	static Logger eventingLogger = Logger.getLogger(logger.getName()+".eventing");
	
	public Car(World world) {
		
		this.world = world;
		
		state = CarStateEnum.DRIVING;
		
//		driver = new Driver(this);
		engine = new Engine(world, this);
	}
	
	public void computeCtorProperties() {
		
		Point p0 = new Point(-CAR_LENGTH / 2, -CAR_WIDTH / 2);
		Point p1 = new Point(CAR_LENGTH / 2, -CAR_WIDTH / 2);
		Point p2 = new Point(CAR_LENGTH / 2, CAR_WIDTH / 2);
		Point p3 = new Point(-CAR_LENGTH / 2, CAR_WIDTH / 2);
		localQuad = APP.platform.createShapeEngine().createQuad(this, p0, p1, p2, p3);
		
		CAR_LOCALX = -CAR_LENGTH / 2;
		CAR_LOCALY = -CAR_WIDTH / 2;
		
		BRAKE_SIZE = 0.25;
		BRAKE_LOCALX = -BRAKE_SIZE / 2;
		BRAKE_LOCALY = -BRAKE_SIZE / 2;
		
		CAR_BRAKE1X = CAR_LOCALX + BRAKE_LOCALX;
		CAR_BRAKE1Y = CAR_LOCALY + CAR_WIDTH/4 + BRAKE_LOCALY;
		
		CAR_BRAKE2X = CAR_LOCALX + BRAKE_LOCALX;
		CAR_BRAKE2Y = CAR_LOCALY + 3 * CAR_WIDTH/4 + BRAKE_LOCALY;
	}
	
	public void computeStartingProperties() {
		
		driver.computeStartingProperties();
		
		p = driver.gpppPointToCenter(driver.overallPos.p);
		
		if (driver.overallSide == null) {
			
			GraphPositionPathPosition next = driver.overallPos.nextBound();
			
			Point nextDTGoalPoint = driver.gpppPointToCenter(next.p);
			
			Point dp = new Point(nextDTGoalPoint.x-p.x, nextDTGoalPoint.y-p.y);
			
			angle = Math.atan2(dp.y, dp.x);
			
		} else {
			angle = driver.overallSide.getAngle();
		}
		
		setTransform(p, angle);
	}
	
	public void setTransform(Point p, double angle) {
		this.p = p;
		this.angle = angle;
		Geom.rotationMatrix(angle, carTransArr);
		shape = Geom.localToWorld(localQuad, carTransArr, p);
	}
	
	public void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)p.x, (float)p.y);
		bodyDef.angle = (float)angle;
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
		b2dShape.setAsBox((float)(CAR_LENGTH / 2), (float)(CAR_WIDTH / 2));
		
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
	
	public void computeDynamicPropertiesAlways() {
		vel = b2dBody.getLinearVelocity();
	}
	
	public void computeDynamicPropertiesMoving() {
		
		prevWorldPoint0 = shape.p0;
		prevWorldPoint3 = shape.p3;
		
		pVec2 = b2dBody.getPosition();
		p = new Point(pVec2.x, pVec2.y);
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		
		angle = b2dBody.getAngle();
		assert !Double.isNaN(angle);
		
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		forwardSpeed = Vec2.dot(vel, currentRightNormal);
		
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
				if (hit instanceof Merger && ShapeUtils.containsAQ((AABB)hit.getShape(), shape)) {
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
		case IDLE:
			assert false;
			break;
		}
		
	}
	
	public void destroy() {
		switch (state) {
		case BRAKING:
		case CRASHED:
		case DRIVING:
		case SINKED:
		case SKIDDED:
			b2dCleanup();
			break;
		case IDLE:
			break;
		}
		destroyed = true;
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
		case IDLE:
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
				
//				logger.debug("sink");
				
				driver.clear();
				
				s.match.outstandingCars--;
				state = CarStateEnum.SINKED;
				
				return false;
			}
			return true;
		}
		case BRAKING:
			
			computeDynamicPropertiesAlways();
			
			if (driver.stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
//					if (logger.isDebugEnabled()) {
//						logger.debug("stopped: " + t);
//					}
					driver.stoppedTime = t;
					
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
			
			if (driver.stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
//					if (logger.isDebugEnabled()) {
//						logger.debug("stopped: " + t);
//					}
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
			
			return true;
		case SKIDDED:
			
			computeDynamicPropertiesAlways();
			
			if (driver.stoppedTime == -1) {
				
				if (DMath.equals(vel.lengthSquared(), 0.0)) {
					
//					if (logger.isDebugEnabled()) {
//						logger.debug("stopped: " + t);
//					}
					driver.stoppedTime = t;
					
				}
				
				world.quadrantMap.grassMap.mowGrass(shape);
				
				synchronized (APP) {
					world.grassMarkMap.addGrassMark(prevWorldPoint0, shape.p0);
					world.grassMarkMap.addGrassMark(prevWorldPoint3, shape.p3);
				}
				
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
			
			return true;
		case SINKED:
			
			assert false;
			
			return true;
			
		case IDLE:
			return true;
		}
		
		return true;
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (APP.CARTEXTURE_DRAW) {
			paintImage(ctxt);
		} else {
			if (!driver.deadlocked) {
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
			
			if (driver.overallPos != null) {
				ctxt.setColor(Color.DARKGREEN);
				APP.platform.createShapeEngine().createCircle(null, driver.overallPos.p, 0.2).paint(ctxt);
			}
			
			if (driver.goalPoint != null) {
				ctxt.setColor(Color.GREEN);
				APP.platform.createShapeEngine().createCircle(null, driver.goalPoint, 0.2).paint(ctxt);
			}
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.getAABB().draw(ctxt);
			
			paintID(ctxt);
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(Color.BLUE);
		paintRect(ctxt);
	}
	
	public double CAR_LOCALX;
	public double CAR_LOCALY;
	
	public double BRAKE_SIZE;
	public double BRAKE_LOCALX;
	public double BRAKE_LOCALY;
	
	public double CAR_BRAKE1X;
	public double CAR_BRAKE1Y;
	
	public double CAR_BRAKE2X;
	public double CAR_BRAKE2Y;
	
	public int sheetColStart;
	public int sheetColEnd;
	public int sheetRowStart;
	public int sheetRowEnd;
	
	protected void paintImage(RenderingContext ctxt) {
			
		Transform origTransform = ctxt.getTransform();
		if (inMerger) {
			ctxt.setAlpha(0.5);
		}
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		ctxt.translate(CAR_LOCALX, CAR_LOCALY);
		
		ctxt.paintImage(APP.carSheet, world.screen.pixelsPerMeter,
				0, 0, CAR_LENGTH, CAR_WIDTH,
				sheetColStart, sheetRowStart, sheetColEnd, sheetRowEnd);
		
		if (inMerger) {
			ctxt.setAlpha(1.0);
		}
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintRect(RenderingContext ctxt) {
		shape.paint(ctxt);
	}
	
	private void paintBrakes(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		
		Transform brakeTransform = ctxt.getTransform();
		
		ctxt.translate(CAR_BRAKE1X, CAR_BRAKE1Y);
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				0, 0, BRAKE_SIZE, BRAKE_SIZE,
				0, 64, 0+8, 64+8);
		
		ctxt.setTransform(brakeTransform);
		
		ctxt.translate(CAR_BRAKE2X, CAR_BRAKE2Y);
		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
				0, 0, BRAKE_SIZE, BRAKE_SIZE,
				0, 64, 0+8, 64+8);
		
		ctxt.setTransform(origTransform);
		
	}
	
	private void paintID(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x, p.y);
		
		ctxt.setColor(Color.WHITE);
		ctxt.paintString(CAR_LOCALX, 0.0, 2.0/world.screen.pixelsPerMeter, Integer.toString(id));
		
		ctxt.setTransform(origTransform);
	}

}
