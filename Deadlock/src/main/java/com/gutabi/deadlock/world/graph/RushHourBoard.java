package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		
		
		List<JointStud> jStuds = new ArrayList<JointStud>();
		
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
					jStuds.add(s);
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.BOTTOM);
						s.f = f;
						world.addFixture(f);
					} else if (i >= originRow + 6) {
						removePerimeterSegment(s.aabb.getP0P1Line());
						Fixture f = new Fixture(world, point(i - originRow + 1.0, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.TOP);
						s.f = f;
						world.addFixture(f);
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol), Axis.LEFTRIGHT);
						f.setSide(Side.RIGHT);
						s.f = f;
						world.addFixture(f);
					} else {
						assert j >= originCol + 6;
						removePerimeterSegment(s.aabb.getP3P0Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setSide(Side.LEFT);
						s.f = f;
						world.addFixture(f);
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
		
		JointStud js0 = jStuds.get(0);
		JointStud js1 = jStuds.get(1);
		Fixture f0 = js0.f;
		Fixture f1 = js1.f;
		
		CubicCurve c = APP.platform.createShapeEngine().createCubicCurve(new Point(f0.p.x, f0.p.y - 5), new Point(f0.p.x, f0.p.y - 15), new Point(f1.p.x - 15, f1.p.y), new Point(f1.p.x - 5, f1.p.y));
		List<Point> pts = c.skeleton();
		
		Stroke stroke = new Stroke(world);
		stroke.add(f0.p);
		for (Point p : pts) {
			stroke.add(p);
		}
		stroke.add(f1.p);
		stroke.finish();
		
		stroke.processNewStroke();
		
		/*
		 * create paths 
		 */
		
		
		Map<Integer, List<RushHourBoardPosition>> rowTracks = new HashMap<Integer, List<RushHourBoardPosition>>();
		Map<Integer, List<RushHourBoardPosition>> colTracks = new HashMap<Integer, List<RushHourBoardPosition>>();
		
		/*
		 * row tracks
		 */
		for (int i = 0; i < 6; i++) {
			List<RushHourBoardPosition> track = new ArrayList<RushHourBoardPosition>();
			for (int j = 0; j < 6; j++) {
				track.add(new RushHourBoardPosition(this, i + 0.5, j));
			}
			rowTracks.put(i, track);
		}
		
		/*
		 * col tracks
		 */
		for (int i = 0; i < 6; i++) {
			List<RushHourBoardPosition> track = new ArrayList<RushHourBoardPosition>();
			for (int j = 0; j < 6; j++) {
				track.add(new RushHourBoardPosition(this, j, i + 0.5));
			}
			colTracks.put(i, track);
		}
		

		
		List<RushHourBoardPosition> js0Track;
		if (js0.col < originCol) {
			js0Track = rowTracks.remove(js0.row);
		} else if (js0.col >= originCol + 6) {
			js0Track = rowTracks.remove(js0.row);
		} else if (js0.row < originRow) {
			js0Track = colTracks.remove(js0.col);
		} else {
			assert js0.row >= originRow + 6;
			js0Track = colTracks.remove(js0.col);
		}
		
		List<RushHourBoardPosition> js1Track;
		if (js1.col < originCol) {
			js1Track = rowTracks.remove(js1.row);
		} else if (js1.col >= originCol + 6) {
			js1Track = rowTracks.remove(js1.row);
		} else if (js1.row < originRow) {
			js1Track = colTracks.remove(js1.col);
		} else {
			assert js1.row >= originRow + 6;
			js1Track = colTracks.remove(js1.col);
		}
		
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
//		from other side of js0 track to js0
		if (js0.col < originCol) {
			for (int i = 5; i >= 1; i--) {
				poss.add(js0Track.get(i));
			}
		} else if (js0.col >= originCol + 6) {
			for (int i = 0; i < 5; i++) {
				poss.add(js0Track.get(i));
			}
		} else if (js0.row < originRow) {
			for (int i = 5; i >= 1; i--) {
				poss.add(js0Track.get(i));
			}
		} else {
			assert js0.row >= originRow + 6;
			for (int i = 0; i < 5; i++) {
				poss.add(js0Track.get(i));
			}
		}
//		to vertex
//		to road
//		to vertex
		Road r = js0.f.roads.get(0);
		if (js0.f == r.start) {
			poss.add(new VertexPosition(r.start));
			for (int ii = 1; ii <= r.pointCount()-2; ii++) {
				poss.add(new RoadPosition(r, ii, 0.0));
			}
			poss.add(new VertexPosition(r.end));
		} else {
			poss.add(new VertexPosition(r.end));
			for (int ii = r.pointCount()-2; ii >= 1; ii--) {
				poss.add(new RoadPosition(r, ii, 0.0));
			}
			poss.add(new VertexPosition(r.start));
		}
//		to js1
//		to other side of js1 track
		if (js1.col < originCol) {
			for (int i = 1; i < 6; i++) {
				poss.add(js1Track.get(i));
			}
		} else if (js1.col >= originCol + 6) {
			for (int i = 5; i >= 0; i--) {
				poss.add(js1Track.get(i));
			}
		} else if (js1.row < originRow) {
			for (int i = 1; i > 6; i++) {
				poss.add(js1Track.get(i));
			}
		} else {
			assert js1.row >= originRow + 6;
			for (int i = 5; i >= 0; i--) {
				poss.add(js1Track.get(i));
			}
		}
		
//		add to rowPaths and colPaths as needed
		GraphPositionPath path = new GraphPositionPath(poss);
		if (js0.col < originCol) {
			rowPaths.put(js0.row, path);
		} else if (js0.col >= originCol + 6) {
			rowPaths.put(js0.row, path);
		} else if (js0.row < originRow) {
			colPaths.put(js0.col, path);
		} else {
			assert js0.row >= originRow + 6;
			colPaths.put(js0.col, path);
		}
		
		if (js1.col < originCol) {
			rowPaths.put(js1.row, path);
		} else if (js1.col >= originCol + 6) {
			rowPaths.put(js1.row, path);
		} else if (js1.row < originRow) {
			colPaths.put(js1.col, path);
		} else {
			assert js1.row >= originRow + 6;
			colPaths.put(js1.col, path);
		}
		
		
		
		
		
		
		
		
		
		
		
		/*
		 * regular row tracks
		 */
		for (Entry<Integer, List<RushHourBoardPosition>> entry : rowTracks.entrySet()) {
			Integer i = entry.getKey();
			List<RushHourBoardPosition> rt = entry.getValue();
			poss = new ArrayList<GraphPosition>();
			for (RushHourBoardPosition pos : rt) {
				poss.add(pos);
			}
			path = new GraphPositionPath(poss);
			rowPaths.put(i, path);
		}
		
		/*
		 * regular col tracks
		 */
		for (Entry<Integer, List<RushHourBoardPosition>> entry : colTracks.entrySet()) {
			Integer i = entry.getKey();
			List<RushHourBoardPosition> ct = entry.getValue();
			poss = new ArrayList<GraphPosition>();
			for (RushHourBoardPosition pos : ct) {
				poss.add(pos);
			}
			path = new GraphPositionPath(poss);
			colPaths.put(i, path);
		}
		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		/*
//		 * row paths
//		 */
//		for (int i = rowRange[0]; i <= rowRange[1]; i++) {
//			List<GraphPosition> poss = new ArrayList<GraphPosition>();
//			for (int j = colRange[0]; j <= colRange[1]; j++) {
//				RushHourStud ss = findStud(i, j);
//				if (ss != null) {
//					if (ss instanceof JointStud) {
//						JointStud jj = (JointStud)ss;
//						if (j < originCol) {
//							Road r = jj.f.roads.get(0);
//							if (jj.f == r.start) {
//								poss.add(new VertexPosition(r.end));
//								for (int ii = r.pointCount()-2; ii >= 1; ii--) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.start));
//							} else {
//								poss.add(new VertexPosition(r.start));
//								for (int ii = 1; ii <= r.pointCount()-2; ii++) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.end));
//							}
//						} else if (j >= originCol + 6) {
//							Road r = jj.f.roads.get(0);
//							if (jj.f == r.start) {
//								poss.add(new VertexPosition(r.start));
//								for (int ii = 1; ii <= r.pointCount()-2; ii++) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.end));
//							} else {
//								poss.add(new VertexPosition(r.end));
//								for (int ii = r.pointCount()-2; ii >= 1; ii--) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.start));
//							}
//						}
//					} else {
//						poss.add(new RushHourBoardPosition(this, i + 0.5, j));
//					}
//				}
//			}
//			if (!poss.isEmpty()) {
//				GraphPositionPath path = new GraphPositionPath(poss);
//				rowPaths.put(i, path);
//			}
//		}
//		
//		/*
//		 * col paths
//		 */
//		for (int i = colRange[0]; i <= colRange[1]; i++) {
//			List<GraphPosition> poss = new ArrayList<GraphPosition>();
//			for (int j = rowRange[0]; j <= rowRange[1]; j++) {
//				RushHourStud ss = findStud(j, i);
//				if (ss != null) {
//					if (ss instanceof JointStud) {
//						JointStud jj = (JointStud)ss;
//						if (j < originRow) {
//							Road r = jj.f.roads.get(0);
//							if (jj.f == r.start) {
//								poss.add(new VertexPosition(r.end));
//								for (int ii = r.pointCount()-2; ii >= 1; ii--) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.start));
//							} else {
//								poss.add(new VertexPosition(r.start));
//								for (int ii = 1; ii <= r.pointCount()-2; ii++) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.end));
//							}
//						} else if (j >= originRow + 6) {
//							Road r = jj.f.roads.get(0);
//							if (jj.f == r.start) {
//								poss.add(new VertexPosition(r.start));
//								for (int ii = 1; ii <= r.pointCount()-2; ii++) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.end));
//							} else {
//								poss.add(new VertexPosition(r.end));
//								for (int ii = r.pointCount()-2; ii >= 1; ii--) {
//									poss.add(new RoadPosition(r, ii, 0.0));
//								}
//								poss.add(new VertexPosition(r.start));
//							}
//						}
//					} else {
//						poss.add(new RushHourBoardPosition(this, j, i + 0.5));
//					}
//				}
//			}
//			if (!poss.isEmpty()) {
//				GraphPositionPath path = new GraphPositionPath(poss);
//				colPaths.put(i, path);
//			}
//		}
		
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
	
//	private RushHourStud findStud(int i, int j) {
//		for (RushHourStud ss : studs) {
//			if (ss.row == i && ss.col == j) {
//				return ss;
//			}
//		}
//		return null;
//	}
	
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
