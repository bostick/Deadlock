package com.gutabi.deadlock.world.physics;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.common.Vec2;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;

public abstract class PhysicsWorld {
	
	public WorldScreen worldScreen;
	
	public PhysicsDebugDraw debugDraw;
	
	public org.jbox2d.dynamics.World b2dWorld;
	
	public PhysicsWorld(WorldScreen worldScreen) {
		this.worldScreen = worldScreen;
		
		b2dWorld = new org.jbox2d.dynamics.World(new Vec2(0.0f, 0.0f), true);
		
		debugDraw = new PhysicsDebugDraw();
		
		b2dWorld.setContactListener(new CarEventListener(this));
		b2dWorld.setDebugDraw(debugDraw);
		
	}
	
	
	int velocityIterations = 6;
	int positionIterations = 2;
	
	public void step() {
		
		b2dWorld.step((float)worldScreen.DT, velocityIterations, positionIterations);
		
	}
	
	public int getBodyCount() {
		return b2dWorld.getBodyCount();
	}
	
	public void drawPhysicsDebug(RenderingContext ctxt) {
		
		debugDraw.ctxt = ctxt;
		
		b2dWorld.drawDebugData();
		
		debugDraw.ctxt = null;
		
	}
	
	public abstract void carCrash(Point p);
	
	public boolean intersectsPhysicsBodies(AABB aabb) {
		
		org.jbox2d.collision.AABB b2dAABB = new org.jbox2d.collision.AABB(PhysicsUtils.vec2(aabb.ul), PhysicsUtils.vec2(aabb.br));
		
		final boolean[] intersecting = new boolean[1];
		
		b2dWorld.queryAABB(new QueryCallback() {
			public boolean reportFixture(org.jbox2d.dynamics.Fixture fixture) {
				intersecting[0] = true;
				return false;
			}
		}, b2dAABB);
		
		return intersecting[0];
	}
	
}
