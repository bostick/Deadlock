package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public abstract class BypassStud {
	
	public static final double SIZE = 1.0;
	
	World world;
	public BypassBoard board;
	
	public final int row;
	public final int col;
	
	SpriteSheetSprite sprite;
	
	public AABB aabb;
	
	public BypassStud(World world, BypassBoard board, int row, int col) {
		this.world = world;
		this.board = board;
		
		this.row = row;
		this.col = col;
		
		if (row == -1) {
			sprite = SpriteSheetSprite.TOPSTUD;
		} else if (row == board.rowCount) {
			sprite = SpriteSheetSprite.BOTTOMSTUD;
		} else if (col == -1) {
			sprite = SpriteSheetSprite.LEFTSTUD;
		} else if (col == board.colCount) {
			sprite = SpriteSheetSprite.RIGHTSTUD;
		} else {
			sprite = SpriteSheetSprite.INNERSTUD;
		}
		
		aabb = new AABB(board.ul.x + BypassStud.SIZE * col, board.ul.y + BypassStud.SIZE * row, BypassStud.SIZE, BypassStud.SIZE);
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
		
		APP.spriteSheet.paint(ctxt, sprite, ctxt.cam.pixelsPerMeter, aabb.ul.x, aabb.ul.y, aabb.ul.x + aabb.width, aabb.ul.y + aabb.height);
		
	}
	
}
