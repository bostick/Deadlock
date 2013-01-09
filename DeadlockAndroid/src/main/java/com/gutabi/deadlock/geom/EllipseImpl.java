package com.gutabi.deadlock.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.ui.paint.RenderingContextImpl;

public class EllipseImpl extends Ellipse {
	
	Path p;
	
	public EllipseImpl(Point center, double xRadius, double yRadius) {
		super(center, xRadius, yRadius);
		
		p = new Path();
		p.addOval(new RectF((float)(center.x - xRadius), (float)(center.y - yRadius), (float)(center.x+xRadius), (float)(center.y+yRadius)), Direction.CW);
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
