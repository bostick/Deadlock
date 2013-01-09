package com.gutabi.deadlock.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class CircleImpl extends Circle {
	
	Path p;
	
	public CircleImpl(Object parent, Point center, double radius) {
		super(parent, center, radius);
		
		p = new Path();
		p.addCircle((float)center.x, (float)center.y, (float)radius, Direction.CW);
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
