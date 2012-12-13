package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.SweepableShape;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class Merger extends Edge {
	
	public static final double MERGER_WIDTH = 5.0;
	public static final double MERGER_HEIGHT = 5.0;
	
	public final Point ul;
	
	public final Fixture top;
	public final Fixture left;
	public final Fixture right;
	public final Fixture bottom;
	
	private double[] cumulativeLengthsFromTop;
	private double[] cumulativeLengthsFromLeft;
	
	private Direction leftRightDir;
	private Direction topBottomDir;
	
	private Quad shape;
	
	private final Line debugSkeletonTopBottomLine;
	private final Line debugSkeletonLeftRightLine;
	
	public Merger(Point center) {
		
		this.ul = center.plus(new Point(-MERGER_WIDTH/2,  -MERGER_HEIGHT/2));
		
		top = new Fixture(new Point(ul.x + MERGER_WIDTH/2, ul.y), Axis.TOPBOTTOM);
		left = new Fixture(new Point(ul.x, ul.y + MERGER_HEIGHT/2), Axis.LEFTRIGHT);
		right = new Fixture(new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT/2), Axis.LEFTRIGHT);
		bottom = new Fixture(new Point(ul.x + MERGER_WIDTH/2, ul.y+MERGER_HEIGHT), Axis.TOPBOTTOM);
		
		top.match = bottom;
		bottom.match = top;
		
		left.match = right;
		right.match = left;
		
		top.setSide(Side.BOTTOM);
		bottom.setSide(Side.BOTTOM);
		left.setSide(Side.RIGHT);
		right.setSide(Side.RIGHT);
		
		Point p0 = ul;
		Point p1 = new Point(ul.x + MERGER_WIDTH, ul.y);
		Point p2 = new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT);
		Point p3 = new Point(ul.x, ul.y + MERGER_HEIGHT);
		shape = new Quad(this, p0, p1, p2, p3);
		
		top.m = this;
		left.m = this;
		right.m = this;
		bottom.m = this;
		
		computeLengths();
		
		debugSkeletonTopBottomLine = new Line(top.shape.center, bottom.shape.center);
		debugSkeletonLeftRightLine = new Line(left.shape.center, right.shape.center);
		
	}
	
	public void destroy() {
		top.m = null;
		left.m = null;
		right.m = null;
		bottom.m = null;
	}
	
	public String toString() {
		return "Merger[" + top.id + " " + left.id + " " + right.id + " " + bottom.id + "]";
	}
	
	public int pointCount() {
		return 3;
	}
	
	public double getTotalLength(Vertex a, Vertex b) {
		if (a == top || a == bottom) {
			return MERGER_HEIGHT;
		} else {
			return MERGER_WIDTH;
		}
	}
	
	public Vertex getReferenceVertex(Axis a) {
		assert a != null;
		if (a == Axis.LEFTRIGHT) {
			return left;
		} else {
			return top;
		}
	}
	
	public Vertex getOtherVertex(Axis a) {
		assert a != null;
		if (a == Axis.LEFTRIGHT) {
			return right;
		} else {
			return bottom;
		}
	}
	
	public void setDirection(Axis a, Direction dir) {
		if (a == Axis.LEFTRIGHT) {
			this.leftRightDir = dir;
		} else if (a == Axis.TOPBOTTOM) {
			this.topBottomDir = dir;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public Direction getDirection(Axis a) {
		if (a == Axis.LEFTRIGHT) {
			return leftRightDir;
		} if (a == Axis.TOPBOTTOM) {
			return topBottomDir;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public boolean canTravelFromTo(Vertex a, Vertex b) {
		if (a == top) {
			assert b == bottom;
			return topBottomDir != Direction.ENDTOSTART;
		} else if (a == left) {
			assert b == right;
			return leftRightDir != Direction.ENDTOSTART;
		} else if (a == right) {
			assert b == left;
			return leftRightDir != Direction.STARTTOEND;
		} else {
			assert a == bottom;
			assert b == top;
			return topBottomDir != Direction.STARTTOEND;
		}
	}
	
	public SweepableShape getShape() {
		return shape;
	}
	
	public final Entity hitTest(Point p) {
		if (shape.hitTest(p)) {
			return this;
		} else {
			return null;
		}
	}
	
	public void enterDistancesMatrix(double[][] distances) {
		distances[top.id][bottom.id] = Merger.MERGER_HEIGHT;
		distances[bottom.id][top.id] = Merger.MERGER_HEIGHT;
		distances[left.id][right.id] = Merger.MERGER_WIDTH;
		distances[right.id][left.id] = Merger.MERGER_WIDTH;
	}
	
	public GraphPosition travelFromReferenceVertex(Axis a, double dist) {
		if (a == Axis.TOPBOTTOM) {
			return MergerPosition.travelFromTop(this, dist);
		} else {
			assert a == Axis.LEFTRIGHT;
			return MergerPosition.travelFromLeft(this, dist);
		}
	}
	
	public GraphPosition travelFromOtherVertex(Axis a, double dist) {
		if (a == Axis.TOPBOTTOM) {
			return MergerPosition.travelFromBottom(this, dist);
		} else {
			assert a == Axis.LEFTRIGHT;
			return MergerPosition.travelFromRight(this, dist);
		}
	}
	
	public Entity decorationsHitTest(Point p) {
		return null;
	}
	
	public Entity decorationsBestHitTest(Shape s) {
		return null;
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public Point get(int index, Axis a) {
		if (index == 0 || index == 2) {
			if (a == Axis.TOPBOTTOM) {
				if (index == 0) {
					return ul.plus(new Point(Merger.MERGER_WIDTH/2, 0));
				} else {
					return ul.plus(new Point(Merger.MERGER_WIDTH/2, Merger.MERGER_HEIGHT));
				}
			} else {
				if (index == 0) {
					return ul.plus(new Point(0, Merger.MERGER_HEIGHT/2));
				} else {
					return ul.plus(new Point(Merger.MERGER_WIDTH, Merger.MERGER_HEIGHT/2));
				}
			}
		} else {
			assert index == 1;
			return ul.plus(new Point(Merger.MERGER_WIDTH/2, Merger.MERGER_HEIGHT/2));
		}
	}
	
	public double getLengthFromLeft(int i) {
		return cumulativeLengthsFromLeft[i];
	}
	
	public double getLengthFromTop(int i) {
		return cumulativeLengthsFromTop[i];
	}
	
	private void computeLengths() {
		
		cumulativeLengthsFromTop = new double[3];
		cumulativeLengthsFromLeft = new double[3];
		
		double length;
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				cumulativeLengthsFromTop[i] = 0;
			}
			length = Merger.MERGER_HEIGHT/2;
			cumulativeLengthsFromTop[i+1] = cumulativeLengthsFromTop[i] + length;
		}
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				cumulativeLengthsFromLeft[i] = 0;
			}
			length = Merger.MERGER_HEIGHT/2;
			cumulativeLengthsFromLeft[i+1] = cumulativeLengthsFromLeft[i] + length;
		}
	}
	
	public void paint(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			if (!APP.DEBUG_DRAW) {
				
				ctxt.setColor(Color.GRAY);
				
				shape.paint(ctxt);
				
			} else {
				
				ctxt.setColor(Color.GRAY);
				ctxt.setWorldPixelStroke(1);
				shape.draw(ctxt);
				
				paintSkeleton(ctxt);
				
			}
			break;
		case PREVIEW:
			ctxt.setColor(Color.GRAY);
			shape.paint(ctxt);
			break;
		}
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		ctxt.setWorldPixelStroke(1);
		ctxt.setColor(Color.GRAY);
		shape.draw(ctxt);
	}
	
	void paintSkeleton(RenderingContext ctxt) {
		
		ctxt.setColor(Color.BLACK);
		ctxt.setWorldPixelStroke(1);
		debugSkeletonTopBottomLine.draw(ctxt);
		
		debugSkeletonLeftRightLine.draw(ctxt);
		
	}
	
	public void paintBorders(RenderingContext ctxt) {
		;
	}
	
	public void paintDecorations(RenderingContext ctxt) {
		;
	}
	
}
