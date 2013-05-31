package com.brentonbostick.capsloc.world.graph;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.brentonbostick.capsloc.Entity;
import com.brentonbostick.capsloc.geom.AABB;
import com.brentonbostick.capsloc.geom.CubicCurve;
import com.brentonbostick.capsloc.geom.Geom;
import com.brentonbostick.capsloc.geom.Line;
import com.brentonbostick.capsloc.geom.MutableAABB;
import com.brentonbostick.capsloc.geom.MutableOBB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.Image;
import com.brentonbostick.capsloc.ui.paint.Cap;
import com.brentonbostick.capsloc.ui.paint.Color;
import com.brentonbostick.capsloc.ui.paint.Join;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.Stroke;
import com.brentonbostick.capsloc.world.World;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.graph.gpp.GraphPositionPath;
import com.brentonbostick.capsloc.world.graph.gpp.MutableGPPP;

public class BypassBoard extends Entity {
	
	public static final Color BOARDCOLOR = new Color(0xE1, 0xE1, 0xE1, 255);
	
	public static final double STRAIGHTEXTENSION = 1.5;
	public static final double CONTROLEXTENSION = 18;
	public static final double EXITROADLENGTH = 30;
	
	public final World world;
	public final Point center;
	public final char[][] ini;
	
	public List<BypassStud> studs = new ArrayList<BypassStud>();
	
	public List<Line> perimeterSegments = new ArrayList<Line>();
	
	public List<AABB> neg = new ArrayList<AABB>();
	
	public int originRow;
	public int originCol;
	
	public int rowCount;
	public int colCount;
	
	public Point ul;
	public AABB allStudsAABB;
	public AABB gridAABB;
	/*
	 * gridFudgeAABB is like gridAABB, but with fudge
	 * when a car is leaving the board, when it starts to turn, its AABB slightly extends over gridAABB
	 * gridFudgeAABB should contain a car as its exiting the board, and only have overlap on the side that the car is leaving
	 */
	public AABB gridFudgeAABB;
	public AABB backAABB;
	public AABB zoomingAABB;
	
	int jStudCount = 0;
	BorderStud[] jStuds = new BorderStud[2];
	int kStudCount = 0;
	BorderStud[] kStuds = new BorderStud[2];
	BorderStud yStud = null;
	public Vertex exitVertex;
	
	Map<Integer, List<GraphPosition>> rowTracks = new HashMap<Integer, List<GraphPosition>>();
	Map<Integer, List<GraphPosition>> colTracks = new HashMap<Integer, List<GraphPosition>>();
	private Map<Integer, GraphPositionPath> rowPaths = new HashMap<Integer, GraphPositionPath>();
	private Map<Integer, GraphPositionPath> colPaths = new HashMap<Integer, GraphPositionPath>();
	
	Image img;
	
