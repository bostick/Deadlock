package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.ProgressMeter;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.Car;

public final class Fixture extends Vertex {
	
	public static double SPAWN_FREQUENCY_SECONDS = 1500.5;
	
	public final Axis a;
	
	private Side s;
	private FixtureType type;
	
	public int matchID;
	public Fixture match;
	
	public GraphPositionPath shortestPathToMatch;
	public double lastSpawnTime;
	public int outstandingCars;
	private ProgressMeter progress;
	
	static int carIDCounter;
	
	public Fixture(World w, Point p, Axis a) {
		super(w, p);
		
		assert p != null;
		assert a != null;
		
		this.world = w;
		this.a = a;
	}
	
	public void setType(FixtureType type) {
		this.type = type;
		
		if (type == FixtureType.SOURCE) {
			progress = new ProgressMeter(p.x - r - 1.5, p.y - r, 2.0, 0.5);
		} else {
			progress = null;
		}
		
	}
	
	public FixtureType getType() {
		return type;
	}
	
	public void setSide(Side s) {
		this.s = s;
	}
	
	public Side getSide() {
		return s;
	}
	
	public final boolean supportsStopSigns() {
		return false;
	}
	
	public final boolean isUserDeleteable() {
		return false;
	}
	
	public void preStart() {
		if (type == FixtureType.SOURCE) {
			assert match != null;
			
			List<Vertex> poss = new ArrayList<Vertex>();
			poss.add(this);
			poss.add(match);
			shortestPathToMatch = world.graph.pathFactory.createShortestPathFromSkeleton(poss);
			
			lastSpawnTime = -1;
			outstandingCars = 0;
			carIDCounter = 0;
		}
	}
	
	public void postStop() {
		
		driverQueue.clear();
		
		if (type == FixtureType.SOURCE) {
			
			if (shortestPathToMatch != null) {
				shortestPathToMatch.currentDrivers.clear();
				shortestPathToMatch.sharedEdgesMap.clear();
			}
			
			progress.setProgress(0.0);
			
		}
		
	}
	
	public void preStep(double t) {
		if (type == FixtureType.SOURCE) {
//			if (SPAWN_FREQUENCY_SECONDS > 0 && (t == 0 || (t - lastSpawnTime) >= SPAWN_FREQUENCY_SECONDS)) {
	//			if (active()) {
	//				spawnNewCar(t);
	//			}
	//		}
		
			if (shortestPathToMatch != null) {
				shortestPathToMatch.precomputeHitTestData();
			}
			
			progress.setProgress((t - lastSpawnTime) / SPAWN_FREQUENCY_SECONDS);
			
			if (active(t)) {
				spawnNewCar(t);
			}
		}
	}
	
	public boolean postStep(double t) {
		if (shortestPathToMatch != null) {
			shortestPathToMatch.clearHitTestData();
		}
		return true;
	}
	
	public GraphPositionPath getShortestPathToMatch() {
		return shortestPathToMatch;
	}
	
	public GraphPositionPath getRandomPathToMatch() {
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(match);
		GraphPositionPath path = world.graph.pathFactory.createRandomPathFromSkeleton(poss);
		return path;
	}
	
	
	Car waitingCar;
	
	private void spawnNewCar(double t) {
		
		Car c;
		if (waitingCar == null) {
			c = createNewCar();
		} else {
			c = waitingCar;
		}
		
		boolean intersecting = world.carMap.intersect(c.shape.aabb);
		
		if (intersecting) {
			
			waitingCar = c;
			
			return;
		}
		
		waitingCar = null;
		
		c.b2dInit();
		c.computeDynamicPropertiesAlways();
		c.computeDynamicPropertiesMoving();
		
		driverQueue.add(c.driver);
		
		if (c != null) {
			c.startingTime = t;
			c.id = carIDCounter;
			carIDCounter++;
			synchronized (APP) {
				world.carMap.addCar(c);
			}
			lastSpawnTime = t;
			outstandingCars++;
		}
		
	} 
	
	private boolean active(double t) {
		
		double d = world.graph.distanceBetweenVertices(this, match);
		
		if (d == Double.POSITIVE_INFINITY) {
			return false;
		}
		
//		if (outstandingCars > 0) {
//			return false;
//		}
		if (lastSpawnTime != -1 && (t - lastSpawnTime) < SPAWN_FREQUENCY_SECONDS) {
			return false;
		}
		
		if (!driverQueue.isEmpty()) {
			return false;
		}
		
		boolean carIntersecting = world.carMap.intersect(shape.aabb);
		
		if (carIntersecting) {
			return false;
		}
		
		return true;
	}
	
