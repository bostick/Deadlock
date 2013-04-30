package com.gutabi.bypass.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.geom.Polygon;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class PolygonImpl extends Polygon {
	
	Path p;
	
	public PolygonImpl(Point p0, Point p1, Point p2, Point p3) {
		super(p0, p1, p2, p3);
		
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
