package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourBoard extends Entity {
	
	World world;
	Point p;
	
	public List<RushHourStud> studs = new ArrayList<RushHourStud>();
	public List<AABB> neg = new ArrayList<AABB>();
	
	public Point ul;
	public int[] rowRange = { 0, 0 };
	public int[] colRange = { 0, 0 };
	public AABB aabb;
	
	private Map<Integer, GraphPositionPath> rowPaths = new HashMap<Integer, GraphPositionPath>();
	private Map<Integer, GraphPositionPath> colPaths = new HashMap<Integer, GraphPositionPath>();
	
	public RushHourBoard(World world, Point p) {
		this.world = world;
		this.p = p;
		
		ul = new Point(p.x - 3 * RushHourStud.SIZE, p.y - 3 * RushHourStud.SIZE);
		
		RushHourStud s;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				s = new RushHourStud(world, this, i, j);
				studs.add(s);
				if (i > rowRange[1]) {
					rowRange[1] = i;
				}
				if (i < rowRange[0]) {
					rowRange[0] = i;
				}
				if (j > colRange[1]) {
					colRange[1] = j;
				}
				if (j < colRange[0]) {
					colRange[0] = j;
				}
				aabb = AABB.union(aabb, s.aabb);
			}
		}
		
		s = new RushHourStud(world, this, 2, 6);
		studs.add(s);
		if (2 > rowRange[1]) {
			rowRange[1] = 2;
		}
		if (2 < rowRange[0]) {
			rowRange[0] = 2;
		}
		if (6 > colRange[1]) {
			colRange[1] = 6;
		}
		if (6 < colRange[0]) {
			colRange[0] = 6;
		}
		
		s = new RushHourStud(world, this, 2, 7);
		studs.add(s);
		if (2 > rowRange[1]) {
			rowRange[1] = 2;
		}
		if (2 < rowRange[0]) {
			rowRange[0] = 2;
		}
		if (7 > colRange[1]) {
			colRange[1] = 7;
		}
		if (7 < colRange[0]) {
			colRange[0] = 7;
		}
		
		s = new RushHourStud(world, this, 0, -1);
		studs.add(s);
		if (0 > rowRange[1]) {
			rowRange[1] = 0;
		}
		if (0 < rowRange[0]) {
			rowRange[0] = 0;
		}
		if (-1 > colRange[1]) {
			colRange[1] = -1;
		}
		if (-1 < colRange[0]) {
			colRange[0] = -1;
		}
		
		aabb = null;
		for (RushHourStud ss : studs) {
			aabb = AABB.union(aabb, ss.aabb);
		}
		
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
