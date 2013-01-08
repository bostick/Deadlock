package com.gutabi.deadlock.world.cars;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.sprites.AnimatedExplosion;

public class CarEventListener implements ContactListener {
	
	World world;
	
//	static Logger logger = Logger.getLogger(CarEventListener.class);
	
	public CarEventListener(World world) {
		this.world = world;
	}
	
	public void beginContact(Contact contact) {
		
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		if (bodyA.getUserData() instanceof Car) {
			Car carA = (Car)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				beginContactCarCar(carA, carB, contact);
				
			} else {
				throw new AssertionError();
			}
		} else {
			throw new AssertionError();
		}
		
	}
	
	private void beginContactCarCar(Car a, Car b, Contact c) {
		
		if (a.state != CarStateEnum.CRASHED || b.state != CarStateEnum.CRASHED) {
			
			WorldManifold worldManifold = new WorldManifold();
			c.getWorldManifold(worldManifold);
			
			Vec2 p = worldManifold.points[0];
			
			world.explosionMap.add(new AnimatedExplosion(world, Point.point(p)));
			
			if (a.state != CarStateEnum.CRASHED) {
				a.crash();
			}
			if (b.state != CarStateEnum.CRASHED) {
				b.crash();
			}
			
		}
		
	}
	
	public void endContact(Contact contact) {
		
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		if (bodyA.getUserData() instanceof Car) {
			Car carA = (Car)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				endContactCarCar(carA, carB);
				
			} else {
				throw new AssertionError();
			}	
		} else {
			throw new AssertionError();
		}
		
	}
	
	private void endContactCarCar(Car a, Car b) {
		;
	}
	
	public void preSolve(Contact contact, Manifold oldManifold) {
		;
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
		;
	}
	
}
