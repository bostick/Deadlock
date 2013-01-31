package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;

public class RushHourBoard extends Entity {
	
	World world;
	Point center;
	
	public List<RushHourStud> studs = new ArrayList<RushHourStud>();
	public List<AABB> neg = new ArrayList<AABB>();
	
	public Point ul;
	public int[] rowRange = { 0, 0 };
	public int[] colRange = { 0, 0 };
	public AABB aabb;
	
	private Map<Integer, GraphPositionPath> rowPaths = new HashMap<Integer, GraphPositionPath>();
	private Map<Integer, GraphPositionPath> colPaths = new HashMap<Integer, GraphPositionPath>();
	
	public RushHourBoard(World world, Point center, char[][] ini) {
		this.world = world;
		this.center = center;
		
		ul = new Point(center.x - 3 * RushHourStud.SIZE, center.y - 3 * RushHourStud.SIZE);
		
		/*
		 * find where ul of main board is in ini
		 */
		
		int originRow = 0;
		int originCol = 0;
		rowLoop:
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				char c = ini[i][j];
				if (c == 'X') {
					originRow = i;
					originCol = j;
					break rowLoop;
				}
			}
		}
		
		/*
		 * add studs
		 */
		
		List<Fixture> jFixtures = new ArrayList<Fixture>();
		
		RushHourStud s;
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				char c = ini[i][j];
				switch (c) {
				case ' ':
					break;
				case 'X':
					s = new RushHourStud(world, this, i - originRow, j - originCol);
					addStud(s);
					break;
				case 'J':
					s = new RushHourStud(world, this, i - originRow, j - originCol);
					addStud(s);
					if (i < originRow) {
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.BOTTOM);
						world.addFixture(f);
						jFixtures.add(f);
					} else if (i >= originRow + 6) {
						Fixture f = new Fixture(world, point(i - originRow + 1.0, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.TOP);
						world.addFixture(f);
						jFixtures.add(f);
					} else if (j < originCol) {
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol), Axis.LEFTRIGHT);
						f.setSide(Side.RIGHT);
						world.addFixture(f);
						jFixtures.add(f);
					} else {
						assert j >= originCol + 6;
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setSide(Side.LEFT);
						world.addFixture(f);
						jFixtures.add(f);
					}
					break;
				case 'E':
					s = new RushHourStud(world, this, i - originRow, j - originCol);
					addStud(s);
					break;
				}
			}
		}
		
		Fixture f0 = jFixtures.get(0);
		Fixture f1 = jFixtures.get(1);
		
		CubicCurve c = APP.platform.createShapeEngine().createCubicCurve(f0.p, new Point(f0.p.x, f0.p.y - 15), new Point(f1.p.x - 15, f1.p.y), f1.p);
		List<Point> pts = c.skeleton();
		
		Stroke stroke = new Stroke(world);
		for (Point p : pts) {
			stroke.add(p);
		}
		stroke.finish();
		
		stroke.processNewStroke();
		
		/*
		 * create paths
		 */
		
		for (int i = rowRange[0]; i <= rowRange[1]; i++) {
			List<GraphPosition> poss = new ArrayList<GraphPosition>();
			for (int j = colRange[0]; j <= colRange[1]; j++) {
				for (RushHourStud ss : studs) {
					if (ss.row == i && ss.col == j) {
						poss.add(new RushHourBoardPosition(this, i + 0.5, j));
					}
				}
			}
			GraphPositionPath path = new GraphPositionPath(poss);
			rowPaths.put(i, path);
		}
		for (int i = colRange[0]; i <= colRange[1]; i++) {
			List<GraphPosition> poss = new ArrayList<GraphPosition>();
			for (int j = rowRange[0]; j <= rowRange[1]; j++) {
				for (RushHourStud ss : studs) {
					if (ss.row == j && ss.col == i) {
						poss.add(new RushHourBoardPosition(this, j, i + 0.5));
					}
				}
			}
			GraphPositionPath path = new GraphPositionPath(poss);
			colPaths.put(i, path);
		}
		
		/*
		 * negative space
		 */
		
		int colCount = (int)Math.round(aabb.width / RushHourStud.SIZE);
		int rowCount = (int)Math.round(aabb.height / RushHourStud.SIZE);
		for (int i = 0; i < rowCount; i++) {
			jloop:
			for (int j = 0; j < colCount; j++) {
				AABB n = APP.platform.createShapeEngine().createAABB(this, aabb.ul.x + j * RushHourStud.SIZE, aabb.ul.y + i * RushHourStud.SIZE, RushHourStud.SIZE, RushHourStud.SIZE);
				
				for (RushHourStud ss : studs) {
					if (ss.aabb.equals(n)) {
						continue jloop;
					}
				}
				
				neg.add(n);
			}
		}
		
		
	}
	
	private void addStud(RushHourStud s) {
		studs.add(s);
		if (s.row > rowRange[1]) {
			rowRange[1] = s.row;
		}
		if (s.row < rowRange[0]) {
			rowRange[0] = s.row;
		}
		if (s.col > colRange[1]) {
			colRange[1] = s.col;
		}
		if (s.col < colRange[0]) {
			colRange[0] = s.col;
		}
		aabb = AABB.union(aabb, s.aabb);
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
	
	public GraphPositionPath getPath(Side s, int index) {
		switch (s) {
		case LEFT:
		case RIGHT:
			return rowPaths.get(index);
		case TOP:
		case BOTTOM:
			return colPaths.get(index);
		}
		
		assert false;
		return null;
	}
	
	public Point point(double row, double col) {
		return ul.plus(new Point(col * RushHourStud.SIZE, row * RushHourStud.SIZE));
	}
	
	public RushHourBoard hitTest(Point p) {
		for (RushHourStud s : studs) {
			if (s.hitTest(p)) {
				return this;
			}
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
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void paint_panel(RenderingContext ctxt) {
		for (RushHourStud s : studs) {
			s.paint(ctxt);
		}
	}
	
	public void paint_preview(RenderingContext ctxt) {
		for (RushHourStud s : studs) {
			s.paint(ctxt);
		}
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
	}

}
