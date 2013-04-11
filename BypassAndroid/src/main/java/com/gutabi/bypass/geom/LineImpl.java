package com.gutabi.bypass.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class LineImpl extends Line {
	
	Path p;
	
	public LineImpl(Point p0, Point p1) {
		super(p0, p1);
		
		p = new Path();
		p.moveTo((float)p0.x, (float)p0.y);
		p.lineTo((float)p1.x, (float)p1.y);
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
