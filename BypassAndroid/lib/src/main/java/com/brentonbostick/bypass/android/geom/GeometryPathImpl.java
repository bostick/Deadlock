package com.brentonbostick.bypass.android.geom;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

import com.brentonbostick.bypass.android.ui.paint.RenderingContextImpl;
import com.brentonbostick.capsloc.geom.Circle;
import com.brentonbostick.capsloc.geom.CubicCurve;
import com.brentonbostick.capsloc.geom.Ellipse;
import com.brentonbostick.capsloc.geom.GeometryPath;
import com.brentonbostick.capsloc.geom.MutableOBB;
import com.brentonbostick.capsloc.geom.MutablePolygon;
import com.brentonbostick.capsloc.geom.OBB;
import com.brentonbostick.capsloc.geom.Polygon;
import com.brentonbostick.capsloc.geom.Polyline;
import com.brentonbostick.capsloc.geom.QuadCurve;
import com.brentonbostick.capsloc.geom.Triangle;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public class GeometryPathImpl implements GeometryPath {

	public Path p = new Path();
	
	public void reset() {
		p.reset();
	}
	
	public void add(QuadCurve q) {
		p.moveTo((float)q.p0.x, (float)q.p0.y);
		p.quadTo((float)q.c0.x, (float)q.c0.y, (float)q.p1.x, (float)q.p1.y);
	}
	
	public void add(CubicCurve c) {
		p.moveTo((float)c.p0.x, (float)c.p0.y);
		p.cubicTo((float)c.c0.x, (float)c.c0.y, (float)c.c1.x, (float)c.c1.y, (float)c.p1.x, (float)c.p1.y);
	}
	
	public void add(Ellipse e) {
		p.addOval(new RectF((float)(e.center.x - e.xRadius), (float)(e.center.y - e.yRadius), (float)(e.center.x+e.xRadius), (float)(e.center.y+e.yRadius)), Direction.CW);
	}
	
	public void add(Circle c) {
		p.addCircle((float)c.center.x, (float)c.center.y, (float)c.radius, Direction.CW);
	}
	
	public void add(OBB o) {
		p.moveTo((float)o.p0.x, (float)o.p0.y);
		p.lineTo((float)o.p1.x, (float)o.p1.y);
		p.lineTo((float)o.p2.x, (float)o.p2.y);
		p.lineTo((float)o.p3.x, (float)o.p3.y);
		p.close();
	}
	
	public void add(MutableOBB o) {
		p.moveTo((float)o.p0.x, (float)o.p0.y);
		p.lineTo((float)o.p1.x, (float)o.p1.y);
		p.lineTo((float)o.p2.x, (float)o.p2.y);
		p.lineTo((float)o.p3.x, (float)o.p3.y);
		p.close();
	}
	
	public void add(Triangle t) {
		p.moveTo((float)t.p0.x, (float)t.p0.y);
		p.lineTo((float)t.p1.x, (float)t.p1.y);
		p.lineTo((float)t.p2.x, (float)t.p2.y);
		p.close();
	}
	
	public void add(Polygon poly) {
		for (int i = 0; i < poly.pts.length; i++) {
			if (i == 0) {
				p.moveTo((float)poly.pts[i].x, (float)poly.pts[i].y);
			} else {
				p.lineTo((float)poly.pts[i].x, (float)poly.pts[i].y);
			}
		}
		p.close();
	}
	
	public void add(MutablePolygon poly) {
		for (int i = 0; i < poly.pts.length; i++) {
			if (i == 0) {
				p.moveTo((float)poly.pts[i][0], (float)poly.pts[i][1]);
			} else {
				p.lineTo((float)poly.pts[i][0], (float)poly.pts[i][1]);
			}
		}
		p.close();
	}
	
	public void add(Polyline poly) {
		for (int i = 0; i < poly.pts.length; i++) {
			if (i == 0) {
				p.moveTo((float)poly.pts[i].x, (float)poly.pts[i].y);
			} else {
				p.lineTo((float)poly.pts[i].x, (float)poly.pts[i].y);
			}
		}
		p.close();
	}
	
	
	
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.paintPath(this);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.drawPath(this);
	}

}
