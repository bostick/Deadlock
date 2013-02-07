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
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.sprites.Sheet.Sprite;

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
	
	public AABB localAABB;
	public Body b2dBody;
	protected PolygonShape b2dShape;
	public org.jbox2d.dynamics.Fixture b2dFixture;
	
	
	public Point p;
	public double angle = Double.NaN;
//	private double[][] carTransArr = new double[2][2];
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
	
//	static Logger logger = Logger.getLogger(Car.class);
//	static Logger pathingLogger = Logger.getLogger(logger.getName()+".pathing");
//	static Logger eventingLogger = Logger.getLogger(logger.getName()+".eventing");
	
	public Car(World world) {
		
		this.world = world;
		
		state = CarStateEnum.DRIVING;
		
		engine = new Engine(world, this);
	}
	
	public void computeCtorProperties() {
		
//		Point p0 = new Point(-CAR_LENGTH / 2, -CAR_WIDTH / 2);
//		Point p1 = new Point(CAR_LENGTH / 2, -CAR_WIDTH / 2);
//		Point p2 = new Point(CAR_LENGTH / 2, CAR_WIDTH / 2);
//		Point p3 = new Point(-CAR_LENGTH / 2, CAR_WIDTH / 2);
		localAABB = APP.platform.createShapeEngine().createAABB(-CAR_LENGTH / 2, -CAR_WIDTH / 2, CAR_LENGTH, CAR_WIDTH);
		
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
	
	public void setTransform(Point p, double angle) {
		this.p = p;
		this.angle = angle;
//		Geom.rotationMatrix(angle, carTransArr);
		shape = Geom.localToWorld(localAABB, angle, p);
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
	
//	public int sheetColStart;
//	public int sheetColEnd;
//	public int sheetRowStart;
//	public int sheetRowEnd;
	public Sprite sprite;
	
	public abstract void paint(RenderingContext ctxt);
	
	protected void paintImage(RenderingContext ctxt) {
			
		Transform origTransform = ctxt.getTransform();
		if (inMerger) {
			ctxt.setAlpha(0.5);
		}
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		ctxt.translate(CAR_LOCALX, CAR_LOCALY);
		
//		ctxt.paintImage(APP.carSheet, world.screen.pixelsPerMeter,
//				0, 0, CAR_LENGTH, CAR_WIDTH,
//				sheetColStart, sheetRowStart, sheetColEnd, sheetRowEnd);
		
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
		
		ctxt.translate(p.x, p.y);
		ctxt.rotate(angle);
		
		Transform brakeTransform = ctxt.getTransform();
		
//		ctxt.translate(CAR_BRAKE1X, CAR_BRAKE1Y);
//		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
//				0, 0, BRAKE_SIZE, BRAKE_SIZE,
//				0, 64, 0+8, 64+8);
		
		APP.spriteSheet.paint(ctxt, Sprite.BRAKE, world.screen.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(brakeTransform);
		
//		ctxt.translate(CAR_BRAKE2X, CAR_BRAKE2Y);
//		ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
//				0, 0, BRAKE_SIZE, BRAKE_SIZE,
//				0, 64, 0+8, 64+8);
		
		APP.spriteSheet.paint(ctxt, Sprite.BRAKE, world.screen.pixelsPerMeter, 0, 0, BRAKE_SIZE, BRAKE_SIZE);
		
		ctxt.setTransform(origTransform);
		
	}
	
	public void paintID(RenderingContext ctxt) {
		
		Transform origTransform = ctxt.getTransform();
		
		ctxt.translate(p.x, p.y);
		
		ctxt.setColor(Color.WHITE);
		ctxt.paintString(CAR_LOCALX, 0.0, 2.0/world.screen.pixelsPerMeter, Integer.toString(id));
		
		ctxt.setTransform(origTransform);
	}

}
