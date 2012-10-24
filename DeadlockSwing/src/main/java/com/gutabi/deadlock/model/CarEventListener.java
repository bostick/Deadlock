package com.gutabi.deadlock.model;

import org.apache.log4j.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

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
				
				beginContactCarCar(carA, carB);
				
			} else {
				throw new AssertionError();
			}
		} else {
			throw new AssertionError();
		}
		
	}
	
	private void beginContactCarCar(Car a, Car b) {
		if (a.getState() != CarStateEnum.CRASHED) {
			a.crash();
		}
		if (b.getState() != CarStateEnum.CRASHED) {
			b.crash();
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
