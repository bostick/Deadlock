package com.gutabi.deadlock.model;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Hilitable;

public class DeadlockModel {
	
	public static final DeadlockModel MODEL = new DeadlockModel();
	
	public World world;
	
	private ControlMode mode = ControlMode.IDLE;
	
	public Stroke stroke;
	
	public Hilitable hilited;
	
	public DeadlockModel() {
		
	}
	
	public void init() {
		
		world = new World();
		
		world.init();
		
		stroke = new Stroke();
		
	}
	
	public void clear() {
		stroke.clearAll();
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
