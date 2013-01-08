package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourBoard extends Entity {
	
	World world;
	Point p;
	
	public RushHourStud[][] studs = new RushHourStud[6][6];
	public RushHourStud exit0;
	public RushHourStud exit1;
	
	public AABB aabb;
	
	public RushHourBoard(World world, Point p) {
		this.world = world;
		this.p = p;
		
		aabb =APP.platform.createShapeEngine().createAABB(p.x - 3 * RushHourStud.SIZE, p.y - 3 * RushHourStud.SIZE, 6 * RushHourStud.SIZE,  6 * RushHourStud.SIZE);
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				studs[i][j] = new RushHourStud(world, this, i, j);		
			}
		}
		exit0 = new RushHourStud(world, this, 2, 6);
		exit1 = new RushHourStud(world, this, 2, 7);
		
	}
	
	public void preStart() {
		
	}
	
	public void postStop() {
		
	}
	
	public void preStep(double t) {
		
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public Point point(double row, double col) {
		return aabb.ul.plus(new Point(col * RushHourStud.SIZE, row * RushHourStud.SIZE));
	}
	
	public RushHourBoard hitTest(Point p) {
		if (aabb.hitTest(p)) {
			return this;
		}
		return null;
	}
	
	public AABB getShape() {
		return aabb;
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		aabb.paint(ctxt);
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				studs[i][j].paint(ctxt);
			}
		}
		exit0.paint(ctxt);
		exit1.paint(ctxt);
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		aabb.paint(ctxt);
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
	}

}
