package com.gutabi.bypass.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.deadlock.geom.MutablePolygon;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MutablePolygonImpl extends MutablePolygon {
	
	Path p = new Path();
	
	public void setPoints(Point p0, Point p1, Point p2, Point p3) {
		
		super.setPoints(p0, p1, p2, p3);
		
		p.reset();
		p.moveTo((float)p0.x, (float)p0.y);
		p.lineTo((float)p1.x, (float)p1.y);
		p.lineTo((float)p2.x, (float)p2.y);
		p.lineTo((float)p3.x, (float)p3.y);
		p.close();
		
	}
	
	public void setPoints(double p0x, double p0y, double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {
		super.setPoints(p0x, p0y, p1x, p1y, p2x, p2y, p3x, p3y);
		
		p.reset();
		p.moveTo((float)p0x, (float)p0y);
		p.lineTo((float)p1x, (float)p1y);
		p.lineTo((float)p2x, (float)p2y);
		p.lineTo((float)p3x, (float)p3y);
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
