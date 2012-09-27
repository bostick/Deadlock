package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Driveable;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Driveable hilited;
	
	public Point lastPointRaw;
	public List<Point> curStrokeRaw = new ArrayList<Point>();
	public List<List<Point>> allStrokes = new ArrayList<List<Point>>();
	
	public World world;
	
	public DeadlockModel() {
		
	}
	
	public void init() {
		world = new World();
	}
	
	public void clear() {
		lastPointRaw = null;
		curStrokeRaw.clear();
		allStrokes.clear();
		
		world = new World();
	}
	
	public void processNewStroke(List<Point> stroke) {
		world.processNewStroke(stroke);	
	}
	
	public void removeEdgeTop(Edge e) {
		world.removeEdgeTop(e);
	}
	
	public void removeVertexTop(Vertex i) {
		world.removeVertexTop(i);
	}
	
	public void addVertexTop(Point p) {
		world.addVertexTop(p);
	}
	
	
	public boolean checkConsistency() {
		return world.checkConsistency();
	}
	
	public ControlMode getMode() {
		assert Thread.holdsLock(MODEL);
		
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
}
