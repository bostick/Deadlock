package com.gutabi.deadlock.utils;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;

public class Java2DUtils {
	
	public static List<Point> shapeToList(Shape s, double flatness) {
		
		PathIterator pi = s.getPathIterator(null, flatness);
		float[] coords = new float[6];
		Point lastPoint;
		List<Point> poly = null;
		while (!pi.isDone()) {
			int res = pi.currentSegment(coords);
			switch (res) {
			case PathIterator.SEG_MOVETO:
				assert poly == null;
				poly = new ArrayList<Point>();
				lastPoint = new Point(coords[0], coords[1]);
				poly.add(lastPoint);
				break;
			case PathIterator.SEG_LINETO:
				lastPoint = new Point(coords[0], coords[1]);
				poly.add(lastPoint);
				break;
			case PathIterator.SEG_CLOSE:
				break;
			default:
				assert false;
				break;
			}
			pi.next();
		}
		
		return poly;
	}
	
	public static Path2D listToPath(List<Point> poly) {
		GeneralPath path = new GeneralPath();
		for (int i = 0; i < poly.size(); i++) {
			Point p = poly.get(i);
			if (i == 0) {
				path.moveTo(p.x, p.y);
			} else {
				path.lineTo(p.x, p.y);
			}
		}
		path.closePath();
		return path;
	}
	
}
