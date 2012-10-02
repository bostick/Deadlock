package com.gutabi.deadlock.model;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Driveable;
import com.gutabi.deadlock.core.Point;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public World world;
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Driveable hilited;
	
	public Point lastPanelPoint;
	public List<Point> curPanelStroke = new ArrayList<Point>();
	public List<List<Point>> allPanelStrokes = new ArrayList<List<Point>>();
	
	public DeadlockModel() {
		
	}
	
	public void init() {
		
		world = new World();
		
	}
	
	public void clear() {
		lastPanelPoint = null;
		curPanelStroke.clear();
		allPanelStrokes.clear();
		
		world = new World();
	}
	
	public ControlMode getMode() {
		assert Thread.holdsLock(MODEL);
		
		return mode;
	}
	
	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
}
