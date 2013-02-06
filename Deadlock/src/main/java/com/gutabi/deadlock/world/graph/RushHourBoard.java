package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.CubicCurve;
import com.gutabi.deadlock.geom.Line;
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
	
	public List<Line> perimeterSegments = new ArrayList<Line>();
	
	public List<AABB> neg = new ArrayList<AABB>();
	
	public int originRow;
	public int originCol;
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
		
		originRow = 0;
		originCol = 0;
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
		 * add perimeter and add studs
		 */
		
		Point p0 = ul;
		for (int i = 0; i < 6; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p0.plus(new Point(i * RushHourStud.SIZE, 0)), p0.plus(new Point((i+1) * RushHourStud.SIZE, 0))));
		}
		Point p1 = ul.plus(new Point(6 * RushHourStud.SIZE, 0));
		for (int i = 0; i < 6; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p1.plus(new Point(0, i * RushHourStud.SIZE)), p1.plus(new Point(0, (i+1) * RushHourStud.SIZE))));
		}
		Point p2 = ul.plus(new Point(6 * RushHourStud.SIZE, 6 * RushHourStud.SIZE));
		for (int i = 0; i < 6; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p2.plus(new Point(i * -RushHourStud.SIZE, 0)), p2.plus(new Point((i+1) * -RushHourStud.SIZE, 0))));
		}
		Point p3 = ul.plus(new Point(0, 6 * RushHourStud.SIZE));
		for (int i = 0; i < 6; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p3.plus(new Point(0, i * -RushHourStud.SIZE)), p3.plus(new Point(0, (i+1) * -RushHourStud.SIZE))));
		}
		
		
		List<Fixture> jFixtures = new ArrayList<Fixture>();
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				char c = ini[i][j];
				switch (c) {
				case ' ':
					break;
				case 'X': {
					RegularStud s = new RegularStud(world, this, i - originRow, j - originCol);
					addStud(s);
					break;
				}
				case 'J': {
					JointStud s = new JointStud(world, this, i - originRow, j - originCol);
					addStud(s);
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.BOTTOM);
						s.f = f;
						world.addFixture(f);
						jFixtures.add(f);
					} else if (i >= originRow + 6) {
						removePerimeterSegment(s.aabb.getP0P1Line());
						Fixture f = new Fixture(world, point(i - originRow + 1.0, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.TOP);
						s.f = f;
						world.addFixture(f);
						jFixtures.add(f);
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol), Axis.LEFTRIGHT);
						f.setSide(Side.RIGHT);
						s.f = f;
						world.addFixture(f);
						jFixtures.add(f);
					} else {
						assert j >= originCol + 6;
						removePerimeterSegment(s.aabb.getP3P0Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setSide(Side.LEFT);
						s.f = f;
						world.addFixture(f);
						jFixtures.add(f);
					}
					break;
				}
				case 'E': {
					ExitStud s = new ExitStud(world, this, i - originRow, j - originCol);
					addStud(s);
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
					} else if (i >= originRow + 6) {
						removePerimeterSegment(s.aabb.getP0P1Line());
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
					} else {
						assert j >= originCol + 6;
						removePerimeterSegment(s.aabb.getP3P0Line());
					}
					break;
				}
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
				RushHourStud ss = findStud(i, j);
				if (ss != null) {
					if (ss instanceof JointStud) {
						JointStud jj = (JointStud)ss;
						if (j < originCol) {
							poss.add(new VertexPosition(jj.f));
//							poss.add(new RushHourBoardPosition(this, i + 0.5, j));
						} else if (j >= originCol + 6) {
//							poss.add(new RushHourBoardPosition(this, i + 0.5, j));
							poss.add(new VertexPosition(jj.f));
						}
					} else {
						poss.add(new RushHourBoardPosition(this, i + 0.5, j));
					}
				} else {
					String.class.getName();
				}
			}
			if (!poss.isEmpty()) {
				GraphPositionPath path = new GraphPositionPath(poss);
				rowPaths.put(i, path);
			}
		}
		for (int i = colRange[0]; i <= colRange[1]; i++) {
			List<GraphPosition> poss = new ArrayList<GraphPosition>();
			for (int j = rowRange[0]; j <= rowRange[1]; j++) {
				RushHourStud ss = findStud(j, i);
				if (ss != null) {
					if (ss instanceof JointStud) {
						JointStud jj = (JointStud)ss;
						if (j < originRow) {
							poss.add(new VertexPosition(jj.f));
//							poss.add(new RushHourBoardPosition(this, j, i + 0.5));
						} else if (j >= originRow + 6) {
//							poss.add(new RushHourBoardPosition(this, j, i + 0.5));
							poss.add(new VertexPosition(jj.f));
						}
					} else {
						poss.add(new RushHourBoardPosition(this, j, i + 0.5));
					}
				}
			}
			if (!poss.isEmpty()) {
				GraphPositionPath path = new GraphPositionPath(poss);
				colPaths.put(i, path);
			}
		}
		
		/*
		 * negative space
		 */
		
		int colCount = (int)Math.round(aabb.width / RushHourStud.SIZE);
		int rowCount = (int)Math.round(aabb.height / RushHourStud.SIZE);
		for (int i = 0; i < rowCount; i++) {
			jloop:
			for (int j = 0; j < colCount; j++) {
				AABB n = APP.platform.createShapeEngine().createAABB(aabb.ul.x + j * RushHourStud.SIZE, aabb.ul.y + i * RushHourStud.SIZE, RushHourStud.SIZE, RushHourStud.SIZE);
				
				for (RushHourStud ss : studs) {
					if (ss.aabb.equals(n)) {
						continue jloop;
					}
				}
				
				neg.add(n);
			}
		}	
		
	}
	
	private RushHourStud findStud(int i, int j) {
		for (RushHourStud ss : studs) {
			if (ss.row == i && ss.col == j) {
				return ss;
			}
		}
		return null;
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
	
	private void removePerimeterSegment(Line l) {
		
		Line toRemove = null;
		for (Line p : perimeterSegments) {
			if (l.p0.equals(p.p0) && l.p1.equals(p.p1) || l.p0.equals(p.p1) && l.p1.equals(p.p0)) {
				toRemove = p;
				break;
			}
		}
		if (toRemove != null) {
			perimeterSegments.remove(toRemove);
		} else {
//			assert false;
		}
		
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
	
	public RushHourBoardPosition position(Point p) {
		return new RushHourBoardPosition(this, (p.y - ul.y) / RushHourStud.SIZE, (p.x - ul.x) / RushHourStud.SIZE);
	}
	
	public RushHourBoard hitTest(Point p) {
		for (RushHourStud s : studs) {
			if (s.hitTest(p)) {
				return this;
			}
		}
		return null;
	}
	
//	public boolean contains(Shape s) {
//		
//		if (!ShapeUtils.contains(aabb, s)) {
//			return false;
//		}
//		
//		for (AABB n : neg) {
//			if (ShapeUtils.intersectArea(s, n)) {
//				return false;
//			}
//		}
//		
//		return true;
//	}
	
	public boolean overlapsPerimeter(Shape s) {
		for (Line l : perimeterSegments) {
			if (ShapeUtils.intersectArea(l, s)) {
				return true;
			}
		}
		return false;
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
