package com.gutabi.capsloc.world.physics;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.Entity;
import com.gutabi.capsloc.geom.AABB;
import com.gutabi.capsloc.geom.MutableOBB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.world.World;

public abstract class PhysicsBody extends Entity {
	
	public World world;
	
	public double length = -1;
	public double width = -1;
	public double localULX;
	public double localULY;
	
	
	public Point center;
	public double angle = Double.NaN;
	public final MutableOBB shape;
	
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
//	protected Body b2dBody;
//	private PolygonShape b2dShape;
//	private org.jbox2d.dynamics.Fixture b2dFixture;
	
	/*
	 * dynamic properties
	 */
//	Vec2 pVec2;
//	Vec2 currentRightNormal;
//	Vec2 currentUpNormal;
	protected Point vel;
//	Vec2 forwardVel;
	
	
	public PhysicsBody(World world) {
		this.world = world;
		
		this.shape = APP.platform.createMutableOBB();
	}
	
	public void physicsInit() {
		
	}
	
	public void computeDynamicPropertiesAlways() {
		
	}
	
	public void computeDynamicPropertiesMoving() {
		
//		prevWorldPoint0 = shape.p0;
//		prevWorldPoint3 = shape.p3;
//		
//		pVec2 = b2dBody.getPosition();
//		center = new Point(pVec2.x, pVec2.y);
//		
//		currentRightNormal = b2dBody.getWorldVector(right);
//		currentUpNormal = b2dBody.getWorldVector(up);
//		
//		angle = b2dBody.getAngle();
//		assert !Double.isNaN(angle);
//		
//		angularVel = b2dBody.getAngularVelocity();
//		
//		forwardVel = currentRightNormal.mul(Vec2.dot(currentRightNormal, vel));
//		forwardSpeed = Vec2.dot(vel, currentRightNormal);
//		
//		double angle = b2dBody.getAngle();
//		
//		shape = Geom.localToWorld(localAABB, angle, center);
	}
	
	public void setPhysicsTransform() {
//		synchronized (world.b2dWorld) {
//			b2dBody.setTransform(PhysicsUtils.vec2(center), (float)angle);
//		}
	}
	
	public void setB2dCollisions(boolean collisions) {
		
	}
	
	public void destroy() {
		
	}
	
	public void applyForwardImpulse(double coeff, double dv) {
		
	}
	
	public void applyAngularImpulse(double coeff, double dw) {
		
	}
	
	public void applyCancelingForwardImpulse(double coeff) {
		
	}
	
	public void applyCancelingLateralImpulse(double coeff) {
		
	}
	
	public void applyCancelingAngularImpulse(double coeff) {
		
	}
	
}