	public BypassBoard(World world, Point center, char[][] ini) {
		this.world = world;
		this.center = center;
		this.ini = ini;
		
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
				case ' ':
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
			case ' ':
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
			case ' ':
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
		
		ul = new Point(center.x - 0.5 * colCount * BypassStud.SIZE, center.y - 0.5 * rowCount * BypassStud.SIZE);
		gridAABB = new AABB(ul.x, ul.y, colCount * BypassStud.SIZE, rowCount * BypassStud.SIZE);
		gridFudgeAABB = new AABB(ul.x - 0.1, ul.y - 0.1, colCount * BypassStud.SIZE + 0.2, rowCount * BypassStud.SIZE + 0.2);
		backAABB = new AABB(ul.x + 0.5 * BypassStud.SIZE, ul.y + 0.5 * BypassStud.SIZE, (colCount - 1) * BypassStud.SIZE, (rowCount - 1) * BypassStud.SIZE);
		allStudsAABB = new AABB(ul.x - BypassStud.SIZE, ul.y - BypassStud.SIZE, (colCount + 2) * BypassStud.SIZE, (rowCount + 2) * BypassStud.SIZE);
		double zoomingExtension = 5.0;
		zoomingAABB = new AABB(ul.x - zoomingExtension, ul.y - zoomingExtension, gridAABB.width + 2 * zoomingExtension, gridAABB.height + 2 * zoomingExtension);
		
		/*
		 * add perimeter and add studs
		 */
		
		Point p0 = ul;
		for (int i = 0; i < colCount; i++) {
			perimeterSegments.add(new Line(p0.plus(new Point(i * BypassStud.SIZE, 0)), p0.plus(new Point((i+1) * BypassStud.SIZE, 0))));
		}
		Point p1 = ul.plus(new Point(colCount * BypassStud.SIZE, 0));
		for (int i = 0; i < rowCount; i++) {
			perimeterSegments.add(new Line(p1.plus(new Point(0, i * BypassStud.SIZE)), p1.plus(new Point(0, (i+1) * BypassStud.SIZE))));
		}
		Point p2 = ul.plus(new Point(colCount * BypassStud.SIZE, rowCount * BypassStud.SIZE));
		for (int i = 0; i < colCount; i++) {
			perimeterSegments.add(new Line(p2.plus(new Point(i * -BypassStud.SIZE, 0)), p2.plus(new Point((i+1) * -BypassStud.SIZE, 0))));
		}
		Point p3 = ul.plus(new Point(0, rowCount * BypassStud.SIZE));
		for (int i = 0; i < rowCount; i++) {
			perimeterSegments.add(new Line(p3.plus(new Point(0, i * -BypassStud.SIZE)), p3.plus(new Point(0, (i+1) * -BypassStud.SIZE))));
		}
		
		for (int i = 0; i < ini.length; i++) {
			for (int j = 0; j < ini[i].length; j++) {
				char c = ini[i][j];
				switch (c) {
				
				case '/':
				case '-':
				case '\\':
				case '|':
					break;
					
				case 'X':
				case ' ':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'R': {
					RegularStud s = new RegularStud(world, this, i - originRow, j - originCol);
					studs.add(s);
					break;
				}
				case 'J':
				case 'K': {
					BorderStud s = new BorderStud(world, this, i - originRow, j - originCol);
					studs.add(s);
					if (c == 'J') {
						jStuds[jStudCount] = s;
						jStudCount++;
					} else {
						kStuds[kStudCount] = s;
						kStudCount++;
					}
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setFacingSide(Side.BOTTOM);
						s.f = f;
						f.s = s;
						world.addFixture(f);
					} else if (i >= originRow + rowCount) {
						removePerimeterSegment(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						
						Fixture f = new Fixture(world, point(i - originRow + 1.0, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setFacingSide(Side.TOP);
						s.f = f;
						f.s = s;
						world.addFixture(f);
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol), Axis.LEFTRIGHT);
						f.setFacingSide(Side.RIGHT);
						s.f = f;
						f.s = s;
						world.addFixture(f);
					} else {
						assert j >= originCol + colCount;
						removePerimeterSegment(s.aabb.getP3P0Line());
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setFacingSide(Side.LEFT);
						s.f = f;
						f.s = s;
						world.addFixture(f);
					}
					break;
				}
				case 'Y': {
					BorderStud s = new BorderStud(world, this, i - originRow, j - originCol);
					yStud = s;
					studs.add(s);
					if (i < originRow) {
						removePerimeterSegment(s.aabb.getP2P3Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						
						Fixture f = new Fixture(world, point(i - originRow, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setFacingSide(Side.BOTTOM);
						s.f = f;
						f.s = s;
						world.addFixture(f);
						exitVertex = f;
						
					} else if (i >= originRow + rowCount) {
						removePerimeterSegment(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP3P0Line());
						
						Fixture f = new Fixture(world, point(i - originRow + 1.0, j - originCol + 0.5), Axis.TOPBOTTOM);
						f.setFacingSide(Side.TOP);
						s.f = f;
						f.s = s;
						world.addFixture(f);
						exitVertex = f;
						
					} else if (j < originCol) {
						removePerimeterSegment(s.aabb.getP1P2Line());
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol), Axis.LEFTRIGHT);
						f.setFacingSide(Side.RIGHT);
						s.f = f;
						f.s = s;
						world.addFixture(f);
						exitVertex = f;
						
					} else {
						assert j >= originCol + colCount;
						removePerimeterSegment(s.aabb.getP3P0Line());
						perimeterSegments.add(s.aabb.getP0P1Line());
						perimeterSegments.add(s.aabb.getP2P3Line());
						
						Fixture f = new Fixture(world, point(i - originRow + 0.5, j - originCol + 1.0), Axis.LEFTRIGHT);
						f.setFacingSide(Side.LEFT);
						s.f = f;
						f.s = s;
						world.addFixture(f);
						exitVertex = f;
						
					}
					break;
				}
				default:
					assert false;
				}
			}
		}
		
		addExitRoad(yStud);
		
		if (jStudCount != 0) {
			addJointRoad(jStuds);
		}
		if (kStudCount != 0) {
			addJointRoad(kStuds);
		}
		
		/*
		 * create paths 
		 */
		
		/*
		 * row tracks
		 */
		for (int i = 0; i < rowCount; i++) {
			List<GraphPosition> track = new ArrayList<GraphPosition>();
			if (yStud.col == -1 && yStud.row == i) {
				addExitRoadToTrack(track, yStud);
			} else if ((jStudCount == 2 && (jStuds[0].col == -1 && jStuds[0].row == i || jStuds[1].col == -1 && jStuds[1].row == i)) ||
					(kStudCount == 2 && (kStuds[0].col == -1 && kStuds[0].row == i || kStuds[1].col == -1 && kStuds[1].row == i))) {
				track.add(new BypassBoardPosition(this, i + 0.5, -1));
			}
			for (int j = 0; j <= colCount; j++) {
				track.add(new BypassBoardPosition(this, i + 0.5, j));
			}
			if (yStud.col == colCount && yStud.row == i) {
				/*
				 * on right, so add last BypassBoardPosition
				 */
//				track.add(new BypassBoardPosition(this, i + 0.5, colCount));
				addExitRoadToTrack(track, yStud);
			} else if ((jStudCount == 2 && (jStuds[0].col == colCount && jStuds[0].row == i || jStuds[1].col == colCount && jStuds[1].row == i)) ||
					(kStudCount == 2 && (kStuds[0].col == colCount && kStuds[0].row == i || kStuds[1].col == colCount && kStuds[1].row == i))) {
//				track.add(new BypassBoardPosition(this, i + 0.5, colCount));
			}
			rowTracks.put(i, track);
		}
		
		/*
		 * col tracks
		 */
		for (int i = 0; i < colCount; i++) {
			List<GraphPosition> track = new ArrayList<GraphPosition>();
			if (yStud.row == -1 && yStud.col == i) {
				addExitRoadToTrack(track, yStud);
			} else if ((jStudCount == 2 && (jStuds[0].row == -1 && jStuds[0].col == i || jStuds[1].row == -1 && jStuds[1].col == i)) ||
					(kStudCount == 2 && (kStuds[0].row == -1 && kStuds[0].col == i || kStuds[1].row == -1 && kStuds[1].col == i))) {
				track.add(new BypassBoardPosition(this, -1, i + 0.5));
			}
			for (int j = 0; j <= rowCount; j++) {
				track.add(new BypassBoardPosition(this, j, i + 0.5));
			}
			if (yStud.row == rowCount && yStud.col == i) {
				/*
				 * on bottom, so add last BypassBoardPosition
				 */
//				track.add(new BypassBoardPosition(this, rowCount, i + 0.5));
				addExitRoadToTrack(track, yStud);
			} else if ((jStudCount == 2 && (jStuds[0].row == rowCount && jStuds[0].col == i || jStuds[1].row == rowCount && jStuds[1].col == i)) ||
					(kStudCount == 2 && (kStuds[0].row == rowCount && kStuds[0].col == i || kStuds[1].row == rowCount && kStuds[1].col == i))) {
//				track.add(new BypassBoardPosition(this, rowCount, i + 0.5));
			}
			colTracks.put(i, track);
		}
		
		/*
		 * joint tracks -> paths
		 */
		if (jStudCount != 0 && kStudCount != 0) {
			
			BorderStud js0 = jStuds[0];
			BorderStud js1 = jStuds[1];
			BorderStud ks0 = kStuds[0];
			BorderStud ks1 = kStuds[1];
			
			if (js0.across(ks0)) {
				if (js1.across(yStud)) {
					jointTracksToPath(ks1, ks0, js0, js1);
				} else if (ks1.across(yStud)) {
					jointTracksToPath(js1, js0, ks0, ks1);
				} else {
					// not connected to exit, order doesn't matter
					jointTracksToPath(js1, js0, ks0, ks1);
					
					exitTrackToPath(yStud);
				}
			} else if (js0.across(ks1)) {
				if (js1.across(yStud)) {
					jointTracksToPath(ks0, ks1, js0, js1);
				} else if (ks0.across(yStud)) {
					jointTracksToPath(js1, js0, ks1, ks0);
				} else {
					// not connected to exit, order doesn't matter
					jointTracksToPath(js1, js0, ks1, ks0);
					
					exitTrackToPath(yStud);
				}
			} else if (js1.across(ks0)) {
				if (js0.across(yStud)) {
					jointTracksToPath(ks1, ks0, js1, js0);
				} else if (ks1.across(yStud)) {
					jointTracksToPath(js0, js1, ks0, ks1);
				} else {
					// not connected to exit, order doesn't matter
					jointTracksToPath(js0, js1, ks0, ks1);
					
					exitTrackToPath(yStud);
				}
			} else if (js1.across(ks1)) {
				if (js0.across(yStud)) {
					jointTracksToPath(ks0, ks1, js1, js0);
				} else if (ks0.across(yStud)) {
					jointTracksToPath(js0, js1, ks1, ks0);
				} else {
					// not connected to exit, order doesn't matter
					jointTracksToPath(js0, js1, ks1, ks0);
					
					exitTrackToPath(yStud);
				}
			} else {
				
				/*
				 * not connected
				 */
				boolean yProcessed = false;
				if (js0.across(yStud)) {
					jointTracksToPath(js1, js0);
					yProcessed = true;
				} else if (js1.across(yStud)) {
					jointTracksToPath(js0, js1);
					yProcessed = true;
				} else {
					jointTracksToPath(js0, js1);
				}
				
				if (ks0.across(yStud)) {
					jointTracksToPath(ks1, ks0);
					yProcessed = true;
				} else if (ks1.across(yStud)) {
					jointTracksToPath(ks0, ks1);
					yProcessed = true;
				} else {
					jointTracksToPath(ks0, ks1);
				}
				
				if (!yProcessed) {
					exitTrackToPath(yStud);
				}
			}
			
		} else if (jStudCount != 0) {
			
			BorderStud js0 = jStuds[0];
			BorderStud js1 = jStuds[1];
			
			if (js0.across(yStud)) {
				jointTracksToPath(js1, js0);
			} else if (js1.across(yStud)) {
				jointTracksToPath(js0, js1);
			} else {
				jointTracksToPath(js0, js1);
				
				exitTrackToPath(yStud);
			}
			
		} else if (kStudCount != 0) {
			
			BorderStud ks0 = kStuds[0];
			BorderStud ks1 = kStuds[1];
			
			if (ks0.across(yStud)) {
				jointTracksToPath(ks1, ks0);
			} else if (ks1.across(yStud)) {
				jointTracksToPath(ks0, ks1);
			} else {
				jointTracksToPath(ks0, ks1);
				
				exitTrackToPath(yStud);
			}
			
		} else {
			/*
			 * no joints, do exit here
			 */
			exitTrackToPath(yStud);
		}
		
		/*
		 * regular row tracks -> paths
		 */
		List<GraphPosition> poss;
		GraphPositionPath path;
		
		for (Entry<Integer, List<GraphPosition>> entry : rowTracks.entrySet()) {
			Integer i = entry.getKey();
			
			if (!rowPaths.containsKey(i)) {
				List<GraphPosition> rt = entry.getValue();
				poss = new ArrayList<GraphPosition>();
				for (GraphPosition pos : rt) {
					poss.add(pos);
				}
				path = new GraphPositionPath(poss);
				rowPaths.put(i, path);
			}
			
		}
		
		/*
		 * regular col tracks
		 */
		for (Entry<Integer, List<GraphPosition>> entry : colTracks.entrySet()) {
			Integer i = entry.getKey();
			
			if (!colPaths.containsKey(i)) {
				List<GraphPosition> ct = entry.getValue();
				poss = new ArrayList<GraphPosition>();
				for (GraphPosition pos : ct) {
					poss.add(pos);
				}
				path = new GraphPositionPath(poss);
				colPaths.put(i, path);
			}
			
		}
		
		rowTracks = null;
		colTracks = null;
		
		/*
		 * negative space
		 */
		
		int allColCount = (int)Math.round(allStudsAABB.width / BypassStud.SIZE);
		int allRowCount = (int)Math.round(allStudsAABB.height / BypassStud.SIZE);
		for (int i = 0; i < allRowCount; i++) {
			jloop:
			for (int j = 0; j < allColCount; j++) {
				AABB n = new AABB(allStudsAABB.x + j * BypassStud.SIZE, allStudsAABB.y + i * BypassStud.SIZE, BypassStud.SIZE, BypassStud.SIZE);
				
				for (BypassStud ss : studs) {
					if (ss.aabb.equals(n)) {
						continue jloop;
					}
				}
				
				neg.add(n);
			}
		}		
	}
	
	/**
	 * create the last vertex and add a road connecting the stud fixture to it
	 */
	private void addExitRoad(BorderStud y) {
		
		Point other = null;
		Fixture f;
		switch(y.f.getFacingSide()) {
		case TOP:
			other = y.f.p.plus(new Point(0, EXITROADLENGTH));
			f = new Fixture(world, other, Axis.TOPBOTTOM);
			f.setFacingSide(Side.TOP);
			world.addFixture(f);
			break;
		case LEFT:
			other = y.f.p.plus(new Point(EXITROADLENGTH, 0));
			f = new Fixture(world, other, Axis.LEFTRIGHT);
			f.setFacingSide(Side.LEFT);
			world.addFixture(f);
			break;
		case RIGHT:
			other = y.f.p.plus(new Point(-EXITROADLENGTH, 0));
			f = new Fixture(world, other, Axis.LEFTRIGHT);
			f.setFacingSide(Side.RIGHT);
			world.addFixture(f);
			break;
		case BOTTOM:
			other = y.f.p.plus(new Point(0, -EXITROADLENGTH));
			f = new Fixture(world, other, Axis.TOPBOTTOM);
			f.setFacingSide(Side.BOTTOM);
			world.addFixture(f);
			break;
		}
		
		Stroke stroke = new Stroke(world);
		stroke.add(y.f.p);
		stroke.add(other);
		stroke.finish();
		
		stroke.processNewStroke(false);
	}
	
	/**
	 * 
	 */
	private static void addExitRoadToTrack(List<GraphPosition> track, BorderStud yStud) {
		
		Road r;
		switch(yStud.f.getFacingSide()) {
		case TOP:
		case LEFT:
			/*
			 * end of track, so add from stud to other vertex
			 */
			r = yStud.f.roads.get(0);
			if (yStud.f == r.start) {
				track.add(new VertexPosition(yStud.f));
				for (int ii = 1; ii < r.pointCount()-1; ii++) {
					track.add(new RoadPosition(r, ii, 0.0));
				}
				track.add(new VertexPosition(r.end));
			} else {
				track.add(new VertexPosition(yStud.f));
				for (int ii = r.pointCount()-2; ii >= 1; ii--) {
					track.add(new RoadPosition(r, ii, 0.0));
				}
				track.add(new VertexPosition(r.start));
			}
			break;
		case RIGHT:
		case BOTTOM:
			/*
			 * start of track, so add from other vertex to stud
			 */
			r = yStud.f.roads.get(0);
			if (yStud.f == r.end) {
				track.add(new VertexPosition(r.start));
				for (int ii = 1; ii < r.pointCount()-1; ii++) {
					track.add(new RoadPosition(r, ii, 0.0));
				}
				track.add(new VertexPosition(yStud.f));
			} else {
				track.add(new VertexPosition(r.end));
				for (int ii = r.pointCount()-2; ii >= 1; ii--) {
					track.add(new RoadPosition(r, ii, 0.0));
				}
				track.add(new VertexPosition(yStud.f));
			}
			break;
		}
		
	}
	
	private void addJointRoad(BorderStud[] joints) {
		
		BorderStud js0 = joints[0];
		BorderStud js1 = joints[1];
		Fixture f0 = js0.f;
		Fixture f1 = js1.f;
		Point start;
		Point c0;
		Point c1;
		Point end;
		if (js0.col < 0) {
			start = new Point(f0.p.x - STRAIGHTEXTENSION, f0.p.y);
			c0 = new Point(f0.p.x - CONTROLEXTENSION, f0.p.y);
		} else if (js0.col >= colCount) {
			start = new Point(f0.p.x + STRAIGHTEXTENSION, f0.p.y);
			c0 = new Point(f0.p.x + CONTROLEXTENSION, f0.p.y);
		} else if (js0.row < 0) {
			start = new Point(f0.p.x, f0.p.y - STRAIGHTEXTENSION);
			c0 = new Point(f0.p.x, f0.p.y - CONTROLEXTENSION);
		} else {
			assert js0.row >= rowCount;
			start = new Point(f0.p.x, f0.p.y + STRAIGHTEXTENSION);
			c0 = new Point(f0.p.x, f0.p.y + CONTROLEXTENSION);
		}
		if (js1.col < 0) {
			end = new Point(f1.p.x - STRAIGHTEXTENSION, f1.p.y);
			c1 = new Point(f1.p.x - CONTROLEXTENSION, f1.p.y);
		} else if (js1.col >= colCount) {
			end = new Point(f1.p.x + STRAIGHTEXTENSION, f1.p.y);
			c1 = new Point(f1.p.x + CONTROLEXTENSION, f1.p.y);
		} else if (js1.row < 0) {
			end = new Point(f1.p.x, f1.p.y - STRAIGHTEXTENSION);
			c1 = new Point(f1.p.x, f1.p.y - CONTROLEXTENSION);
		} else {
			assert js1.row >= rowCount;
			end = new Point(f1.p.x, f1.p.y + STRAIGHTEXTENSION);
			c1 = new Point(f1.p.x, f1.p.y + CONTROLEXTENSION);
		}
		CubicCurve c = new CubicCurve(start, c0, c1, end);
		
		List<Point> pts = c.skeleton();
		
		Stroke stroke = new Stroke(world);
		stroke.add(f0.p);
		for (Point p : pts) {
			stroke.add(p);
		}
		stroke.add(f1.p);
		stroke.finish();
		
		stroke.processNewStroke(false);
		
	}
	
	private void exitTrackToPath(BorderStud s0) {
		
		List<GraphPosition> s0Track;
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
		
		List<GraphPosition> poss = new ArrayList<GraphPosition>();
//		from other side of js0 track to js0
		if (s0.col < 0) {
			for (int i = s0Track.size()-1; i >= 0; i--) {
				poss.add(s0Track.get(i));
			}
		} else if (s0.col >= colCount) {
			for (int i = 0; i < s0Track.size(); i++) {
				poss.add(s0Track.get(i));
			}
		} else if (s0.row < 0) {
			for (int i = s0Track.size()-1; i >= 0; i--) {
				poss.add(s0Track.get(i));
			}
		} else {
			assert s0.row >= rowCount;
			for (int i = 0; i < s0Track.size(); i++) {
				poss.add(s0Track.get(i));
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
		
	}
	
	private void jointTracksToPath(BorderStud s0, BorderStud s1) {
				
		List<GraphPosition> s0Track;
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
		
		List<GraphPosition> s1Track;
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
	
	private void jointTracksToPath(BorderStud js0, BorderStud js1, BorderStud ks1, BorderStud ks0) {
		
		List<GraphPosition> js0Track;
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
		
		List<GraphPosition> js1Track;
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
		
		List<GraphPosition> ks1Track = js1Track;
		assert ks1Track != null;
		
		List<GraphPosition> ks0Track;
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
	
	
	
	
	public BypassStud stud(BypassBoardPosition pos) {
		for (BypassStud stud : studs) {
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
	
	public boolean preStep(double t) {
		return false;
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
		return ul.plus(new Point(col * BypassStud.SIZE, row * BypassStud.SIZE));
	}
	
	public BypassBoardPosition position(Point p) {
		return new BypassBoardPosition(this, (p.y - ul.y) / BypassStud.SIZE, (p.x - ul.x) / BypassStud.SIZE);
	}
	
	public BypassBoard hitTest(Point p) {
		for (BypassStud s : studs) {
			if (s.hitTest(p)) {
				return this;
			}
		}
		return null;
	}
	
	
	
	MutableAABB test = new MutableAABB();
	
	public boolean withinGrid(Car c, double angle, Point p) {
		
		Geom.localToWorldAndTakeAABB(c.localAABB, angle, p, test);
		
		boolean within = test.completelyWithin(gridAABB);
		
		return within;
	}
	
	
	static final MutableGPPP tmpFloorPos = new MutableGPPP();
	static final MutableAABB testFloor = new MutableAABB();
	
	static final MutableGPPP tmpCeilPos = new MutableGPPP();
	static final MutableAABB testCeil = new MutableAABB();
	
	public boolean floorAndCeilWithinGrid(Car c) {
		
		tmpFloorPos.set(c.driver.overallPos);
		tmpFloorPos.floor(c.length/2);
		Point tmpFloorP = tmpFloorPos.p;
		
		tmpCeilPos.set(c.driver.overallPos);
		tmpCeilPos.ceil(c.length/2);
		Point tmpCeilP = tmpCeilPos.p;
		
		Geom.localToWorldAndTakeAABB(c.localAABB, c.angle, tmpFloorP, testFloor);
		Geom.localToWorldAndTakeAABB(c.localAABB, c.angle, tmpCeilP, testCeil);
		
		boolean floorWithin = testFloor.completelyWithin(gridAABB);
		boolean ceilWithin = testCeil.completelyWithin(gridAABB);
		
		return floorWithin && ceilWithin;
	}
	
	public boolean enteringBoard(BypassBoardPosition prev, BypassBoardPosition cur) {
		assert !cur.equals(prev);
		
		double centerRow = 0.5 * rowCount;
		double centerCol = 0.5 * colCount;
		
		if (prev.rowCombo == cur.rowCombo) {
			
			if (Math.abs(cur.colCombo - centerCol) < Math.abs(prev.colCombo - centerCol)) {
				
				return true;
				
			} else {
				return false;
			}
			
		} else {
			assert prev.colCombo == cur.colCombo;
			
			if (Math.abs(cur.rowCombo - centerRow) < Math.abs(prev.rowCombo - centerRow)) {
				
				return true;
				
			} else {
				return false;
			}
			
		}
		
	}
	
	public double carInGridFraction(Car c) {
		
		MutableOBB o = c.shape;
		
		MutableAABB a = o.aabb;
		
		double frac = a.fractionWithin(gridFudgeAABB, allStudsAABB);
		
		return frac;
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void render() {
		
		switch (world.background.method) {
			case MONOLITHIC:
				break;
			case DYNAMIC:
				break;
			case RENDERED_GRAPH:
				break;
			case RENDERED_ROADS:
			case RENDERED_ROADS_VERTICES:
				break;
			case RENDERED_ROADS_VERTICES_BOARDS:
				
				if (img != null) {
					img.dispose();
				}
				
				img = APP.platform.createTransparentImage((int)(allStudsAABB.width * world.worldCamera.pixelsPerMeter), (int)(allStudsAABB.height * world.worldCamera.pixelsPerMeter));
				
				RenderingContext ctxt = APP.platform.createRenderingContext();
				APP.platform.setRenderingContextFields1(ctxt, img);
				
				ctxt.cam = world.worldCamera;
				
				ctxt.scale(world.worldCamera.pixelsPerMeter, world.worldCamera.pixelsPerMeter);
				ctxt.translate(-allStudsAABB.x, -allStudsAABB.y);
				
				paintStuds(ctxt);
				
				ctxt.dispose();
				
				break;
			
		}
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
//		if (!ShapeUtils.intersectAA(allStudsAABB, ctxt.cam.worldViewport)) {
//			return;
//		}
		
		switch (world.background.method) {
		case MONOLITHIC:
			break;
		case DYNAMIC:
			paintStuds(ctxt);
			break;
		case RENDERED_GRAPH:
			break;
		case RENDERED_ROADS:
		case RENDERED_ROADS_VERTICES:
			break;
		case RENDERED_ROADS_VERTICES_BOARDS:
			ctxt.paintImage(img, ctxt.cam.pixelsPerMeter, ctxt.cam.pixelsPerMeter,
					allStudsAABB.x, allStudsAABB.y, allStudsAABB.x+allStudsAABB.width, allStudsAABB.y+allStudsAABB.height,
					0, 0, img.getWidth(), img.getHeight());
			break;
		}
		
		if (APP.DEBUG_DRAW) {
			
			for (Line l : perimeterSegments) {
				ctxt.setColor(Color.RED);
				double pixel = 1/ctxt.cam.pixelsPerMeter;
				ctxt.setStroke(2.0 * pixel, Cap.SQUARE, Join.MITER);
				l.draw(ctxt);
			}
			
			ctxt.setColor(Color.BLUE);
			gridAABB.draw(ctxt);
			
			ctxt.setColor(Color.GREEN);
			allStudsAABB.draw(ctxt);
			
			ctxt.setColor(Color.YELLOW);
			zoomingAABB.draw(ctxt);
		}
		
	}
	
	public void paintStuds(RenderingContext ctxt) {
		
		ctxt.setColor(Color.ROADCOLOR);
		backAABB.paint(ctxt);
		
		for (int i = 0; i < studs.size(); i++) {
			BypassStud s = studs.get(i);
			s.paint(ctxt);
		}
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		for (BypassStud s : studs) {
			s.paint(ctxt);
		}
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
	}

}
