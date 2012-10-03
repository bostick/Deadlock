package com.gutabi.deadlock.b2d;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;


public class TopDownCar {
	
	static BufferedImage im;
	
	static Ground ground;
	static Car car1;
	static Car car2;
	
	static int i;
	
	static CListener listener = new CListener();
	
	public static void main(String[] args) throws Exception {
		
		im = ImageIO.read(new File("media\\normalCar.png"));
		
		ground = new Ground();
		ground.x = 0.0f;
		ground.y = -10.0f;
		ground.halfWidth = 50;
		ground.halfHeight = 10;
		
		car1 = new Car();
		car1.x = -25.0f;
		car1.y = 30.0f;
		car1.rad = 0.4f;
		car1.halfWidth = 2.0f;
		car1.halfHeight = 2.0f;
		
		car2 = new Car();
		car2.x = 0.0f;
		car2.y = 4.0f;
		car2.rad = 0.1f;
		car2.halfWidth = 2.0f;
		car2.halfHeight = 2.0f;
		
		JFrame frame = new JFrame("Deadlock Viewer");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		WorldPanel panel = new WorldPanel();
		panel.setFocusable(true);
		
		Container cp = frame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(panel);
		
		frame.pack();
		
		frame.setSize(800, 600);
		
		// Make a World
		//
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		boolean doSleep = true;
		World world = new World(gravity, doSleep);
		
		// Make a Body for the ground via definition and shape binding that gives it a boundary
		// 
		BodyDef groundBodyDef = new BodyDef(); // body definition
		groundBodyDef.position.set(ground.x, ground.y); // set bodydef position
		Body groundBody = world.createBody(groundBodyDef); // create body based on definition
		groundBody.setUserData(ground);
		PolygonShape groundBox = new PolygonShape(); // make a shape representing ground
		groundBox.setAsBox(ground.halfWidth, ground.halfHeight); // shape is a rect: 100 wide, 20 high
		groundBody.createFixture(groundBox, 0.0f); // bind shape to ground body
		
		// Make another Body that is dynamic, and will be subject to forces.
		//
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC; // dynamic means it is subject to forces
		bodyDef.position.set(car1.x, car1.y);
		bodyDef.angle = car1.rad;
		bodyDef.bullet = true;
		Body body = world.createBody(bodyDef);
		body.setUserData(car1);
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(car1.halfWidth, car1.halfHeight);
		FixtureDef fixtureDef = new FixtureDef(); // fixture def that we load up with the following info:
		fixtureDef.shape = dynamicBox; // ... its shape is the dynamic box (2x2 rectangle)
		fixtureDef.density = 1.0f; // ... its density is 1 (default is zero)
		fixtureDef.friction = 1f; // ... its surface has some friction coefficient
		body.createFixture(fixtureDef); // bind the dense, friction-laden fixture to the body
		
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyType.DYNAMIC; // dynamic means it is subject to forces
		bodyDef2.position.set(car2.x, car2.y);
		bodyDef2.angle = car2.rad;
		bodyDef2.bullet = true;
		Body body2 = world.createBody(bodyDef2);
		body2.setUserData(car2);
		PolygonShape dynamicBox2 = new PolygonShape();
		dynamicBox2.setAsBox(car2.halfWidth, car2.halfHeight);
		FixtureDef fixtureDef2 = new FixtureDef(); // fixture def that we load up with the following info:
		fixtureDef2.shape = dynamicBox2; // ... its shape is the dynamic box (2x2 rectangle)
		fixtureDef2.density = 1.0f; // ... its density is 1 (default is zero)
		fixtureDef2.friction = 1f; // ... its surface has some friction coefficient
		body2.createFixture(fixtureDef2); // bind the dense, friction-laden fixture to the body
		
		frame.setVisible(true);
		
		// Simulate the world
		//
		
		world.setContactListener(listener);
		
		
		float c = 10.0f;
		
		Vec2 v1 = new Vec2(1.0f, 0.0f);
		v1.mulLocal(c);
		Vec2 v2 = new Vec2(1.0f, -1.0f);
		v2.normalize();
		v2.mulLocal(c);
		Vec2 v3 = new Vec2(0.0f, -1.0f);
		v3.mulLocal(c);
		
		float timeStep = 1.0f / 60.f;
		int velocityIterations = 6;
		int positionIterations = 2;
		for (i = 0; i < 5000; ++i) {
			
			if (i == 0) {
				Vec2 v = body.getWorldCenter();
				//body.applyLinearImpulse(v1, v);
				body.setLinearVelocity(v1);
			} else if (i == 100) {
				Vec2 v = body.getWorldCenter();
				//body.applyLinearImpulse(v1.negate(), v);
				//body.applyLinearImpulse(v2, v);
				body.setLinearVelocity(v2);
			} else if (i == 200) {
				Vec2 v = body.getWorldCenter();
				//body.applyLinearImpulse(v2.negate(), v);
				//body.applyLinearImpulse(v3, v);
				body.setLinearVelocity(v3);
			}
			
			world.step(timeStep, velocityIterations, positionIterations);
		  
			Vec2 position = body.getPosition();
			float angle = body.getAngle();
			car1.x = position.x;
			car1.y = position.y;
			car1.rad = angle;
			
			position = body2.getPosition();
			angle = body2.getAngle();
			car2.x = position.x;
			car2.y = position.y;
			car2.rad = angle;
			
			panel.repaint();
			Thread.sleep(16);
			
		}
		
		
	}
	
