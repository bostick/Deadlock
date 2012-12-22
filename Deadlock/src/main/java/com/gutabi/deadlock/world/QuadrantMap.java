package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import com.gutabi.deadlock.core.DMath;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class QuadrantMap {
	
	public static final double QUADRANT_WIDTH = 16.0;
	public static final double QUADRANT_HEIGHT = QUADRANT_WIDTH;
	
	WorldCamera cam;
	
	public final double worldWidth;
	public final double worldHeight;
	
	public final int quadrantCols;
	public final int quadrantRows;
	
	public final int[][] ini;
	private Quadrant[][] quadrants;
	
	public BufferedImage quadrantGrass;
	
	public GrassMap grassMap = new GrassMap();
	
	public final AABB aabb;
	
	public QuadrantMap(WorldCamera cam, int[][] ini) {
		this.cam = cam;
		this.ini = ini;
		
		quadrants = initQuadrants(ini);
		
		quadrantCols = quadrants[0].length;
		quadrantRows = quadrants.length;
		
		worldWidth = quadrantCols * QuadrantMap.QUADRANT_WIDTH;
		worldHeight = quadrantRows * QuadrantMap.QUADRANT_HEIGHT;
		
		aabb = new AABB(0, 0, quadrantCols * QUADRANT_WIDTH, quadrantRows * QUADRANT_HEIGHT);
	}
	
	private Quadrant[][] initQuadrants(int[][] ini) {

		int cols = ini[0].length;
		int rows = ini.length;
		
		Quadrant[][] newQuads = new Quadrant[rows][cols];
		
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				Quadrant q;
				if (ini[j][i] == 1) {
					q = new Quadrant(cam, this, j, i, true);
				} else {
					q = new Quadrant(cam, this, j, i, false);
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
	
	public void canvasPostDisplay() {
		
		int quadrantWidthPixels = (int)Math.ceil(cam.pixelsPerMeter * QUADRANT_WIDTH);
		int quadrantHeightPixels = (int)Math.ceil(cam.pixelsPerMeter * QUADRANT_HEIGHT);
		
		quadrantGrass = new BufferedImage(quadrantWidthPixels, quadrantHeightPixels, BufferedImage.TYPE_INT_RGB);
		Graphics2D quadrantGrassG2 = quadrantGrass.createGraphics();
		
		int maxCols = (int)Math.ceil(quadrantWidthPixels/32.0);
		int maxRows = (int)Math.ceil(quadrantHeightPixels/32.0);
		for (int i = 0; i < maxRows; i++) {
			for (int j = 0; j < maxCols; j++) {
				quadrantGrassG2.drawImage(
						VIEW.sheet,
						32 * j, 32 * i, 32 * j + 32, 32 * i + 32,
						0, 224, 0+32, 224+32, null);
			}
		}
		
	}
	
	public Quadrant findQuadrant(Point p) {
		
		if (DMath.greaterThanEquals(p.x / QUADRANT_WIDTH, 0.0) &&
				DMath.greaterThanEquals(p.y / QUADRANT_HEIGHT, 0.0) &&
				DMath.lessThanEquals(p.x / QUADRANT_WIDTH, quadrantCols) &&
				DMath.lessThanEquals(p.y / QUADRANT_HEIGHT, quadrantRows)) {
			
			int col = DMath.lessThan(p.x / QUADRANT_WIDTH, quadrantCols) ? (DMath.equals(p.x / QUADRANT_WIDTH, 0.0) ? 0 : (int)Math.floor(p.x / QUADRANT_WIDTH)) : quadrantCols-1;
			int row = DMath.lessThan(p.y / QUADRANT_HEIGHT, quadrantRows) ? (DMath.equals(p.y / QUADRANT_HEIGHT, 0.0) ? 0 : (int)Math.floor(p.y / QUADRANT_HEIGHT)) : quadrantRows-1;
			return quadrants[row][col];
			
		} else {
			
			return null;
			
		}
	}
	
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
				q.computeGridSpacing(cam.pixelsPerMeter);
			}
		}
	}
	
	public void preStart() {
		grassMap.preStart();
	}
	
	public void preStep(double t) {
		grassMap.preStep(t);
	}
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("start quadrantMap\n");
		
		s.append(quadrantRows + " " + quadrantCols + "\n");
		
		for (int i = 0; i < quadrantRows; i++) {
			for (int j = 0; j < quadrantCols; j++) {
				Quadrant q = quadrants[i][j];
				s.append(q.toFileString());
				s.append(" ");
			}
			s.append("\n");
		}
		
		s.append("end quadrantMap\n");
		
		return s.toString();
	}
	
	public static QuadrantMap fromFileString(WorldCamera cam, String s) {
		BufferedReader r = new BufferedReader(new StringReader(s));
		
		int[][] ini = null;
		
		try {
			String l = r.readLine();
			assert l.equals("start quadrantMap");
			
			l = r.readLine();
			Scanner scanner = new Scanner(l);
			int rows = scanner.nextInt();
			int cols = scanner.nextInt();
			
			ini = new int[rows][cols];
			
			for (int i = 0; i < rows; i++) {
				l = r.readLine();
				scanner = new Scanner(l);
				for (int j = 0; j < cols; j++) {
					ini[i][j] = scanner.nextInt();
				}
			}
			
			l = r.readLine();
			assert l.equals("end quadrantMap");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		QuadrantMap qm = new QuadrantMap(cam, ini);
		return qm;
	}
	
	public void render(RenderingContext ctxt) {
		for (int i = 0; i < quadrantRows; i++) {
			for (int j = 0; j < quadrantCols; j++) {
				Quadrant q = quadrants[i][j];
				q.paint(ctxt);
			}
		}
	}
	
	public void paintScene(RenderingContext ctxt) {
		grassMap.paintScene(ctxt);
	}
	
}
