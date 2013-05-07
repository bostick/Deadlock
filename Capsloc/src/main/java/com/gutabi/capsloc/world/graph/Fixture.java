package com.gutabi.capsloc.world.graph;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.gutabi.capsloc.geom.ShapeUtils;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.ProgressMeter;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.cars.AutonomousCar;
import com.gutabi.capsloc.world.cars.AutonomousDriver;
import com.gutabi.capsloc.world.sprites.CarSheet;
import com.gutabi.capsloc.world.sprites.CarSheet.CarType;

public final class Fixture extends Vertex {
	
	public static final Color FIXTURECOLOR = new Color(0xE1, 0xE1, 0xE1, 255);
	
	public static double SPAWN_FREQUENCY_SECONDS = 1500.5;
	
	public final Axis a;
	
	private Side facingSide;
	private FixtureType type;
	
	public int matchID;
	public Fixture match;
	
	public GraphPositionPath shortestPathToMatch;
	public double lastSpawnTime;
	public int outstandingCars;
	private ProgressMeter progress;
	
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
	
	public void setFacingSide(Side facingSide) {
		this.facingSide = facingSide;
	}
	
	public Side getFacingSide() {
		return facingSide;
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
			shortestPathToMatch = world.graph.pathFactory.createShortestVertexPath(poss);
			
			lastSpawnTime = -1;
			outstandingCars = 0;
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
	
	public boolean preStep(double t) {
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
		
		return false;
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
		GraphPositionPath path = world.graph.pathFactory.createRandomVertexPath(poss);
		return path;
	}
	
	
	AutonomousCar waitingCar;
	
	private void spawnNewCar(double t) {
		
		AutonomousCar c;
		if (waitingCar == null) {
			c = createNewCar();
		} else {
			c = waitingCar;
		}
		
		boolean intersecting = world.intersectsPhysicsBodies(c.shape.aabb);
		
		if (intersecting) {
			
			waitingCar = c;
			
			return;
		}
		
		waitingCar = null;
		
		c.physicsInit();
		c.computeDynamicPropertiesAlways();
		c.computeDynamicPropertiesMoving();
		
		driverQueue.add((AutonomousDriver)c.driver);
		
		if (c != null) {
			c.startingTime = t;
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
		
		boolean carIntersecting = world.intersectsPhysicsBodies(shape.getAABB());
		
		if (carIntersecting) {
			return false;
		}
		
		return true;
	}
	
	private AutonomousCar createNewCar() {
		
		int r = APP.RANDOM.nextInt(12);
		CarType type = CarSheet.sprite(r).type();
		
		AutonomousCar c = AutonomousCar.createCar(world, type, this, r);
		
		int randomSpeed = APP.RANDOM.nextInt(3);
		
		switch (randomSpeed) {
		case 0:
			c.engine.maxSpeed = 2.5;
			break;
		case 1:
			c.engine.maxSpeed = 5.0;
			break;
		case 2:
			c.engine.maxSpeed = 10.0;
			break;
		}
		
		if (c.engine.maxSpeed == 2.5) {
			((AutonomousDriver)c.driver).carProximityLookahead = 2.0;
		} else if (c.engine.maxSpeed == 5.0) {
			((AutonomousDriver)c.driver).carProximityLookahead = 2.0;
		} else if (c.engine.maxSpeed == 10.0) {
			((AutonomousDriver)c.driver).carProximityLookahead = 2.25;
		} else {
			assert false;
		}
		
		if (c.engine.maxSpeed == 2.5) {
			((AutonomousDriver)c.driver).vertexArrivalLookahead = 0.95;
		} else if (c.engine.maxSpeed == 5.0) {
			((AutonomousDriver)c.driver).vertexArrivalLookahead = 1.30;
		} else if (c.engine.maxSpeed == 10.0) {
			((AutonomousDriver)c.driver).vertexArrivalLookahead = 2.00;
		} else {
			assert false;
		}
		
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
		Side facingSide = null;
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
			facingSide = Side.fromFileString(rest);
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
		f.facingSide = facingSide;
		f.setType(type);
		
		return f;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
//		if (!ShapeUtils.intersectAA(shape.getAABB(), ctxt.cam.worldViewport)) {
//			return;
//		}
		
		if (!APP.DEBUG_DRAW) {
			
			ctxt.setColor(FIXTURECOLOR);
			shape.paint(ctxt);
			
		} else {
			ctxt.setColor(FIXTURECOLOR);
			shape.paint(ctxt);
			
			ctxt.setColor(Color.BLACK);
			ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
			shape.getAABB().draw(ctxt);
		}
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		ctxt.setColor(FIXTURECOLOR);
		shape.paint(ctxt);
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setColor(Color.fixtureHiliteColor);
		shape.paint(ctxt);
	}
	
	public void paintScene(RenderingContext ctxt) {
		
		if (!ShapeUtils.intersectAA(shape.getAABB(), ctxt.cam.worldViewport)) {
			return;
		}
		
		if (type == FixtureType.SOURCE) {
			progress.paint(ctxt);
		}
		
	}
	
}
