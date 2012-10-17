package com.gutabi.deadlock.utils;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;

public class Java2DUtils {
	
	public static List<Point> shapeToList(Shape s) {
		
		PathIterator pi = s.getPathIterator(null, 0.1);
		float[] coords = new float[6];
		Point lastPoint;
		List<Point> poly = null;
		while (!pi.isDone()) {
			int res = pi.currentSegment(coords);
			switch (res) {
			case PathIterator.SEG_MOVETO:
//				logger.debug("moveto");
				assert poly == null;
				poly = new ArrayList<Point>();
				lastPoint = new Point(coords[0], coords[1]);
				poly.add(lastPoint);
				break;
			case PathIterator.SEG_LINETO:
//				logger.debug("lineto");
				lastPoint = new Point(coords[0], coords[1]);
				poly.add(lastPoint);
				break;
			case PathIterator.SEG_CLOSE:
//				logger.debug("close");
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
				path.moveTo(p.getX(), p.getY());
			} else {
				path.lineTo(p.getX(), p.getY());
			}
		}
		path.closePath();
		return path;
	}
	
}
