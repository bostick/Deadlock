package com.gutabi.deadlock.model.b2d;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarStateEnum;

public class CarContactListener implements ContactListener {
	
	@Override
	public void beginContact(Contact contact) {
		
		if (contact.getFixtureA().getBody().getUserData() instanceof Car) {
			Body body = contact.getFixtureA().getBody();
			Car carA = (Car)body.getUserData();
			
			carA.state = CarStateEnum.CRASHED;
			
			body.setLinearDamping(1.0f);
			body.setAngularDamping(1.0f);
			
		}
		
		if (contact.getFixtureB().getBody().getUserData() instanceof Car) {
			Body body = contact.getFixtureB().getBody();
			Car carB = (Car)body.getUserData();
			
			carB.state = CarStateEnum.CRASHED;
			
			body.setLinearDamping(1.0f);
			body.setAngularDamping(1.0f);
			
		}
		
	}

	@Override
	public void endContact(Contact contact) {
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
