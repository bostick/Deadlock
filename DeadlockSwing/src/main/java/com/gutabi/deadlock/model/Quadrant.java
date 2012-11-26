package com.gutabi.deadlock.model;

import com.gutabi.deadlock.core.Point;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

@SuppressWarnings("static-access")
public class Quadrant {
	
	public final int r;
	public final int c;
//	public final int id;
	public final boolean active;
	
	public Quadrant up;
	public Quadrant left;
	public Quadrant right;
	public Quadrant down;
	
	public Quadrant(int r, int c, boolean active) {
		this.r = r;
		this.c = c;
//		this.id = id;
		this.active = active;
	}
	
	public Point center() {
//		int r = id / MODEL.world.quadrantCols;
//		int c = id % MODEL.world.quadrantCols;
		return new Point(c * MODEL.QUADRANT_WIDTH + MODEL.QUADRANT_WIDTH/2, r * MODEL.QUADRANT_HEIGHT + MODEL.QUADRANT_HEIGHT/2);
	}
	
}
