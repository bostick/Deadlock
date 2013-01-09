package com.gutabi.deadlock.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;

import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class AABBImpl extends AABB {
	
	Path p;
	
	public AABBImpl(double x, double y, double width, double height) {
		super(x, y, width, height);
		
		p = new Path();
		p.addRect((float)x, (float)y, (float)(x+width), (float)(y+height), Direction.CW);
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