	private Car createNewCar() {
		
//		List<Class> l = new ArrayList<Class>();
//		if (APP.NORMALCAR) {
//			l.add(NormalCar.class);
//		}
//		if (APP.FASTCAR) {
//			l.add(FastCar.class);
//		}
////		if (random) {
////			l.add(RandomCar.class);
////		}
//		if (APP.REALLYCAR) {
//			l.add(ReallyFastCar.class);
//		}
//		if (APP.TRUCK) {
//			l.add(Truck.class);
//		}
//		
//		
//		if (l.isEmpty()) {
//			return null;
//		}
		
		int r = APP.RANDOM.nextInt(12);
		
		Car c = new Car(world, this);
		
		switch (r) {
		case 0:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 0;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 1:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 32;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 2:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 64;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 1.0;
			c.CAR_WIDTH = 0.5;
			break;
		case 3:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 128;
			c.sheetRowStart = 96;
			c.sheetRowEnd = c.sheetRowStart + 64;
			c.CAR_LENGTH = 4.0;
			c.CAR_WIDTH = 2.0;
			break;
		case 4:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 160;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 5:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 192;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 6:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 224;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 7:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 256;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 8:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 288;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 9:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 320;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 10:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 96;
			c.sheetRowStart = 352;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 3.0;
			c.CAR_WIDTH = 1.0;
			break;
		case 11:
			c.sheetColStart = 0;
			c.sheetColEnd = c.sheetColStart + 64;
			c.sheetRowStart = 384;
			c.sheetRowEnd = c.sheetRowStart + 32;
			c.CAR_LENGTH = 2.0;
			c.CAR_WIDTH = 1.0;
			break;
		}
		
		r = APP.RANDOM.nextInt(3);
		
		switch (r) {
		case 0:
			c.maxSpeed = 2.5;
			break;
		case 1:
			c.maxSpeed = 5.0;
			break;
		case 2:
			c.maxSpeed = 10.0;
			break;
		}
		
		if (c.maxSpeed == 2.5) {
			c.driver.carProximityLookahead = 2.0;
		} else if (c.maxSpeed == 5.0) {
			c.driver.carProximityLookahead = 2.0;
		} else if (c.maxSpeed == 10.0) {
			c.driver.carProximityLookahead = 2.25;
		} else {
			assert false;
		}
		
		if (c.maxSpeed == 2.5) {
			c.driver.vertexArrivalLookahead = 0.95;
		} else if (c.maxSpeed == 5.0) {
			c.driver.vertexArrivalLookahead = 1.30;
		} else if (c.maxSpeed == 10.0) {
			c.driver.vertexArrivalLookahead = 2.00;
		} else {
			assert false;
		}
		
		c.computeCtorProperties();
		c.computeStartingProperties();
		
		return c;
	}
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("start fixture\n");
		
		s.append("id " + id + "\n");
		
		s.append("point " + p.toFileString() + "\n");
		s.append("axis " + a + "\n");
		s.append("side " + this.s + "\n");
		s.append("type " + type + "\n");
		s.append("match " + match.id + "\n");
		
		s.append("end fixture\n");
		
		return s.toString();
	}
	
	public static Fixture fromFileString(World world, String s) {
		BufferedReader r = new BufferedReader(new StringReader(s));
		
		Point p = null;
		Axis a = null;
		int id = -1;
		Side side = null;
		FixtureType type = null;
		int match = -1;
		
		try {
			String l = r.readLine();
			assert l.equals("start fixture");
			
			l = r.readLine();
			Scanner sc = new Scanner(l);
			String tok = sc.next();
			assert tok.equals("id");
			id = sc.nextInt();
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("point");
			
			String rest = sc.useDelimiter("\\A").next();
			sc.close();
			
			p = Point.fromFileString(rest);
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("axis");
			rest = sc.next();
			a = Axis.fromFileString(rest);
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("side");
			rest = sc.next();
			side = Side.fromFileString(rest);
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("type");
			rest = sc.next();
			type = FixtureType.fromFileString(rest);
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("match");
			match = sc.nextInt();
			sc.close();
			
			l = r.readLine();
			assert l.equals("end fixture");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Fixture f = new Fixture(world, p, a);
		
		f.id = id;
		f.matchID = match;
		f.s = side;
		f.setType(type);
		
		return f;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		if (!APP.DEBUG_DRAW) {
			
			AffineTransform origTransform = ctxt.getTransform();
			
			ctxt.translate(p.x, p.y);
			
			switch (s) {
			case TOP:
				ctxt.rotate(3 * Math.PI / 2);
				break;
			case LEFT:
				ctxt.rotate(Math.PI);
				break;
			case RIGHT:
				ctxt.rotate(0.0);
				break;
			case BOTTOM:
				ctxt.rotate(Math.PI / 2);
				break;
			}
			
			ctxt.translate(-r, -r);
			ctxt.paintImage(APP.spriteSheet, world.screen.pixelsPerMeter,
					0, 0, 2 * r, 2 * r,
					96, 0, 96+32, 0+32);
			
			ctxt.setTransform(origTransform);
			
		} else {
			ctxt.setColor(APP.LIGHTGREEN);
			shape.paint(ctxt);
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStrokeWidth(0.0);
			shape.getAABB().draw(ctxt);
		}
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		ctxt.setColor(APP.LIGHTGREEN);
		shape.paint(ctxt);
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(APP.fixtureHiliteColor);
		shape.paint(ctxt);
	}
	
	public void paintScene(RenderingContext ctxt) {
		
		if (type == FixtureType.SOURCE) {
			progress.paint(ctxt);
		}
		
	}
	
}
