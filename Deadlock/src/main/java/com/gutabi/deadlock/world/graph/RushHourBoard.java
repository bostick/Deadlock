package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourBoard extends Entity {
	
	World world;
	Point p;
	
	public RushHourStud[][] studs = new RushHourStud[6][6];
	public RushHourStud exit0;
	public RushHourStud exit1;
	
	public List<AABB> neg = new ArrayList<AABB>();
	
	public RushHourStud a;
	
	public Point ul;
	public AABB aabb;
	
	public RushHourBoard(World world, Point p) {
		this.world = world;
		this.p = p;
		
		ul = new Point(p.x - 3 * RushHourStud.SIZE, p.y - 3 * RushHourStud.SIZE);
		
		aabb = null;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				studs[i][j] = new RushHourStud(world, this, i, j);
				aabb = AABB.union(aabb, studs[i][j].aabb);
			}
		}
		exit0 = new RushHourStud(world, this, 2, 6);
		exit1 = new RushHourStud(world, this, 2, 7);
		aabb = AABB.union(aabb, exit0.aabb);
		aabb = AABB.union(aabb, exit1.aabb);
		
		neg.add(APP.platform.createShapeEngine().createAABB(this, ul.x + 6 * RushHourStud.SIZE, ul.y, 2 * RushHourStud.SIZE, 2 * RushHourStud.SIZE));
		neg.add(APP.platform.createShapeEngine().createAABB(this, ul.x + 6 * RushHourStud.SIZE, ul.y + 3 * RushHourStud.SIZE, 2 * RushHourStud.SIZE, 3 * RushHourStud.SIZE));
		
		a = new RushHourStud(world, this, 0, -1);
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
		return ul.plus(new Point(col * RushHourStud.SIZE, row * RushHourStud.SIZE));
	}
	
	public RushHourBoard hitTest(Point p) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (studs[i][j].hitTest(p)) {
					return this;
				}
			}
		}
		if (exit0.hitTest(p)) {
			return this;
		}
		if (exit1.hitTest(p)) {
			return this;
		}
		if (a.hitTest(p)) {
			return this;
		}
		return null;
	}
	
	public boolean contains(Shape s) {
		
		if (!ShapeUtils.contains(aabb, s)) {
			return false;
		}
		
		for (AABB n : neg) {
			if (ShapeUtils.intersectArea(s, n)) {
				return false;
			}
		}
		
		return true;
	}
	
//	public AABB getShape() {
//		return aabb;
//	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
//		ctxt.setColor(Color.GRAY);
//		aabb.paint(ctxt);
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				studs[i][j].paint(ctxt);
			}
		}
		exit0.paint(ctxt);
		exit1.paint(ctxt);
		
		a.paint(ctxt);
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
//		ctxt.setColor(Color.GRAY);
//		aabb.paint(ctxt);
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				studs[i][j].paint(ctxt);
			}
		}
		exit0.paint(ctxt);
		exit1.paint(ctxt);
		
		a.paint(ctxt);
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
	}

}
