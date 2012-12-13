package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class Map {
	
	public final int quadrantCols;
	public final int quadrantRows;
	
	private Quadrant[][] quadrants;
	
	public final AABB aabb;
	
	public Map(int[][] ini) {
		
		quadrants = initQuadrants(ini);
		
		quadrantCols = quadrants[0].length;
		quadrantRows = quadrants.length;
		
		aabb = new AABB(0, 0, quadrantCols * APP.QUADRANT_WIDTH, quadrantRows * APP.QUADRANT_HEIGHT);
		
	}
	
	private Quadrant[][] initQuadrants(int[][] ini) {

		int cols = ini[0].length;
		int rows = ini.length;
		
		Quadrant[][] newQuads = new Quadrant[rows][cols];
		
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				Quadrant q;
				if (ini[j][i] == 1) {
					q = new Quadrant(j, i, true);
				} else {
					q = new Quadrant(j, i, false);
				}
				newQuads[j][i] = q;
				
			}
		}
		
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				Quadrant q = newQuads[j][i];
				
				if (j > 0) {
					Quadrant upQuad = newQuads[j-1][i];
					upQuad.down = q;
					q.up = upQuad;
				}
				if (j < quadrantRows-1) {
					Quadrant downQuad = newQuads[j+1][i];
					downQuad.up = q;
					q.down = downQuad;
				}
				if (i > 0) {
					Quadrant leftQuad = newQuads[j][i-1];
					leftQuad.right = q;
					q.left = leftQuad;
				}
				if (i < quadrantCols-1) {
					Quadrant rightQuad = newQuads[j][i+1];
					rightQuad.left = q;
					q.right = rightQuad;
				}
				
			}
		}
		
		return newQuads;
	}
	
	public Quadrant findQuadrant(Point p) {
		int col = (int)Math.floor(p.x / APP.QUADRANT_WIDTH);
		int row = (int)Math.floor(p.y / APP.QUADRANT_HEIGHT);
		if (col < 0 || col > quadrantCols-1) {
			return null;
		}
		if (row < 0 || row > quadrantRows-1) {
			return null;
		}
		return quadrants[row][col];
	}
	
//	public void setCursorPoint(Cursor c, Point lastPoint) {
//		Quadrant q = findQuadrant(lastPoint);
//		if (q != null) {
//			q.setCursorPoint(c, lastPoint);
//		} else {
//			c.setPoint(lastPoint);
//		}
//	}
	
	public Point getPoint(Point p) {
		Quadrant q = findQuadrant(p);
		if (q != null) {
			return q.getPoint(p);
		} else {
			return p;
		}
	}
	
	public Quadrant upFixPoint(Quadrant q) {
		Quadrant up = q;
		while (true) {
			Quadrant next = up.up;
			if (next != null && next.active) {
				up = next;
			} else {
				break;
			}
		}
		return up;
	}
	
	public Quadrant leftFixPoint(Quadrant q) {
		Quadrant left = q;
		while (true) {
			Quadrant next = left.left;
			if (next != null && next.active) {
				left = next;
			} else {
				break;
			}
		}
		return left;
	}
	
	public Quadrant rightFixPoint(Quadrant q) {
		Quadrant right = q;
		while (true) {
			Quadrant next = right.right;
			if (next != null && next.active) {
				right = next;
			} else {
				break;
			}
		}
		return right;
	}
	
	public Quadrant downFixPoint(Quadrant q) {
		Quadrant down = q;
		while (true) {
			Quadrant next = down.down;
			if (next != null && next.active) {
				down = next;
			} else {
				break;
			}
		}
		return down;
	}
	
	public boolean completelyContains(Shape s) {
		
		if (!ShapeUtils.intersect(s, aabb)) {
			return false;
		}
		
		for (int i = 0; i < quadrantCols; i++) {
			for (int j = 0; j < quadrantRows; j++) {
				
				Quadrant q = quadrants[j][i];
				
				if (!q.active) {
					
					if (ShapeUtils.intersect(s, q.aabb)) {
						return false;
					}
					
				} else {
					
				}
			}
		}
		
		return true;
	}
	
	public void toggleGrid() {
		for (int i = 0; i < quadrantRows; i++) {
			for (int j = 0; j < quadrantCols; j++) {
				Quadrant q = quadrants[i][j];
				q.toggleGrid();
			}
		}
	}
	
	public void computeGridSpacing() {
		for (int i = 0; i < quadrantRows; i++) {
			for (int j = 0; j < quadrantCols; j++) {
				Quadrant q = quadrants[i][j];
				q.computeGridSpacing();
			}
		}
	}
	
	public void renderBackground(RenderingContext ctxt) {
		for (int i = 0; i < quadrantCols; i++) {
			for (int j = 0; j < quadrantRows; j++) {
				Quadrant q = quadrants[j][i];
				q.paint(ctxt);
			}
		}
	}
	
}
