package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import org.apache.log4j.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.AnimatedExplosion;

public class CarEventListener implements ContactListener {
	
	static Logger logger = Logger.getLogger(CarEventListener.class);
	
	@Override
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
		
		WorldManifold worldManifold = new WorldManifold();
		c.getWorldManifold(worldManifold);
		
		if (a.state != CarStateEnum.CRASHED || b.state != CarStateEnum.CRASHED) {
			
			Vec2 p = worldManifold.points[0];
			
			MODEL.world.addExplosion(new AnimatedExplosion(Point.point(p)));
			
			if (a.state != CarStateEnum.CRASHED) {
				a.crash();
			}
			if (b.state != CarStateEnum.CRASHED) {
				b.crash();
			}
			
		}
		
	}
	
	@Override
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
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		;
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		;
	}
	
}
