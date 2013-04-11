package com.gutabi.deadlock.world.graph;

import com.gutabi.deadlock.world.World;

public class BorderStud extends BypassStud {
	
	public Fixture f;
	
	public BorderStud(World world, BypassBoard board, int row, int col) {
		super(world, board, row, col);
	}
	
	public boolean across(BorderStud other) {
		assert this != other;
		return withinRowRange(this.row) && withinRowRange(other.row) && this.row == other.row ||
				withinColRange(this.col) && withinColRange(other.col) && this.col == other.col;
	}
	
	boolean withinRowRange(int r) {
		return r >= 0 && r < board.rowCount;
	}
	
	boolean withinColRange(int c) {
		return c >= 0 && c < board.colCount;
	}
	
}
