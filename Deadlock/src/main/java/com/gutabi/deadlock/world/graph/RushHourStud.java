package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public abstract class RushHourStud {
	
	public static final double SIZE = 1.0;
	
	World world;
	RushHourBoard board;
	
	public final int row;
	public final int col;
	
	public AABB aabb;
	
	public RushHourStud(World world, RushHourBoard board, int row, int col) {
		this.world = world;
		this.board = board;
		
		this.row = row;
		this.col = col;
		
		aabb = new AABB(board.ul.x + RushHourStud.SIZE * col, board.ul.y + RushHourStud.SIZE * row, RushHourStud.SIZE, RushHourStud.SIZE);
	}
	
	public String toString() {
		return "stud " + row + " " + col;
	}
	
	public boolean isFree(Car ignore) {
		
		for (Car c : world.carMap.cars) {
			if (c == ignore) {
				continue;
			}
			if (ShapeUtils.intersectAreaAO(aabb, c.shape)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hitTest(Point p) {
		return aabb.hitTest(p);
	}
	
	public void paint(RenderingContext ctxt) {
		
		APP.spriteSheet.paint(ctxt, SpriteSheetSprite.STUD, ctxt.cam.pixelsPerMeter, aabb.ul.x, aabb.ul.y, aabb.ul.x + aabb.width, aabb.ul.y + aabb.height);
		
	}
	
}
