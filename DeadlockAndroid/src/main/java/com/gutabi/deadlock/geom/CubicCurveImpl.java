package com.gutabi.deadlock.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class CubicCurveImpl extends CubicCurve {
	
	Path p;
	
	public CubicCurveImpl(Point p0, Point c0, Point c1, Point p1) {
		super(p0, c0, c1, p1);
		
		Path p = new Path();
		p.moveTo((float)p0.x, (float)p0.y);
		p.cubicTo((float)c0.x, (float)c0.y, (float)c1.x, (float)c1.y, (float)p1.x, (float)p1.y);
		
	}
	
	public void paint(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.paint.setStyle(Style.FILL);
		c.canvas.drawPath(p, c.paint);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.paint.setStyle(Style.FILL);
		c.canvas.drawPath(p, c.paint);
	}
}