	static class Ground {
		float x;
		float y;
		float halfWidth;
		float halfHeight;
		Color color = Color.GREEN;
	}
	
	static class Car {
		float x;
		float y;
		float rad;
		float halfWidth;
		float halfHeight;
		Color color = Color.BLUE;
	}
	
	static class CListener implements ContactListener {

		@Override
		public void beginContact(Contact contact) {
			
			if (contact.getFixtureA().getBody().getUserData() instanceof Car) {
				Body body = contact.getFixtureA().getBody();
				Car carA = (Car)body.getUserData();
				carA.color = Color.ORANGE;
				
				body.setLinearDamping(1.0f);
				body.setAngularDamping(1.0f);
				
			}
			
			if (contact.getFixtureB().getBody().getUserData() instanceof Car) {
				Body body = contact.getFixtureB().getBody();
				Car carB = (Car)body.getUserData();
				carB.color = Color.ORANGE;
				
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
	
	static class WorldPanel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
//			Dimension d = getSize();
			
			Graphics2D g2 = (Graphics2D)g;
			
			g2.drawString("i: " + i, 10, 10);
			
			AffineTransform orig = g2.getTransform();
			
			g2.setColor(ground.color);
			
			g2.translate(400.0d, 300d);
			g2.scale(8.0d, -6.0d);
			
			g2.translate(ground.x, ground.y);
			
			g2.fillRect((int)(-ground.halfWidth), (int)(-ground.halfHeight), (int)(2*ground.halfWidth), (int)(2*ground.halfHeight));
			
			g2.setTransform(orig);
			
			orig = g2.getTransform();
			
			g2.setColor(car1.color);
			
			g2.translate(400.0d, 300d);
			g2.scale(8.0d, -6.0d);
			
			g2.translate(car1.x, car1.y);
			g2.rotate(car1.rad);
			g2.fillRect((int)(-car1.halfWidth), (int)(-car1.halfHeight), (int)(2*car1.halfWidth), (int)(2*car1.halfHeight));
			
			g2.setTransform(orig);
			
			orig = g2.getTransform();
			
			g2.setColor(car2.color);
			
			g2.translate(400.0d, 300d);
			g2.scale(8.0d, -6.0d);
			
			g2.translate(car2.x, car2.y);
			g2.rotate(car2.rad);
			g2.fillRect((int)(-car2.halfWidth), (int)(-car2.halfHeight), (int)(2*car2.halfWidth), (int)(2*car2.halfHeight));
			
			g2.setTransform(orig);
		}
		
	}
}
