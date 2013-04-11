package com.gutabi.bypass.geom;

import android.graphics.Path;
import android.graphics.Paint.Style;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.Polygon;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class PolygonImpl extends Polygon {
	
	Path p;
	
	public PolygonImpl(Point... pts) {
		super(pts);
		
		p = new Path();
		p.moveTo((float)pts[0].x, (float)pts[0].y);
		for (int i = 1; i < pts.length; i++) {
			Point pp = pts[i];
			p.lineTo((float)pp.x, (float)pp.y);
		}
		p.close();
		
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.paint.setStyle(Style.FILL);
		c.canvas.drawPath(p, c.paint);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.paint.setStyle(Style.STROKE);
		c.canvas.drawPath(p, c.paint);
	}
	
}
