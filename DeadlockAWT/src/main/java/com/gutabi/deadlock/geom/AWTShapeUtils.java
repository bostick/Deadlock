package com.gutabi.deadlock.geom;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.math.Point;

public class AWTShapeUtils {
	
	public static List<Point> skeleton(java.awt.Shape s) {
		
		PathIterator pi = s.getPathIterator(null, 0.05);
		
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
	
}
