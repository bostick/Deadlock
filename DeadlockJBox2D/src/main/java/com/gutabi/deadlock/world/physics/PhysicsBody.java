package com.gutabi.deadlock.world.physics;

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
import com.gutabi.deadlock.math.Point;

public abstract class PhysicsBody extends Entity {
	
	public PhysicsWorld world;
	
	public double length = -1;
	public double width = -1;
	public double localULX;
	public double localULY;
	
	
	public Point center;
	public double angle = Double.NaN;
	public OBB shape;
	
	public double forwardSpeed;
	public double angularVel;
	
	float mass;
	float momentOfInertia;
	
	public AABB localAABB;
	
	protected Point prevWorldPoint0;
	protected Point prevWorldPoint3;
	
	/*
	 * only protected for InteractiveCar's fake coasting steps
	 */
	protected Body b2dBody;
	private PolygonShape b2dShape;
	private org.jbox2d.dynamics.Fixture b2dFixture;
	
	/*
	 * dynamic properties
	 */
	Vec2 pVec2;
	Vec2 currentRightNormal;
	Vec2 currentUpNormal;
	protected Vec2 vel;
	Vec2 forwardVel;
	
	
	public PhysicsBody(PhysicsWorld world) {
		this.world = world;
	}
	
	public void physicsInit() {
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
		b2dShape.setAsBox((float)(length / 2), (float)(width / 2));
		
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
	}
	
	public void setPhysicsTransform() {
		
		synchronized (world.b2dWorld) {
			b2dBody.setTransform(PhysicsUtils.vec2(center), (float)angle);
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
	
	public void destroy() {
		b2dBody.destroyFixture(b2dFixture);
		world.b2dWorld.destroyBody(b2dBody);
	}
	
	public void applyForwardImpulse(double coeff, double dv) {
		
		float forwardImpulse = (float)(coeff * mass * dv);
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(forwardImpulse), b2dBody.getWorldCenter());
		
	}
	
	public void applyAngularImpulse(double coeff, double dw) {
		
//		System.out.println("dw: " + dw);
		
		float cancelingAngImpulse = (float)(coeff * momentOfInertia * dw);
		
		b2dBody.applyAngularImpulse(cancelingAngImpulse);
		
	}
	
	public void applyCancelingForwardImpulse(double coeff) {
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		
		if (cancelingImpulse.lengthSquared() == 0.0f) {
			return;
		}
		
		float cancelingForwardImpulse = (float)(coeff * Vec2.dot(currentRightNormal, cancelingImpulse));
		
		b2dBody.applyLinearImpulse(currentRightNormal.mul(cancelingForwardImpulse), b2dBody.getWorldCenter());
		
	}
	
	public void applyCancelingLateralImpulse(double coeff) {
		
		Vec2 cancelingImpulse = vel.mul(-1).mul(mass);
		
		if (cancelingImpulse.lengthSquared() == 0.0f) {
			return;
		}
		
		float cancelingLateralImpulse = (float)(coeff * Vec2.dot(currentUpNormal, cancelingImpulse));
		
		b2dBody.applyLinearImpulse(currentUpNormal.mul(cancelingLateralImpulse), b2dBody.getWorldCenter());
		
	}
	
	public void applyCancelingAngularImpulse(double coeff) {
		
		if (angularVel == 0.0f) {
			return;
		}
		
		float cancelingAngImpulse = (float)(coeff * momentOfInertia * -angularVel);
		
		b2dBody.applyAngularImpulse(cancelingAngImpulse);
		
	}

}
