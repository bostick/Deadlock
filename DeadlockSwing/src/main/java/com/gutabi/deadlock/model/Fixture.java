package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.GraphPositionPath;
import com.gutabi.deadlock.core.graph.Side;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.model.car.FastCar;
import com.gutabi.deadlock.model.car.NormalCar;
import com.gutabi.deadlock.model.car.RandomCar;
import com.gutabi.deadlock.model.car.ReallyFastCar;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public final class Fixture extends Vertex {
	
	/*
	 * spawn cars every SPAWN_FREQUENCY seconds
	 * -1 means no spawning
	 */
	public static double SPAWN_FREQUENCY_SECONDS = 1.0;
	
	public final Axis a;
	
	public Side s;
	public FixtureType type;
	public Fixture match;
	
	public GraphPositionPath shortestPathToMatch;
	
	public double lastSpawnTime;
	
	public int outstandingCars;
	
	static int carIDCounter;
	
	public Fixture(Point p, Axis a) {
		super(p);
		this.a = a;
		hiliteColor = new Color(0, 255, 255);
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
			shortestPathToMatch = GraphPositionPath.createShortestPathFromSkeleton(poss);
			
			lastSpawnTime = -1;
			outstandingCars = 0;
			carIDCounter = 0;
		}
	}
	
	public void postStop() {
		
		carQueue.clear();
		
		if (type == FixtureType.SOURCE) {
			
			if (shortestPathToMatch != null) {
				shortestPathToMatch.currentCars.clear();
				shortestPathToMatch.sharedEdgesMap.clear();
			}
			
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
		GraphPositionPath path = GraphPositionPath.createRandomPathFromSkeleton(poss);
		return path;
	}
	
	private void spawnNewCar(double t) {
		
		Car c = createNewCar();
		
		if (c != null) {
//			assert c.hitTest(p, r);
			c.startingTime = t;
			c.id = carIDCounter;
			carIDCounter++;
			synchronized (MODEL) {
				MODEL.world.addCar(c);
			}
			lastSpawnTime = t;
			outstandingCars++;
		}
		
	}
	
	private boolean active(double t) {
		
//		if (outstandingCars > 0) {
//			return false;
//		}
		if (lastSpawnTime != -1 && (t - lastSpawnTime) < SPAWN_FREQUENCY_SECONDS) {
			return false;
		}
		
		double d = MODEL.world.distanceBetweenVertices(this, match);
		
		if (d == Double.POSITIVE_INFINITY) {
			return false;
		}
		
//		if (MODEL.world.isUnderAnyCars(this)) {
//			return false;
//		}
		
		if (!carQueue.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings({"rawtypes"})
	private Car createNewCar() {
		
		boolean normal = VIEW.controlPanel.normalCarButton.isSelected();
		boolean fast = VIEW.controlPanel.fastCarButton.isSelected();
//		boolean random = VIEW.controlPanel.randomCarButton.isSelected();
		boolean really = VIEW.controlPanel.reallyFastCarButton.isSelected();
		
		List<Class> l = new ArrayList<Class>();
		if (normal) {
			l.add(NormalCar.class);
		}
		if (fast) {
			l.add(FastCar.class);
		}
//		if (random) {
//			l.add(RandomCar.class);
//		}
		if (really) {
			l.add(ReallyFastCar.class);
		}
		
		if (l.isEmpty()) {
			return null;
		}
		
		int r = MODEL.world.RANDOM.nextInt(l.size());
		
		Class c = l.get(r);
		
		if (c == NormalCar.class) {
			return new NormalCar(this);
		} else if (c == FastCar.class) {
			return new FastCar(this);
		} else if (c == RandomCar.class) {
			return new RandomCar(this);
		} else if (c == ReallyFastCar.class) {
			return new ReallyFastCar(this);
		} else {
			throw new AssertionError();
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			if (!MODEL.DEBUG_DRAW) {
				
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
				
				ctxt.paintImage(-r, -r, VIEW.sheet,
						0,
						0,
						2 * VIEW.metersToPixels(r),
						2 * VIEW.metersToPixels(r),
						96, 224, 96+32, 224+32);
				
				ctxt.setTransform(origTransform);
				
			} else {
				
				ctxt.setColor(VIEW.LIGHTGREEN);
				shape.paint(ctxt);
				
				ctxt.setColor(Color.BLACK);
				ctxt.setPixelStroke(1);
				shape.getAABB().draw(ctxt);
				
			}
			break;
		case PREVIEW:
			ctxt.setColor(VIEW.LIGHTGREEN);
			
			shape.paint(ctxt);
			break;
		}
		
	}

}
