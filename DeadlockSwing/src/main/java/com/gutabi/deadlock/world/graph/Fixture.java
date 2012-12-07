package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.ProgressMeter;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.car.Car;
import com.gutabi.deadlock.world.car.FastCar;
import com.gutabi.deadlock.world.car.NormalCar;
import com.gutabi.deadlock.world.car.RandomCar;
import com.gutabi.deadlock.world.car.ReallyFastCar;

@SuppressWarnings("static-access")
public final class Fixture extends Vertex {
	
	/*
	 * spawn cars every SPAWN_FREQUENCY seconds
	 * -1 means no spawning
	 */
	public static double SPAWN_FREQUENCY_SECONDS = 1.0;
	
	public final Axis a;
	
	private Side s;
	private FixtureType type;
	public Fixture match;
	
	public GraphPositionPath shortestPathToMatch;
	
	public double lastSpawnTime;
	
	public int outstandingCars;
	
	
	
	private org.jbox2d.collision.AABB b2dAABB;
	private ProgressMeter progress;
	
	
	
	static int carIDCounter;
	
	public Fixture(Point p, Axis a) {
		super(p);
		this.a = a;
		hiliteColor = new Color(0, 255, 255);
	}
	
	public void setType(FixtureType type) {
		this.type = type;
		
		if (type == FixtureType.SOURCE) {
			
			b2dAABB = new org.jbox2d.collision.AABB(shape.aabb.ul.vec2(), shape.aabb.br.vec2());
			progress = new ProgressMeter(p.x - r - 1.5, p.y - r, 2, 0.5);
			
		} else {
			
			b2dAABB = null;
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
	
	public void computeRadius(double maximumRadius) {
		super.computeRadius(maximumRadius);
		if (type == FixtureType.SOURCE) {
			
			b2dAABB = new org.jbox2d.collision.AABB(shape.aabb.ul.vec2(), shape.aabb.br.vec2());
			
		}
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
			synchronized (APP) {
				APP.world.addCar(c);
			}
			lastSpawnTime = t;
			outstandingCars++;
		}
		
	}
	
	
	private boolean overlapping;
	
	private QueryCallback qc = new QueryCallback() {
		public boolean reportFixture(org.jbox2d.dynamics.Fixture fixture) {
			overlapping = true;
			return false;
		}
	};
	
	private boolean active(double t) {
		
		double d = APP.world.distanceBetweenVertices(this, match);
		
		if (d == Double.POSITIVE_INFINITY) {
			return false;
		}
		
//		if (outstandingCars > 0) {
//			return false;
//		}
		if (lastSpawnTime != -1 && (t - lastSpawnTime) < SPAWN_FREQUENCY_SECONDS) {
			return false;
		}
		
		if (!carQueue.isEmpty()) {
			return false;
		}
		
		overlapping = false;
		APP.world.b2dWorld.queryAABB(qc, b2dAABB);
		if (overlapping) {
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
		
		int r = APP.world.RANDOM.nextInt(l.size());
		
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
				
				ctxt.paintWorldImage(-r, -r, VIEW.sheet,
						0,
						0,
						2 * (int)Math.round(APP.world.PIXELS_PER_METER_DEBUG * r),
						2 * (int)Math.round(APP.world.PIXELS_PER_METER_DEBUG * r),
						96, 224, 96+32, 224+32);
				
				ctxt.setTransform(origTransform);
				
			} else {
				
				ctxt.setColor(VIEW.LIGHTGREEN);
				shape.paint(ctxt);
				
				ctxt.setColor(Color.BLACK);
				ctxt.setWorldPixelStroke(1);
				shape.getAABB().draw(ctxt);
				
			}
			break;
		case PREVIEW:
			ctxt.setColor(VIEW.LIGHTGREEN);
			
			shape.paint(ctxt);
			break;
		}
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		
		if (type == FixtureType.SOURCE) {
			progress.paint(ctxt);
		}
		
	}
	
}
