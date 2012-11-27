package com.gutabi.deadlock.core.graph;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Color;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Line;
import com.gutabi.deadlock.core.geom.Quad;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.core.geom.SweepableShape;
import com.gutabi.deadlock.model.fixture.MergerSink;
import com.gutabi.deadlock.model.fixture.MergerSource;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class Merger extends Edge {
	
	public static final double MERGER_WIDTH = 5.0;
	public static final double MERGER_HEIGHT = 5.0;
	
	public final Point ul;
	
	private Color color;
	private Color hiliteColor;
	
	public final MergerSink top;
	public final MergerSink left;
	public final MergerSource right;
	public final MergerSource bottom;
	
	private double[] cumulativeLengthsFromTop;
	private double[] cumulativeLengthsFromLeft;
	
	private EdgeDirection leftRightDir;
	private EdgeDirection topBottomDir;
	
	private Quad shape;
	
	private final Line debugSkeletonTopBottomLine;
	private final Line debugSkeletonLeftRightLine;
	
	public Merger(Point center) {
		
		this.ul = center.plus(new Point(-MERGER_WIDTH/2,  -MERGER_HEIGHT/2));
		
		color = Color.BLUE;
		hiliteColor = Color.ORANGE;
		
		top = new MergerSink(new Point(ul.x + MERGER_WIDTH/2, ul.y), Axis.TOPBOTTOM);
		left = new MergerSink(new Point(ul.x, ul.y + MERGER_HEIGHT/2), Axis.LEFTRIGHT);
		right = new MergerSource(new Point(ul.x + MERGER_WIDTH, ul.y + MERGER_HEIGHT/2), Axis.LEFTRIGHT);
		bottom = new MergerSource(new Point(ul.x + MERGER_WIDTH/2, ul.y+MERGER_HEIGHT), Axis.TOPBOTTOM);
		
		top.matchingSource = bottom;
		bottom.matchingSink = top;
		
		left.matchingSource = right;
		right.matchingSink = left;
		
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
		
		debugSkeletonTopBottomLine = new Line(top.shape.center.x, top.shape.center.y, bottom.shape.center.x, bottom.shape.center.y);
		debugSkeletonLeftRightLine = new Line(left.shape.center.x, left.shape.center.y, right.shape.center.x, right.shape.center.y);
		
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
		assert a != Axis.NONE;
		if (a == Axis.LEFTRIGHT) {
			return left;
		} else {
			return top;
		}
	}
	
	public Vertex getOtherVertex(Axis a) {
		assert a != Axis.NONE;
		if (a == Axis.LEFTRIGHT) {
			return right;
		} else {
			return bottom;
		}
	}
	
	public void setDirection(Axis a, EdgeDirection dir) {
		if (a == Axis.LEFTRIGHT) {
			this.leftRightDir = dir;
		} else if (a == Axis.TOPBOTTOM) {
			this.topBottomDir = dir;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public EdgeDirection getDirection(Axis a) {
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
			return topBottomDir != EdgeDirection.ENDTOSTART;
		} else if (a == left) {
			assert b == right;
			return leftRightDir != EdgeDirection.ENDTOSTART;
		} else if (a == right) {
			assert b == left;
			return leftRightDir != EdgeDirection.STARTTOEND;
		} else {
			assert a == bottom;
			assert b == top;
			return topBottomDir != EdgeDirection.STARTTOEND;
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
	
	
	@Override
	public boolean isDeleteable() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		;
	}
	
	@Override
	public void preStep(double t) {
		;
	}

	@Override
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
	
	@Override
	public void paint(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			if (!MODEL.DEBUG_DRAW) {
				
				ctxt.g2.setColor(color);
				
				shape.paint(ctxt);
				
			} else {
				
				ctxt.g2.setColor(color);
				
				shape.draw(ctxt);
				
				paintSkeleton(ctxt);
				
			}
			break;
		case PREVIEW:
			ctxt.g2.setColor(color);
			shape.paint(ctxt);
			break;
		}
		
	}

	@Override
	public void paintHilite(RenderingContext ctxt) {
		
		ctxt.g2.setColor(hiliteColor);
		
		shape.paint(ctxt);
		
	}
	
	void paintSkeleton(RenderingContext ctxt) {
		
		ctxt.g2.setColor(Color.BLACK);
		
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
