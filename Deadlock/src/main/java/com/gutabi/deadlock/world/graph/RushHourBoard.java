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
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
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
	public AABB allStudsAABB;
	public AABB gridAABB;
	
	Map<Integer, List<RushHourBoardPosition>> rowTracks = new HashMap<Integer, List<RushHourBoardPosition>>();
	Map<Integer, List<RushHourBoardPosition>> colTracks = new HashMap<Integer, List<RushHourBoardPosition>>();
	private Map<Integer, GraphPositionPath> rowPaths = new HashMap<Integer, GraphPositionPath>();
	private Map<Integer, GraphPositionPath> colPaths = new HashMap<Integer, GraphPositionPath>();
	
	List<ExitStud> exitStuds = new ArrayList<ExitStud>();
	
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
		gridAABB = new AABB(ul.x, ul.y, colCount * RushHourStud.SIZE, rowCount * RushHourStud.SIZE);
		
		/*
		 * add perimeter and add studs
		 */
		
		Point p0 = ul;
		for (int i = 0; i < colCount; i++) {
			perimeterSegments.add(new Line(p0.plus(new Point(i * RushHourStud.SIZE, 0)), p0.plus(new Point((i+1) * RushHourStud.SIZE, 0))));
		}
		Point p1 = ul.plus(new Point(colCount * RushHourStud.SIZE, 0));
		for (int i = 0; i < rowCount; i++) {
			perimeterSegments.add(new Line(p1.plus(new Point(0, i * RushHourStud.SIZE)), p1.plus(new Point(0, (i+1) * RushHourStud.SIZE))));
		}
		Point p2 = ul.plus(new Point(colCount * RushHourStud.SIZE, rowCount * RushHourStud.SIZE));
		for (int i = 0; i < colCount; i++) {
			perimeterSegments.add(new Line(p2.plus(new Point(i * -RushHourStud.SIZE, 0)), p2.plus(new Point((i+1) * -RushHourStud.SIZE, 0))));
		}
		Point p3 = ul.plus(new Point(0, rowCount * RushHourStud.SIZE));
		for (int i = 0; i < rowCount; i++) {
			perimeterSegments.add(new Line(p3.plus(new Point(0, i * -RushHourStud.SIZE)), p3.plus(new Point(0, (i+1) * -RushHourStud.SIZE))));
		}
		
		
		int jStudCount = 0;
		JointStud[] jStuds = new JointStud[2];
		int kStudCount = 0;
		JointStud[] kStuds = new JointStud[2];
		ExitStud yStud = null;
		
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
						jStuds[jStudCount] = s;
						jStudCount++;
					} else {
						kStuds[kStudCount] = s;
						kStudCount++;
					}
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setFacingSide(Side.BOTTOM);
						s.f = f;
						world.addFixture(f);
					} else if (i >= originRow + rowCount) {
						removePerimeterSegment(s.aabb.getP0P1Line());
						Fixture f = new Fixture(world, point(i - originRow + 1.0, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setFacingSide(Side.TOP);
						s.f = f;
						world.addFixture(f);
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol), Axis.LEFTRIGHT);
						f.setFacingSide(Side.RIGHT);
						s.f = f;
						world.addFixture(f);
					} else {
						assert j >= originCol + colCount;
						removePerimeterSegment(s.aabb.getP3P0Line());
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setFacingSide(Side.LEFT);
						s.f = f;
						world.addFixture(f);
					}
					break;
				}
				case 'Y': {
					ExitStud s = new ExitStud(world, this, i - originRow, j - originCol);
					yStud = s;
					addStud(s);
					exitStuds.add(s);
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						s = new ExitStud(world, this, i - originRow - 1, j - originCol);
						addStud(s);
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
//						perimeterSegments.add(s.aabb.getP2P3Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						exitStuds.add(s);
					} else if (i >= originRow + rowCount) {
						removePerimeterSegment(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						s = new ExitStud(world, this, i - originRow + 1, j - originCol);
						addStud(s);
//						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						exitStuds.add(s);
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						s = new ExitStud(world, this, i - originRow, j - originCol - 1);
						addStud(s);
						perimeterSegments.add(s.aabb.getP0P1Line());
//						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						exitStuds.add(s);
					} else {
						assert j >= originCol + colCount;
						removePerimeterSegment(s.aabb.getP3P0Line());
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						s = new ExitStud(world, this, i - originRow, j - originCol + 1);
						addStud(s);
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
//						perimeterSegments.add(s.aabb.getP3P0Line());
						exitStuds.add(s);
					}
					break;
				}
				default:
					assert false;
				}
			}
		}
		
		if (jStudCount != 0) {
			addRoad(jStuds);
		}
		if (kStudCount != 0) {
			addRoad(kStuds);
		}
		
		/*
		 * create paths 
		 */
		
		/*
		 * row tracks
		 */
		for (int i = 0; i < rowCount; i++) {
			List<RushHourBoardPosition> track = new ArrayList<RushHourBoardPosition>();
			if (yStud.col == -1 && yStud.row == i) {
				track.add(new RushHourBoardPosition(this, i + 0.5, -2));
				track.add(new RushHourBoardPosition(this, i + 0.5, -1));
			} else if ((jStudCount == 2 && (jStuds[0].col == -1 && jStuds[0].row == i || jStuds[1].col == -1 && jStuds[1].row == i)) ||
					(kStudCount == 2 && (kStuds[0].col == -1 && kStuds[0].row == i || kStuds[1].col == -1 && kStuds[1].row == i))) {
				track.add(new RushHourBoardPosition(this, i + 0.5, -1));
			}
			for (int j = 0; j < colCount; j++) {
				track.add(new RushHourBoardPosition(this, i + 0.5, j));
			}
			if (yStud.col == colCount && yStud.row == i) {
				track.add(new RushHourBoardPosition(this, i + 0.5, colCount));
				track.add(new RushHourBoardPosition(this, i + 0.5, colCount+1));
			} else if ((jStudCount == 2 && (jStuds[0].col == colCount && jStuds[0].row == i || jStuds[1].col == colCount && jStuds[1].row == i)) ||
					(kStudCount == 2 && (kStuds[0].col == colCount && kStuds[0].row == i || kStuds[1].col == colCount && kStuds[1].row == i))) {
				track.add(new RushHourBoardPosition(this, i + 0.5, colCount));
			}
			rowTracks.put(i, track);
		}
		
		/*
		 * col tracks
		 */
		for (int i = 0; i < colCount; i++) {
			List<RushHourBoardPosition> track = new ArrayList<RushHourBoardPosition>();
			if (yStud.row == -1 && yStud.col == i) {
				track.add(new RushHourBoardPosition(this, -2, i + 0.5));
				track.add(new RushHourBoardPosition(this, -1, i + 0.5));
			} else if ((jStudCount == 2 && (jStuds[0].row == -1 && jStuds[0].col == i || jStuds[1].row == -1 && jStuds[1].col == i)) ||
					(kStudCount == 2 && (kStuds[0].row == -1 && kStuds[0].col == i || kStuds[1].row == -1 && kStuds[1].col == i))) {
				track.add(new RushHourBoardPosition(this, -1, i + 0.5));
			}
			for (int j = 0; j < rowCount; j++) {
				track.add(new RushHourBoardPosition(this, j, i + 0.5));
			}
			if (yStud.row == rowCount && yStud.col == i) {
				track.add(new RushHourBoardPosition(this, rowCount, i + 0.5));
				track.add(new RushHourBoardPosition(this, rowCount+1, i + 0.5));
			} else if ((jStudCount == 2 && (jStuds[0].row == rowCount && jStuds[0].col == i || jStuds[1].row == rowCount && jStuds[1].col == i)) ||
					(kStudCount == 2 && (kStuds[0].row == rowCount && kStuds[0].col == i || kStuds[1].row == rowCount && kStuds[1].col == i))) {
				track.add(new RushHourBoardPosition(this, rowCount, i + 0.5));
			}
			colTracks.put(i, track);
		}
		
		/*
		 * joint tracks -> paths
		 */
		if (jStudCount != 0 && kStudCount != 0) {
			
			JointStud js0 = jStuds[0];
			JointStud js1 = jStuds[1];
			JointStud ks0 = kStuds[0];
			JointStud ks1 = kStuds[1];
			
			if (withinRowRange(js0.row) && withinRowRange(ks0.row) && js0.row == ks0.row) {
				jointTracksToPath(js1, js0, ks0, ks1);
			} else if (withinRowRange(js0.row) && withinRowRange(ks1.row) && js0.row == ks1.row) {
				jointTracksToPath(js1, js0, ks1, ks0);
			} else if (withinRowRange(js1.row) && withinRowRange(ks0.row) && js1.row == ks0.row) {
				jointTracksToPath(js0, js1, ks0, ks1);
			} else if (withinRowRange(js1.row) && withinRowRange(ks1.row) && js1.row == ks1.row) {
				jointTracksToPath(js0, js1, ks1, ks0);
			} else if (withinColRange(js0.col) && withinColRange(ks0.col) && js0.col == ks0.col) {
				jointTracksToPath(js1, js0, ks0, ks1);
			} else if (withinColRange(js0.col) && withinColRange(ks1.col) && js0.col == ks1.col) {
				jointTracksToPath(js1, js0, ks1, ks0);
			} else if (withinColRange(js1.col) && withinColRange(ks0.col) && js1.col == ks0.col) {
				jointTracksToPath(js0, js1, ks0, ks1);
			} else if (withinColRange(js1.col) && withinColRange(ks1.col) && js1.col == ks1.col) {
				jointTracksToPath(js0, js1, ks1, ks0);
			} else {
				if (jStudCount != 0) {
					jointTracksToPath(jStuds[0], jStuds[1]);
				}
				if (kStudCount != 0) {
					jointTracksToPath(kStuds[0], kStuds[1]);
				}
			}
		} else {
			if (jStudCount != 0) {
				jointTracksToPath(jStuds[0], jStuds[1]);
			}
			if (kStudCount != 0) {
				jointTracksToPath(kStuds[0], kStuds[1]);
			}
		}
		
		/*
		 * regular row tracks -> paths
		 */
		List<GraphPosition> poss;
		GraphPositionPath path;
		
		for (Entry<Integer, List<RushHourBoardPosition>> entry : rowTracks.entrySet()) {
			Integer i = entry.getKey();
			
			if (!rowPaths.containsKey(i)) {
				List<RushHourBoardPosition> rt = entry.getValue();
				poss = new ArrayList<GraphPosition>();
				for (RushHourBoardPosition pos : rt) {
					poss.add(pos);
				}
				path = new GraphPositionPath(poss);
				rowPaths.put(i, path);
			}
			
		}
		
		/*
		 * regular col tracks
		 */
		for (Entry<Integer, List<RushHourBoardPosition>> entry : colTracks.entrySet()) {
			Integer i = entry.getKey();
			
			if (!colPaths.containsKey(i)) {
				List<RushHourBoardPosition> ct = entry.getValue();
				poss = new ArrayList<GraphPosition>();
				for (RushHourBoardPosition pos : ct) {
					poss.add(pos);
				}
				path = new GraphPositionPath(poss);
				colPaths.put(i, path);
			}
			
		}
		
		/*
		 * negative space
		 */
		
		int colCount = (int)Math.round(allStudsAABB.width / RushHourStud.SIZE);
		int rowCount = (int)Math.round(allStudsAABB.height / RushHourStud.SIZE);
		for (int i = 0; i < rowCount; i++) {
			jloop:
			for (int j = 0; j < colCount; j++) {
				AABB n = new AABB(allStudsAABB.ul.x + j * RushHourStud.SIZE, allStudsAABB.ul.y + i * RushHourStud.SIZE, RushHourStud.SIZE, RushHourStud.SIZE);
				
				for (RushHourStud ss : studs) {
					if (ss.aabb.equals(n)) {
						continue jloop;
					}
				}
				
				neg.add(n);
			}
		}	
		
	}
	
	boolean withinRowRange(int r) {
		return r >= 0 && r < rowCount;
	}
	
	boolean withinColRange(int c) {
		return c >= 0 && c < colCount;
	}
	
	private void addRoad(JointStud[] joints) {
		
		JointStud js0 = joints[0];
		JointStud js1 = joints[1];
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
	
	private void jointTracksToPath(JointStud s0, JointStud s1) {
				
		List<RushHourBoardPosition> s0Track;
		if (s0.col < 0) {
			s0Track = rowTracks.remove(s0.row);
		} else if (s0.col >= colCount) {
			s0Track = rowTracks.remove(s0.row);
		} else if (s0.row < 0) {
			s0Track = colTracks.remove(s0.col);
		} else {
			assert s0.row >= rowCount;
			s0Track = colTracks.remove(s0.col);
		}
		assert s0Track != null;
		
		List<RushHourBoardPosition> s1Track;
		if (s1.col < 0) {
			s1Track = rowTracks.remove(s1.row);
		} else if (s1.col >= colCount) {
			s1Track = rowTracks.remove(s1.row);
		} else if (s1.row < 0) {
			s1Track = colTracks.remove(s1.col);
		} else {
			assert s1.row >= rowCount;
			s1Track = colTracks.remove(s1.col);
		}
		assert s1Track != null;
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
//		from other side of js0 track to js0
		if (s0.col < 0) {
//			last iteration is stud and negative, so skip
			for (int i = s0Track.size()-1; i >= 1; i--) {
				poss.add(s0Track.get(i));
			}
		} else if (s0.col >= colCount) {
//			d;
			for (int i = 0; i < s0Track.size(); i++) {
				poss.add(s0Track.get(i));
			}
		} else if (s0.row < 0) {
//			last iteration is stud and negative, so skip
			for (int i = s0Track.size()-1; i >= 1; i--) {
				poss.add(s0Track.get(i));
			}
		} else {
			assert s0.row >= rowCount;
//			d;
			for (int i = 0; i < s0Track.size(); i++) {
				poss.add(s0Track.get(i));
			}
		}
		
		Vertex v = s0.f;
//		to vertex
		poss.add(new VertexPosition(v));
		
		Road r = null;
		roadLoop:
		while (true) {
//			to road
//			to vertex
			
			r = v.bestMatchingRoad(r, s1.f.roads.get(0));
			
			if (v == r.start) {
				for (int ii = 1; ii <= r.pointCount()-2; ii++) {
					poss.add(new RoadPosition(r, ii, 0.0));
				}
				poss.add(new VertexPosition(r.end));
				if (r.end == s1.f) {
					break roadLoop;
				}
				v = r.end;
			} else {
				for (int ii = r.pointCount()-2; ii >= 1; ii--) {
					poss.add(new RoadPosition(r, ii, 0.0));
				}
				poss.add(new VertexPosition(r.start));
				if (r.start == s1.f) {
					break roadLoop;
				}
				v = r.start;
			}
		}
		
//		to js1
//		to other side of js1 track
		if (s1.col < 0) {
//			first iteration is stud and negative, so skip
			for (int i = 1; i < s1Track.size(); i++) {
				poss.add(s1Track.get(i));
			}
		} else if (s1.col >= colCount) {
//			d;
			for (int i = s1Track.size()-1; i >= 0; i--) {
				poss.add(s1Track.get(i));
			}
		} else if (s1.row < 0) {
//			first iteration is stud and negative, so skip
			for (int i = 1; i < s1Track.size(); i++) {
				poss.add(s1Track.get(i));
			}
		} else {
			assert s1.row >= rowCount;
//			d;
			for (int i = s1Track.size()-1; i >= 0; i--) {
				poss.add(s1Track.get(i));
			}
		}
		
//		add to rowPaths and colPaths as needed
		GraphPositionPath path = new GraphPositionPath(poss);
		GraphPositionPath res;
		if (s0.col < 0) {
			res = rowPaths.put(s0.row, path);
			assert res == null;
		} else if (s0.col >= colCount) {
			res = rowPaths.put(s0.row, path);
			assert res == null;
		} else if (s0.row < 0) {
			res = colPaths.put(s0.col, path);
			assert res == null;
		} else {
			assert s0.row >= rowCount;
			res = colPaths.put(s0.col, path);
			assert res == null;
		}
		
		if (s1.col < 0) {
			res = rowPaths.put(s1.row, path);
			assert res == null;
		} else if (s1.col >= colCount) {
			res = rowPaths.put(s1.row, path);
			assert res == null;
		} else if (s1.row < 0) {
			res = colPaths.put(s1.col, path);
			assert res == null;
		} else {
			assert s1.row >= rowCount;
			res = colPaths.put(s1.col, path);
			assert res == null;
		}
		
	}
	
	private void jointTracksToPath(JointStud js0, JointStud js1, JointStud ks1, JointStud ks0) {
		
		List<RushHourBoardPosition> js0Track;
		if (js0.col < 0) {
			js0Track = rowTracks.remove(js0.row);
		} else if (js0.col >= colCount) {
			js0Track = rowTracks.remove(js0.row);
		} else if (js0.row < 0) {
			js0Track = colTracks.remove(js0.col);
		} else {
			assert js0.row >= rowCount;
			js0Track = colTracks.remove(js0.col);
		}
		assert js0Track != null;
		
		List<RushHourBoardPosition> js1Track;
		if (js1.col < 0) {
			js1Track = rowTracks.remove(js1.row);
		} else if (js1.col >= colCount) {
			js1Track = rowTracks.remove(js1.row);
		} else if (js1.row < 0) {
			js1Track = colTracks.remove(js1.col);
		} else {
			assert js1.row >= rowCount;
			js1Track = colTracks.remove(js1.col);
		}
		assert js1Track != null;
		
		List<RushHourBoardPosition> ks1Track = js1Track;
		assert ks1Track != null;
		
		List<RushHourBoardPosition> ks0Track;
		if (ks0.col < 0) {
			ks0Track = rowTracks.remove(ks0.row);
		} else if (ks0.col >= colCount) {
			ks0Track = rowTracks.remove(ks0.row);
		} else if (ks0.row < 0) {
			ks0Track = colTracks.remove(ks0.col);
		} else {
			assert ks0.row >= rowCount;
			ks0Track = colTracks.remove(ks0.col);
		}
		assert ks0Track != null;
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
		
//		from other side of js0 track to js0
		if (js0.col < 0) {
//			last iteration is stud and negative, so skip
			for (int i = js0Track.size()-1; i >= 1; i--) {
				poss.add(js0Track.get(i));
			}
		} else if (js0.col >= colCount) {
//			d;
			for (int i = 0; i < js0Track.size(); i++) {
				poss.add(js0Track.get(i));
			}
		} else if (js0.row < 0) {
//			last iteration is stud and negative, so skip
			for (int i = js0Track.size()-1; i >= 1; i--) {
				poss.add(js0Track.get(i));
			}
		} else {
			assert js0.row >= rowCount;
//			d;
			for (int i = 0; i < js0Track.size(); i++) {
				poss.add(js0Track.get(i));
			}
		}
		
		Vertex v = js0.f;
//		to vertex
		poss.add(new VertexPosition(v));
		
		Road r = null;
		roadLoop:
		while (true) {
//			to road
//			to vertex
			
			r = v.bestMatchingRoad(r, js1.f.roads.get(0));
			
			if (v == r.start) {
				for (int ii = 1; ii <= r.pointCount()-2; ii++) {
					poss.add(new RoadPosition(r, ii, 0.0));
				}
				poss.add(new VertexPosition(r.end));
				if (r.end == js1.f) {
					break roadLoop;
				}
				v = r.end;
			} else {
				for (int ii = r.pointCount()-2; ii >= 1; ii--) {
					poss.add(new RoadPosition(r, ii, 0.0));
				}
				poss.add(new VertexPosition(r.start));
				if (r.start == js1.f) {
					break roadLoop;
				}
				v = r.start;
			}
		}
		
//		to js1
//		to other side of js1 track (ks1)
		if (js1.col < 0) {
//			first iteration is stud and negative, so skip
			for (int i = 1; i < js1Track.size(); i++) {
				poss.add(js1Track.get(i));
			}
		} else if (js1.col >= colCount) {
//			last iteration is stud and negative, so skip
			for (int i = js1Track.size()-1; i >= 1; i--) {
				poss.add(js1Track.get(i));
			}
		} else if (js1.row < 0) {
//			first iteration is stud and negative, so skip
			for (int i = 1; i < js1Track.size(); i++) {
				poss.add(js1Track.get(i));
			}
		} else {
			assert js1.row >= rowCount;
//			last iteration is stud and negative, so skip
			for (int i = js1Track.size()-1; i >= 1; i--) {
				poss.add(js1Track.get(i));
			}
		}
		
		v = ks1.f;
//		to vertex
		poss.add(new VertexPosition(v));
		
		r = null;
		roadLoop:
		while (true) {
//			to road
//			to vertex
			
			r = v.bestMatchingRoad(r, ks0.f.roads.get(0));
			
			if (v == r.start) {
				for (int ii = 1; ii <= r.pointCount()-2; ii++) {
					poss.add(new RoadPosition(r, ii, 0.0));
				}
				poss.add(new VertexPosition(r.end));
				if (r.end == ks0.f) {
					break roadLoop;
				}
				v = r.end;
			} else {
				for (int ii = r.pointCount()-2; ii >= 1; ii--) {
					poss.add(new RoadPosition(r, ii, 0.0));
				}
				poss.add(new VertexPosition(r.start));
				if (r.start == ks0.f) {
					break roadLoop;
				}
				v = r.start;
			}
		}
		
//		to ks0
//		to other side of ks0 track
		if (ks0.col < 0) {
//			first iteration is stud and negative, so skip
			for (int i = 1; i < ks0Track.size(); i++) {
				poss.add(ks0Track.get(i));
			}
		} else if (ks0.col >= colCount) {
//			d;
			for (int i = ks0Track.size()-1; i >= 0; i--) {
				poss.add(ks0Track.get(i));
			}
		} else if (ks0.row < 0) {
//			first iteration is stud and negative, so skip
			for (int i = 1; i < ks0Track.size(); i++) {
				poss.add(ks0Track.get(i));
			}
		} else {
			assert ks0.row >= rowCount;
//			d;
			for (int i = ks0Track.size()-1; i >= 0; i--) {
				poss.add(ks0Track.get(i));
			}
		}
		
//		add to rowPaths and colPaths as needed
		GraphPositionPath path = new GraphPositionPath(poss);
		
		GraphPositionPath res;
		if (js0.col < 0) {
			res = rowPaths.put(js0.row, path);
			assert res == null;
		} else if (js0.col >= colCount) {
			res = rowPaths.put(js0.row, path);
			assert res == null;
		} else if (js0.row < 0) {
			res = colPaths.put(js0.col, path);
			assert res == null;
		} else {
			assert js0.row >= rowCount;
			res = colPaths.put(js0.col, path);
			assert res == null;
		}
		
		if (js1.col < 0) {
			res = rowPaths.put(js1.row, path);
			assert res == null;
		} else if (js1.col >= colCount) {
			res = rowPaths.put(js1.row, path);
			assert res == null;
		} else if (js1.row < 0) {
			res = colPaths.put(js1.col, path);
			assert res == null;
		} else {
			assert js1.row >= rowCount;
			res = colPaths.put(js1.col, path);
			assert res == null;
		}
		
		if (ks0.col < 0) {
			res = rowPaths.put(ks0.row, path);
			assert res == null;
		} else if (ks0.col >= colCount) {
			res = rowPaths.put(ks0.row, path);
			assert res == null;
		} else if (ks0.row < 0) {
			res = colPaths.put(ks0.col, path);
			assert res == null;
		} else {
			assert ks0.row >= rowCount;
			res = colPaths.put(ks0.col, path);
			assert res == null;
		}
	}
	
	private void addStud(RushHourStud s) {
		studs.add(s);
		allStudsAABB = AABB.union(allStudsAABB, s.aabb);
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
		}
	}
	
//	public boolean allowablePosition(Car c) {
//		if (ShapeUtils.containsAO(gridAABB, c.shape)) {
//			return true;
//		}
//		
//		if (c.sprite == CarSheetSprite.RED) {
//			for (ExitStud s : exitStuds) {
//				if (ShapeUtils.intersectAO(s.aabb, c.shape)) {
//					return true;
//				}
//			}
//		}
//		
//		return false;
//	}
	
	public RushHourStud stud(RushHourBoardPosition pos) {
		for (RushHourStud stud : studs) {
			if (stud.row == pos.rowIndex && stud.col == pos.colIndex) {
				return stud;
			}
		}
		assert false;
		return null;
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
	
	public GraphPositionPath getPath(Axis a, int index) {
		switch (a) {
		case LEFTRIGHT:
			return rowPaths.get(index);
		case TOPBOTTOM:
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
		
		if (APP.DEBUG_DRAW) {
			
			for (Line l : perimeterSegments) {
				ctxt.setColor(Color.RED);
				double pixel = 1/world.worldScreen.pixelsPerMeter;
				ctxt.setStroke(2.0 * pixel, Cap.SQUARE, Join.MITER);
				l.draw(ctxt);
			}
			
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
