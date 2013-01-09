package com.gutabi.deadlock.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class QuadImpl extends Quad {
	
	Path p;
	
	public QuadImpl(Object parent, Point p0, Point p1, Point p2, Point p3) {
		super(parent, p0, p1, p2, p3);
		
		p = new Path();
		p.moveTo((float)p0.x, (float)p0.y);
		p.lineTo((float)p1.x, (float)p1.y);
		p.lineTo((float)p2.x, (float)p2.y);
		p.lineTo((float)p3.x, (float)p3.y);
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
