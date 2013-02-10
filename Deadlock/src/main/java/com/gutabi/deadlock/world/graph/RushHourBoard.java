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
	
	public int rowCount;
	public int colCount;
	
	public Point ul;
	public AABB aabb;
	
	private Map<Integer, GraphPositionPath> rowPaths = new HashMap<Integer, GraphPositionPath>();
	private Map<Integer, GraphPositionPath> colPaths = new HashMap<Integer, GraphPositionPath>();
	
	public RushHourBoard(World world, Point center, char[][] ini) {
		this.world = world;
		this.center = center;
		
		/*
		 * find originRow, originCol, rowCount, colCount
		 */
		
		originRow = 0;
		originCol = 0;
		rowLoop:
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				char c = ini[i][j];
				switch (c) {
				case 'X':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'R':
					originRow = i;
					originCol = j;
					break rowLoop;
				}
			}
		}
		
		rowCount = 1;
		rowLoop:
		while (true) {
			char c = ini[originRow+rowCount][originCol];
			switch (c) {
			case 'X':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'R':
				rowCount++;
				break;
			default:
				break rowLoop;
			}
		}
		
		colCount = 1;
		colLoop:
		while (true) {
			char c = ini[originRow][originCol+colCount];
			switch (c) {
			case 'X':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'R':
				colCount++;
				break;
			default:
				break colLoop;
			}
		}
		
		ul = new Point(center.x - 0.5 * colCount * RushHourStud.SIZE, center.y - 0.5 * rowCount * RushHourStud.SIZE);
		
		/*
		 * add perimeter and add studs
		 */
		
		Point p0 = ul;
		for (int i = 0; i < colCount; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p0.plus(new Point(i * RushHourStud.SIZE, 0)), p0.plus(new Point((i+1) * RushHourStud.SIZE, 0))));
		}
		Point p1 = ul.plus(new Point(colCount * RushHourStud.SIZE, 0));
		for (int i = 0; i < rowCount; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p1.plus(new Point(0, i * RushHourStud.SIZE)), p1.plus(new Point(0, (i+1) * RushHourStud.SIZE))));
		}
		Point p2 = ul.plus(new Point(colCount * RushHourStud.SIZE, rowCount * RushHourStud.SIZE));
		for (int i = 0; i < colCount; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p2.plus(new Point(i * -RushHourStud.SIZE, 0)), p2.plus(new Point((i+1) * -RushHourStud.SIZE, 0))));
		}
		Point p3 = ul.plus(new Point(0, rowCount * RushHourStud.SIZE));
		for (int i = 0; i < rowCount; i++) {
			perimeterSegments.add(APP.platform.createShapeEngine().createLine(p3.plus(new Point(0, i * -RushHourStud.SIZE)), p3.plus(new Point(0, (i+1) * -RushHourStud.SIZE))));
		}
		
		
		List<JointStud> jStuds = new ArrayList<JointStud>();
		List<JointStud> kStuds = new ArrayList<JointStud>();
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				char c = ini[i][j];
				switch (c) {
				case ' ':
					break;
				case 'X':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'R': {
					RegularStud s = new RegularStud(world, this, i - originRow, j - originCol);
					addStud(s);
					break;
				}
				case 'J':
				case 'K': {
					JointStud s = new JointStud(world, this, i - originRow, j - originCol);
					addStud(s);
					if (c == 'J') {
						jStuds.add(s);
					} else {
						kStuds.add(s);
					}
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setSide(Side.BOTTOM);
						s.f = f;
						world.addFixture(f);
					} else if (i >= originRow + rowCount) {
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
						assert j >= originCol + colCount;
						removePerimeterSegment(s.aabb.getP3P0Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setSide(Side.LEFT);
						s.f = f;
						world.addFixture(f);
					}
					break;
				}
				case 'Y': {
					ExitStud s = new ExitStud(world, this, i - originRow, j - originCol);
					addStud(s);
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
					} else if (i >= originRow + rowCount) {
						removePerimeterSegment(s.aabb.getP0P1Line());
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
					} else {
						assert j >= originCol + colCount;
						removePerimeterSegment(s.aabb.getP3P0Line());
					}
					break;
				}
				default:
					assert false;
				}
			}
		}
		
		addRoad(jStuds);
		addRoad(kStuds);
		
		/*
		 * create paths 
		 */
		
		
		Map<Integer, List<RushHourBoardPosition>> rowTracks = new HashMap<Integer, List<RushHourBoardPosition>>();
		Map<Integer, List<RushHourBoardPosition>> colTracks = new HashMap<Integer, List<RushHourBoardPosition>>();
		
		/*
		 * row tracks
		 */
		for (int i = 0; i < rowCount; i++) {
			List<RushHourBoardPosition> track = new ArrayList<RushHourBoardPosition>();
			for (int j = 0; j < colCount; j++) {
				track.add(new RushHourBoardPosition(this, i + 0.5, j));
			}
			rowTracks.put(i, track);
		}
		
		/*
		 * col tracks
		 */
		for (int i = 0; i < colCount; i++) {
			List<RushHourBoardPosition> track = new ArrayList<RushHourBoardPosition>();
			for (int j = 0; j < rowCount; j++) {
				track.add(new RushHourBoardPosition(this, j, i + 0.5));
			}
			colTracks.put(i, track);
		}
		

		
