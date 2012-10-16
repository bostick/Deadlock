package com.gutabi.deadlock.model;


import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import org.apache.log4j.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.GraphPosition;
import com.gutabi.deadlock.core.Vertex;

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
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				Vertex v = (Vertex)bodyB.getUserData();
				
				carA.incrementB2DVertexCount();
				
				if (carA.getB2DVertexCount() == 1) {
					beginContactCarVertex(carA, v);
				}
				
			} else {
				throw new AssertionError();
			}
		} else if (bodyA.getUserData() instanceof Vertex) {
			Vertex v = (Vertex)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				carB.incrementB2DVertexCount();
				
				if (carB.getB2DVertexCount() == 1) {
					beginContactCarVertex(carB, v);
				}
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				throw new AssertionError();
			} else {
				throw new AssertionError();
			}
		} else if (bodyA.getUserData() instanceof Edge) {
			Edge e = (Edge)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				carB.incrementB2DEdgeCount();
				
				if (carB.getB2DEdgeCount() == 1) {
					beginContactCarEdge(carB, e);
				}
				
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
//			logger.debug("vertex spawn " + v.id);
			c.state = CarStateEnum.RUNNING;
		} else {
//			logger.debug("vertex begin " + v.id);
		}
		
	}
	
	private void beginContactCarEdge(Car c, Edge e) {
		
		if (c.getState() == CarStateEnum.NEW) {
//			logger.debug("edge spawn " + e.id + " " + c.getB2DEdgeCount());
		} else {
//			logger.debug("edge begin " + e.id + " " + c.getB2DEdgeCount());
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
				
				carA.decrementB2DVertexCount();
				
				if (carA.getB2DVertexCount() == 0) {
					endContactCarVertex(carA, v);
				}
				
			} else {
				throw new AssertionError();
			}	
		} else if (bodyA.getUserData() instanceof Vertex) {
			Vertex v = (Vertex)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData();
				
				carB.decrementB2DVertexCount();
				
				if (carB.getB2DVertexCount() == 0) {
					endContactCarVertex(carB, v);
				}
				
			} else if (bodyB.getUserData() instanceof Vertex) {
				throw new AssertionError();
			} else {
				throw new AssertionError();
			}
		} else if (bodyA.getUserData() instanceof Edge) {
			Edge e = (Edge)bodyA.getUserData();
			
			if (bodyB.getUserData() instanceof Car) {
				Car carB = (Car)bodyB.getUserData(); 
				
				carB.decrementB2DEdgeCount();
				
				if (carB.getB2DEdgeCount() == 0) {
					endContactCarEdge(carB, e);
				}
				
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
//			logger.debug("vertex sinked " + v.id);
		} else {
//			logger.debug("vertex end " + v.id);
		}
		
//		logger.debug("vertex radius: " + v.getRadius() + " " + v.b2dShape.m_radius);
		
		GraphPosition closest = MODEL.world.graph.findClosestGraphPosition(c.getPoint(), null, Double.POSITIVE_INFINITY);
		
		MODEL.world.b2dWorld.raycast(c, c.getPoint().vec2(), closest.getPoint().vec2());
		
	}
	
	private void endContactCarEdge(Car c, Edge e) {
		
		if (c.getState() == CarStateEnum.SINKED) {
//			logger.debug("edge sinked " + e.id + " " + c.getB2DEdgeCount());
		} else {
//			logger.debug("edge end " + e.id + " " + c.getB2DEdgeCount());
		}
		
		GraphPosition closest = MODEL.world.graph.findClosestGraphPosition(c.getPoint(), null, Double.POSITIVE_INFINITY);
		
		MODEL.world.b2dWorld.raycast(c, c.getPoint().vec2(), closest.getPoint().vec2());
		
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
