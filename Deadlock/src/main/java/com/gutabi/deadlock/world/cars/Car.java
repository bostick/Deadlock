package com.gutabi.deadlock.world.cars;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.sprites.CarSheet;
import com.gutabi.deadlock.world.sprites.CarSheet.CarSheetSprite;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public abstract class Car extends Entity {
	
	public static final double METERS_PER_CARLENGTH = 1.0;
	
	public double CAR_LENGTH = -1;
	public double CAR_WIDTH = -1;
	
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
	
	public AABB localAABB;
	public Point localFront;
	public Body b2dBody;
	protected PolygonShape b2dShape;
	public org.jbox2d.dynamics.Fixture b2dFixture;
	
	
	public Point center;
	public double angle = Double.NaN;
	public OBB shape;
	
	
	
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
	public OBB toolOrigShape;
	
	public boolean destroyed;
	
	public Car(World world) {
		
		this.world = world;
		
		state = CarStateEnum.DRIVING;
	}
	
	public void computeCtorProperties(int r) {
		
		sprite = CarSheet.sprite(r);
		CAR_LENGTH = sprite.carLength() * METERS_PER_CARLENGTH;
		CAR_WIDTH = sprite.carWidth() * METERS_PER_CARLENGTH;
		localFront = new Point(0.75 * CAR_LENGTH, 0.0);
		
		localAABB = new AABB(-CAR_LENGTH / 2, -CAR_WIDTH / 2, CAR_LENGTH, CAR_WIDTH);
		
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
	
	public void setTransform(Point center, double angle) {
		this.center = center;
		this.angle = angle;
		shape = Geom.localToWorld(localAABB, angle, center);
	}
	
	public void b2dInit() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set((float)center.x, (float)center.y);
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
	
	public void computeDynamicPropertiesAlways() {
		vel = b2dBody.getLinearVelocity();
	}
	
	public void computeDynamicPropertiesMoving() {
		
		prevWorldPoint0 = shape.p0;
		prevWorldPoint3 = shape.p3;
		
		pVec2 = b2dBody.getPosition();
		center = new Point(pVec2.x, pVec2.y);
		
		currentRightNormal = b2dBody.getWorldVector(right);
		currentUpNormal = b2dBody.getWorldVector(up);
		
		angle = b2dBody.getAngle();
		assert !Double.isNaN(angle);
		
		angularVel = b2dBody.getAngularVelocity();
		
		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
		forwardSpeed = Vec2.dot(vel, currentRightNormal);
		
		double angle = b2dBody.getAngle();
		
		shape = Geom.localToWorld(localAABB, angle, center);
		
		switch (state) {
		case DRIVING:
		case BRAKING:
			
			((AutonomousDriver)driver).computeDynamicPropertiesMoving();
			
			Entity hit = ((AutonomousDriver)driver).overallPath.pureGraphIntersectOBB(this.shape, ((AutonomousDriver)driver).overallPos);
			
			boolean wasInMerger = inMerger;
			if (hit == null) {
				atleastPartiallyOnRoad = false;
				inMerger = false;
			} else {
				atleastPartiallyOnRoad = true;
				if (hit instanceof Merger && ShapeUtils.containsAO((AABB)((Merger)hit).getShape(), shape)) {
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
		case IDLE:
		case DRAGGING:
			assert false;
			break;
		case COASTING:
			
			((InteractiveDriver)driver).computeDynamicPropertiesMoving();
			
			break;
		}
		
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
	public void setB2dCollisions(boolean collisions) {
		if (collisions) {
			b2dFixture.setFilterData(normalCarFilter);
		} else {
			b2dFixture.setFilterData(mergingCarFilter);
		}
		
	}
	
	public void skid() {
		state = CarStateEnum.SKIDDED;
		((AutonomousDriver)driver).clear();
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
		case DRAGGING:
		case COASTING:
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
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
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
	
	public CarSheetSprite sprite;
	
	public abstract void paint(RenderingContext ctxt);
	
	protected void paintImage(RenderingContext ctxt) {
			
		Transform origTransform = ctxt.getTransform();
		if (inMerger) {
			ctxt.setAlpha(0.5);
		}
		
		ctxt.translate(center.x, center.y);
		ctxt.rotate(angle);
		ctxt.translate(CAR_LOCALX, CAR_LOCALY);
		
		APP.carSheet.paint(ctxt, sprite, world.screen.pixelsPerMeter, 0, 0, CAR_LENGTH, CAR_WIDTH);
		
		if (inMerger) {
			ctxt.setAlpha(1.0);
		}
		ctxt.setTransform(origTransform);
		
	}
	
	protected void paintRect(RenderingContext ctxt) {
		shape.paint(ctxt);
	}
	
	protected void paintBrakes(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(center.x, center.y);
		ctxt.rotate(angle);
		
		Transform brakeTransform = ctxt.getTransform();
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BRAKE, world.screen.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(brakeTransform);
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BRAKE, world.screen.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public void paintID(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(center.x, center.y);
		
		ctxt.setColor(Color.WHITE);
		ctxt.paintString(CAR_LOCALX, 0.0, 2.0/world.screen.pixelsPerMeter, Integer.toString(id));
		
		ctxt.setTransform(origTransform);
	}

}
