package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.path.GraphPositionPath;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.FastCar;
import com.gutabi.deadlock.model.NormalCar;
import com.gutabi.deadlock.model.RandomCar;

@SuppressWarnings("static-access")
public class Source extends Vertex {
	
	/*
	 * spawn cars every SPAWN_FREQUENCY seconds
	 * -1 means no spawning
	 */
	public static double SPAWN_FREQUENCY_SECONDS = 10.0;
	
	public Sink matchingSink;
	GraphPositionPath shortestPathToMatchingSink;
	
	public double lastSpawnTime;
	
	public int outstandingCars;
	
	public Source(Point p) {
		super(p);
		color = Color.GREEN;
		hiliteColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public boolean isDeleteable() {
		return false;
	}
	
	public void preStart() {
		
		assert matchingSink != null;
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		shortestPathToMatchingSink = GraphPositionPath.createShortestPathFromSkeleton(poss);
		
		lastSpawnTime = -1;
		outstandingCars = 0;
	}
	
	public GraphPositionPath getShortestPathToMatchingSink() {
		return shortestPathToMatchingSink;
	}
	
	public GraphPositionPath getRandomPathToMatchingSink() {
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(this);
		poss.add(matchingSink);
		GraphPositionPath path = GraphPositionPath.createRandomPathFromSkeleton(poss);
		return path;
	}
	
	public void preStep(double t) {
//		if (SPAWN_FREQUENCY_SECONDS > 0 && (t == 0 || (t - lastSpawnTime) >= SPAWN_FREQUENCY_SECONDS)) {
//			if (active()) {
//				spawnNewCar(t);
//			}
//		}
		if (active()) {
			spawnNewCar(t);
		}
	}
	
	private void spawnNewCar(double t) {
		
		Car c = createNewCar();
		
		if (c != null) {
			assert c.hitTest(p, r);
			c.startingTime = t;
			synchronized (MODEL) {
				MODEL.world.cars.add(c);
			}
			lastSpawnTime = t;
			outstandingCars++;
		}
		
	}
	
	private boolean active() {
		
		if (outstandingCars > 0) {
			return false;
		}
		
		double d = MODEL.world.distanceBetweenVertices(this, matchingSink);
		
		if (d == Double.POSITIVE_INFINITY) {
			return false;
		}
		
		synchronized (MODEL) {
			for (Car c : MODEL.world.cars) {
				if (hitTest(c.worldPoint1())) {
					return false;
				}
				if (hitTest(c.worldPoint2())) {
					return false;
				}
				if (hitTest(c.worldPoint3())) {
					return false;
				}
				if (hitTest(c.worldPoint4())) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	private Car createNewCar() {
		
		boolean normal = VIEW.controlPanel.normalCarButton.isSelected();
		boolean fast = VIEW.controlPanel.fastCarButton.isSelected();
		boolean random = VIEW.controlPanel.randomCarButton.isSelected();
		
		List<Class> l = new ArrayList<Class>();
		if (normal) {
			l.add(NormalCar.class);
		}
		if (fast) {
			l.add(FastCar.class);
		}
		if (random) {
			l.add(RandomCar.class);
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
		} else {
			throw new AssertionError();
		}
		
	}

	
	public boolean postStep() {
		return true;
	}
	
	
	/**
	 * 
	 * @param g2 in pixels, <0, 0> is world origin
	 */
	public void paintID(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		
		Point worldPoint = p.minus(new Point(getRadius(), 0));
		Point panelPoint = worldPoint.multiply(MODEL.PIXELS_PER_METER);
		
		g2.drawString(Integer.toString(id), (int)(panelPoint.x), (int)(panelPoint.y));
		
		g2.drawString(Integer.toString(outstandingCars), (int)(panelPoint.x + 10), (int)(panelPoint.y));
	}
	
}
