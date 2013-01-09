package com.gutabi.deadlock.geom;

import java.util.List;

import android.graphics.Path;
import android.graphics.Paint.Style;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class PolylineImpl extends Polyline {
	
	Path p;
	
	public PolylineImpl(List<Point> pts) {
		super(pts);
		
		p = new Path();
		
		Point p0 = pts.get(0);
		
		p.moveTo((float)p0.x, (float)p0.y);
		for (int i = 1; i < pts.size(); i++) {
			Point pp = pts.get(i);
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