//		List<RushHourBoardPosition> js0Track;
//		if (js0.col < 0) {
//			js0Track = rowTracks.remove(js0.row);
//		} else if (js0.col >= colCount) {
//			js0Track = rowTracks.remove(js0.row);
//		} else if (js0.row < 0) {
//			js0Track = colTracks.remove(js0.col);
//		} else {
//			assert js0.row >= rowCount;
//			js0Track = colTracks.remove(js0.col);
//		}
//		
//		List<RushHourBoardPosition> js1Track;
//		if (js1.col < 0) {
//			js1Track = rowTracks.remove(js1.row);
//		} else if (js1.col >= colCount) {
//			js1Track = rowTracks.remove(js1.row);
//		} else if (js1.row < 0) {
//			js1Track = colTracks.remove(js1.col);
//		} else {
//			assert js1.row >= rowCount;
//			js1Track = colTracks.remove(js1.col);
//		}
//		
//		
//		List<GraphPosition> poss = new ArrayList<GraphPosition>();
////		from other side of js0 track to js0
//		if (js0.col < 0) {
//			for (int i = colCount-1; i >= 1; i--) {
//				poss.add(js0Track.get(i));
//			}
//		} else if (js0.col >= colCount) {
//			for (int i = 0; i < colCount-1; i++) {
//				poss.add(js0Track.get(i));
//			}
//		} else if (js0.row < 0) {
//			for (int i = rowCount-1; i >= 1; i--) {
//				poss.add(js0Track.get(i));
//			}
//		} else {
//			assert js0.row >= rowCount;
//			for (int i = 0; i < rowCount-1; i++) {
//				poss.add(js0Track.get(i));
//			}
//		}
////		to vertex
////		to road
////		to vertex
//		Road r = js0.f.roads.get(0);
//		if (js0.f == r.start) {
//			poss.add(new VertexPosition(r.start));
//			for (int ii = 1; ii <= r.pointCount()-2; ii++) {
//				poss.add(new RoadPosition(r, ii, 0.0));
//			}
//			poss.add(new VertexPosition(r.end));
//		} else {
//			poss.add(new VertexPosition(r.end));
//			for (int ii = r.pointCount()-2; ii >= 1; ii--) {
//				poss.add(new RoadPosition(r, ii, 0.0));
//			}
//			poss.add(new VertexPosition(r.start));
//		}
////		to js1
////		to other side of js1 track
//		if (js1.col < 0) {
//			for (int i = 1; i < colCount; i++) {
//				poss.add(js1Track.get(i));
//			}
//		} else if (js1.col >= colCount) {
//			for (int i = colCount-1; i >= 0; i--) {
//				poss.add(js1Track.get(i));
//			}
//		} else if (js1.row < 0) {
//			for (int i = 1; i > rowCount; i++) {
//				poss.add(js1Track.get(i));
//			}
//		} else {
//			assert js1.row >= rowCount;
//			for (int i = rowCount-1; i >= 0; i--) {
//				poss.add(js1Track.get(i));
//			}
//		}
//		
////		add to rowPaths and colPaths as needed
//		GraphPositionPath path = new GraphPositionPath(poss);
//		if (js0.col < 0) {
//			rowPaths.put(js0.row, path);
//		} else if (js0.col >= colCount) {
//			rowPaths.put(js0.row, path);
//		} else if (js0.row < 0) {
//			colPaths.put(js0.col, path);
//		} else {
//			assert js0.row >= rowCount;
//			colPaths.put(js0.col, path);
//		}
//		
//		if (js1.col < 0) {
//			rowPaths.put(js1.row, path);
//		} else if (js1.col >= colCount) {
//			rowPaths.put(js1.row, path);
//		} else if (js1.row < 0) {
//			colPaths.put(js1.col, path);
//		} else {
//			assert js1.row >= rowCount;
//			colPaths.put(js1.col, path);
//		}
		
		
		
		
		
		
		
		
		
		
		
		/*
		 * regular row tracks
		 */
		List<GraphPosition> poss;
		GraphPositionPath path;
		
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
	
	private void addRoad(List<JointStud> joints) {
		
		JointStud js0 = joints.get(0);
		JointStud js1 = joints.get(1);
		Fixture f0 = js0.f;
		Fixture f1 = js1.f;
		Point start;
		Point c0;
		Point c1;
		Point end;
		double straightExtension = 1.5;
		double controlExtension = 18;
		if (js0.col < 0) {
			start = new Point(f0.p.x - straightExtension, f0.p.y);
			c0 = new Point(f0.p.x - controlExtension, f0.p.y);
		} else if (js0.col >= colCount) {
			start = new Point(f0.p.x + straightExtension, f0.p.y);
			c0 = new Point(f0.p.x + controlExtension, f0.p.y);
		} else if (js0.row < 0) {
			start = new Point(f0.p.x, f0.p.y - straightExtension);
			c0 = new Point(f0.p.x, f0.p.y - controlExtension);
		} else {
			assert js0.row >= rowCount;
			start = new Point(f0.p.x, f0.p.y + straightExtension);
			c0 = new Point(f0.p.x, f0.p.y + controlExtension);
		}
		if (js1.col < 0) {
			end = new Point(f1.p.x - straightExtension, f1.p.y);
			c1 = new Point(f1.p.x - controlExtension, f1.p.y);
		} else if (js1.col >= colCount) {
			end = new Point(f1.p.x + straightExtension, f1.p.y);
			c1 = new Point(f1.p.x + controlExtension, f1.p.y);
		} else if (js1.row < 0) {
			end = new Point(f1.p.x, f1.p.y - straightExtension);
			c1 = new Point(f1.p.x, f1.p.y - controlExtension);
		} else {
			assert js1.row >= rowCount;
			end = new Point(f1.p.x, f1.p.y + straightExtension);
			c1 = new Point(f1.p.x, f1.p.y + controlExtension);
		}
		CubicCurve c = APP.platform.createShapeEngine().createCubicCurve(start, c0, c1, end);
		
		List<Point> pts = c.skeleton();
		
		Stroke stroke = new Stroke(world);
		stroke.add(f0.p);
		for (Point p : pts) {
			stroke.add(p);
		}
		stroke.add(f1.p);
		stroke.finish();
		
		stroke.processNewStroke();
		
	}
	
	private void addStud(RushHourStud s) {
		studs.add(s);
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
