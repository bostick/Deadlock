package com.gutabi.bypass.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.geom.Polyline;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class PolylineImpl extends Polyline {
	
	Path p;
	
	public PolylineImpl(Point... pts) {
		super(pts);
		
		p = new Path();
		
		Point p0 = pts[0];
		
		p.moveTo((float)p0.x, (float)p0.y);
		for (int i = 1; i < pts.length; i++) {
			Point pp = pts[i];
			p.lineTo((float)pp.x, (float)pp.y);
		}
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
