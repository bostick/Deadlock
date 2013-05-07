package com.gutabi.bypass.geom;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.geom.CubicCurve;
import com.gutabi.capsloc.geom.Ellipse;
import com.gutabi.capsloc.geom.GeometryPath;
import com.gutabi.capsloc.geom.MutableOBB;
import com.gutabi.capsloc.geom.MutablePolygon;
import com.gutabi.capsloc.geom.OBB;
import com.gutabi.capsloc.geom.Polygon;
import com.gutabi.capsloc.geom.Polyline;
import com.gutabi.capsloc.geom.QuadCurve;
import com.gutabi.capsloc.geom.Triangle;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class GeometryPathImpl implements GeometryPath {
	
	public GeneralPath path = new GeneralPath();
	
	public void reset() {
		path.reset();
	}
	
	public void add(QuadCurve q) {
		path.moveTo(q.p0.x, q.p0.y);
		path.quadTo(q.c0.x, q.c0.y, q.p1.x, q.p1.y);	
	}
	
	public void add(CubicCurve c) {
		path.moveTo(c.p0.x, c.p0.y);
		path.curveTo(c.c0.x, c.c0.y, c.c1.x, c.c1.y, c.p1.x, c.p1.y);	
	}
	
	public void add(Ellipse e) {
		path.append(new Ellipse2D.Double(e.center.x-e.xRadius, e.center.y-e.yRadius, 2*e.xRadius, 2*e.yRadius), true);
	}
	
	public void add(Circle c) {
		path.append(new Ellipse2D.Double(c.center.x-c.radius, c.center.y-c.radius, 2*c.radius, 2*c.radius), true);
	}
	
	public void add(Triangle t) {
		path.moveTo(t.p0.x, t.p0.y);
		path.lineTo(t.p1.x, t.p1.y);
		path.lineTo(t.p2.x, t.p2.y);
		path.closePath();
	}
	
	public void add(OBB o) {
		path.moveTo(o.p0.x, o.p0.y);
		path.lineTo(o.p1.x, o.p1.y);
		path.lineTo(o.p2.x, o.p2.y);
		path.lineTo(o.p3.x, o.p3.y);
		path.closePath();
	}
	
	public void add(MutableOBB o) {
		path.moveTo(o.p0.x, o.p0.y);
		path.lineTo(o.p1.x, o.p1.y);
		path.lineTo(o.p2.x, o.p2.y);
		path.lineTo(o.p3.x, o.p3.y);
		path.closePath();
	}
	
	public void add(Polygon poly) {
		for (int i = 0; i < poly.pts.length; i++) {
			if (i == 0) {
				path.moveTo(poly.pts[i].x, poly.pts[i].y);
			} else {
				path.lineTo(poly.pts[i].x, poly.pts[i].y);
			}
		}
		path.closePath();
	}
	
	public void add(MutablePolygon poly) {
		for (int i = 0; i < poly.pts.length; i++) {
			if (i == 0) {
				path.moveTo(poly.pts[i][0], poly.pts[i][1]);
			} else {
				path.lineTo(poly.pts[i][0], poly.pts[i][1]);
			}
		}
		path.closePath();
	}
	
	public void add(Polyline poly) {
		for (int i = 0; i < poly.pts.length; i++) {
			if (i == 0) {
				path.moveTo(poly.pts[i].x, poly.pts[i].y);
			} else {
				path.lineTo(poly.pts[i].x, poly.pts[i].y);
			}
		}
		path.closePath();
	}
	
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.fill(path);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl ct = (RenderingContextImpl)ctxt;
		ct.g2.draw(path);
	}
	
}
