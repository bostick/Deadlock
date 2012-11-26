package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.geom.tree.AABB;
import com.gutabi.deadlock.model.cursor.RegularCursor;

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
		
		aabb = new AABB(0, 0, quadrantCols * MODEL.QUADRANT_WIDTH, quadrantRows * MODEL.QUADRANT_HEIGHT);
		
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
		int col = (int)Math.floor(p.x / MODEL.QUADRANT_WIDTH);
		int row = (int)Math.floor(p.y / MODEL.QUADRANT_HEIGHT);
		if (col < 0 || col > quadrantCols-1) {
			return null;
		}
		if (row < 0 || row > quadrantRows-1) {
			return null;
		}
		return quadrants[row][col];
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
				if (!quadrants[j][i].active) {
					
					AABB quadAABB = new AABB(i * MODEL.QUADRANT_WIDTH, j * MODEL.QUADRANT_HEIGHT, MODEL.QUADRANT_WIDTH, MODEL.QUADRANT_HEIGHT);
					
					if (ShapeUtils.intersect(s, quadAABB)) {
						return false;
					}
					
				} else {
					
				}
			}
		}
		
		return true;
	}
	
	public void renderBackground(Graphics2D backgroundGrassImageG2, Graphics2D previewBackgroundGrassImageG2) {
		for (int i = 0; i < quadrantCols; i++) {
			for (int j = 0; j < quadrantRows; j++) {
				if (quadrants[j][i].active) {
					
					if (!MODEL.DEBUG_DRAW) {
						
						backgroundGrassImageG2.drawImage(VIEW.quadrantGrass, (int)(i * MODEL.QUADRANT_WIDTH * MODEL.PIXELS_PER_METER), (int)(j * MODEL.QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER), null);
						
					} else {
						
						backgroundGrassImageG2.setColor(VIEW.LIGHTGREEN);
						backgroundGrassImageG2.fillRect(
								(int)(i * MODEL.QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
								(int)(j * MODEL.QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER),
								(int)(MODEL.QUADRANT_WIDTH * MODEL.PIXELS_PER_METER),
								(int)(MODEL.QUADRANT_HEIGHT * MODEL.PIXELS_PER_METER));
						
					}
					
					if (MODEL.grid) {
						backgroundGrassImageG2.setColor(Color.GRAY);
						backgroundGrassImageG2.setStroke(RegularCursor.solidOutlineStroke);
						for (int k = 0; k <= MODEL.QUADRANT_HEIGHT; k+=2) {
							backgroundGrassImageG2.drawLine(
									(int)((i * MODEL.QUADRANT_WIDTH + 0) * MODEL.PIXELS_PER_METER),
									(int)((j * MODEL.QUADRANT_HEIGHT + k) * MODEL.PIXELS_PER_METER),
									(int)((i * MODEL.QUADRANT_WIDTH + MODEL.QUADRANT_WIDTH) * MODEL.PIXELS_PER_METER),
									(int)((j * MODEL.QUADRANT_HEIGHT + k) * MODEL.PIXELS_PER_METER));
						}
						for (int k = 0; k <= MODEL.QUADRANT_WIDTH; k+=2) {
							backgroundGrassImageG2.drawLine(
									(int)((i * MODEL.QUADRANT_WIDTH + k) * MODEL.PIXELS_PER_METER),
									(int)((j * MODEL.QUADRANT_HEIGHT + 0) * MODEL.PIXELS_PER_METER),
									(int)((i * MODEL.QUADRANT_WIDTH + k) * MODEL.PIXELS_PER_METER),
									(int)((j * MODEL.QUADRANT_HEIGHT + MODEL.QUADRANT_HEIGHT) * MODEL.PIXELS_PER_METER));	
						}
					}
					
					previewBackgroundGrassImageG2.setColor(VIEW.DARKGREEN);
					previewBackgroundGrassImageG2.fillRect(
							(int)(i * 100 / quadrantCols),
							(int)(j * 100 / quadrantRows),
							(int)(100 / quadrantCols),
							(int)(100 / quadrantRows));
					
				}
			}
		}
	}
	
}
