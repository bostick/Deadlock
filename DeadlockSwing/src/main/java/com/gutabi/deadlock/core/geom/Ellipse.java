package com.gutabi.deadlock.core.geom;

import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class Ellipse {
	
	public final Point center;
	public final double xRadius;
	public final double yRadius;
	
	public final AABB aabb;
	
	private final Ellipse2D ellipse;
	
	static Logger logger = Logger.getLogger(Circle.class);
	
	public Ellipse(Point center, double xRadius, double yRadius) {
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		ellipse = new Ellipse2D.Double(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
		
		aabb = new AABB(center.x - xRadius, center.y - yRadius, 2*xRadius, 2*yRadius);
	}
	
	public List<Point> skeleton() {
		
		PathIterator pi = ellipse.getPathIterator(null, 0.1);
		
		double[] coords = new double[6];
		
		List<Point> pts = null;
		Point firstPoint = null;
		Point lastPoint;
		
		while (true) {
			
			int res = pi.currentSegment(coords);
			switch (res) {
			case PathIterator.SEG_MOVETO:
				pts = new ArrayList<Point>();
				firstPoint = new Point(coords[0], coords[1]);
				lastPoint = firstPoint;
				pts.add(lastPoint);
				break;
			case PathIterator.SEG_LINETO:
				lastPoint = new Point(coords[0], coords[1]);
				pts.add(lastPoint);
				break;
			case PathIterator.SEG_CLOSE:
				pts.add(firstPoint);
				break;
			}
			
			pi.next();
			
			if (pi.isDone()) {
				break;
			}
			
		}
		
		return pts;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.fill(ellipse);
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		ctxt.draw(ellipse);
		
	}
}
