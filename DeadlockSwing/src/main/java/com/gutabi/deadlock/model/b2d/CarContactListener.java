package com.gutabi.deadlock.model.b2d;

import org.apache.log4j.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.CarStateEnum;

public class CarContactListener implements ContactListener {
	
	static Logger logger = Logger.getLogger(CarContactListener.class);
	
	@Override
	public void beginContact(Contact contact) {
		
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		
		if (bodyA.getUserData() instanceof Car) {
			Car carA = (Car)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				beginContactCarCar(carA, carB);
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				Vertex v = (Vertex)bodyB.getUserData();
				
				beginContactCarVertex(carA, v);
				
			} else {
				throw new AssertionError();
			}
		} else if (bodyA.getUserData() instanceof Vertex) {
			Vertex v = (Vertex)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				beginContactCarVertex(carB, v);
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				throw new AssertionError();
			} else {
				throw new AssertionError();
			}
		} else {
			throw new AssertionError();
		}
		
	}
	
	private void beginContactCarCar(Car a, Car b) {
		a.crash();
		b.crash();
	}
	
	private void beginContactCarVertex(Car c, Vertex v) {
		
		if (c.getState() == CarStateEnum.NEW) {
			logger.debug("vertex spawn " + v.id + " " + Point.distance(c.getPoint(), v.getPoint()));
			c.state = CarStateEnum.RUNNING;
		} else {
			logger.debug("vertex begin " + v.id + " " + Point.distance(c.getPoint(), v.getPoint()));
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
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				Vertex v = (Vertex)bodyB.getUserData();
				
				endContactCarVertex(carA, v);
				
			} else {
				throw new AssertionError();
			}	
		} else if (bodyA.getUserData() instanceof Vertex) {
			Vertex v = (Vertex)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				endContactCarVertex(carB, v);
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				throw new AssertionError();
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
	
	private void endContactCarVertex(Car c, Vertex v) {
		
		if (c.getState() == CarStateEnum.SINKED) {
			logger.debug("vertex sinked " + v.id + " " + Point.distance(c.getPoint(), v.getPoint()));
		} else {
			logger.debug("vertex end " + v.id + " " + Point.distance(c.getPoint(), v.getPoint()));
		}
		
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
